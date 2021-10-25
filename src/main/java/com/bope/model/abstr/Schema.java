package com.bope.model.abstr;

import com.bope.Main;
import com.bope.db.WordMongo;
import com.bope.db.WordsListMongo;
import com.bope.model.Card;
import com.bope.model.GameColor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Schema {

    @Getter protected final Card[][] array = new Card[5][5];
    protected final Random rand = new Random();
    protected List<WordMongo> wordList;
    protected List<WordMongo> allWordList = new ArrayList<>();
    @Getter @Setter protected boolean isRedFirst;
    @Getter @Setter protected String lang = "rus";

    public void update(String lang) {
        wordList = getWordList(lang);
        setLang(lang);
        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());
    }

    protected abstract void setArray(boolean isRedFirst);
    public abstract int howMuchLeft(GameColor gameColor, boolean isFirst);

    public int howMuchLeft(GameColor gameColor) {
        int count = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (array[i][j].getGameColor() == gameColor && !array[i][j].isOpen())
                    count++;

        return count;
    }

    public List<WordMongo> getWordList(String lang) {
        if (allWordList.size() < 25 || !getLang().equals(lang))
            allWordList = Main.ctx.getBean(WordsListMongo.class).findByLang(lang);

        List<WordMongo> resultWordsList = new ArrayList<>();
        for (int i = 0; i < 25; i++)
            resultWordsList.add(allWordList.remove(rand.nextInt(allWordList.size())));
        return resultWordsList;
    }

    public void setDefaultCards() {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                array[i][j] = new Card("default", GameColor.YELLOW);
    }

    protected String getRandomWord() {
        String word = wordList.remove(rand.nextInt(wordList.size())).getWord();
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public void openCards(boolean isOpen) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                array[i][j].setOpen(isOpen);
                array[i][j].setOpenBySecondPlayer(isOpen);
            }
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

    public boolean checkWord(String word, boolean isFirst) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (array[i][j].getWord().equalsIgnoreCase(word)) {
                    if (!array[i][j].isOpen() && isFirst) {
                        array[i][j].setOpen(true);
                        if (array[i][j].getGameColor().equals(GameColor.GREEN) && array[i][j].getSecondGameColor().equals(GameColor.GREEN))
                            array[i][j].setOpenBySecondPlayer(true);
                        return true;
                    }
                    if (!array[i][j].isOpenBySecondPlayer() && !isFirst) {
                        array[i][j].setOpenBySecondPlayer(true);
                        if (array[i][j].getGameColor().equals(GameColor.GREEN) && array[i][j].getSecondGameColor().equals(GameColor.GREEN))
                            array[i][j].setOpen(true);
                        return true;
                    }
                }
        return false;
    }

}
