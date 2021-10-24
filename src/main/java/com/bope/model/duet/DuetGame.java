package com.bope.model.duet;

import com.bope.model.GameColor;
import com.bope.model.Prompt;
import com.bope.model.abstr.Game;
import lombok.Getter;
import lombok.Setter;

public class DuetGame extends Game {

    @Getter private long secondPlayerId;
    public DuetGame(Game game) {
        super(game);
    }
    @Getter @Setter private Prompt prompt;
    @Getter private int openGreensLeft;
    @Getter private int turnsLeft;

    public DuetGame(long chatId, String lang, boolean isUseKeyboard) {
        super(chatId, lang, isUseKeyboard);
        setSchema(new DuetSchema());
        //refreshGreenLeft();
        this.turnsLeft = 9;
        this.openGreensLeft = 18;
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

    public void refreshGreenLeft() {
        this.openGreensLeft = getSchema().howMuchLeft(GameColor.GREEN);
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
