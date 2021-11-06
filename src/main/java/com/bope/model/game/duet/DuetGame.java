package com.bope.model.game.duet;

import com.bope.model.game.GameColor;
import com.bope.model.game.Prompt;
import com.bope.model.game.abstr.Game;
import lombok.Getter;
import lombok.Setter;

public class DuetGame extends Game {

    @Getter private long secondPlayerId;
    @Getter @Setter private Prompt prompt;
    @Getter private int openGreensLeft;
    @Getter private int turnsLeft;

    public DuetGame(Game game) {
        super(game);
    }

    public DuetGame(long chatId, String lang, boolean isUseKeyboard) {
        super(chatId, lang, isUseKeyboard);
        setSchema(new DuetSchema());
        this.turnsLeft = 9;
        this.openGreensLeft = 18;
    }

    public void setSecondPlayerId(long secondPlayerId) {
        this.secondPlayerId = secondPlayerId;
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
