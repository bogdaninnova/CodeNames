package com.bope;

import com.bope.db.UserMongo;
import com.bope.db.UsersListMongo;
import com.bope.model.original.OriginalDrawer;
import com.bope.model.abstr.Game;
import com.bope.model.GameColor;
import com.bope.model.duet.DuetGame;
import com.bope.model.original.OriginalGame;
import com.bope.model.pictures.PicturesDrawer;
import com.bope.model.pictures.PicturesGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class CodeNamesBot extends TelegramLongPollingBot {

    @Autowired private UsersListMongo usersListMongo;
    @Autowired private CodeNamesDuet codeNamesDuet;

    protected final Map<Long, Game> games = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(CodeNamesBot.class);

    @Value("${TOKEN}") private String token;
    @Value("${BOT_USER_NAME}") private String botUsername;

    @Value("${GAME_NOT_STARTED}") private String GAME_NOT_STARTED;
    @Value("${BLUE_TEAM_STARTS}") private String BLUE_TEAM_STARTS;
    @Value("${RED_TEAM_STARTS}") private String RED_TEAM_STARTS;
    @Value("${START_INSTRUCTION}") private String START_INSTRUCTION;
    @Value("${CHOOSE_CAPTAINS}") private String CHOOSE_CAPTAINS;
    @Value("${USER_IS_NOT_REGISTERED}") private String USER_IS_NOT_REGISTERED;
    @Value("${USER_IS_BLOCKED}") private String USER_IS_BLOCKED;

    @Value("${KEYBOARD_USAGE}") private String KEYBOARD_USAGE;
    @Value("${ENABLED_KEYBOARD}") private String ENABLED_KEYBOARD;
    @Value("${DISABLED_KEYBOARD}") private String DISABLED_KEYBOARD;
    @Value("${ENABLE_BUTTON}") private String ENABLE_BUTTON;
    @Value("${DISABLE_BUTTON}") private String DISABLE_BUTTON;

    @Value("${CHOOSE_LANGUAGE}") private String CHOOSE_LANGUAGE;
    @Value("${SET_LANGUAGE}") private String SET_LANGUAGE;
    @Value("${LANG_ENG}") private String LANG_ENG;
    @Value("${LANG_RUS}") protected String LANG_RUS;
    @Value("${LANG_RUS2}") private String LANG_RUS2;
    @Value("${LANG_UKR}") private String LANG_UKR;
    @Value("${LANG_PICTURES}") private String LANG_PICTURES;

    @Value("${BLACK_CARD_OPENED}") protected String BLACK_CARD_OPENED;
    @Value("${RED_TEAM_WIN}") private String RED_TEAM_WIN;
    @Value("${BLUE_TEAM_WIN}") private String BLUE_TEAM_WIN;

    @Value("${KEYBOARD_COMMAND}") private String KEYBOARD_COMMAND;
    @Value("${BOARD_COMMAND}") private String BOARD_COMMAND;
    @Value("${LANG_COMMAND}") private String LANG_COMMAND;
    @Value("${START_COMMAND}") private String START_COMMAND;
    @Value("${CAPS_COMMAND}") private String CAPS_COMMAND;

    @Value("${DUET_YOU_NEED_CHOOSE_ONE}") private String DUET_YOU_NEED_CHOOSE_ONE;
    @Value("${DUET_PASS_WORD}") private String DUET_PASS_WORD;
    @Value("${DUET_PASS_WORD_RUS}") private String DUET_PASS_WORD_RUS;
    @Value("${DUET_COMMAND}") private String DUET_COMMAND;

    @Override
    public void onUpdateReceived(Update update) {

        String text = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        long chatId = update.getMessage().getChatId();

        LOG.info("Sent text = " + text);
        LOG.info("User = " + user);
        LOG.info("ChatId = " + chatId);

        if (text.equals(" ") || update.getMessage().getForwardFrom() != null) {
            LOG.info("Empty message");
            return;
        }

        if (update.getMessage().getReplyToMessage() != null && chatId != user.getId()) {
            LOG.info("Keyboard reply received");
            if ((text.equals(LANG_ENG) || text.equals(LANG_UKR) || text.equals(LANG_RUS) || text.equals(LANG_RUS2) || text.equals(LANG_PICTURES))
                    && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                    && update.getMessage().getReplyToMessage().getText().equals(CHOOSE_LANGUAGE)
            ) {
                LOG.info("Language keyboard reply: " + text);
                botLangCommand(chatId, text);
                return;
            }

            if ((text.equals(ENABLE_BUTTON) || text.equals(DISABLE_BUTTON)
                    && update.getMessage().getReplyToMessage().getFrom().getUserName().equals(getBotUsername())
                    && update.getMessage().getReplyToMessage().getText().equals(KEYBOARD_USAGE)
            )) {
                LOG.info("Keyboard usage reply: " + text);
                botKeyboardCommand(chatId, text);
                return;
            }
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(KEYBOARD_COMMAND) ||
                text.toLowerCase().equals(KEYBOARD_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            LOG.info("Keyboard usage check");
            sendSimpleMessage(KEYBOARD_USAGE, getKeyboard(new String[]{ENABLE_BUTTON, DISABLE_BUTTON}), chatId);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(LANG_COMMAND) ||
                text.toLowerCase().equals(LANG_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            LOG.info("Keyboard language check");
            sendSimpleMessage(CHOOSE_LANGUAGE, getKeyboard(new String[]{LANG_RUS, LANG_ENG, LANG_UKR}, new String[]{LANG_PICTURES, LANG_RUS2}), chatId);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(BOARD_COMMAND) ||
                text.toLowerCase().equals(BOARD_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            LOG.info("Send board to chat");
            botBoardCommand(chatId);
            return;
        }

        if (text.toLowerCase().equals(START_COMMAND) ||
                text.toLowerCase().equals(START_COMMAND + "@" + getBotUsername().toLowerCase())
        ) {
            LOG.info("Original game start");
            botStartCommand(chatId, user);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(CAPS_COMMAND) ||
                text.toLowerCase().equals(CAPS_COMMAND + "@" + getBotUsername().toLowerCase()))
        ) {
            LOG.info("Send captains to chat");
            botSendCaptainsCommand(chatId);
            return;
        }

        if (chatId == user.getId() && text.length() > 7) {
            if (text.toLowerCase().startsWith(DUET_COMMAND + " @")) {
                LOG.info("Duet game starting");
                String username = text.substring(text.indexOf('@') + 1);
                UserMongo userMongo = usersListMongo.findByUserName(username);
                if (userMongo == null) {
                    LOG.info("User is not registered: " + username);
                    sendSimpleMessage(String.format(USER_IS_NOT_REGISTERED, username), chatId);
                    return;
                }
                codeNamesDuet.botStartNewGameDuet(usersListMongo.findByUserName(user.getUserName()), userMongo);
                return;
            }
        }

        if (chatId == user.getId() && games.containsKey(chatId)) {
            if (codeNamesDuet.sendPromptDuet((DuetGame) games.get(chatId), user, text)) {
                LOG.info("Try to send prompt in duet game: " + text);
                return;
            }
        }

        if (chatId == user.getId() && games.containsKey(chatId) &&
                (text.toLowerCase().equals(DUET_PASS_WORD) || text.toLowerCase().equals(DUET_PASS_WORD_RUS))) {
            LOG.info("Duet game - try to pass the turn");
            DuetGame game = (DuetGame) games.get(chatId);
            if (game.getCaps().get(0).getUserName().equals(user.getUserName())) {
                if (game.getPrompt().getNumbersLeft() != game.getPrompt().getNumber())
                    codeNamesDuet.switchTurnDuet(game);
                else {
                    LOG.info("Duet game - player should choose at least one card");
                    sendSimpleMessage(DUET_YOU_NEED_CHOOSE_ONE, chatId);
                }
            }
            return;
        }

        if (chatId == user.getId() && games.containsKey(chatId)) {
            LOG.info("Duet game - start checking word");
            DuetGame game = (DuetGame) games.get(chatId);
            UserMongo currentUserMongo = game.getCaps().get(0);
            if (game.getSchema().howMuchLeft(GameColor.GREEN, game.getChatId() != user.getId()) > 0) {
                LOG.info("Duet game - word checked");
                if (currentUserMongo.getUserName().equals(user.getUserName())) {
                    codeNamesDuet.botCheckWordDuet(currentUserMongo, text);
                } else if (game.getTurnsLeft() == 0) {
                    codeNamesDuet.botCheckWordDuet(game.getCaps().get(1), text);
                }
            }
            return;
        }

        if (text.length() > (START_COMMAND + " @").length()) {
            if (text.toLowerCase().substring(0, 8).equals(START_COMMAND + " @")) {
                LOG.info("Original game starting");
                Set<String> set = new HashSet<>(
                        Arrays.asList(text.replace(" ", "")
                                .substring(text.indexOf("@")).split("@"))
                );

                if (set.size() != 2) {
                    LOG.info("Original game starting - wrong captains amount");
                    sendSimpleMessage(CHOOSE_CAPTAINS, chatId, true);
                    return;
                }

                ArrayList<UserMongo> userList = new ArrayList<>();
                for (String cap : set) {
                    if (cap.equals("me"))
                        cap = user.getUserName();
                    UserMongo userMongo = usersListMongo.findByUserName(cap);
                    if (userMongo == null) {
                        LOG.info("Original game starting - user is not registered: " + cap);
                        sendSimpleMessage(String.format(USER_IS_NOT_REGISTERED, cap), chatId, true);
                        return;
                    } else
                        userList.add(userMongo);
                }
                botStartNewGame(chatId, userList, games.containsKey(chatId) && games.get(chatId).getLang().equals(LANG_PICTURES));
                return;
            }
        }

        if (chatId != user.getId() && games.containsKey(chatId)) {
            if (text.startsWith("/")) {
                LOG.info("Added / to text: " + text);
                text = text.substring(1);
            }
            if (games.get(chatId).getSchema().checkWord(text, true))
                botChooseWord(chatId);
        }
    }


    private void botLangCommand(long chatId, String text) {
        LOG.info("Choose language command");
        if (games.containsKey(chatId))
            games.get(chatId).setLang(text);
        else {
            Game game = new OriginalGame(chatId, text, false);
            game.getSchema().setDefaultCards();
            games.put(chatId, game);
        }
        sendSimpleMessage(SET_LANGUAGE + " " + text, chatId);
    }

    private void botKeyboardCommand(long chatId, String text) {
        LOG.info("Choose keyboard usage command");
        boolean isEnable = text.equals(ENABLE_BUTTON);
        if (isEnable)
            sendSimpleMessage(ENABLED_KEYBOARD, chatId);
        else
            sendSimpleMessage(DISABLED_KEYBOARD, chatId);

        Game game;
        if (games.containsKey(chatId) && games.get(chatId) != null) {
            game = games.get(chatId);
            game.setUseKeyboard(isEnable);
            if (isEnable && game.getSchema() != null) {
                if (game.getSchema().howMuchLeft(GameColor.BLACK) != 0) {
                    try {
                        sendPicture(game, game.isUseKeyboard(), false, chatId);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            game = new OriginalGame(chatId, LANG_RUS, isEnable);
            game.getSchema().setDefaultCards();
            games.put(chatId, game);
        }
    }

    private void botBoardCommand(long chatId) {
        LOG.info("Bot board command");
        if (games.containsKey(chatId) && games.get(chatId) != null) {
            Game game = games.get(chatId);
            if (game.getSchema().howMuchLeft(GameColor.BLACK) != 0) {
                try {
                    sendPicture(game, game.isUseKeyboard(), false, chatId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                sendSimpleMessage(GAME_NOT_STARTED, chatId, true);
            }
        } else
            sendSimpleMessage(GAME_NOT_STARTED, chatId, true);
    }

    private void botStartCommand(long chatId, User user) {
        LOG.info("Bot start command");
        if (chatId == user.getId() && usersListMongo.findByUserName(user.getUserName()) == null) {
            LOG.info("New player registered: " + user.getUserName());
            usersListMongo.save(new UserMongo(user.getUserName(), String.valueOf(user.getId())));
        }
        sendSimpleMessage(START_INSTRUCTION, chatId, false);
    }

    private void botSendCaptainsCommand(long chatId) {
        LOG.info("Bot send captains command");
        if (games.containsKey(chatId))
            sendSimpleMessage(games.get(chatId).getCaptainsToString(), chatId, false);
        else
            sendSimpleMessage(GAME_NOT_STARTED, chatId, true);
    }

    private void botStartNewGame(long chatId, ArrayList<UserMongo> captains, boolean isPicturesGame) {
        Game game;
        if (isPicturesGame) {
            if (games.containsKey(chatId) && games.get(chatId) != null && games.get(chatId) instanceof PicturesGame)
                game = new PicturesGame(games.get(chatId)).setCaps(captains).createSchema();
            else
                game = new PicturesGame(chatId).setCaps(captains).createSchema();
        } else {
            if (games.containsKey(chatId) && games.get(chatId) != null && games.get(chatId) instanceof OriginalGame)
                game = new OriginalGame(games.get(chatId)).setCaps(captains).createSchema();
            else
                game = new OriginalGame(chatId, LANG_RUS, false).setCaps(captains).createSchema();
        }
        games.put(game.getChatId(), game);
        sendPicturesToAll(game, (game.getSchema().howMuchLeft(GameColor.RED) == 9) ? RED_TEAM_STARTS : BLUE_TEAM_STARTS);
    }

    private void sendPicturesToAll(Game game, String capture) {
            LOG.info("Original game update boards");
            UserMongo blockedUser = null;
            try {
                sendPicture(game, false,true, game.getCaps().get(0).getLongId());
            } catch (TelegramApiException e) {
                blockedUser = game.getCaps().get(0);
                e.printStackTrace();
            }
            if (blockedUser == null) {
                try {
                    sendPicture(game, false,true, game.getCaps().get(1).getLongId());
                } catch (TelegramApiException e) {
                    blockedUser = game.getCaps().get(1);
                    e.printStackTrace();
                }
            }
            try {
                if (blockedUser == null) {
                    sendPicture(game, capture, game.isUseKeyboard(), false, game.getChatId());
                } else {
                    if (capture.equals(""))
                        sendPicture(game, String.format(USER_IS_BLOCKED, blockedUser.getUserName()), game.isUseKeyboard(), true, game.getChatId());
                    else
                        sendSimpleMessage(String.format(USER_IS_NOT_REGISTERED, blockedUser.getUserName()), game.getChatId());
                    games.remove(game.getChatId());
                    usersListMongo.removeByUserName(blockedUser.getUserName());
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }

    private void botChooseWord(long chatId) {
        LOG.info("Original game -- word chosen");
        Game game = games.get(chatId);
        int blackLeft = game.getSchema().howMuchLeft(GameColor.BLACK);
        int redLeft = game.getSchema().howMuchLeft(GameColor.RED);
        int blueLeft = game.getSchema().howMuchLeft(GameColor.BLUE);

        if (blackLeft != 0 && redLeft != 0 && blueLeft != 0) {
            sendPicturesToAll(game, "");
        } else {
            LOG.info("Original game finished -- update boards");
            String capture;
            if (redLeft == 0) {
                capture = RED_TEAM_WIN;
            } else if (blueLeft == 0) {
                capture = BLUE_TEAM_WIN;
            } else
                capture = BLACK_CARD_OPENED;

            try {
                sendPicture(game, capture, false, true, chatId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private ReplyKeyboardMarkup getGameKeyboard(long chatId) {
        LOG.info("Game Keyboard build");
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

    private ReplyKeyboardMarkup getKeyboard(String[]... args) {
        LOG.info("Option Keyboard build");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(false);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard= new ArrayList<>();

        for (String[] arg : args) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String button : arg)
                keyboardRow.add(button);
            keyboard.add(keyboardRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private void sendSimpleMessage(String text, ReplyKeyboardMarkup keyboard, long chatId) {
        LOG.info("Message sending");
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(text);
            sendMessage.setReplyMarkup(keyboard);
            execute(sendMessage);
            LOG.info("Message sent");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            LOG.error("Error occurred while message sending");
        }
    }

    private void sendSimpleMessage(String text, long chatId, boolean eraseKeyboard) {
        LOG.info("Message sending. keyboard erase = " + eraseKeyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        if (eraseKeyboard)
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        try {
            execute(sendMessage);
            LOG.info("Message sent");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            LOG.error("Error occurred while message sending");
        }
    }

    protected void sendSimpleMessage(String text, long chatId) {
        sendSimpleMessage(text, chatId, true);
    }

    private void sendPicture(Game game, boolean sendKeyboard, boolean isAdmin, long chatId) throws TelegramApiException {
        sendPicture(game, "", sendKeyboard, isAdmin, chatId);
    }

    private void sendPicture(Game game, String caption, boolean sendKeyboard, boolean isAdmin, long chatId) throws TelegramApiException {
        LOG.info("Picture sending");
        String filepath = getFilePath(game.getChatId(), isAdmin);
        if (game instanceof OriginalGame)
            new OriginalDrawer(game, filepath, isAdmin);
        else if (game instanceof PicturesGame)
            new PicturesDrawer((PicturesGame) game, filepath, isAdmin);
        SendPhoto photo = new SendPhoto();
        photo.setPhoto(new InputFile(filepath));
        photo.setChatId(String.valueOf(chatId));
        if (sendKeyboard)
            photo.setReplyMarkup(getGameKeyboard(chatId));
        else
            photo.setReplyMarkup(new ReplyKeyboardRemove());

        if (!caption.equals(""))
            photo.setCaption(caption);

        execute(photo);
        LOG.info("Picture sent");
    }



    protected static String getFilePath(long chatId, boolean isAdmin) {
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
