package com.bope;

import com.bope.model.Prompt;
import com.bope.model.duet.DuetDrawer;
import com.bope.model.original.OriginalDrawer;
import com.bope.model.abstr.Game;
import com.bope.model.GameColor;
import com.bope.model.duet.DuetGame;
import com.bope.model.original.OriginalGame;
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

    @Value("${TOKEN}") private String token;
    @Value("${BOT_USER_NAME}") private String botUsername;

    @Value("${GAME_NOT_STARTED}") private String GAME_NOT_STARTED;
    @Value("${BLUE_TEAM_STARTS}") private String BLUE_TEAM_STARTS;
    @Value("${RED_TEAM_STARTS}") private String RED_TEAM_STARTS;
    @Value("${START_INSTRUCTION}") private String START_INSTRUCTION;
    @Value("${CHOOSE_CAPTAINS}") private String CHOOSE_CAPTAINS;
    @Value("${USER_IS_NOT_REGISTERED}") private String USER_IS_NOT_REGISTERED;

    @Value("${KEYBOARD_USAGE}") private String KEYBOARD_USAGE;
    @Value("${ENABLED_KEYBOARD}") private String ENABLED_KEYBOARD;
    @Value("${DISABLED_KEYBOARD}") private String DISABLED_KEYBOARD;
    @Value("${ENABLE_BUTTON}") private String ENABLE_BUTTON;
    @Value("${DISABLE_BUTTON}") private String DISABLE_BUTTON;

    @Value("${CHOOSE_LANGUAGE}") private String CHOOSE_LANGUAGE;
    @Value("${SET_LANGUAGE}") private String SET_LANGUAGE;
    @Value("${LANG_ENG}") private String LANG_ENG;
    @Value("${LANG_RUS}") private String LANG_RUS;
    @Value("${LANG_UKR}") private String LANG_UKR;

    @Value("${BLACK_CARD_OPENED}") private String BLACK_CARD_OPENED;
    @Value("${RED_TEAM_WIN}") private String RED_TEAM_WIN;
    @Value("${BLUE_TEAM_WIN}") private String BLUE_TEAM_WIN;

    @Value("${KEYBOARD_COMMAND}") private String KEYBOARD_COMMAND;
    @Value("${BOARD_COMMAND}") private String BOARD_COMMAND;
    @Value("${LANG_COMMAND}") private String LANG_COMMAND;
    @Value("${START_COMMAND}") private String START_COMMAND;
    @Value("${CAPS_COMMAND}") private String CAPS_COMMAND;

    private final Map<Long, Game> games = new HashMap<>();

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

        if (text.equals(" ") || update.getMessage().getForwardFrom() != null)
            return;

        if (update.getMessage().getReplyToMessage() != null && chatId != user.getId()) {
            if ((text.equals(LANG_ENG) || text.equals(LANG_UKR) || text.equals(LANG_RUS))
                    && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                    && update.getMessage().getReplyToMessage().getText().equals(CHOOSE_LANGUAGE)
            ) {
                botLangCommand(chatId, text);
                return;
            }

            if ((text.equals(ENABLE_BUTTON) || text.equals(DISABLE_BUTTON)
                    && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                    && update.getMessage().getReplyToMessage().getText().equals(KEYBOARD_USAGE)
            )) {
                botKeyboardCommand(chatId, text);
                return;
            }
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(KEYBOARD_COMMAND) ||
                text.toLowerCase().equals(KEYBOARD_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            sendSimpleMessage(KEYBOARD_USAGE, getKeyboard(ENABLE_BUTTON, DISABLE_BUTTON), chatId);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(LANG_COMMAND) ||
                text.toLowerCase().equals(LANG_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            sendSimpleMessage(CHOOSE_LANGUAGE, getKeyboard(LANG_RUS, LANG_ENG, LANG_UKR), chatId);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(BOARD_COMMAND) ||
                text.toLowerCase().equals(BOARD_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            botBoardCommand(chatId);
            return;
        }

        if (text.toLowerCase().equals(START_COMMAND) ||
                text.toLowerCase().equals(START_COMMAND + "@" + getBotUsername().toLowerCase())
        ) {
            botStartCommand(chatId, user);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(CAPS_COMMAND) ||
                text.toLowerCase().equals(CAPS_COMMAND + "@" + getBotUsername().toLowerCase()))
        ) {
            botSendCommand(chatId);
            return;
        }


        if (chatId == user.getId() && text.length() > 7) {
            if (text.toLowerCase().startsWith("/duet @")) {

                String username = text.substring(text.indexOf('@') + 1);
                UserMongo userMongo = usersListMongo.findByUserName(username);
                if (userMongo == null) {
                    sendSimpleMessage(String.format(USER_IS_NOT_REGISTERED, username), chatId);
                    return;
                }
                botStartNewDuelGame(usersListMongo.findByUserName(user.getUserName()), userMongo);
                return;
            }
        }


        if (chatId == user.getId() && games.containsKey(chatId)) {
            if (games.get(chatId).getCaps().get(1).getUserName().equals(user.getUserName())) {

                Prompt prompt = getPrompt(text);
                if (prompt == null) {
                    sendSimpleMessage("Your prompt is incorrect!", chatId);
                    return;
                }

                DuetGame game = (DuetGame) games.get(chatId);
                if (game.getPrompt() == null) {
                    game.setPrompt(prompt);
                    sendSimpleMessage(
                            "@" + user.getUserName() + "'s prompt:" +
                                    "\nWord: " + prompt.getWord() +
                                    "\nCount: " + prompt.getNumber(),
                            game.getPartnerId(chatId));
                    sendSimpleMessage("Your prompt sent!", user.getId());
                    return;
                }
            }
        }


        if (chatId == user.getId() && games.containsKey(chatId)) {
            UserMongo currentUserMongo = games.get(chatId).getCaps().get(0);
            if (currentUserMongo.getUserName().equals(user.getUserName())) {
                botCheckWordDuet(currentUserMongo, text);
            }
            return;
        }





        if (text.length() > (START_COMMAND + " @").length())
            if (text.toLowerCase().substring(0, 8).equals(START_COMMAND + " @")) {

                Set<String> set = new HashSet<>(
                        Arrays.asList(text.replace(" ", "")
                                .substring(text.indexOf("@")).split("@"))
                );

                if (set.size() != 2) {
                    sendSimpleMessage(CHOOSE_CAPTAINS, chatId, true);
                    return;
                }

                Set<UserMongo> userSet = new HashSet<>();
                for (String cap : set) {
                    UserMongo userMongo = usersListMongo.findByUserName(cap);
                    if (userMongo == null) {
                        sendSimpleMessage(String.format(USER_IS_NOT_REGISTERED, cap), chatId, true);
                        return;
                    } else
                        userSet.add(userMongo);
                }

                botStartNewGame(chatId, userSet);
                return;
            }

        if (text.startsWith("/"))
            text = text.substring(1);
        if (games.get(chatId).getSchema().checkWord(text, true))
            botChooseWord(chatId);
    }

    private static Prompt getPrompt(String text) {
        Prompt prompt = null;
        try {
            prompt = new Prompt(
                    text.substring(0, text.indexOf(' ')),
                    Integer.parseInt(text.substring(text.indexOf(' ') + 1))
            );
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return prompt;
    }

    private void botCheckWordDuet(UserMongo userMongo, String text) {
        DuetGame game = (DuetGame) games.get(userMongo.getLongId());

        if (game.getPrompt() == null) {
            sendSimpleMessage("Wait for prompt!", userMongo.getLongId(), true);
            return;
        }

        if (game.getSchema().checkWord(text, game.getChatId() == userMongo.getLongId())) {
            if (game.getSchema().howMuchLeft(GameColor.BLACK) < 6) {
                game.getSchema().openCards(false);
                sendDuetPicture(game, game.getChatId(), true);
                sendDuetPicture(game, game.getChatId(), false);
                sendDuetPicture(game, game.getSecondPlayerId(), true);
                sendDuetPicture(game, game.getSecondPlayerId(), false);
                sendSimpleMessage("Black card opened!", game.getChatId(), true);
                sendSimpleMessage("Black card opened!", game.getSecondPlayerId(), true);
                game.getSchema().openCards(true);
            } else {
                sendDuetPicture(game, game.getChatId(), true);
                sendDuetPicture(game, game.getSecondPlayerId(), false);

                if (game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN) || game.getPrompt().isFinished()) {
                    game.swapCaptains();
                    game.setPrompt(null);
                    sendSimpleMessage("Now is your turn!\n Waiting for prompt!", game.getCaps().get(0).getLongId());
                    sendSimpleMessage("Now is @" + game.getCaps().get(0).getUserName() + "'s turn!\nPlease send a prompt!", game.getCaps().get(1).getLongId());
                } else {
                    sendSimpleMessage("Correct! " + game.getPrompt().getNumbersLeft() + " left!", game.getCaps().get(0).getLongId());
                    sendSimpleMessage("Correct! " + game.getPrompt().getNumbersLeft() + " left!", game.getCaps().get(1).getLongId());
                }
                game.refereshGreenLeft();
            }
        }
    }

    private void botLangCommand(long chatId, String text) {
        if (games.containsKey(chatId))
            games.get(chatId).setLang(text);
        else
            games.put(chatId, new OriginalGame(chatId, text,false));
        sendSimpleMessage(SET_LANGUAGE + " " + text, chatId);
    }

    private void botKeyboardCommand(long chatId, String text) {
        boolean isEnable = text.equals(ENABLE_BUTTON);
        if (isEnable)
            sendSimpleMessage(ENABLED_KEYBOARD, chatId);
        else
            sendSimpleMessage(DISABLED_KEYBOARD, chatId);

        if (games.containsKey(chatId)) {
            games.get(chatId).setUseKeyboard(isEnable);
            if (isEnable)
                sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
        }
        else
            games.put(chatId, new OriginalGame(chatId, LANG_RUS, isEnable));
    }

    private void botBoardCommand(long chatId) {
        if (games.containsKey(chatId))
            sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
        else
            sendSimpleMessage(GAME_NOT_STARTED, chatId, true);
    }

    private void botStartCommand(long chatId, User user) {
        if (chatId == user.getId() && usersListMongo.findByUserName(user.getUserName()) == null)
            usersListMongo.save(new UserMongo(user.getUserName(), String.valueOf(user.getId())));
        sendSimpleMessage(START_INSTRUCTION, chatId, false);
    }

    private void botSendCommand(long chatId) {
        if (games.containsKey(chatId))
            sendSimpleMessage(games.get(chatId).getCaptainsToString(), chatId, false);
        else
            sendSimpleMessage(GAME_NOT_STARTED, chatId, true);
    }

    private void botStartNewGame(long chatId, Set<UserMongo> captains) {
        if (games.containsKey(chatId))
            games.put(chatId, new OriginalGame(games.get(chatId)).setCaps(captains).createSchema());
        else
            games.put(chatId, new OriginalGame(chatId, LANG_RUS, false).setCaps(captains).createSchema());

        sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);

        if (games.get(chatId).getSchema().howMuchLeft(GameColor.RED) == 9)
            sendSimpleMessage(RED_TEAM_STARTS, chatId, false);
        else
            sendSimpleMessage(BLUE_TEAM_STARTS, chatId, false);

        for (UserMongo cap : captains)
            sendPicture(games.get(chatId), cap.getLongId(),false,true);
    }

    private void botStartNewDuelGame(UserMongo firstUser, UserMongo secondUser) {

        Game game;
        if (games.containsKey(firstUser.getLongId()))
            game = new DuetGame(games.get(firstUser.getLongId())).setSecondPlayerId(secondUser.getLongId()).createSchema();
        else
            game = new DuetGame(firstUser.getLongId(), LANG_RUS, false).setSecondPlayerId(secondUser.getLongId()).createSchema();

        games.put(firstUser.getLongId(), game);
        games.put(secondUser.getLongId(), game);

        sendDuetPicture(game, firstUser.getLongId(), true);
        sendDuetPicture(game, secondUser.getLongId(), false);

        if (game.getSchema().isRedFirst()) {
            game.setCaps(firstUser, secondUser);
            sendSimpleMessage("You start first!\n Waiting for prompt!", firstUser.getLongId(), false);
            sendSimpleMessage("Player @" + firstUser.getUserName() + " starts!\nPlease send a prompt!", secondUser.getLongId(), false);
        } else {
            game.setCaps(secondUser, firstUser);
            sendSimpleMessage("You start first!\n Waiting for prompt!", secondUser.getLongId(), false);
            sendSimpleMessage("Player @" + secondUser.getUserName() + " starts!\nPlease send a prompt!", firstUser.getLongId(), false);
        }
    }

    private void botChooseWord(long chatId) {
        int blackLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.BLACK);
        int redLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.RED);
        int blueLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.BLUE);

        if (blackLeft != 0 && redLeft != 0 && blueLeft != 0) {
            for (UserMongo cap : games.get(chatId).getCaps())
                sendPicture(games.get(chatId), cap.getLongId(),false,true);
            sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
        } else {
            games.get(chatId).getSchema().openCards(true);
            sendPicture(games.get(chatId), chatId, false, false);
            if (redLeft == 0) {
                sendSimpleMessage(RED_TEAM_WIN, chatId);
            } else if (blueLeft == 0) {
                sendSimpleMessage(BLUE_TEAM_WIN, chatId);
            } else
                sendSimpleMessage(BLACK_CARD_OPENED, chatId);
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

    private void sendSimpleMessage(String text, long chatId) {
        sendSimpleMessage(text, chatId, true);
    }

    private void sendPicture(Game game, long chatId, boolean sendKeyboard, boolean isAdmin) {
        String filepath = getFilePath(game.getChatId(), isAdmin);
        new OriginalDrawer(game.getSchema(), filepath, isAdmin);
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

    private void sendDuetPicture(Game game, long chatId, boolean isFirst) {
        String filepath = getFilePath(game.getChatId(), isFirst);
        new DuetDrawer(game.getSchema(), filepath, isFirst);
        try {
            File file = new File(filepath);
            SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(file)).setChatId(chatId);
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
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
