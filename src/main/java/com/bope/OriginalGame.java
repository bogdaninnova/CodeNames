package com.bope;

import java.util.HashSet;
import java.util.Set;

public class OriginalGame {

    private long chatId;
    private Set<String> caps = new HashSet<>();
    private Schema schema = new Schema();
    private String lang;
    private boolean useKeyboard;

    public OriginalGame(long chatId, String lang, boolean useKeyboard) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(useKeyboard);
    }

    public OriginalGame(long chatId, String lang) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(false);
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
