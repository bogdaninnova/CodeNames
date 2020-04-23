package com.bope.model.duet;

import com.bope.model.Card;
import com.bope.model.GameColor;
import com.bope.model.abstr.Schema;

public class DuetSchema extends Schema {

    protected void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(getRandomWord(), GameColor.YELLOW, GameColor.YELLOW);

        setColorsOnCard(GameColor.GREEN, true, 9);
        setColorsOnCard(GameColor.GREEN, false, 9);
        setColorsOnCard(GameColor.BLACK, true,3);
        setColorsOnCard(GameColor.BLACK, false,3);
    }

    public int howMuchLeft(GameColor gameColor) {
        int count = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                if (array[i][j].getGameColor() == gameColor && !array[i][j].isOpenBySecondPlayer())
                    count++;
                if (array[i][j].getSecondGameColor() == gameColor && !array[i][j].isOpen())
                    count++;
            }
        return count;
    }
}
