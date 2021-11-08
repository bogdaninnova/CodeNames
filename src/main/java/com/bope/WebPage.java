package com.bope;

import com.bope.model.dao.repo.GamesListMongo;
import com.bope.model.game.abstr.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RestController
public class WebPage {

    private static final Logger LOG = LoggerFactory.getLogger(WebPage.class);

    @Autowired private GamesListMongo gamesListMongo;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] greeting(@PathVariable String id) {

        Game game = getGame(-Long.parseLong(id));
        String filepath = id + ".jpg";
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(game.draw(filepath, false), "jpg", bao);
            return bao.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            File file = new File(filepath);
            boolean isDeleted = file.delete();
            LOG.info("File deleted: " + isDeleted);
        }
    }

    public Game getGame(long chatId) {
        return Game.getFromBinary(gamesListMongo.findFirstByGameIdOrderByDateDesc(chatId).getBinaryGameString());
    }
}
