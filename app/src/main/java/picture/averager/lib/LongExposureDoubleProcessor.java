package picture.averager.lib;

import java.awt.image.BufferedImage;
import java.math.BigInteger;

public class LongExposureDoubleProcessor extends PictureProcessor {
    private long imageCount;
    private double[][] r_data;
    private double[][] g_data;
    private double[][] b_data;

    public LongExposureDoubleProcessor(int rows, int cols)
    {
        super(rows, cols);

        r_data = new double[rows][cols];
        g_data = new double[rows][cols];
        b_data = new double[rows][cols];

        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                r_data[r][c] = 0.0;
                g_data[r][c] = 0.0;
                b_data[r][c] = 0.0;
            }
        }

        imageCount = 0l;
    }

    @Override
    public synchronized BufferedImage getImageResult() 
    {
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);

        for (int r = 0; r < Math.min(img.getHeight(), this.rows); r++)
        {
            for (int c = 0; c < Math.min(img.getWidth(), this.cols); c++)
            {
                int red = Math.min(255, (int) Math.round(r_data[r][c]));
                int green = Math.min(255, (int) Math.round(g_data[r][c]));
                int blue = Math.min(255, (int) Math.round(b_data[r][c]));

                int argb = 0xFF000000 | red << 16 | green << 8 | blue;
                img.setRGB(c, r, argb);
            }
        }

        return img;
    }

    @Override
    public synchronized void addImage(BufferedImage img) 
    {
        if (imageCount == Long.MAX_VALUE)
        {
            System.err.println("Maximum image count reached.");
            return;
        }

        for (int r = 0; r < Math.min(img.getHeight(), this.rows); r++)
        {
            for (int c = 0; c < Math.min(img.getWidth(), this.cols); c++)
            {
                int argb = img.getRGB(c, r);
                double red = (double) ((argb & 0x00FF0000) >> 16);
                double green = (double) ((argb & 0x0000FF00) >> 8);
                double blue = (double) (argb & 0x000000FF);
                r_data[r][c] = (imageCount * r_data[r][c] + red) / (imageCount + 1);
                g_data[r][c] = (imageCount * g_data[r][c] + green) / (imageCount + 1);
                b_data[r][c] = (imageCount * b_data[r][c] + blue) / (imageCount + 1);
            }
        }

        imageCount++;
    }
}
