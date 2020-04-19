package com.bope;

public class DuetGame extends Game {

    private long secondPlayerId;

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
}
