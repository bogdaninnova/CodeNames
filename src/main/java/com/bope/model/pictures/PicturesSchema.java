package com.bope.model.pictures;

import com.bope.model.Card;
import com.bope.model.GameColor;
import com.bope.model.abstr.Schema;

import java.util.ArrayList;
import java.util.HashMap;

public class PicturesSchema extends Schema {

    private ArrayList<Integer> picturesList = new ArrayList<>();

    private HashMap<Integer, String> picturesMapping;

    public void update(String lang) {
        for (int i = 0; i<=100; i++)
            picturesList.add(i);
        picturesMapping = new HashMap<>();
        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());

    }

    protected void setArray(boolean isRedFirst) {
        int num = 0;
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++) {
                String pic_num = getRandomCard();
                picturesMapping.put(++num, pic_num);
                array[i][j] = new Card(String.valueOf(num), GameColor.YELLOW);
            }
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
        return String.valueOf(picNumber);
    }

    public boolean isRedFirst() {
        return isRedFirst;
    }

    public void setRedFirst(boolean redFirst) {
        isRedFirst = redFirst;
    }

    public HashMap<Integer, String> getPicturesMapping() {
        return picturesMapping;
    }
}
