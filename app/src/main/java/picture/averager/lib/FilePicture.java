package picture.averager.lib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FilePicture extends Picture {
    private Pixel[][] image;

    public FilePicture(String filename) {
        BufferedImage bufImage = null;
  
        // READ IMAGE
        try {
            File input_file = new File(filename);
            bufImage = ImageIO.read(input_file);

            width = bufImage.getWidth();
            height = bufImage.getHeight();
            
            image = new Pixel[width][height];

            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    Pixel p = new Pixel( 
                        ((short) ((bufImage.getRGB(col, row) & 0xFF0000) >> 16)),
                        ((short) ((bufImage.getRGB(col, row) & 0x00FF00) >> 8)),
                        ((short) (bufImage.getRGB(col, row) & 0x0000FF))
                    );
                    image[col][row] = p;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }

        System.out.print("");

    }

    public FilePicture(Picture p) {
        super(p.getWidth(), p.getHeight());
        image = new Pixel[width][height];
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                image[col][row] = p.getPixel(col, row);
            }
        }
    }

    // Saves to output.png
    public void saveImage() {
        saveImage("output");
    }

    // Saves to imageName.png
    public void saveImage(String imageName) {
        if(imageName.length() == 0)
            saveImage();

        File output = new File(imageName + ".png");
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                int color = ((getPixel(col, row).getRed() % 256) << 16) +
                            ((getPixel(col, row).getGreen() % 256) << 8) +
                            (getPixel(col, row).getBlue() % 256);
                img.setRGB(col, row, color);
            }
        }

        try {
            ImageIO.write(img, "png", output);
            //System.out.println("Success writing image!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pixel getPixel(int x, int y) {
        // TODO Auto-generated method stub
        if(x >= width || y >= height)
            return Pixel.BLACK;
        return image[x][y];
    }
}
