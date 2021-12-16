package com.bope.model.game;

import com.bope.model.game.original.OriginalDrawer;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;

public class Card implements Serializable {

    @Getter @Setter private String word;
    @Getter @Setter private boolean isOpen;
    @Getter @Setter private GameColor gameColor;
    @Getter @Setter private GameColor secondGameColor;
    @Getter @Setter private boolean isOpenBySecondPlayer;

    public Card(String word, GameColor gameColor) {
        setWord(word);
        setGameColor(gameColor);
    }

    public Card(String word, GameColor gameColor, GameColor secondGameColor) {
        setWord(word);
        setGameColor(gameColor);
        setSecondGameColor(secondGameColor);
    }

    public String getColor(boolean isAdmin) {
        return getHTMLColorString(OriginalDrawer.getColor(this, isAdmin));
    }

    public static String getHTMLColorString(Color color) {
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());

        return "#" +
                (red.length() == 1? "0" + red : red) +
                (green.length() == 1? "0" + green : green) +
                (blue.length() == 1? "0" + blue : blue);
    }

    public boolean isWhiteText() {
        return isOpen() && getGameColor().equals(GameColor.YELLOW);
    }
}
