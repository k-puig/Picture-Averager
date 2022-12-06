package picture.averager.lib;

import java.awt.image.BufferedImage;

public class DarkestPicture extends LongExposurePicture {
    private Pixel[][] image;
    private boolean firstImageAdded;

    public DarkestPicture(int width, int height) {
        super(width, height);
        firstImageAdded = false;
        image = new Pixel[width][height];
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                image[col][row] = Pixel.BLACK;
            }
        }
    }

    @Override
    public void addImage(Picture pic) {
        if(pic == null) return;
        
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                if(!firstImageAdded)
                    image[col][row] = pic.getPixel(col, row);
                else
                    image[col][row] = Pixel.min(image[col][row], pic.getPixel(col, row));
            }
        }
        firstImageAdded = true;
    }

    @Override
    public Pixel getPixel(int x, int y) {
        return image[x][y];
    }

    @Override
    public void addImage(BufferedImage buf) {
        if(buf == null) return;

        BufferPicture bufPic = new BufferPicture(buf);
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                if(!firstImageAdded)
                    image[col][row] = bufPic.getPixel(col, row);
                else
                    image[col][row] = Pixel.min(image[col][row], bufPic.getPixel(col, row));
            }
        }
    }
    
}
