package com.bope.model.duet;

import com.bope.model.Card;
import com.bope.model.GameColor;
import com.bope.model.abstr.Schema;

public class DuetSchema extends Schema {

    protected void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(getRandomWord(), GameColor.YELLOW, GameColor.YELLOW);

        setColorsOnCard(GameColor.GREEN,  9);
        setColorsOnCard(GameColor.BLACK, 3);

        setSecondColorsOnCard(GameColor.BLACK, GameColor.BLACK, 1);
        setSecondColorsOnCard(GameColor.BLACK, GameColor.GREEN, 1);
        setSecondColorsOnCard(GameColor.BLACK, GameColor.YELLOW, 1);
        setSecondColorsOnCard(GameColor.GREEN, GameColor.BLACK, 1);
        setSecondColorsOnCard(GameColor.GREEN, GameColor.GREEN, 3);
        setSecondColorsOnCard(GameColor.GREEN, GameColor.YELLOW, 5);

    }



    private void setSecondColorsOnCard(GameColor gameColor, GameColor onGameColor, int count) {
        while (count > 0) {
            int i = rand.nextInt(5);
            int j = rand.nextInt(5);

            if (array[i][j].getGameColor().equals(onGameColor) && array[i][j].getSecondGameColor().equals(GameColor.YELLOW)) {
                array[i][j].setSecondGameColor(gameColor);
                count--;
            }

        }
    }

    public int howMuchLeft(GameColor gameColor) {
        return howMuchLeft(gameColor, true) + howMuchLeft(gameColor, false);
    }

    public int howMuchLeft(GameColor gameColor, boolean isFirst) {
        int count = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                if (isFirst && array[i][j].getGameColor() == gameColor && !array[i][j].isOpenBySecondPlayer())
                    count++;
                if (!isFirst && array[i][j].getSecondGameColor() == gameColor && !array[i][j].isOpen())
                    count++;
            }
        return count;
    }
}
