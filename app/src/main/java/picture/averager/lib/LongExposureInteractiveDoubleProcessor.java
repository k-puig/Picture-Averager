package picture.averager.lib;

import java.awt.image.BufferedImage;
import java.util.Scanner;

public class LongExposureInteractiveDoubleProcessor extends PictureProcessor {
    private long imageCount;
    private double[][] r_data;
    private double[][] g_data;
    private double[][] b_data;
    private double brightnessCoefficient = 1.0;
    private Scanner scanner;

    public LongExposureInteractiveDoubleProcessor(int rows, int cols)
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

        this.scanner = new Scanner(System.in);
    }

    @Override
    public synchronized BufferedImage getImageResult() 
    {
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_ARGB);

        for (int r = 0; r < Math.min(img.getHeight(), this.rows); r++)
        {
            for (int c = 0; c < Math.min(img.getWidth(), this.cols); c++)
            {
                int red = Math.min(255, (int) Math.round(brightnessCoefficient * r_data[r][c]));
                int green = Math.min(255, (int) Math.round(brightnessCoefficient * g_data[r][c]));
                int blue = Math.min(255, (int) Math.round(brightnessCoefficient * b_data[r][c]));

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
                double red = expMap((double) ((argb & 0x00FF0000) >> 16), 1275.0, 100.0);
                double green = expMap((double) ((argb & 0x0000FF00) >> 8), 1275.0, 100.0);
                double blue = expMap((double) (argb & 0x000000FF), 1275.0, 100.0);
                r_data[r][c] = (imageCount * r_data[r][c] + red) / (imageCount + 1);
                g_data[r][c] = (imageCount * g_data[r][c] + green) / (imageCount + 1);
                b_data[r][c] = (imageCount * b_data[r][c] + blue) / (imageCount + 1);
            }
        }

        imageCount++;
    }

    @Override
    public boolean hasImages() 
    {
        System.out.println("Enter a positive decimal to change the brightness coefficient (1.0 default) of the output image, or E to exit, or P to print out the raw double data. ");
        String input = scanner.nextLine();

        if (input.toLowerCase().equals("e"))
        {
            scanner.close();
            return false;
        }
        else if (input.toLowerCase().equals("p"))
        {
            for (int r = 0; r < rows; r++)
            {
                for (int c = 0; c < cols; c++)
                {
                    System.out.printf("{%f, %f, %f} ", r_data[r][c], g_data[r][c], b_data[r][c]);
                }
                System.out.println();
            }
        }
        else
        {
            brightnessCoefficient = Double.parseDouble(input);
        }

        return true;
    }

    /*
     * expMap(x) = ae^((ln((u+a)/(a)))/255)x)-a
     * Maps an input value x onto the range [0, upper] using an exponential function
     */
    private static double expMap(double x, double upper, double curveModifier)
    {
        double expCoefficient = Math.log((upper + curveModifier) / curveModifier) / 255.0;
        double exponentialResult = Math.exp(expCoefficient * x);
        double finalAnswer = curveModifier * exponentialResult - curveModifier;
        return finalAnswer;
    }
}
