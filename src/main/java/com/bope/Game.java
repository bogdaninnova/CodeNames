package com.bope;

import java.util.HashSet;
import java.util.Set;

public class Game {

    private long chatId;
    private Set<String> caps = new HashSet<>();
    private Schema schema;

    public Game(long chatId, Set<String> caps, String lang) {
        setChatId(chatId);
        createSchema(lang);
        setCaps(caps);
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

    private void setCaps(Set<String> set) {
        caps = new HashSet<>();
        caps.addAll(set);
    }

    public Schema getSchema() {
        return schema;
    }

    private void createSchema(String lang) {
        this.schema = new Schema(lang);
    }
}
