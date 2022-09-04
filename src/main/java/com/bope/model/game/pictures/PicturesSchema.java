package com.bope.model.game.pictures;

import com.bope.model.dao.repo.WordsListMongo;
import com.bope.model.game.Card;
import com.bope.model.game.GameColor;
import com.bope.model.game.abstr.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PicturesSchema extends Schema {

    private final ArrayList<Integer> picturesList = new ArrayList<>();

    private HashMap<Integer, String> picturesMapping;

    @Override
    public void update(String lang, WordsListMongo wordsListMongo) {
        if (picturesList.isEmpty())
            for (int i = 0; i < 100; i++)
                picturesList.add(i);
        picturesMapping = new HashMap<>();
        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());
    }

    @Override
    protected void setArray(boolean isRedFirst) {
        int num = 0;
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++) {
                picturesMapping.put(++num, getRandomCard());
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

    @Override
    public int howMuchLeft(GameColor gameColor, boolean isFirst) {
        return howMuchLeft(gameColor);
    }

    protected String getRandomCard() {
        int picNumber = picturesList.remove(rand.nextInt(picturesList.size()));
        return String.valueOf(picNumber);
    }

    public Map<Integer, String> getPicturesMapping() {
        return picturesMapping;
    }
}
