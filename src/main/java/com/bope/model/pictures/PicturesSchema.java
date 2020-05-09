package com.bope.model.pictures;

import com.bope.model.Card;
import com.bope.model.GameColor;
import com.bope.model.abstr.Schema;

import java.util.ArrayList;
import java.util.Random;

public class PicturesSchema extends Schema {

    private ArrayList<Integer> picturesList = new ArrayList<>();


    public void update(String lang) {
        for (int i = 0; i<=100; i++)
            picturesList.add(i);

        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());

    }

    protected void setArray(boolean isRedFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card(getRandomCard(), GameColor.YELLOW);

        setColorsOnCard(GameColor.RED,  8);
        setColorsOnCard(GameColor.BLUE, 8);
        setColorsOnCard(GameColor.BLACK, 1);

        if (isRedFirst)
            setColorsOnCard(GameColor.RED, 1);
        else
            setColorsOnCard(GameColor.BLUE, 1);
    }

    public int howMuchLeft(GameColor gameColor) {
        int count = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (array[i][j].getGameColor() == gameColor && !array[i][j].isOpen())
                    count++;

        return count;
    }

    @Override
    public int howMuchLeft(GameColor gameColor, boolean isFirst) {
        return 0;
    }

    protected void setColorsOnCard(GameColor gameColor, int count) {
        while (count > 0) {
            int i = rand.nextInt(5);
            int j = rand.nextInt(5);
            if (array[i][j].getGameColor().equals(GameColor.YELLOW)) {
                array[i][j].setGameColor(gameColor);
                count--;
            }
        }
    }

    protected String getRandomCard() {
        int picNumber = picturesList.remove(rand.nextInt(picturesList.size()));
        return picNumber + ".jpg";
    }

    public boolean isRedFirst() {
        return isRedFirst;
    }

    public void setRedFirst(boolean redFirst) {
        isRedFirst = redFirst;
    }

}
