package picture.averager.lib;

import java.awt.image.BufferedImage;

public abstract class PictureProcessor
{
    protected int rows, cols;

    protected PictureProcessor(int rows, int cols)
    {
        assert(rows > 0 && cols > 0);
        this.rows = rows;
        this.cols = cols;
    }

    public abstract BufferedImage getImageResult();
    public abstract void addImage(BufferedImage img);
    public abstract boolean hasImages();
}
