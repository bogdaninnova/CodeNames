package com.bope.model;

import lombok.Getter;
import lombok.Setter;

public class Prompt {

    @Getter @Setter private String word;
    @Getter @Setter private int number;
    @Getter @Setter private int numbersLeft;

    public Prompt(String word, int number) {
        setWord(word);
        setNumber(number);
        setNumbersLeft(number);
    }

    public void decrementNumbersLeft() {
        numbersLeft--;
    }
}
