package com.bope;

import java.util.Random;

public class Schema {

    private Card[][] array = new Card[5][5];
    private boolean isRedFirst;
    private Random rand = new Random();
    private WordsPool wordsPool;


    public Schema(String lang) {
        wordsPool = new WordsPool(lang);
        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());
    }


    public boolean checkWord(String word) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (array[i][j].getWord().toLowerCase().equals(word.toLowerCase()) && !array[i][j].isOpen()) {
                    array[i][j].setOpen(true);
                    return true;
                }
        return false;
    }

    public void openCards() {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j].setOpen(true);
    }

    public int howMuchLeft(GameColor gameColor) {
        int count = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (array[i][j].getGameColor() == gameColor && !array[i][j].isOpen())
                    count++;

        return count;
    }

    private void setColorsOnCard(GameColor gameColor, int count) {
        while (count > 0) {
            int i = rand.nextInt(5);
            int j = rand.nextInt(5);
            if (array[i][j].getGameColor().equals(GameColor.YELLOW)) {
                array[i][j].setGameColor(gameColor);
                count--;
            }
        }
    }

    private void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(wordsPool.getRandomWord(), GameColor.YELLOW);

        setColorsOnCard(GameColor.RED, 8);
        setColorsOnCard(GameColor.BLUE, 8);
        setColorsOnCard(GameColor.BLACK, 1);

        if (isRedFirst)
            setColorsOnCard(GameColor.RED, 1);
        else
            setColorsOnCard(GameColor.BLUE, 1);
    }

    public Card[][] getArray() {
        return array;
    }

    public boolean isRedFirst() {
        return isRedFirst;
    }

    public void setRedFirst(boolean redFirst) {
        isRedFirst = redFirst;
    }
}
