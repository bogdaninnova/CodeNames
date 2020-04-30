package com.bope.model;

public class Card {

    private String word;

    private boolean isOpen;
    private GameColor gameColor;

    private GameColor secondGameColor;
    private boolean isOpenBySecondPlayer;

    public Card(String word, GameColor gameColor) {
        setWord(word);
        setGameColor(gameColor);
    }

    public Card(String word, GameColor gameColor, GameColor secondGameColor) {
        setWord(word);
        setGameColor(gameColor);
        setSecondGameColor(secondGameColor);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public GameColor getGameColor() {
        return gameColor;
    }

    public void setGameColor(GameColor gameColor) {
        this.gameColor = gameColor;
    }

    public GameColor getSecondGameColor() {
        return secondGameColor;
    }

    public void setSecondGameColor(GameColor secondGameColor) {
        this.secondGameColor = secondGameColor;
    }

    public boolean isOpenBySecondPlayer() {
        return isOpenBySecondPlayer;
    }

    public void setOpenBySecondPlayer(boolean openBySecondPlayer) {
        isOpenBySecondPlayer = openBySecondPlayer;
    }
}
