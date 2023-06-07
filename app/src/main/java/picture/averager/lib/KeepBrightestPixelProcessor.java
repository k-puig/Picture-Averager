package picture.averager.lib;

import java.awt.image.BufferedImage;

public class KeepBrightestPixelProcessor extends PictureProcessor
{
    private boolean imageAdded;
    private int[][] img_data;

    public KeepBrightestPixelProcessor(int rows, int cols) 
    {
        super(rows, cols);
        this.imageAdded = false;
        img_data = new int[rows][cols];
    }

    @Override
    public BufferedImage getImageResult() 
    {
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);

        for (int r = 0; r < Math.min(img.getHeight(), this.rows); r++)
        {
            for (int c = 0; c < Math.min(img.getWidth(), this.cols); c++)
            {
                img.setRGB(c, r, img_data[r][c]);
            }
        }

        return img;
    }

    @Override
    public void addImage(BufferedImage img) 
    {
        for (int r = 0; r < Math.min(img.getHeight(), this.rows); r++)
        {
            for (int c = 0; c < Math.min(img.getWidth(), this.cols); c++)
            {
                int argb = img.getRGB(c, r);
                int brightness = (
                    ((argb & 0x00FF0000) >> 16) +
                    ((argb & 0x0000FF00) >> 8) +
                    ((argb & 0x000000FF))
                ) / 3;

                int thisBrightness = (
                    ((img_data[r][c] & 0x00FF0000) >> 16) +
                    ((img_data[r][c] & 0x0000FF00) >> 8) +
                    ((img_data[r][c] & 0x000000FF))
                ) / 3;

                if (brightness > thisBrightness)
                {
                    img_data[r][c] = argb | 0xFF000000;
                }
            }
        }
        
        imageAdded = true;
    }

    @Override
    public boolean hasImages() 
    {
        return imageAdded;
    }
}
