package picture.averager.lib;

import java.awt.image.BufferedImage;
import java.math.BigInteger;

public class LongExposureBrightenProcessor extends PictureProcessor {
    private BigInteger imageCount;
    private byte[][][] r_data;
    private byte[][][] g_data;
    private byte[][][] b_data;
    int maxBrightness = 0;

    public LongExposureBrightenProcessor(int rows, int cols)
    {
        super(rows, cols);

        r_data = new byte[rows][cols][0];
        g_data = new byte[rows][cols][0];
        b_data = new byte[rows][cols][0];

        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                r_data[r][c] = BigInteger.ZERO.toByteArray();
                g_data[r][c] = BigInteger.ZERO.toByteArray();
                b_data[r][c] = BigInteger.ZERO.toByteArray();
            }
        }

        imageCount = BigInteger.ZERO;
    }

    @Override
    public synchronized BufferedImage getImageResult() 
    {
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);

        for (int r = 0; r < Math.min(img.getHeight(), this.rows); r++)
        {
            for (int c = 0; c < Math.min(img.getWidth(), this.cols); c++)
            {
                int red = Math.min(255, 256 * new BigInteger(r_data[r][c]).divide(imageCount).intValue() / maxBrightness);
                int green = Math.min(255, 256 * new BigInteger(g_data[r][c]).divide(imageCount).intValue() / maxBrightness);
                int blue = Math.min(255, 256 * new BigInteger(b_data[r][c]).divide(imageCount).intValue() / maxBrightness);

                int argb = 0xFF000000 | red << 16 | green << 8 | blue;
                img.setRGB(c, r, argb);
            }
        }

        return img;
    }

    @Override
    public synchronized void addImage(BufferedImage img) 
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
                if (brightness > maxBrightness)
                {
                    maxBrightness = brightness;
                }

                BigInteger red = new BigInteger(r_data[r][c]);
                BigInteger green = new BigInteger(g_data[r][c]);
                BigInteger blue = new BigInteger(b_data[r][c]);

                r_data[r][c] = red.add(
                    BigInteger.valueOf((argb & 0x00FF0000) >> 16))
                    .toByteArray();
                g_data[r][c] = green.add(
                    BigInteger.valueOf((argb & 0x0000FF00) >> 8))
                    .toByteArray();
                b_data[r][c] = blue.add(
                    BigInteger.valueOf((argb & 0x000000FF)))
                    .toByteArray();
            }
        }
        imageCount = imageCount.add(BigInteger.ONE);
    }
}
