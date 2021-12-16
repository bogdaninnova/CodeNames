package com.bope;

import com.bope.model.dao.repo.GamesListMongo;
import com.bope.model.game.Card;
import com.bope.model.game.GameColor;
import com.bope.model.game.abstr.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.bope.model.game.Colors.BLUE_CARD;
import static com.bope.model.game.Colors.RED_CARD;

@Controller
public class WebPage {

    private static final Logger LOG = LoggerFactory.getLogger(WebPage.class);

    @Autowired private GamesListMongo gamesListMongo;

    @RequestMapping(value = "/game/{id}", method = RequestMethod.GET, produces = "image/jpg")
    public String homePage(Model model, @PathVariable String id) {
        Game game = getGame(-Long.parseLong(id));
        model.addAttribute("array", game.getSchema().getArray());
        model.addAttribute("isGameOver", game.getSchema().isGameOver());
        model.addAttribute("redLeft", game.getSchema().howMuchLeft(GameColor.RED));
        model.addAttribute("blueLeft", game.getSchema().howMuchLeft(GameColor.BLUE));
        model.addAttribute("redColor", Card.getHTMLColorString(RED_CARD));
        model.addAttribute("blueColor", Card.getHTMLColorString(BLUE_CARD));

        return "game";
    }

    public Game getGame(long chatId) {
        return Game.getFromBinary(gamesListMongo.findFirstByGameIdOrderByDateDesc(chatId).getBinaryGameString());
    }

    public byte[] getGameBoard(Game game) {
        String filepath = game.getChatId() + ".jpg";
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
}
