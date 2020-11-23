package com.bope;

import com.bope.model.GameColor;
import com.bope.model.Prompt;
import com.bope.model.abstr.Game;
import com.bope.model.duet.DuetDrawer;
import com.bope.model.duet.DuetGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.io.FileInputStream;


public class CodeNamesDuet {

    private final CodeNamesBot codeNamesBot;
    private static final Logger LOG = LoggerFactory.getLogger(CodeNamesDuet.class);

    public CodeNamesDuet(CodeNamesBot codeNamesBot) {
        this.codeNamesBot = codeNamesBot;
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

    protected boolean sendPromptDuet(DuetGame game, User user, String text) {
        LOG.info("Duet game - prompt sending");
        if (game.getCaps().get(1).getUserName().equals(user.getUserName()) && game.getPrompt() == null) {
            LOG.info("Duet game - prompt checking");
            Prompt prompt = getPromptDuet(text);
            if (prompt == null) {
                LOG.info("Duet game - prompt incorrect");
                codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_INCORRECT_PROMPT, user.getId());
                return true;
            }

            if (game.getPrompt() == null) {
                LOG.info("Duet game - prompt sent");
                game.setPrompt(prompt);
                codeNamesBot.sendSimpleMessage(String.format(codeNamesBot.DUET_PLAYERS_PROMPT, user.getUserName(), prompt.getWord(), prompt.getNumber()), game.getPartnerId(user.getId()));
                codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_PROMPT_SENT, user.getId());
            }
            return true;
        }
        return false;
    }

    private void finishGameDuet(DuetGame game, String text) {
        LOG.info("Duet game - finishing");
        game.getSchema().openCards(false);
        sendDuetPicture(game, game.getChatId(), true);
        sendDuetPicture(game, game.getChatId(), false);
        sendDuetPicture(game, game.getSecondPlayerId(), true);
        sendDuetPicture(game, game.getSecondPlayerId(), false);
        codeNamesBot.sendSimpleMessage(text, game.getCaps().get(0).getLongId());
        codeNamesBot.sendSimpleMessage(text, game.getCaps().get(1).getLongId());
        game.getSchema().openCards(true);
    }

    private void sendDuetPicture(DuetGame game, long chatId, boolean isFirst) {
        LOG.info("Duet picture sending");
        String filepath = CodeNamesBot.getFilePath(game.getChatId(), isFirst);
        new DuetDrawer(game, filepath, isFirst);
        try {
            File file = new File(filepath);
            SendPhoto photo = new SendPhoto().setPhoto("board", new FileInputStream(file)).setChatId(chatId);
            codeNamesBot.execute(photo);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            LOG.info("Duet picture sent");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error occurred while duet picture sending");
        }
    }

    protected void botStartNewGameDuet(UserMongo firstUser, UserMongo secondUser) {
        LOG.info("Duet game starting");
        Game game;
        if (codeNamesBot.games.containsKey(firstUser.getLongId()))
            game = new DuetGame(codeNamesBot.games.get(firstUser.getLongId())).setSecondPlayerId(secondUser.getLongId()).createSchema();
        else
            game = new DuetGame(firstUser.getLongId(), codeNamesBot.LANG_RUS, false).setSecondPlayerId(secondUser.getLongId()).createSchema();

        codeNamesBot.games.put(firstUser.getLongId(), game);
        codeNamesBot.games.put(secondUser.getLongId(), game);

        if (game.getSchema().isRedFirst())
            game.setCaps(firstUser, secondUser);
        else
            game.setCaps(secondUser, firstUser);

        sendDuetPicture((DuetGame) game, firstUser.getLongId(), true);
        sendDuetPicture((DuetGame) game, secondUser.getLongId(), false);
        switchTurnDuet((DuetGame) game);
    }

    protected void switchTurnDuet(DuetGame game) {
        LOG.info("Duet game - turn switching");
        game.minusTurnsLeft();
        if (game.getTurnsLeft() == 0) {
            LOG.info("Duet game - turn switching: last turn");
            codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_LAST_TURN, game.getCaps().get(0).getLongId());
            codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_LAST_TURN, game.getCaps().get(1).getLongId());
        } else {
            if (game.getSchema().howMuchLeft(GameColor.GREEN, true) != 0 && game.getSchema().howMuchLeft(GameColor.GREEN, false) != 0)
                game.swapCaptains();
            game.setPrompt(null);
            codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_YOUR_TURN, game.getCaps().get(0).getLongId());
            codeNamesBot.sendSimpleMessage(String.format(codeNamesBot.DUET_PLAYERS_TURN, game.getCaps().get(0).getUserName()), game.getCaps().get(1).getLongId());
        }
    }

    protected void botCheckWordDuet(UserMongo userMongo, String text) {
        LOG.info("Duet game starting word checking: " + text);
        DuetGame game = (DuetGame) codeNamesBot.games.get(userMongo.getLongId());

        if (game.getPrompt() == null) {
            LOG.info("Duet game prompt is not sent yet");
            codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_WAIT_FOR_PROMPT, userMongo.getLongId());
            return;
        }

        if (game.getSchema().checkWord(text, game.getChatId() == userMongo.getLongId())) {
            LOG.info("Duet game word checked");
            if (game.getSchema().howMuchLeft(GameColor.BLACK) < 6) {
                LOG.info("Duet game black card opened");
                finishGameDuet(game, codeNamesBot.BLACK_CARD_OPENED);
            } else if (game.getTurnsLeft() == 0 && game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN)) {
                LOG.info("Duet game - game over");
                finishGameDuet(game, codeNamesBot.DUET_GAME_OVER);
            } else if (game.getSchema().howMuchLeft(GameColor.GREEN) == 0) {
                LOG.info("Duet game - win");
                finishGameDuet(game, codeNamesBot.DUET_YOU_WON);
            } else {
                LOG.info("Duet game - word checked -- pictures sending");
                sendDuetPicture(game, game.getChatId(), true);
                sendDuetPicture(game, game.getSecondPlayerId(), false);

                if (game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN) || game.getPrompt().isFinished()) {
                    LOG.info("Duet game - incorrect word, switch turn");
                    switchTurnDuet(game);
                } else {
                    LOG.info("Duet game - correct word");
                    codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_CORRECT, game.getCaps().get(0).getLongId());
                    codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_CORRECT, game.getCaps().get(1).getLongId());

                    if (game.getSchema().howMuchLeft(GameColor.GREEN, game.getSecondPlayerId() == userMongo.getLongId()) == 0) {
                        LOG.info("Duet game - player finished his words!");
                        codeNamesBot.sendSimpleMessage(codeNamesBot.DUET_YOU_FINISHED, game.getCaps().get(0).getLongId());
                        codeNamesBot.sendSimpleMessage(String.format(codeNamesBot.DUET_PLAYER_FINISHED, userMongo.getUserName()), game.getCaps().get(1).getLongId());
                        game.swapCaptains();
                        switchTurnDuet(game);
                    }
                }
                game.refereshGreenLeft();
            }
        }
    }

}
