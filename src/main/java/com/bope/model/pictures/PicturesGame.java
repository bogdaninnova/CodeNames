package com.bope.model.pictures;

import com.bope.model.abstr.Game;

public class PicturesGame extends Game {

    public PicturesGame(Game game) {
        super(game);
    }

    public PicturesGame(long chatId) {
        super(chatId, "none", false);
        setSchema(new PicturesSchema());
    }

    @Override
    public void reset() {

    }
}
