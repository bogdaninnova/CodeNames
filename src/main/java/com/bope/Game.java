package com.bope;

public class Game {

    private long chatId;
    private String lang;
    private boolean useKeyboard;

    public Game(long chatId, String lang, boolean useKeyboard) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(useKeyboard);
    }

    public boolean isUseKeyboard() {
        return useKeyboard;
    }

    public void setUseKeyboard(boolean useKeyboard) {
        this.useKeyboard = useKeyboard;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
