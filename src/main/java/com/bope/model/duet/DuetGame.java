package com.bope.model.duet;

import com.bope.model.abstr.Game;

public class DuetGame extends Game {

    private long secondPlayerId;

    private boolean isPromptSend;

    public DuetGame(Game game) {
        super(game);
    }

    public DuetGame(long chatId, String lang, boolean isUseKeyboard) {
        super(chatId, lang, isUseKeyboard);
        setSchema(new DuetSchema());
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

    public boolean isPromptSend() {
        return isPromptSend;
    }

    public void setPromptSend(boolean promptSend) {
        isPromptSend = promptSend;
    }

    public long getPartnerId(long chatId) {
        return getChatId() != chatId ? getChatId() : getSecondPlayerId();

    }

}
