package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Drawer {

    private int sizeX = 500;
    private int sizeY = 200;
    private BufferedImage bi;
    private Graphics2D g;

    public Drawer() {
        setBufferedImage(new BufferedImage(5*sizeX, 5*sizeY, BufferedImage.TYPE_4BYTE_ABGR));
        this.g = getBackgroundedGraphics2D(bi, Color.white);
        this.g.setFont(new Font( "SansSerif", Font.BOLD, 48 ));
    }

    public void drawCard(Card card) {
        g.setColor(card.getColor());
        g.setStroke(new BasicStroke(5.0f));
        g.fillRect(0, 0, sizeX, sizeY);
        g.setColor(Color.BLACK);
        g.drawString(card.getWord(), 50, 120);

    }

    public void setBufferedImage(BufferedImage bi) {
        this.bi = bi;
    }

    public static Graphics2D getBackgroundedGraphics2D(BufferedImage bi, Color color) {
        Graphics2D g = bi.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        return g;
    }

    public void save(String path) {
        try {
            ImageIO.write(bi, "PNG", new File(path + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
