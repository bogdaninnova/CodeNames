package com.bope.model.original;

import com.bope.model.Card;
import com.bope.model.GameColor;
import com.bope.model.abstr.Schema;

public class OriginalSchema extends Schema {

    protected void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(getRandomWord(), GameColor.YELLOW);

        setColorsOnCard(GameColor.RED, true, 8);
        setColorsOnCard(GameColor.BLUE, true,8);
        setColorsOnCard(GameColor.BLACK, true,1);

        if (isRedFirst)
            setColorsOnCard(GameColor.RED, true,1);
        else
            setColorsOnCard(GameColor.BLUE,true, 1);
    }

    public int howMuchLeft(GameColor gameColor) {
        int count = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (array[i][j].getGameColor() == gameColor && !array[i][j].isOpen())
                    count++;

        return count;
    }
}
