package com.bope.model.abstr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Drawer {

    protected final int sizeX = 500;
    protected final int sizeY = 200;
    protected BufferedImage bi = new BufferedImage(5*sizeX, 6*sizeY, BufferedImage.TYPE_4BYTE_ABGR);
    protected Graphics2D g = getBackgroundedGraphics2D(bi, Color.WHITE);

    protected void setBufferedImage(BufferedImage bi) {
        this.bi = bi;
    }

    protected static Graphics2D getBackgroundedGraphics2D(BufferedImage bi, Color color) {
        Graphics2D g = bi.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.setFont(new Font( "Arial", Font.BOLD, 60 ));
        return g;
    }

    public void save(String path) {
        try {
            ImageIO.write(bi, "PNG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
