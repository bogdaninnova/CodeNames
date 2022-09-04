package com.bope.model.game.abstr;

import com.bope.model.dao.model.UserMongo;
import com.bope.model.dao.repo.WordsListMongo;
import com.bope.model.game.duet.DuetDrawer;
import com.bope.model.game.duet.DuetGame;
import com.bope.model.game.original.OriginalDrawer;
import com.bope.model.game.original.OriginalGame;
import com.bope.model.game.pictures.PicturesDrawer;
import com.bope.model.game.pictures.PicturesGame;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

@Getter
@Setter
public abstract class Game implements Serializable {

    private long chatId;
    private String lang;
    private boolean useKeyboard;
    private Schema schema;
    private List<UserMongo> caps;

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

    public void setCaps(List<UserMongo> caps) {
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

    public BufferedImage draw(String fileName, boolean isAdmin) {
        BufferedImage bi = null;
        if (this instanceof OriginalGame)
            bi = new OriginalDrawer(this, fileName, isAdmin).getBufferedImage();
        else if (this instanceof PicturesGame)
            bi = new PicturesDrawer((PicturesGame) this, fileName, isAdmin).getBufferedImage();
        else if (this instanceof DuetGame)
            bi = new DuetDrawer((DuetGame) this, fileName, isAdmin).getBufferedImage();
        return bi;
    }
}
