package com.bope.model;

import lombok.Getter;
import lombok.Setter;

public class Card {

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
}
