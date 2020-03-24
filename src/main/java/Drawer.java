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
        setBufferedImage(new BufferedImage(5*sizeX, 5*sizeY, BufferedImage.TYPE_4BYTE_ABGR));
        this.g = getBackgroundedGraphics2D(bi, Color.white);
        this.g.setFont(new Font( "SansSerif", Font.BOLD, 48 ));
        drawGrid();

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                drawCard(schema.getArray()[i][j], i, j, isAdmin);
        save(fileName);
    }

    private void drawCard(Card card, int i, int j, boolean isAdmin) {

        g.setColor(Color.GREEN);
        if (card.isOpen() || isAdmin)
            g.setColor(card.getColor());

        g.fillRect(i * sizeX + 2, j * sizeY + 2, sizeX - 2, sizeY - 2);
        g.setColor(Color.BLACK);
        if (card.getColor().equals(Color.BLACK) && (card.isOpen() || isAdmin))
            g.setColor(Color.WHITE);
        g.drawString(card.getWord(), i * sizeX + 150, j * sizeY + 120);

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
            ImageIO.write(bi, "PNG", new File("src\\main\\images\\" + path + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
