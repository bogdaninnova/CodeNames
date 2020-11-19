package com.bope.model.original;

import com.bope.model.Card;
import com.bope.model.Colors;
import com.bope.model.abstr.Drawer;
import com.bope.model.GameColor;
import com.bope.model.abstr.Game;
import java.awt.*;

public class OriginalDrawer extends Drawer {

    public OriginalDrawer(Game game, String fileName, boolean isAdmin) {
        super(500, 200, 200);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                drawCard(game.getSchema().getArray()[i][j], i, j, isAdmin);
        drawScores(game.getSchema().howMuchLeft(GameColor.RED), game.getSchema().howMuchLeft(GameColor.BLUE));
        drawGrid();
        save(fileName);
    }

    private void drawCard(Card card, int i, int j, boolean isAdmin) {
        Color[] colors = new Color[]{Colors.WHITE_CARD, Colors.WHITE_TEXT};
        if (card.isOpen() || isAdmin)
            colors = getCardColor(card);

        if (card.isOpen() && isAdmin) {
            colors = getAdminCardColor(card);
        }

        g.setColor(colors[0]);
        g.fillRect(i * sizeX + 2, j * sizeY + 2, sizeX - 2, sizeY - 2);
        g.setColor(colors[1]);

        if (card.getWord().length() <= 11) {
            g.drawString(card.getWord(), i * sizeX + 100, j * sizeY + 120);
        } else {
            g.drawString(card.getWord().substring(0, 10) + "-", i * sizeX + 100, j * sizeY + 120);
            g.drawString(card.getWord().substring(10), i * sizeX + 100, j * sizeY + 170);
        }
    }



    private static Color[] getCardColor(Card card) {
        if (card.getGameColor() == GameColor.RED)
            return new Color[]{Colors.RED_CARD, Colors.RED_TEXT};
        if (card.getGameColor() == GameColor.BLUE)
            return new Color[]{Colors.BLUE_CARD, Colors.BLUE_TEXT};
        if (card.getGameColor() == GameColor.BLACK)
            return new Color[]{Colors.BLACK_CARD, Colors.BLACK_TEXT};
        if (card.getGameColor() == GameColor.YELLOW)
            return new Color[]{Colors.YELLOW_CARD, Colors.YELLOW_TEXT};
        return null;
    }

    private static Color[] getAdminCardColor(Card card) {
        if (card.getGameColor() == GameColor.RED)
            return new Color[]{Colors.RED_OPEN_CARD, Colors.RED_OPEN_TEXT};
        if (card.getGameColor() == GameColor.BLUE)
            return new Color[]{Colors.BLUE_OPEN_CARD, Colors.BLUE_OPEN_TEXT};
        if (card.getGameColor() == GameColor.BLACK)
            return new Color[]{Colors.BLACK_CARD, Colors.BLACK_TEXT};
        if (card.getGameColor() == GameColor.YELLOW)
            return new Color[]{Colors.YELLOW_CARD, Colors.YELLOW_OPEN_TEXT};
        return null;
    }

}
