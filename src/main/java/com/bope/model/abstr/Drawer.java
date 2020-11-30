package com.bope.model.abstr;

import com.bope.model.Colors;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Drawer {

    protected final int sizeX;
    protected final int sizeY;
    protected final int sizeYscores;
    protected BufferedImage bi;
    protected Graphics2D g;

    protected static final String RES_PATH = "src/main/resources/";
    protected static final Font WORD_FONT = createFont();
    protected static final Font font = new Font("Arial", Font.BOLD, 60);

    protected Drawer(int sizeX, int sizeY, int sizeYscores) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeYscores = sizeYscores;

        bi = new BufferedImage(5*sizeX, 5*sizeY + sizeYscores, BufferedImage.TYPE_INT_RGB);
        g = getBackgroundedGraphics2D(bi, Color.WHITE);
    }


    protected static Graphics2D getBackgroundedGraphics2D(BufferedImage bi, Color color) {
        Graphics2D g = bi.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.setFont(font);
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
                g.drawRect(i * sizeX, j * sizeY, sizeX, sizeY);
        g.drawLine(sizeX * 5 / 2, sizeY * 5, sizeX * 5 / 2, sizeY * 6);
        g.drawLine(0, 0, 0, sizeY * 6);
        g.drawLine(sizeX * 5, 0, sizeX * 5, sizeY * 6);
        g.drawLine(sizeX * 5, sizeY * 6, 0, sizeY * 6);
    }

    protected void drawScores(int redLeft, int blueLeft) {
        g.setColor(Colors.RED_CARD);
        g.fillRect(0, sizeY * 5, sizeX * 5 / 2, sizeYscores);
        g.setColor(Colors.BLUE_CARD);
        g.fillRect(sizeX * 5 / 2, sizeY * 5, sizeX * 5 / 2, sizeYscores);

        g.setFont(new Font( "Arial", Font.BOLD, sizeYscores * 3/4));
        g.setColor(Colors.RED_TEXT);
        g.drawString(String.valueOf(redLeft), sizeX * 5 / 4, sizeY * 5 + sizeYscores * 3/4);
        g.setColor(Colors.BLUE_TEXT);
        g.drawString(String.valueOf(blueLeft), sizeX * 15 / 4, sizeY * 5 + sizeYscores * 3/4);

    }

    private static Font createFont() {
        Font font = new Font("Arial", Font.BOLD, 60);
        try {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            //attributes.put(TextAttribute.TRACKING, 0.1);
            font = Font.createFont(Font.TRUETYPE_FONT, new File(RES_PATH + "fonts/FiraMono-Medium.ttf")).deriveFont(80.0f).deriveFont(attributes);
            //font = Font.createFont(Font.TRUETYPE_FONT, new File(RES_PATH + "fonts/FiraMono-Bold.ttf")).deriveFont(60.0f).deriveFont(attributes);
            //font = Font.createFont(Font.TRUETYPE_FONT, new File(RES_PATH + "fonts/FiraMono-Regular.ttf")).deriveFont(60.0f).deriveFont(attributes);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return font;
    }

}
