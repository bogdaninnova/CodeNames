package com.bope;

import com.bope.model.Card;
import com.bope.model.Prompt;
import com.bope.model.duet.DuetDrawer;
import com.bope.model.original.OriginalDrawer;
import com.bope.model.abstr.Game;
import com.bope.model.GameColor;
import com.bope.model.duet.DuetGame;
import com.bope.model.original.OriginalGame;
import com.bope.model.original.OriginalSchema;
import com.bope.model.pictures.PicturesDrawer;
import com.bope.model.pictures.PicturesGame;
import com.bope.model.pictures.PicturesSchema;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Component
public class CodeNamesBot extends TelegramLongPollingBot {

    @Autowired
    private UsersListMongo usersListMongo;

    private static final Logger LOG = LoggerFactory.getLogger(CodeNamesBot.class);

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
    @Value("${LANG_PICTURES}") private String LANG_PICTURES;

    @Value("${BLACK_CARD_OPENED}") private String BLACK_CARD_OPENED;
    @Value("${RED_TEAM_WIN}") private String RED_TEAM_WIN;
    @Value("${BLUE_TEAM_WIN}") private String BLUE_TEAM_WIN;

    @Value("${KEYBOARD_COMMAND}") private String KEYBOARD_COMMAND;
    @Value("${BOARD_COMMAND}") private String BOARD_COMMAND;
    @Value("${LANG_COMMAND}") private String LANG_COMMAND;
    @Value("${START_COMMAND}") private String START_COMMAND;
    @Value("${CAPS_COMMAND}") private String CAPS_COMMAND;

    @Value("${DUET_YOUR_TURN}") private String DUET_YOUR_TURN;
    @Value("${DUET_PLAYERS_TURN}") private String DUET_PLAYERS_TURN;
    @Value("${DUET_LAST_TURN}") private String DUET_LAST_TURN;
    @Value("${DUET_CORRECT}") private String DUET_CORRECT;
    @Value("${DUET_WAIT_FOR_PROMPT}") private String DUET_WAIT_FOR_PROMPT;

    @Value("${DUET_GAME_OVER}") private String DUET_GAME_OVER;
    @Value("${DUET_YOU_WON}") private String DUET_YOU_WON;
    @Value("${DUET_YOU_FINISHED}") private String DUET_YOU_FINISHED;
    @Value("${DUET_PLAYER_FINISHED}") private String DUET_PLAYER_FINISHED;

    @Value("${DUET_YOU_NEED_CHOOSE_ONE}") private String DUET_YOU_NEED_CHOOSE_ONE;
    @Value("${DUET_PASS_WORD}") private String DUET_PASS_WORD;
    @Value("${DUET_PASS_WORD_RUS}") private String DUET_PASS_WORD_RUS;
    @Value("${DUET_COMMAND}") private String DUET_COMMAND;
    @Value("${DUET_PROMPT_SENT}") private String DUET_PROMPT_SENT;
    @Value("${DUET_INCORRECT_PROMPT}") private String DUET_INCORRECT_PROMPT;
    @Value("${DUET_PLAYERS_PROMPT}") private String DUET_PLAYERS_PROMPT;

    private final Map<Long, Game> games = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String text = update.getCallbackQuery().getData();
            if (!text.equals(" ") && games.containsKey(chatId))
                checkWordPicture((PicturesGame) games.get(chatId), text, update.getCallbackQuery().getFrom().getUserName());
            return;
        }

        String text = update.getMessage().getText();
        User user = update.getMessage().getFrom();
        long chatId = update.getMessage().getChatId();

        LOG.info("Sent text = " + text);
        LOG.info("User = " + user);
        LOG.info("ChatId = " + chatId);

//        if (user.getId() != 119970632)
//            return;

        if (text.equals(" ") || update.getMessage().getForwardFrom() != null) {
            LOG.info("Empty message");
            return;
        }

        if (update.getMessage().getReplyToMessage() != null && chatId != user.getId()) {
            LOG.info("Keyboard reply received");
            if ((text.equals(LANG_ENG) || text.equals(LANG_UKR) || text.equals(LANG_RUS) || text.equals(LANG_PICTURES))
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
            sendSimpleMessage(KEYBOARD_USAGE, getKeyboard(ENABLE_BUTTON, DISABLE_BUTTON), chatId);
            return;
        }

        if (chatId != user.getId() && (
                text.toLowerCase().equals(LANG_COMMAND) ||
                text.toLowerCase().equals(LANG_COMMAND + "@" + getBotUsername().toLowerCase())
        )) {
            LOG.info("Keyboard language check");
            sendSimpleMessage(CHOOSE_LANGUAGE, getKeyboard(LANG_RUS, LANG_ENG, LANG_UKR, LANG_PICTURES), chatId);
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
                botStartNewGameDuet(usersListMongo.findByUserName(user.getUserName()), userMongo);
                return;
            }
        }

        if (chatId == user.getId() && games.containsKey(chatId)) {
            if (sendPromptDuet((DuetGame) games.get(chatId), user, text)) {
                LOG.info("Try to send prompt in duet game: " + text);
                return;
            }
        }

        if (chatId == user.getId() && games.containsKey(chatId) && (text.toLowerCase().equals(DUET_PASS_WORD) || text.toLowerCase().equals(DUET_PASS_WORD_RUS))) {
            LOG.info("Duet game - try to pass the turn");
            DuetGame game = (DuetGame) games.get(chatId);
            if (game.getCaps().get(0).getUserName().equals(user.getUserName())) {
                if (game.getPrompt().getNumbersLeft() != game.getPrompt().getNumber())
                    switchTurnDuet(game);
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
                    botCheckWordDuet(currentUserMongo, text);
                } else if (game.getTurnsLeft() == 0) {
                    botCheckWordDuet(game.getCaps().get(1), text);
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
                    UserMongo userMongo = usersListMongo.findByUserName(cap);
                    if (userMongo == null) {
                        LOG.info("Original game starting - user is not registered: " + cap);
                        sendSimpleMessage(String.format(USER_IS_NOT_REGISTERED, cap), chatId, true);
                        return;
                    } else
                        userList.add(userMongo);
                }

                boolean isPicturesStarted = false;
                if (games.containsKey(chatId)) {
                    if (games.get(chatId).getLang().equals(LANG_PICTURES)) {
                        isPicturesStarted = true;
                        botStartNewGamePictures(chatId, userList);
                    }
                }

                if (!isPicturesStarted)
                    botStartNewGame(chatId, userList);
                return;
            }
        }


        if (chatId != user.getId() && games.containsKey(chatId)) {
            Game game = games.get(chatId);
            if (game instanceof OriginalGame) {
                if (text.startsWith("/")) {
                    LOG.info("Added / to text: " + text);
                    text = text.substring(1);
                }
                if (game.getSchema().checkWord(text, true))
                    botChooseWord(chatId);
            } else if (game instanceof PicturesGame) {
                checkWordPicture((PicturesGame) game, text);
            }
        }


    }

    private static Prompt getPromptDuet(String text) {
        Prompt prompt = null;
        try {
            LOG.info("Duet game prompt parsing: " + text);
            prompt = new Prompt(text.substring(0, text.indexOf(' ')), Integer.parseInt(text.substring(text.indexOf(' ') + 1)));
        } catch (Exception e) {
            LOG.warn("Duet game prompt parse error!");
        }
        return prompt;
    }

    private void botCheckWordDuet(UserMongo userMongo, String text) {
        LOG.info("Duet game starting word checking: " + text);
        DuetGame game = (DuetGame) games.get(userMongo.getLongId());

        if (game.getPrompt() == null) {
            LOG.info("Duet game prompt is not sent yet");
            sendSimpleMessage(DUET_WAIT_FOR_PROMPT, userMongo.getLongId());
            return;
        }

        if (game.getSchema().checkWord(text, game.getChatId() == userMongo.getLongId())) {
            LOG.info("Duet game word checked");
            if (game.getSchema().howMuchLeft(GameColor.BLACK) < 6) {
                LOG.info("Duet game black card opened");
                finishGameDuet(game, BLACK_CARD_OPENED);
            } else if (game.getTurnsLeft() == 0 && game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN)) {
                LOG.info("Duet game - game over");
                finishGameDuet(game, DUET_GAME_OVER);
            } else if (game.getSchema().howMuchLeft(GameColor.GREEN) == 0) {
                LOG.info("Duet game - win");
                finishGameDuet(game, DUET_YOU_WON);
            } else {
                LOG.info("Duet game - word checked -- pictures sending");
                sendDuetPicture(game, game.getChatId(), true);
                sendDuetPicture(game, game.getSecondPlayerId(), false);

                if (game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN) || game.getPrompt().isFinished()) {
                    LOG.info("Duet game - incorrect word, switch turn");
                    switchTurnDuet(game);
                } else {
                    LOG.info("Duet game - correct word");
                    sendSimpleMessage(DUET_CORRECT, game.getCaps().get(0).getLongId());
                    sendSimpleMessage(DUET_CORRECT, game.getCaps().get(1).getLongId());

                    if (game.getSchema().howMuchLeft(GameColor.GREEN, game.getSecondPlayerId() == userMongo.getLongId()) == 0) {
                        LOG.info("Duet game - player finished his words!");
                        sendSimpleMessage(DUET_YOU_FINISHED, game.getCaps().get(0).getLongId());
                        sendSimpleMessage(String.format(DUET_PLAYER_FINISHED, userMongo.getUserName()), game.getCaps().get(1).getLongId());
                        game.swapCaptains();
                        switchTurnDuet(game);
                    }
                }
                game.refereshGreenLeft();
            }
        }
    }

    private boolean sendPromptDuet(DuetGame game, User user, String text) {
        LOG.info("Duet game - prompt sending");
        if (game.getCaps().get(1).getUserName().equals(user.getUserName()) && game.getPrompt() == null) {
            LOG.info("Duet game - prompt checking");
            Prompt prompt = getPromptDuet(text);
            if (prompt == null) {
                LOG.info("Duet game - prompt incorrect");
                sendSimpleMessage(DUET_INCORRECT_PROMPT, user.getId());
                return true;
            }

            if (game.getPrompt() == null) {
                LOG.info("Duet game - prompt sent");
                game.setPrompt(prompt);
                sendSimpleMessage(String.format(DUET_PLAYERS_PROMPT, user.getUserName(), prompt.getWord(), prompt.getNumber()), game.getPartnerId(user.getId()));
                sendSimpleMessage(DUET_PROMPT_SENT, user.getId());
            }
            return true;
        }
        return false;
    }

    private void checkWordPicture(PicturesGame game, String text, String userName) {

        if (text.startsWith("/"))
            text = text.substring(1);

        if (game.getSchema().checkWord(text, true)) {
            if (userName != null)
                sendSimpleMessage("User @" + userName + " opened card!", game.getChatId());
            int blackLeft = game.getSchema().howMuchLeft(GameColor.BLACK);
            int redLeft = game.getSchema().howMuchLeft(GameColor.RED);
            int blueLeft = game.getSchema().howMuchLeft(GameColor.BLUE);

            if (blackLeft != 0 && redLeft != 0 && blueLeft != 0) {
                sendPicturePicGame(game, true, game.getCaps().get(0).getLongId(), game.getCaps().get(1).getLongId());
                sendPicturePicGame(game, false, game.getChatId());
            } else {
                game.getSchema().openCards(true);
                sendPicturePicGame(game, true, game.getChatId());
                if (redLeft == 0) {
                    sendSimpleMessage(RED_TEAM_WIN, game.getChatId());
                } else if (blueLeft == 0) {
                    sendSimpleMessage(BLUE_TEAM_WIN, game.getChatId());
                } else
                    sendSimpleMessage(BLACK_CARD_OPENED, game.getChatId());
            }
        }
    }

    private void checkWordPicture(PicturesGame game, String text) {
        checkWordPicture(game, text, null);
    }

    private void finishGameDuet(DuetGame game, String text) {
        LOG.info("Duet game - finishing");
        game.getSchema().openCards(false);
        sendDuetPicture(game, game.getChatId(), true);
        sendDuetPicture(game, game.getChatId(), false);
        sendDuetPicture(game, game.getSecondPlayerId(), true);
        sendDuetPicture(game, game.getSecondPlayerId(), false);
        sendSimpleMessage(text, game.getCaps().get(0).getLongId());
        sendSimpleMessage(text, game.getCaps().get(1).getLongId());
        game.getSchema().openCards(true);
    }

    private void switchTurnDuet(DuetGame game) {
        LOG.info("Duet game - turn switching");
        game.minusTurnsLeft();
        if (game.getTurnsLeft() == 0) {
            LOG.info("Duet game - turn switching: last turn");
            sendSimpleMessage(DUET_LAST_TURN, game.getCaps().get(0).getLongId());
            sendSimpleMessage(DUET_LAST_TURN, game.getCaps().get(1).getLongId());
        } else {
            if (game.getSchema().howMuchLeft(GameColor.GREEN, true) != 0 && game.getSchema().howMuchLeft(GameColor.GREEN, false) != 0)
                game.swapCaptains();
            game.setPrompt(null);
            sendSimpleMessage(DUET_YOUR_TURN, game.getCaps().get(0).getLongId());
            sendSimpleMessage(String.format(DUET_PLAYERS_TURN, game.getCaps().get(0).getUserName()), game.getCaps().get(1).getLongId());
        }
    }

    private void botLangCommand(long chatId, String text) {
        LOG.info("Choose language command");
        if (games.containsKey(chatId))
            games.get(chatId).setLang(text);
        else
            games.put(chatId, new OriginalGame(chatId, text,false));
        sendSimpleMessage(SET_LANGUAGE + " " + text, chatId);
    }

    private void botKeyboardCommand(long chatId, String text) {
        LOG.info("Choose keyboard usage command");
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
        LOG.info("Bot board command");
        if (games.containsKey(chatId))
            sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
        else
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

    private void botStartNewGamePictures(long chatId, ArrayList<UserMongo> captains) {

        boolean isCreated = false;
        if (games.containsKey(chatId)) {
            Game game = games.get(chatId);
            if (game instanceof PicturesGame) {
                isCreated = true;
                games.put(chatId, new PicturesGame(game).setCaps(captains).createSchema());
            }
        }
        if (!isCreated)
            games.put(chatId, new PicturesGame(chatId).setCaps(captains).createSchema());

        sendPicturePicGame((PicturesGame) games.get(chatId), false, chatId);
        sendPicturePicGame((PicturesGame) games.get(chatId), true, captains.get(0).getLongId(), captains.get(1).getLongId());
    }

    private void botStartNewGame(long chatId, ArrayList<UserMongo> captains) {
        LOG.info("Original game starting");
        if (games.containsKey(chatId)) {
            games.put(chatId, new OriginalGame(games.get(chatId)).setCaps(captains).createSchema());
            if (games.get(chatId).getSchema() instanceof PicturesSchema) {
                games.get(chatId).setSchema(new OriginalSchema());
                games.get(chatId).createSchema();
            }
        } else
            games.put(chatId, new OriginalGame(chatId, LANG_RUS, false).setCaps(captains).createSchema());

        sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);

        if (games.get(chatId).getSchema().howMuchLeft(GameColor.RED) == 9)
            sendSimpleMessage(RED_TEAM_STARTS, chatId, false);
        else
            sendSimpleMessage(BLUE_TEAM_STARTS, chatId, false);

        for (UserMongo cap : captains)
            sendPicture(games.get(chatId), cap.getLongId(),false,true);
    }

    private void botStartNewGameDuet(UserMongo firstUser, UserMongo secondUser) {
        LOG.info("Duet game starting");
        Game game;
        if (games.containsKey(firstUser.getLongId()))
            game = new DuetGame(games.get(firstUser.getLongId())).setSecondPlayerId(secondUser.getLongId()).createSchema();
        else
            game = new DuetGame(firstUser.getLongId(), LANG_RUS, false).setSecondPlayerId(secondUser.getLongId()).createSchema();

        games.put(firstUser.getLongId(), game);
        games.put(secondUser.getLongId(), game);

        if (game.getSchema().isRedFirst())
            game.setCaps(firstUser, secondUser);
        else
            game.setCaps(secondUser, firstUser);

        sendDuetPicture((DuetGame) game, firstUser.getLongId(), true);
        sendDuetPicture((DuetGame) game, secondUser.getLongId(), false);
        switchTurnDuet((DuetGame) game);
    }

    private void botChooseWord(long chatId) {
        LOG.info("Original game -- word chosen");
        int blackLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.BLACK);
        int redLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.RED);
        int blueLeft = games.get(chatId).getSchema().howMuchLeft(GameColor.BLUE);

        if (blackLeft != 0 && redLeft != 0 && blueLeft != 0) {
            LOG.info("Original game update boards");
            for (UserMongo cap : games.get(chatId).getCaps())
                sendPicture(games.get(chatId), cap.getLongId(),false,true);
            sendPicture(games.get(chatId), chatId, games.get(chatId).isUseKeyboard(), false);
        } else {
            LOG.info("Original game finished -- update boards");
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

    private ReplyKeyboardMarkup getKeyboard(String... args) {
        LOG.info("Option Keyboard build");
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
        LOG.info("Message sending");
        try {
            execute(new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(keyboard));
            LOG.info("Message sent");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            LOG.error("Error occurred while message sending");
        }
    }

    private void sendSimpleMessage(String text, long chatId, boolean eraseKeyboard) {
        LOG.info("Message sending. keyboard erase = " + eraseKeyboard);
        SendMessage message = new SendMessage().setChatId(chatId).setText(text);
        if (eraseKeyboard)
            message.setReplyMarkup(new ReplyKeyboardRemove());
        try {
            execute(message);
            LOG.info("Message sent");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            LOG.error("Error occurred while message sending");
        }
    }

    private void sendSimpleMessage(String text, long chatId) {
        sendSimpleMessage(text, chatId, true);
    }

    private void sendPicture(Game game, long chatId, boolean sendKeyboard, boolean isAdmin) {
        LOG.info("Picture sending");
        String filepath = getFilePath(game.getChatId(), isAdmin);
        new OriginalDrawer(game, filepath, isAdmin);
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
            LOG.info("Picture sent");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error occurred while picture sending");
        }
    }


    private void sendPicturePicGame(PicturesGame game, boolean isAdmin, long... chatIds) {
        LOG.info("Picture sending");
        String filepath = getFilePath(game.getChatId(), isAdmin);
        new PicturesDrawer(game, filepath, isAdmin);
        try {
            File file = new File(filepath);

            for (long chatId : chatIds) {
                SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(file));
                if (!isAdmin && game.isUseKeyboard())
                    photo.setReplyMarkup(sendInlinePicturesKeyboard(game));
                photo.setChatId(chatId);
                execute(photo);
            }

            //noinspection ResultOfMethodCallIgnored
            file.delete();
            LOG.info("Picture sent");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error occurred while picture sending");
        }
    }

    private InlineKeyboardMarkup sendInlinePicturesKeyboard(PicturesGame game) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int j = 0; j < 5; j++) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Card card = game.getSchema().getArray()[i][j];
                String emoji = card.getWord();
                String action = card.getWord();
                if (card.isOpen()) {
                    action = " ";
                    switch (card.getGameColor()) {
                        case RED: emoji = ":heart:"; break;
                        case BLUE: emoji = ":blue_heart:"; break;
                        case YELLOW: emoji = ":yellow_heart:"; break;
                    }
                }
                keyboardButtonsRow.add(new InlineKeyboardButton().setText(EmojiParser.parseToUnicode(emoji)).setCallbackData(action));
            }
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private void sendDuetPicture(DuetGame game, long chatId, boolean isFirst) {
        LOG.info("Duet picture sending");
        String filepath = getFilePath(game.getChatId(), isFirst);
        new DuetDrawer(game, filepath, isFirst);
        try {
            File file = new File(filepath);
            SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(file)).setChatId(chatId);
            execute(photo);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            LOG.info("Duet picture sent");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error occurred while duet picture sending");
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
