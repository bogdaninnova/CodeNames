package com.bope;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Schema {

    protected Card[][] array = new Card[5][5];
    protected boolean isRedFirst;
    protected Random rand = new Random();
    protected List<WordMongo> wordList;
    protected List<WordMongo> allWordList = new ArrayList<>();
    protected String lang = "rus";

    public void update(String lang) {
        wordList = getWordList(lang);
        setLang(lang);
        setRedFirst(rand.nextBoolean());
        setArray(isRedFirst());
    }

    protected abstract void setArray(boolean isRedFirst);

    public abstract int howMuchLeft(GameColor gameColor);

    public List<WordMongo> getWordList(String lang) {
        if (allWordList.size() < 25 || !getLang().equals(lang))
            allWordList = Main.ctx.getBean(WordsListMongo.class).findByLang(lang);

        List<WordMongo> resultWordsList = new ArrayList<>();
        for (int i = 0; i < 25; i++)
            resultWordsList.add(allWordList.remove(rand.nextInt(allWordList.size())));
        return resultWordsList;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    protected String getRandomWord() {
        String word = wordList.remove(rand.nextInt(wordList.size())).getWord();
        return word.substring(0, 1).toUpperCase() + word.substring(1);
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

    public void openCards() {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                array[i][j].setOpen(true);
                array[i][j].setOpenBySecondPlayer(true);
            }
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

    protected void setColorsOnCard(GameColor gameColor, boolean isFirst, int count) {
        while (count > 0) {
            int i = rand.nextInt(5);
            int j = rand.nextInt(5);

            if (isFirst) {
                if (array[i][j].getGameColor().equals(GameColor.YELLOW)) {
                    array[i][j].setGameColor(gameColor);
                    count--;
                }
            } else {
                if (array[i][j].getSecondGameColor().equals(GameColor.YELLOW)) {
                    array[i][j].setSecondGameColor(gameColor);
                    count--;
                }
            }
        }
    }

}
