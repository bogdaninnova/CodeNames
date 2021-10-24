package com.bope.model.duet;

import com.bope.model.GameColor;
import com.bope.model.Prompt;
import com.bope.model.abstr.Game;

public class DuetGame extends Game {

    private long secondPlayerId;
    public DuetGame(Game game) {
        super(game);
    }
    private Prompt prompt;
    private int openGreensLeft;
    private int turnsLeft;

    public DuetGame(long chatId, String lang, boolean isUseKeyboard) {
        super(chatId, lang, isUseKeyboard);
        setSchema(new DuetSchema());
        //refreshGreenLeft();
        this.turnsLeft = 9;
        this.openGreensLeft = 18;
    }

    public long getSecondPlayerId() {
        return secondPlayerId;
    }

    public Game setSecondPlayerId(long secondPlayerId) {
        this.secondPlayerId = secondPlayerId;
        return this;
    }

    public void swapCaptains() {
        setCaps(getCaps().get(1), getCaps().get(0));
    }

    public long getPartnerId(long chatId) {
        return getChatId() != chatId ? getChatId() : getSecondPlayerId();

    }

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
    }

    public int getOpenGreensLeft() {
        return openGreensLeft;
    }

    public void refreshGreenLeft() {
        this.openGreensLeft = getSchema().howMuchLeft(GameColor.GREEN);
    }

    public int getTurnsLeft() {
        return turnsLeft;
    }

    public void reset() {
        this.turnsLeft = 9;
        this.openGreensLeft = 18;
    }

    public void minusTurnsLeft() {
        if (turnsLeft != 0)
            turnsLeft--;
    }
}
