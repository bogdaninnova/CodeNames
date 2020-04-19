package com.bope;

public class OriginalGame extends Game {

    public OriginalGame(Game game) {
        super(game);
    }

    public OriginalGame(long chatId, String lang, boolean isUseKeyboard) {
        super(chatId, lang, isUseKeyboard);
        setSchema(new OriginalSchema());
    }

}
