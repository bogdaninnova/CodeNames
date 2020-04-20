package com.bope.model.original;

import com.bope.model.Card;
import com.bope.model.Colors;
import com.bope.model.abstr.Drawer;
import com.bope.model.GameColor;
import com.bope.model.abstr.Schema;
import java.awt.*;

public class OriginalDrawer extends Drawer {

    public OriginalDrawer(Schema schema, String fileName, boolean isAdmin) {
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                drawCard(schema.getArray()[i][j], i, j, isAdmin);
        drawScores(schema.howMuchLeft(GameColor.RED), schema.howMuchLeft(GameColor.BLUE));
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
        g.drawString(card.getWord(), i * sizeX + 100, j * sizeY + 120);
    }

    private void drawScores(int redLeft, int blueLeft) {
        g.setColor(Colors.RED_CARD);
        g.fillRect(0, sizeY * 5, sizeX * 5 / 2, sizeY);
        g.setColor(Colors.BLUE_CARD);
        g.fillRect(sizeX * 5 / 2, sizeY * 5, sizeX * 5 / 2, sizeY);

        g.setFont(new Font( "Arial", Font.BOLD, 150 ));
        g.setColor(Colors.RED_TEXT);
        g.drawString(String.valueOf(redLeft), sizeX * 5 / 4 - 50, sizeY * 11 / 2 + 50);
        g.setColor(Colors.BLUE_TEXT);
        g.drawString(String.valueOf(blueLeft), sizeX * 15 / 4 - 50, sizeY * 11 / 2 + 50);

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

    private void drawGrid() {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(5.0f));

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                g.drawRect(i * sizeX, j * sizeY, sizeX, sizeY);
            g.drawLine(sizeX * 5 / 2, sizeY * 5, sizeX * 5 / 2, sizeY * 6);
            g.drawLine(0, 0, 0, sizeY * 6);
            g.drawLine(sizeX * 5, 0, sizeX * 5, sizeY * 6);
            g.drawLine(sizeX * 5, sizeY * 6, 0, sizeY * 6);
    }

}
