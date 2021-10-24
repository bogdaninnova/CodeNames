package com.bope.model.abstr;

import com.bope.db.UserMongo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public abstract class Game {

    @Getter @Setter private long chatId;
    @Getter @Setter private String lang;
    @Getter @Setter private boolean useKeyboard;
    @Getter @Setter private Schema schema;

    private ArrayList<UserMongo> caps = new ArrayList<>();

    protected Game(Game game) {
        setChatId(game.getChatId());
        setLang(game.getLang());
        setUseKeyboard(game.isUseKeyboard());
        setSchema(game.getSchema());
        reset();
    }

    protected Game(long chatId, String lang, boolean isUseKeyboard) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(isUseKeyboard);
        reset();
    }

    public abstract void reset();

    public Game createSchema() {
        schema.update(getLang());
        return this;
    }

    public ArrayList<UserMongo> getCaps() {
        return caps;
    }

    public Game setCaps(ArrayList<UserMongo> list) {
        caps = list;
        return this;
    }

    public void setCaps(UserMongo cap1, UserMongo cap2) {
        caps = new ArrayList<>();
        caps.add(cap1);
        caps.add(cap2);
    }

    public String getCaptainsToString() {
        StringBuilder sb = new StringBuilder("Captains:");
        for (UserMongo cap : getCaps()) {
            sb.append(" @");
            sb.append(cap.getUserName());
        }
        return sb.toString();
    }
}
