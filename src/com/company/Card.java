package com.company;

import java.awt.*;

public class Card {

    private String word;

    private boolean isOpen;
    private Color color;

    public Card(String word, Color cardColor) {
        setWord(word);
        setColor(cardColor);
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color cardColor) {
        this.color = cardColor;
    }

}
