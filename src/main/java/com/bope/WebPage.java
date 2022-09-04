package com.bope;

import com.bope.model.dao.repo.GamesListMongo;
import com.bope.model.game.Card;
import com.bope.model.game.GameColor;
import com.bope.model.game.abstr.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.bope.model.game.Colors.BLUE_CARD;
import static com.bope.model.game.Colors.RED_CARD;

@Controller
@Slf4j
public class WebPage {
    @Autowired private GamesListMongo gamesListMongo;

    @GetMapping(value = "/game/{id}", produces = "image/jpg")
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
            return new byte[0];
        } finally {
            new File(filepath).delete();
        }
    }
}
