package com.bope.model.game.duet;

import com.bope.model.game.Card;
import com.bope.model.game.Colors;
import com.bope.model.game.GameColor;
import com.bope.model.game.abstr.Drawer;

import java.awt.*;

public class DuetDrawer extends Drawer implements Colors {

    public DuetDrawer(DuetGame game, String fileName, boolean isFirstPlayer) {
        super(500, 200, 200);
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                drawCard(game.getSchema().getArray()[i][j], i, j, isFirstPlayer);
        drawText(game.getCaps().get(0).getUserName(), game.getTurnsLeft());
        drawGrid();
        save(fileName);
    }


    private void drawCard(Card card, int i, int j, boolean isFirstPlayer) {
        if (!card.isOpen() && !card.isOpenBySecondPlayer()) {
            Color[] colors = getCardColor(card, isFirstPlayer);
            drawRectangle(card.getWord(), i, j, colors[0], colors[1]);
        } else if ((card.isOpen() && card.getSecondGameColor() == GameColor.GREEN) || (card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.GREEN)) {
            drawRectangle(card.getWord(), i, j, GREEN_OPEN_CARD, GREEN_OPEN_TEXT);
        } else if (card.isOpen() && card.getSecondGameColor() == GameColor.YELLOW && card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.YELLOW) {
            drawRectangle(card.getWord(), i, j, BROWN_OPEN_CARD, BROWN_OPEN_TEXT);
        }

        else if (!isFirstPlayer && card.isOpen() && card.getSecondGameColor() == GameColor.YELLOW) {
            drawRectangleDiagonal(card.getWord(), i, j, BROWN_OPEN_CARD, getCardColor(card, false)[0]);
        } else if (!isFirstPlayer && card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.YELLOW) {
            drawRectangleDiagonal(card.getWord(), i, j, getCardColor(card, false)[0], BROWN_OPEN_CARD);
        } else if (isFirstPlayer && card.isOpenBySecondPlayer() && card.getGameColor() == GameColor.YELLOW) {
            drawRectangleDiagonal(card.getWord(), i, j, BROWN_OPEN_CARD, getCardColor(card, true)[0]);
        } else if (isFirstPlayer && card.isOpen() && card.getSecondGameColor() == GameColor.YELLOW) {
            drawRectangleDiagonal(card.getWord(), i, j, getCardColor(card, true)[0], BROWN_OPEN_CARD);
        }

        else {
            drawRectangle(card.getWord(), i, j, WHITE_CARD, WHITE_TEXT);
        }

    }

    private void drawText(String playersTurn, int turnsLeft) {
        g.setColor(Color.BLACK);
        g.setFont(new Font( "Arial", Font.BOLD, 75 ));

        if (turnsLeft != 0) {
            g.drawString("Turn of @" + playersTurn, 100, 5 * SIZE_Y + 120);
            g.drawString("Turns left: " + turnsLeft, 3 * SIZE_X, 5 * SIZE_Y + 120);
        } else {
            g.drawString("Last turn for all!", 100, 5 * SIZE_Y + 120);
            g.drawString("Last turn!", 3 * SIZE_X, 5 * SIZE_Y + 120);
        }
    }

    private void drawRectangle(String word, int i, int j, Color fillColor, Color textColor) {
        g.setColor(fillColor);
        g.fillRect(i * SIZE_X + 2, j * SIZE_Y + 2, SIZE_X - 2, SIZE_Y - 2);
        g.setColor(textColor);
        if (word.length() <= 11) {
            g.drawString(word, i * SIZE_X + 100, j * SIZE_Y + 120);
        } else {
            g.drawString(word.substring(0, 10) + "-", i * SIZE_X + 100, j * SIZE_Y + 120);
            g.drawString(word.substring(10), i * SIZE_X + 100, j * SIZE_Y + 170);
        }
    }

    private void drawRectangleDiagonal(String word, int i, int j, Color upperFillColor, Color lowerFillColor) {

        int[] x1 = {i * SIZE_X + 2, (i+1) * SIZE_X, i * SIZE_X + 2};
        int[] y1 = {(j+1) * SIZE_Y + 2, j * SIZE_Y + 2, j * SIZE_Y};
        Polygon p1 = new Polygon(x1, y1, 3);

        int[] x2 = {(i+1) * SIZE_X, (i+1) * SIZE_X, i * SIZE_X + 2};
        int[] y2 = {j * SIZE_Y, (j+1) * SIZE_Y + 2, (j+1) * SIZE_Y};
        Polygon p2 = new Polygon(x2, y2, 3);

        g.setColor(upperFillColor);
        g.fillPolygon(p1);
        g.setColor(lowerFillColor);
        g.fillPolygon(p2);
        g.setColor(BROWN_OPEN_TEXT);
        g.drawString(word, i * SIZE_X + 100, j * SIZE_Y + 120);
    }


    private static Color[] getCardColor(Card card, boolean isFirst) {
        GameColor color = isFirst ? card.getGameColor() : card.getSecondGameColor();
        switch (color) {
            case BLACK: return new Color[]{BLACK_CARD, BLACK_TEXT};
            case GREEN: return new Color[]{GREEN_CARD, GREEN_TEXT};
            case YELLOW: return new Color[]{YELLOW_CARD, YELLOW_TEXT};
            default: return new Color[]{WHITE_CARD, WHITE_TEXT};
        }
    }

}
