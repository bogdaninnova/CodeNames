package com.bope;

import com.bope.model.dao.model.UserMongo;
import com.bope.model.dao.repo.WordsListMongo;
import com.bope.model.game.GameColor;
import com.bope.model.game.Prompt;
import com.bope.model.game.duet.DuetDrawer;
import com.bope.model.game.duet.DuetGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@Slf4j
public class CodeNamesDuet {
    private CodeNamesBot codeNamesBot;

    @Value("${DUET_GAME_OVER}") protected String DUET_GAME_OVER;
    @Value("${DUET_YOU_WON}") protected String DUET_YOU_WON;
    @Value("${DUET_YOU_FINISHED}") protected String DUET_YOU_FINISHED;
    @Value("${DUET_PLAYER_FINISHED}") protected String DUET_PLAYER_FINISHED;
    @Value("${DUET_YOUR_TURN}") protected String DUET_YOUR_TURN;
    @Value("${DUET_PLAYERS_TURN}") protected String DUET_PLAYERS_TURN;
    @Value("${DUET_LAST_TURN}") protected String DUET_LAST_TURN;
    @Value("${DUET_CORRECT}") protected String DUET_CORRECT;
    @Value("${DUET_WAIT_FOR_PROMPT}") protected String DUET_WAIT_FOR_PROMPT;
    @Value("${DUET_PROMPT_SENT}") protected String DUET_PROMPT_SENT;
    @Value("${DUET_INCORRECT_PROMPT}") protected String DUET_INCORRECT_PROMPT;
    @Value("${DUET_PLAYERS_PROMPT}") protected String DUET_PLAYERS_PROMPT;


    private static Prompt getPromptDuet(String text) {
        Prompt prompt = null;
        try {
            log.info("Duet game prompt parsing: " + text);
            prompt = new Prompt(text.substring(0, text.indexOf(' ')), Integer.parseInt(text.substring(text.indexOf(' ') + 1)));
        } catch (Exception e) {
            log.warn("Duet game prompt parse error!");
        }
        return prompt;
    }

    protected boolean sendPromptDuet(DuetGame game, User user, String text) {
        log.info("Duet game - prompt sending");
        if (game.getCaps().get(1).getUserName().equals(user.getUserName()) && game.getPrompt() == null) {
            log.info("Duet game - prompt checking");
            Prompt prompt = getPromptDuet(text);
            if (prompt == null) {
                log.info("Duet game - prompt incorrect");
                codeNamesBot.sendSimpleMessage(DUET_INCORRECT_PROMPT, user.getId());
                return true;
            }

            if (game.getPrompt() == null) {
                log.info("Duet game - prompt sent");
                game.setPrompt(prompt);
                codeNamesBot.sendSimpleMessage(String.format(DUET_PLAYERS_PROMPT, user.getUserName(), prompt.getWord(), prompt.getNumber()), game.getPartnerId(user.getId()));
                codeNamesBot.sendSimpleMessage(DUET_PROMPT_SENT, user.getId());
            }
            return true;
        }
        return false;
    }

    private void finishGameDuet(DuetGame game, String text) {
        log.info("Duet game - finishing");
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
        log.info("Duet picture sending");
        String filepath = CodeNamesBot.getFilePath(game.getChatId(), isFirst);
        new DuetDrawer(game, filepath, isFirst);
        try {
            SendPhoto photo = new SendPhoto();
            photo.setPhoto(new InputFile(filepath));
            photo.setChatId(String.valueOf(chatId));
            codeNamesBot.execute(photo);
            log.info("Duet picture sent");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occurred while duet picture sending");
        }
    }

    protected void botStartNewGameDuet(UserMongo firstUser, UserMongo secondUser, WordsListMongo wordsListMongo) {
        log.info("Duet game starting");
        DuetGame game;
        if (codeNamesBot.isGameExists(firstUser.getLongId()))
            game = new DuetGame(codeNamesBot.getGame(firstUser.getLongId()));
        else
            game = new DuetGame(firstUser.getLongId(), codeNamesBot.LANG_RUS, false);
        game.setSecondPlayerId(secondUser.getLongId());
        game.createSchema(wordsListMongo);
        game.setCaps(firstUser, secondUser);
        if (!game.getSchema().isRedFirst())
            game.swapCaptains();

        sendDuetPicture(game, firstUser.getLongId(), true);
        sendDuetPicture(game, secondUser.getLongId(), false);
        switchTurnDuet(game);

        codeNamesBot.saveGame(firstUser.getLongId(), game);
        codeNamesBot.saveGame(secondUser.getLongId(), game);
    }

    protected void switchTurnDuet(DuetGame game) {
        log.info("Duet game - turn switching");
        game.minusTurnsLeft();
        if (game.getTurnsLeft() == 0) {
            log.info("Duet game - turn switching: last turn");
            codeNamesBot.sendSimpleMessage(DUET_LAST_TURN, game.getCaps().get(0).getLongId());
            codeNamesBot.sendSimpleMessage(DUET_LAST_TURN, game.getCaps().get(1).getLongId());
        } else {
            if (game.getSchema().howMuchLeft(GameColor.GREEN, true) != 0 && game.getSchema().howMuchLeft(GameColor.GREEN, false) != 0)
                game.swapCaptains();
            game.setPrompt(null);
            codeNamesBot.sendSimpleMessage(DUET_YOUR_TURN, game.getCaps().get(0).getLongId());
            codeNamesBot.sendSimpleMessage(String.format(DUET_PLAYERS_TURN, game.getCaps().get(0).getUserName()), game.getCaps().get(1).getLongId());
        }
    }

    protected void botCheckWordDuet(UserMongo userMongo, String text) {
        log.info("Duet game starting word checking: " + text);
        DuetGame game = (DuetGame) codeNamesBot.getGame(userMongo.getLongId());

        if (game.getPrompt() == null) {
            log.info("Duet game prompt is not sent yet");
            codeNamesBot.sendSimpleMessage(DUET_WAIT_FOR_PROMPT, userMongo.getLongId());
            return;
        }

        if (game.getSchema().checkWord(text, game.getChatId() == userMongo.getLongId())) {
            log.info("Duet game word checked");
            if (game.getSchema().howMuchLeft(GameColor.BLACK) < 6) {
                log.info("Duet game black card opened");
                finishGameDuet(game, codeNamesBot.BLACK_CARD_OPENED);
            } else if (game.getTurnsLeft() == 0 && game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN)) {
                log.info("Duet game - game over");
                finishGameDuet(game, DUET_GAME_OVER);
            } else if (game.getSchema().howMuchLeft(GameColor.GREEN) == 0) {
                log.info("Duet game - win");
                finishGameDuet(game, DUET_YOU_WON);
            } else {
                log.info("Duet game - word checked -- pictures sending");
                sendDuetPicture(game, game.getChatId(), true);
                sendDuetPicture(game, game.getSecondPlayerId(), false);

                game.getPrompt().decrementNumbersLeft();
                if (game.getOpenGreensLeft() == game.getSchema().howMuchLeft(GameColor.GREEN)) {
                    log.info("Duet game - incorrect word, switch turn");
                    switchTurnDuet(game);
                } else {
                    log.info("Duet game - correct word");
                    codeNamesBot.sendSimpleMessage(DUET_CORRECT, game.getCaps().get(0).getLongId());
                    codeNamesBot.sendSimpleMessage(DUET_CORRECT, game.getCaps().get(1).getLongId());

                    if (game.getSchema().howMuchLeft(GameColor.GREEN, game.getSecondPlayerId() == userMongo.getLongId()) == 0) {
                        log.info("Duet game - player finished his words!");
                        codeNamesBot.sendSimpleMessage(DUET_YOU_FINISHED, game.getCaps().get(0).getLongId());
                        codeNamesBot.sendSimpleMessage(String.format(DUET_PLAYER_FINISHED, userMongo.getUserName()), game.getCaps().get(1).getLongId());
                        game.swapCaptains();
                        switchTurnDuet(game);
                    }
                }
                game.refreshGreenLeft();
            }
        }
    }

    @Autowired
    public void setCodeNamesBot(CodeNamesBot codeNamesBot) {
        this.codeNamesBot = codeNamesBot;
    }

}
