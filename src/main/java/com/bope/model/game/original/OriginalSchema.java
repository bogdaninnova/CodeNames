package com.bope.model.game.original;

import com.bope.model.game.Card;
import com.bope.model.game.GameColor;
import com.bope.model.game.abstr.Schema;

public class OriginalSchema extends Schema {

    @Override
    protected void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(getRandomWord(), GameColor.YELLOW);

        setColorsOnCard(GameColor.RED,  8);
        setColorsOnCard(GameColor.BLUE, 8);
        setColorsOnCard(GameColor.BLACK, 1);
        setColorsOnCard(isRedFirst ? GameColor.RED : GameColor.BLUE, 1);
    }

    @Override
    public int howMuchLeft(GameColor gameColor, boolean isFirst) {
        return -1;
    }
}
