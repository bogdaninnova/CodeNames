package com.bope.model.game.original;

import com.bope.model.game.Card;
import com.bope.model.game.Colors;
import com.bope.model.game.abstr.Drawer;
import com.bope.model.game.GameColor;
import com.bope.model.game.abstr.Game;
import java.awt.*;

public class OriginalDrawer extends Drawer implements Colors {

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
        Color[] colors = new Color[]{WHITE_CARD, WHITE_TEXT};
        if (card.isOpen() || isAdmin)
            colors = getCardColor(card, false);

        if (card.isOpen() && isAdmin)
            colors = getCardColor(card, true);

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

    private static Color[] getCardColor(Card card, boolean isAdmin) {
        switch (card.getGameColor()) {
            case RED : return isAdmin ? new Color[]{RED_OPEN_CARD, RED_OPEN_TEXT} : new Color[]{RED_CARD, RED_TEXT};
            case BLUE : return isAdmin ? new Color[]{BLUE_OPEN_CARD, BLUE_OPEN_TEXT} : new Color[]{BLUE_CARD, BLUE_TEXT};
            case BLACK : return new Color[]{BLACK_CARD, BLACK_TEXT};
            case YELLOW : return isAdmin ? new Color[]{YELLOW_CARD, YELLOW_OPEN_TEXT} : new Color[]{YELLOW_CARD, YELLOW_TEXT};
            default: return null;
        }
    }

    public static Color getColor(Card card, boolean isAdmin) {
        Color[] colors = new Color[]{WHITE_CARD, WHITE_TEXT};
        if (card.isOpen() || isAdmin)
            colors = getCardColor(card, false);

        if (card.isOpen() && isAdmin)
            colors = getCardColor(card, true);

        assert colors != null;
        return colors[0];
    }
}
