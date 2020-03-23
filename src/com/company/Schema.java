package com.company;

import java.awt.*;
import java.util.Random;

public class Schema {

    private Card[][] array = new Card[5][5];
    private boolean isRedFirst;
    private Random rand = new Random();
    private WordsPool wordsPool = new WordsPool();


    public Schema() {
        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());
    }

    private void setColorsOnCard(Color color, int count) {
        while (count > 0) {
            int i = rand.nextInt(5);
            int j = rand.nextInt(5);
            if (array[i][j].getColor().equals(color.GRAY)) {
                array[i][j].setColor(color);
                count--;
            }
        }
    }

    private void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(wordsPool.getRandomWord(), Color.GRAY);

        setColorsOnCard(Color.RED, 8);
        setColorsOnCard(Color.BLUE, 8);
        setColorsOnCard(Color.BLACK, 1);

        if (isRedFirst)
            setColorsOnCard(Color.RED, 1);
        else
            setColorsOnCard(Color.BLUE, 1);
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
