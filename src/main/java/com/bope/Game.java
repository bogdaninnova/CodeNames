package com.bope;

import java.util.ArrayList;
import java.util.Set;

public class Game {

    private long chatId;
    private String lang;
    private boolean useKeyboard;
    private Schema schema;
    private ArrayList<String> caps = new ArrayList<>();

    protected Game(Game game) {
        setChatId(game.getChatId());
        setLang(game.getLang());
        setUseKeyboard(game.isUseKeyboard());
        setSchema(game.getSchema());
    }

    protected Game(long chatId, String lang, boolean isUseKeyboard) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(isUseKeyboard);
        //create default schema
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

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Game createSchema() {
        schema.update(getLang());
        return this;
    }

    public ArrayList<String> getCaps() {
        return caps;
    }

    public Game setCaps(Set<String> set) {
        caps = new ArrayList<>();
        caps.addAll(set);
        return this;
    }

    public String getCaptainsToString() {
        StringBuilder sb = new StringBuilder("Captains:");
        for (String cap : getCaps()) {
            sb.append(" @");
            sb.append(cap);
        }
        return sb.toString();
    }
}
