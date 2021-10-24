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

        if (card.isOpen() && isAdmin)
            colors = getAdminCardColor(card);

        assert colors != null;
        g.setColor(colors[0]);
        g.fillRect(i * SIZE_X + 2, j * SIZE_Y + 2, SIZE_X - 2, SIZE_Y - 2);
        g.setColor(colors[1]);

        //drawWord(card.getWord().toUpperCase(), i, j);
        drawWord(card.getWord().toLowerCase(), i, j);
    }


    private void drawWord(String word, int i, int j) {
        if (word.length() <= 10) {
            int wordWidth = g.getFontMetrics(WORD_FONT).stringWidth(word);
            g.drawString(word, i * SIZE_X + (SIZE_X -wordWidth)/2, j * SIZE_Y + 120);
        } else {
            int wordWidth_part1 = g.getFontMetrics(WORD_FONT).stringWidth(word.substring(0, 9));
            int wordWidth_part2 = g.getFontMetrics(WORD_FONT).stringWidth(word.substring(9));
            g.drawString(word.substring(0, 9), i * SIZE_X + (SIZE_X -wordWidth_part1)/2, j * SIZE_Y + 95);
            g.drawString(word.substring(9), i * SIZE_X + (SIZE_X -wordWidth_part2)/2, j * SIZE_Y + 155);
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
