package picture.averager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import picture.averager.lib.KeepBrightestPixelProcessor;
import picture.averager.lib.KeepDarkestPixelProcessor;
import picture.averager.lib.LongExposureBrightenProcessor;
import picture.averager.lib.LongExposureDoubleProcessor;
import picture.averager.lib.LongExposureInteractiveDoubleProcessor;
import picture.averager.lib.LongExposureProcessor;
import picture.averager.lib.PictureProcessor;

public class App {
    String givenPath;
    PictureProcessor processor;
    String outputFileName;

    public App(String[] args)
    {
        if (args.length != 3 && args.length != 4)
            throw new IllegalArgumentException("Arg length is " + args.length + " (expected: 3 or 4)");
        
        // Process image dimensions
        String[] dimStr = args[2].split("x");
        if (dimStr.length != 2)
            throw new IllegalArgumentException("Given dimension count " + dimStr.length + " is not 2.");
        
        int width = Integer.parseInt(dimStr[0]);
        int height = Integer.parseInt(dimStr[1]);

        // Create image processor
        if (args[0].equals("average"))
        {
            this.processor = new LongExposureProcessor(height, width);
        }
        else if (args[0].equals("doubleaverage"))
        {
            this.processor = new LongExposureDoubleProcessor(height, width);
        }
        else if (args[0].equals("brighten"))
        {
            this.processor = new LongExposureBrightenProcessor(height, width);
        }
        else if (args[0].equals("keepbright"))
        {
            this.processor = new KeepBrightestPixelProcessor(height, width);
        }
        else if (args[0].equals("keepdark"))
        {
            this.processor = new KeepDarkestPixelProcessor(height, width);
        }
        else if (args[0].equals("interactive"))
        {
            this.processor = new LongExposureInteractiveDoubleProcessor(height, width);
        }

        // Process from given file or directory
        this.givenPath = args[1];

        // Specify output file name if given
        outputFileName = null;
        if (args.length == 4)
        {
            outputFileName = args[3];
        }
    }

    public void start() throws IOException, org.bytedeco.javacv.FrameGrabber.Exception
    {
        RecursiveImageAdder recursiveImageAdder = new RecursiveImageAdder(new File(givenPath), processor);
        recursiveImageAdder.start();

        while (processor.hasImages())
        {
            File outputFile;
            if (outputFileName == null)
            {
                long num = 1l;
                outputFile = new File("IMG_" + num + ".png");
                while (outputFile.exists())
                {
                    outputFile = new File("IMG_" + (++num) + ".png");
                }
            }
            else
            {
                outputFile = new File(outputFileName + ".png");
            }

            BufferedImage resultingImage = processor.getImageResult();
            ImageIO.write(resultingImage, "png", outputFile);
            System.out.println("File " + outputFile.getAbsolutePath() + " written");
        }
    }

    public static void main(String[] args) throws IOException, org.bytedeco.javacv.FrameGrabber.Exception
    {
        App app = new App(args);
        app.start();
    }
}
