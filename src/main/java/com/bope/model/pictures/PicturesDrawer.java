package com.bope.model.pictures;

import com.bope.model.Card;
import com.bope.model.GameColor;
import com.bope.model.abstr.Drawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicturesDrawer extends Drawer {

    private static final Logger LOG = LoggerFactory.getLogger(PicturesDrawer.class);

    public PicturesDrawer(PicturesGame game, String fileName, boolean isAdmin) {
        super(614, 614, 400);
        LOG.info("PicturesDrawer starts");
        try {
            BufferedImage image;
            PicturesSchema schema = (PicturesSchema) game.getSchema();
            for (int i = 0; i < 5; i++)
                for (int j = 0; j < 5; j++) {
                    LOG.info("Starting to draw card: i=" + i + ", j=" + j);
                    Card card = schema.getArray()[i][j];
                    LOG.info("Starting to draw card (path = " + RES_PATH + "pictures/" + schema.getPicturesMapping().get(Integer.parseInt(card.getWord())) + ".jpg)");
                    image = ImageIO.read(new File(RES_PATH + "pictures/" + schema.getPicturesMapping().get(Integer.parseInt(card.getWord())) + ".jpg"));
                    LOG.info("Adding image on board");
                    addImage(image, i, j, card, isAdmin);
                    LOG.info("Adding number to image");
                    addNumber(i, j, card, isAdmin);
                }
            LOG.info("Adding scores on board");
            drawScores(schema.howMuchLeft(GameColor.RED), game.getSchema().howMuchLeft(GameColor.BLUE));
            LOG.info("Drawing grid");
            drawGrid();
            LOG.info("File saving");
            save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNumber(int x, int y, Card card, boolean isAdmin) {
        g.setFont(new Font( "Arial", Font.BOLD, SIZE_Y / 6));
        g.setColor(Color.BLACK);
        if (isAdmin && card.getGameColor().equals(GameColor.BLACK))
            g.setColor(Color.WHITE);
        g.drawString(card.getWord(), x * SIZE_X + SIZE_X /18, y * SIZE_Y + SIZE_Y /6);
    }

    private void addImage(BufferedImage image, int x, int y, Card card, boolean isAdmin) {
        if (card.isOpen() || isAdmin) {
            int mask = 0;
            switch (card.getGameColor()) {
                case RED: mask = 0xff6450; break;
                case BLUE: mask = 0x52bbff; break;
                case YELLOW: mask = 0xffd9b3; break;
                case BLACK: mask = 0xcccccc; invertColors(image); break;
            }
            applyFilter(image, mask);
        }

        if (card.isOpen() && isAdmin)
            makeBlank(image);

        g.drawImage(image, x * SIZE_Y, y * SIZE_X, null);
    }

    private static void applyFilter(BufferedImage image, int mask) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y) & mask;
                image.setRGB(x, y, pixel);
            }
        }
    }

    private static void invertColors(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color col = new Color(image.getRGB(x, y), true);
                col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                image.setRGB(x, y, col.getRGB());
            }
        }
    }


    private static void makeBlank(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (x%3==0 || y%3==0) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
    }

}
