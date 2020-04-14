package com.bope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Component
public class CodeNamesBot extends TelegramLongPollingBot {

    @Autowired
    private UsersListMongo usersListMongo;

    @Value("${token}")
    private String token;
    private Map<Long, Game> games = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        String text = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        long chatId = update.getMessage().getChatId();

        System.out.println(text);
        System.out.println(user);
        System.out.println(chatId);

//        if (user.getId() != 119970632)
//            return;

        if (text.equals(" "))
            return;

        if ((text.equals("eng") || text.equals("ukr") || text.equals("rus"))
                && chatId != user.getId()
                && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                && update.getMessage().getReplyToMessage().getText().equals("Choose the language:")
        ) {
            if (games.containsKey(chatId))
                games.get(chatId).setLang(text);
            else
                games.put(chatId, new Game(chatId, text));
            sendSimpleMessage("Set language: " + text, chatId, true);
            return;
        }

        if ((text.equals("Enable") || text.equals("Disable"))
                && chatId != user.getId()
                && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                && update.getMessage().getReplyToMessage().getText().equals("Keyboard usage:")
        ) {
            if (games.containsKey(chatId))
                games.get(chatId).setUseKeyboard(text.equals("Enable"));
            else
                games.put(chatId, new Game(chatId, "rus", text.equals("Enable")));
            sendSimpleMessage("Keyboard " + text + "d!", chatId, true);
            return;
        }

        if (!text.substring(0, 1).equals("/"))
            text = "/" + text;

        if (text.equals("/keyboard") || text.equals("/keyboard@" + getBotUsername())) {
            sendSimpleMessage("Keyboard usage:", getKeyboard("Enable", "Disable"), chatId);
            return;
        }

        if (text.equals("/start")) {
            if (chatId == user.getId() && usersListMongo.findByUserName(user.getUserName()) == null)
                usersListMongo.save(new UserMongo(user.getUserName(), String.valueOf(user.getId())));
            return;
        }

        if (text.toLowerCase().equals("/caps") || text.toLowerCase().equals("/caps@" + getBotUsername())) {
            if (games.containsKey(chatId))
                sendCaptains(games.get(chatId));
            else
                sendSimpleMessage("The game has not started", chatId, true);
            return;
        }

        if (chatId != user.getId() && (text.toLowerCase().equals("/lang") || text.toLowerCase().equals("/lang@" + getBotUsername()))) {
            sendSimpleMessage("Choose the language:", getKeyboard("rus", "eng", "ukr"), chatId);
            return;
        }

        if (text.length() > 10)
            if (text.toLowerCase().substring(0, 10).equals("/newgame @")) {

                Set<String> set = new HashSet<>(Arrays.asList(text.replace(" ", "").substring(text.indexOf("@")).split("@")));
                if (set.size() != 2) {
                    sendSimpleMessage("You need to choose two captains!", chatId, true);
                    return;
                }
                for (String cap : set) {
                    if (usersListMongo.findByUserName(cap) == null) {
                        sendSimpleMessage("User @" + cap + " is not registered. Please send me /start in private message", chatId, true);
                        return;
                    }
                }

                if (games.containsKey(chatId)) {
                    String lang = games.get(chatId).getLang();
                    boolean useKeyboard = games.get(chatId).isUseKeyboard();
                    games.put(chatId, new Game(chatId, lang, useKeyboard).setCaps(set).createSchema());
                } else {
                    games.put(chatId, new Game(chatId, "rus").setCaps(set).createSchema());
                }

                sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);

                if (games.get(chatId).getSchema().howMuchLeft(GameColor.RED) == 9)
                    sendSimpleMessage("Red team starts", chatId, false);
                else
                    sendSimpleMessage("Blue team starts", chatId, false);
                for (String cap : set)
                    sendPicture(games.get(chatId), Long.parseLong(usersListMongo.findByUserName(cap).getUserId()), false, true);
                return;
            }

        if (games.get(chatId).getSchema().checkWord(text.substring(1))) {

            int blackLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.BLACK);
            int redLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.RED);
            int blueLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.BLUE);

            if (blackLeft != 0 && redLeft != 0 && blueLeft != 0) {
                for (String cap : games.get(chatId).getCaps())
                    sendPicture(games.get(chatId), Long.parseLong(usersListMongo.findByUserName(cap).getUserId()), false, true);
                sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
            } else {
                games.get(chatId).getSchema().openCards();
                sendPicture(games.get(chatId), chatId, false, false);
                games.remove(chatId);
                if (redLeft == 0) {
                    sendSimpleMessage("Red team win!", chatId, true);
                } else if (blueLeft == 0) {
                    sendSimpleMessage("Blue team win!", chatId, true);
                } else
                    sendSimpleMessage("Black card opened! Game over!", chatId, true);
            }
        }
    }

    private ReplyKeyboardMarkup getKeyboard(long chatId) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard= new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (int i = 0; i < 5; i++) {
                if (games.get(chatId).getSchema().getArray()[i][j].isOpen())
                    keyboardRow.add(" ");
                else
                    keyboardRow.add(games.get(chatId).getSchema().getArray()[i][j].getWord());
            }
            keyboard.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getKeyboard(String firstArg, String... args) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard= new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(firstArg);
        for (String arg : args)
            keyboardRow.add(arg);

        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }



    private void sendSimpleMessage(String text, ReplyKeyboardMarkup keyboard, long chatId) {
        try {
            execute(new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(keyboard));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendSimpleMessage(String text, long chatId, boolean eraseKeyboard) {
        SendMessage message = new SendMessage().setChatId(chatId).setText(text);
        if (eraseKeyboard)
            message.setReplyMarkup(new ReplyKeyboardRemove());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendCaptains(Game game) {
        SendMessage message = new SendMessage();
        message.setChatId(game.getChatId());
        StringBuilder sb = new StringBuilder("Captains:");
        for (String cap : game.getCaps()) {
            sb.append(" @");
            sb.append(cap);
        }

        message.setText(sb.toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPicture(Game game, long chatId, boolean sendKeyboard, boolean isAdmin) {
        String filepath = getFilePath(game.getChatId(), isAdmin);
        new Drawer(game.getSchema(), filepath, isAdmin);
        try {
            File file = new File(filepath);
            SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(file));
            if (sendKeyboard) {
                photo.setReplyMarkup(getKeyboard(chatId));
            } else
                photo.setReplyMarkup(new ReplyKeyboardRemove());
            photo.setChatId(chatId);
            this.execute(photo);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(long chatId, boolean isAdmin) {
        return chatId + "_admin" + isAdmin + ".jpg";
    }

    @Override
    public String getBotUsername() {
        return "DevCodeNamesBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
