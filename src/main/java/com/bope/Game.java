package com.bope;

import java.util.HashSet;
import java.util.Set;

public class Game {

    private long chatId;
    private Set<String> caps = new HashSet<>();
    private Schema schema = new Schema();
    private String lang;

    public Game(long chatId, String lang) {
        setChatId(chatId);
        setLang(lang);
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

    public Game setCaps(Set<String> set) {
        caps = new HashSet<>();
        caps.addAll(set);
        return this;
    }

    public Schema getSchema() {
        return schema;
    }

    public Game createSchema() {
        schema.update(lang);
        return this;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
