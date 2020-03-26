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

    public Drawer(Schema schema, String fileName, boolean isAdmin) {

        System.out.println("Drawer");

        setBufferedImage(new BufferedImage(5*sizeX, 5*sizeY, BufferedImage.TYPE_4BYTE_ABGR));
        this.g = getBackgroundedGraphics2D(bi, Color.WHITE);
        this.g.setFont(new Font( "Arial", Font.BOLD, 60 ));
        drawGrid();

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                drawCard(schema.getArray()[i][j], i, j, isAdmin);
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
            ImageIO.write(bi, "PNG", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
