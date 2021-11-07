package com.bope.model.game.abstr;

import com.bope.model.dao.model.UserMongo;
import com.bope.model.dao.repo.WordsListMongo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.ArrayList;

public abstract class Game implements Serializable {

    @Getter @Setter private long chatId;
    @Getter @Setter private String lang;
    @Getter @Setter private boolean useKeyboard;
    @Getter @Setter private Schema schema;

    @Getter private ArrayList<UserMongo> caps;

    protected Game(Game game) {
        setChatId(game.getChatId());
        setLang(game.getLang());
        setUseKeyboard(game.isUseKeyboard());
        setSchema(game.getSchema());
        setCaps(new ArrayList<>());
        reset();
    }

    protected Game(long chatId, String lang, boolean isUseKeyboard) {
        setChatId(chatId);
        setLang(lang);
        setUseKeyboard(isUseKeyboard);
        setCaps(new ArrayList<>());
        reset();
    }

    public abstract void reset();

    public void createSchema(WordsListMongo wordsListMongo) {
        schema.update(getLang(), wordsListMongo);
    }

    public void setCaps(ArrayList<UserMongo> caps) {
        this.caps = caps;
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

    public String getBinaryText() {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            oos.writeObject(this);
            oos.flush();
            byte[] binary =  byteArrayOutputStream.toByteArray();
            return Base64.encodeBase64String(binary);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Game getFromBinary(String binaryText) {
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decodeBase64(binaryText));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            return (Game) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public BufferedImage draw(String fileName, boolean isAdmin) {
//        BufferedImage bi = null;
//        switch (this) {
//            case OriginalGame originalGame -> bi = new OriginalDrawer(originalGame, fileName, isAdmin).getBufferedImage();
//            case DuetGame duetGame -> bi = new DuetDrawer(duetGame, fileName, isAdmin).getBufferedImage();
//            case PicturesGame picturesGame -> bi = new PicturesDrawer(picturesGame, fileName, isAdmin).getBufferedImage();
//            default -> LOG.error("Wrong Type in Pattern Matching");
//        }
//        return bi;
//    }
}
