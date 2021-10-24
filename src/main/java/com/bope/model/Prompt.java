package com.bope.model;

public class Prompt {

    private String word;
    private int number;
    private int numbersLeft;

    public Prompt(String word, int number) {
        setWord(word);
        setNumber(number);
        setNumbersLeft(number);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumbersLeft() {
        return numbersLeft;
    }

    public void setNumbersLeft(int numbersLeft) {
        this.numbersLeft = numbersLeft;
    }

    public void decrementNumbersLeft() {
        numbersLeft--;
    }
}
