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
        //System.out.println( card.getWord() + " " + card.isOpen()  + " " + card.getGameColor()  + " " + card.isOpenBySecondPlayer()  + " " + card.getSecondGameColor());

        if (!card.isOpen() && !card.isOpenBySecondPlayer()) {
            Color[] colors = getCardColor(card, isFirstPlayer);
            drawRectangle(card.getWord(), i, j, colors[0], colors[1]);
        } else if ((card.isOpen() && card.getSecondGameColor() == GameColor.GREEN) || (card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.GREEN)) {
            drawRectangle(card.getWord(), i, j, Colors.GREEN_OPEN_CARD, Colors.GREEN_OPEN_TEXT);
        }


        else if (!isFirstPlayer && card.isOpen() && card.getSecondGameColor() == GameColor.YELLOW) {
            drawRectangle(card.getWord(), i, j, Colors.RED_CARD, getCardColor(card, isFirstPlayer)[0], Colors.WHITE_TEXT);
        } else if (!isFirstPlayer && card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.YELLOW) {
            drawRectangle(card.getWord(), i, j, getCardColor(card, isFirstPlayer)[0], Colors.RED_CARD, Colors.WHITE_TEXT);
        } else if (isFirstPlayer && card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.YELLOW) {
            drawRectangle(card.getWord(), i, j, Colors.RED_CARD, getCardColor(card, isFirstPlayer)[0], Colors.WHITE_TEXT);
        } else if (isFirstPlayer && card.isOpen() && card.getSecondGameColor() == GameColor.YELLOW) {
            drawRectangle(card.getWord(), i, j, getCardColor(card, isFirstPlayer)[0], Colors.RED_CARD, Colors.WHITE_TEXT);
        }



        else {
            drawRectangle(card.getWord(), i, j, Colors.WHITE_CARD, Colors.WHITE_TEXT);
        }

    }


    private void drawRectangle(String word, int i, int j, Color fillColor, Color textColor) {
        g.setColor(fillColor);
        g.fillRect(i * sizeX + 2, j * sizeY + 2, sizeX - 2, sizeY - 2);
        g.setColor(textColor);
        g.drawString(word, i * sizeX + 100, j * sizeY + 120);
    }

    private void drawRectangle(String word, int i, int j, Color upperFillColor, Color lowerFillColor, Color textColor) {

        int[] x1 = {i * sizeX + 2, (i+1) * sizeX, i * sizeX + 2};
        int[] y1 = {(j+1) * sizeY + 2, j * sizeY + 2, j * sizeY};
        Polygon p1 = new Polygon(x1, y1, 3);

        int[] x2 = {(i+1) * sizeX, (i+1) * sizeX, i * sizeX + 2};
        int[] y2 = {j * sizeY, (j+1) * sizeY + 2, (j+1) * sizeY};
        Polygon p2 = new Polygon(x2, y2, 3);

        g.setColor(upperFillColor);
        g.fillPolygon(p1);
        g.setColor(lowerFillColor);
        g.fillPolygon(p2);
        g.setColor(textColor);
        g.drawString(word, i * sizeX + 100, j * sizeY + 120);
    }


    private static Color[] getCardColor(Card card, boolean isFirst) {
        GameColor color = isFirst ? card.getGameColor() : card.getSecondGameColor();
        if (color == GameColor.BLACK)
            return new Color[]{Colors.BLACK_CARD, Colors.BLACK_TEXT};
        if (color == GameColor.GREEN)
            return new Color[]{Colors.GREEN_CARD, Colors.GREEN_TEXT};
        if (color == GameColor.YELLOW)
            return new Color[]{Colors.YELLOW_CARD, Colors.YELLOW_TEXT};
        return new Color[]{Colors.WHITE_CARD, Colors.WHITE_TEXT};
    }

}
