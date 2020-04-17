package com.bope;

import java.util.HashSet;
import java.util.Set;

public class OriginalGame {

    private long chatId;
    private Set<String> caps = new HashSet<>();
    private Schema schema;
    private String lang;
    private boolean useKeyboard;

    public OriginalGame(OriginalGame game) {
        setChatId(game.getChatId());
        setLang(game.getLang());
        setUseKeyboard(game.isUseKeyboard());
        setSchema(game.getSchema());
    }

    public OriginalGame(long chatId, String lang, boolean isUseKeyboard) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(isUseKeyboard);
        setSchema(new Schema());
    }

    public String getCaptainsToString() {
        StringBuilder sb = new StringBuilder("Captains:");
        for (String cap : getCaps()) {
            sb.append(" @");
            sb.append(cap);
        }
        return sb.toString();
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Set<String> getCaps() {
        return caps;
    }

    public OriginalGame setCaps(Set<String> set) {
        caps = new HashSet<>();
        caps.addAll(set);
        return this;
    }

    public Schema getSchema() {
        return schema;
    }
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public OriginalGame createSchema() {
        schema.update(lang);
        return this;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isUseKeyboard() {
        return useKeyboard;
    }

    public void setUseKeyboard(boolean useKeyboard) {
        this.useKeyboard = useKeyboard;
    }
}
