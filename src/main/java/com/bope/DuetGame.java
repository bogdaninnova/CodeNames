package com.bope;

public class DuetGame extends Game {

    public DuetGame(Game game) {
        super(game);
    }

    public DuetGame(long chatId, String lang, boolean isUseKeyboard) {
        super(chatId, lang, isUseKeyboard);
        setSchema(new DuetSchema());
    }

}
