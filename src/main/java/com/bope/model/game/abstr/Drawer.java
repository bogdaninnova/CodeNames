package com.bope.model.game.abstr;

import com.bope.model.game.Colors;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Drawer implements Colors {

    protected final int SIZE_X;
    protected final int SIZE_Y;
    protected final int SIZE_Y_SCORES;
    protected final BufferedImage bi;
    protected final Graphics2D g;

    protected static final String RES_PATH = "src/main/resources/";
    protected static final Font WORD_FONT = createFont(80);
    protected final Font SCORE_FONT;

    protected Drawer(int SIZE_X, int SIZE_Y, int SIZE_Y_SCORES) {
        this.SIZE_X = SIZE_X;
        this.SIZE_Y = SIZE_Y;
        this.SIZE_Y_SCORES = SIZE_Y_SCORES;
        this.SCORE_FONT = createFont((float) (SIZE_Y_SCORES * 0.9));

        bi = new BufferedImage(5* SIZE_X, 5* SIZE_Y + SIZE_Y_SCORES, BufferedImage.TYPE_INT_RGB);
        g = getBackgroundedGraphics2D(bi);
    }


    protected static Graphics2D getBackgroundedGraphics2D(BufferedImage bi) {
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.setFont(WORD_FONT);
        return g;
    }

    public void save(String path) {
        try {
            ImageIO.write(bi, "JPG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void drawGrid() {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(5.0f));

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                g.drawRect(i * SIZE_X, j * SIZE_Y, SIZE_X, SIZE_Y);
        g.drawLine(SIZE_X * 5 / 2, SIZE_Y * 5, SIZE_X * 5 / 2, SIZE_Y * 6);
        g.drawLine(0, 0, 0, SIZE_Y * 6);
        g.drawLine(SIZE_X * 5, 0, SIZE_X * 5, SIZE_Y * 6);
        g.drawLine(SIZE_X * 5, SIZE_Y * 6, 0, SIZE_Y * 6);
    }

    protected void drawScores(int redLeft, int blueLeft) {
        g.setColor(RED_CARD);
        g.fillRect(0, SIZE_Y * 5, SIZE_X * 5 / 2, SIZE_Y_SCORES);
        g.setColor(BLUE_CARD);
        g.fillRect(SIZE_X * 5 / 2, SIZE_Y * 5, SIZE_X * 5 / 2, SIZE_Y_SCORES);

        g.setFont(SCORE_FONT);
        //int wordHeight = g.getFontMetrics(SCORE_FONT).getHeight();

        g.setColor(RED_TEXT);
        g.drawString(String.valueOf(redLeft), SIZE_X * 5 / 4, SIZE_Y * 5 + (int) (SIZE_Y_SCORES * 0.85));
        g.setColor(BLUE_TEXT);
        g.drawString(String.valueOf(blueLeft), SIZE_X * 15 / 4, SIZE_Y * 5 + (int) (SIZE_Y_SCORES * 0.85));

    }

    private static Font createFont(float size) {
        Font font = null;
        try {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            font = Font.createFont(Font.TRUETYPE_FONT, new File(RES_PATH + "fonts/FiraMono-Medium.ttf")).deriveFont(size).deriveFont(attributes);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return font;
    }

    public BufferedImage getBufferedImage() {
        return bi;
    }
}
