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
    private Map<Long, OriginalGame> games = new HashMap<>();

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

        if (update.getMessage().getReplyToMessage() != null) {
            if ((text.equals("eng") || text.equals("ukr") || text.equals("rus"))
                    && chatId != user.getId()
                    && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                    && update.getMessage().getReplyToMessage().getText().equals("Choose the language:")
            ) {
                if (games.containsKey(chatId))
                    games.get(chatId).setLang(text);
                else
                    games.put(chatId, new OriginalGame(chatId, text,false));
                sendSimpleMessage("Set language: " + text, chatId, true);
                return;
            }

            if ((text.equals("Enable") || text.equals("Disable"))
                    && chatId != user.getId()
                    && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                    && update.getMessage().getReplyToMessage().getText().equals("Keyboard usage:")
            ) {
                sendSimpleMessage("Keyboard " + text + "d!", chatId, true);
                boolean isEnable = text.equals("Enable");
                if (games.containsKey(chatId)) {
                    games.get(chatId).setUseKeyboard(isEnable);
                    if (isEnable)
                        sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
                }
                else
                    games.put(chatId, new OriginalGame(chatId, "rus", isEnable));
                return;
            }
        }

        if (!text.substring(0, 1).equals("/"))
            text = "/" + text;

        if (chatId != user.getId() && (text.toLowerCase().equals("/keyboard") || text.toLowerCase().equals("/keyboard@" + getBotUsername().toLowerCase()))) {
            sendSimpleMessage("Keyboard usage:", getKeyboard("Enable", "Disable"), chatId);
            return;
        }

        if (chatId != user.getId() && (text.toLowerCase().equals("/lang") || text.toLowerCase().equals("/lang@" + getBotUsername().toLowerCase()))) {
            sendSimpleMessage("Choose the language:", getKeyboard("rus", "eng", "ukr"), chatId);
            return;
        }

        if (chatId != user.getId() && (text.toLowerCase().equals("/board") || text.toLowerCase().equals("/board@" + getBotUsername().toLowerCase()))) {
            if (games.containsKey(chatId))
                sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
            else
                sendSimpleMessage("The game has not started", chatId, true);
            return;
        }

        if (text.toLowerCase().equals("/start") || text.toLowerCase().equals("/start@" + getBotUsername().toLowerCase())) {
            if (chatId == user.getId() && usersListMongo.findByUserName(user.getUserName()) == null)
                usersListMongo.save(new UserMongo(user.getUserName(), String.valueOf(user.getId())));
            sendSimpleMessage("For new game please type in chat next command:\n/start @captain1 @captain2", chatId, false);
            return;
        }

        if (chatId != user.getId() && (text.toLowerCase().equals("/caps") || text.toLowerCase().equals("/caps@" + getBotUsername().toLowerCase()))) {
            if (games.containsKey(chatId))
                sendSimpleMessage(games.get(chatId).getCaptainsToString(), chatId, false);
            else
                sendSimpleMessage("The game has not started", chatId, true);
            return;
        }

        if (text.length() > 8)
            if (text.toLowerCase().substring(0, 8).equals("/start @")) {

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

                if (games.containsKey(chatId))
                    games.put(chatId, new OriginalGame(games.get(chatId)).setCaps(set).createSchema());
                else
                    games.put(chatId, new OriginalGame(chatId, "rus", false).setCaps(set).createSchema());

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
                if (redLeft == 0) {
                    sendSimpleMessage("Red team win!", chatId, true);
                } else if (blueLeft == 0) {
                    sendSimpleMessage("Blue team win!", chatId, true);
                } else
                    sendSimpleMessage("Black card opened! Game over!", chatId, true);
            }
        }
    }

    private ReplyKeyboardMarkup getGameKeyboard(long chatId) {
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

    private ReplyKeyboardMarkup getKeyboard(String... args) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard= new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

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

    private void sendPicture(OriginalGame originalGame, long chatId, boolean sendKeyboard, boolean isAdmin) {
        String filepath = getFilePath(originalGame.getChatId(), isAdmin);
        new Drawer(originalGame.getSchema(), filepath, isAdmin);
        try {
            File file = new File(filepath);
            SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(file)).setChatId(chatId);
            if (sendKeyboard)
                photo.setReplyMarkup(getGameKeyboard(chatId));
            else
                photo.setReplyMarkup(new ReplyKeyboardRemove());
            execute(photo);
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
