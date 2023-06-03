package picture.averager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FrameGrabber.Exception;

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

        // Process from given path
        this.givenPath = args[1];

        // Specify output file name if given
        outputFileName = null;
        if (args.length == 4)
        {
            outputFileName = args[3];
        }
    }

    public void start() throws IOException, Exception
    {
        RecursiveImageAdder recursiveImageAdder = new RecursiveImageAdder(new File(givenPath), processor);
        recursiveImageAdder.start();

        File outputFile;
        if (outputFileName == null)
        {
            int num = 1;
            outputFile = new File("./IMG_1.png");
            while (outputFile.exists())
            {
                num++;
                outputFile = new File("./IMG_" + num + ".png");
            }
        }
        else
        {
            outputFile = new File(outputFileName + ".png");
        }

        BufferedImage resultingImage = processor.getImageResult();
        ImageIO.write(resultingImage, "png", outputFile);
    }

    public static void main(String[] args) throws IOException, Exception
    {
        App app = new App(args);
        app.start();
    }
}
