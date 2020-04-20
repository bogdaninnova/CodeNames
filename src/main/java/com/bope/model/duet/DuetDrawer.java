package com.bope.model.duet;

import com.bope.model.Card;
import com.bope.model.Colors;
import com.bope.model.GameColor;
import com.bope.model.abstr.Drawer;
import com.bope.model.abstr.Schema;

import java.awt.*;

public class DuetDrawer extends Drawer {

    public DuetDrawer(Schema schema, String fileName, boolean isFirstPlayer) {

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                drawCard(schema.getArray()[i][j], i, j, isFirstPlayer);
        //drawScores(schema.howMuchLeft(GameColor.RED), schema.howMuchLeft(GameColor.BLUE));
        drawGrid();
        save(fileName);
    }


    private void drawCard(Card card, int i, int j, boolean isFirstPlayer) {
        Color[] colors = new Color[]{Colors.WHITE_CARD, Colors.WHITE_TEXT};

        if (!card.isOpen() && !card.isOpenBySecondPlayer()) {
            colors = getCardColor(card, isFirstPlayer);
        }

        System.out.println(
                card.getWord() + " " +
                card.isOpen()  + " " +
                card.getGameColor()  + " " +
                card.isOpenBySecondPlayer()  + " " +
                card.getSecondGameColor()
        );
        if (card.isOpen() && card.getSecondGameColor() == GameColor.GREEN ||
                card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.GREEN)
            colors = new Color[]{Colors.GREEN_OPEN_CARD, Colors.GREEN_OPEN_TEXT};

        g.setColor(colors[0]);
        g.fillRect(i * sizeX + 2, j * sizeY + 2, sizeX - 2, sizeY - 2);
        g.setColor(colors[1]);
        g.drawString(card.getWord(), i * sizeX + 100, j * sizeY + 120);
    }




    private static Color[] getCardColor(Card card, boolean isFirst) {
        GameColor color = isFirst ? card.getGameColor() : card.getSecondGameColor();
        if (color == GameColor.BLACK)
            return new Color[]{Colors.BLACK_CARD, Colors.BLACK_TEXT};
        if (color == GameColor.GREEN)
            return new Color[]{Colors.GREEN_CARD, Colors.GREEN_TEXT};
        if (color == GameColor.YELLOW)
            return new Color[]{Colors.YELLOW_CARD, Colors.YELLOW_TEXT};
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
