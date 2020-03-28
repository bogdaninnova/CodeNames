public class Card {

    private String word;

    private boolean isOpen;
    private GameColor gameColor;

    public Card(String word, GameColor gameColor) {
        setWord(word);
        setGameColor(gameColor);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public GameColor getGameColor() {
        return gameColor;
    }

    public void setGameColor(GameColor gameColor) {
        this.gameColor = gameColor;
    }

}
