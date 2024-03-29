package picture.averager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.tika.Tika;

import picture.averager.lib.PictureProcessor;

/*
 * A class which utilizes VideoFrameAdder and Java standard image I/O to add 
 * all possible images to the given processor within a given directory
 */
public class RecursiveImageAdder 
{
    private File file;
    private PictureProcessor processor;

    public RecursiveImageAdder(File file, PictureProcessor processor)
    {
        this.file = file;
        this.processor = processor;
    }

    public void start() throws org.bytedeco.javacv.FrameGrabber.Exception
    {
        if (file.isDirectory())
        {
            for (File f : file.listFiles())
            {
                RecursiveImageAdder recursiveImageAdder = new RecursiveImageAdder(f, processor);
                recursiveImageAdder.start();
            }
        }

        if (file.isFile())
        {
            // Differentiate between picture and video type
            Tika tika = new Tika();
            String fileType = tika.detect(file.getAbsolutePath());
            
            if (fileType.startsWith("video/"))
            {
                VideoFrameAdder videoFrameAdder = new VideoFrameAdder(processor, file.getAbsolutePath());
                videoFrameAdder.start();
            }
            else if (fileType.startsWith("image/"))
            {
                try 
                {
                    BufferedImage image = ImageIO.read(file);
                    if (image != null)
                        processor.addImage(image);
                    else
                        System.err.println("Read image " + file.getAbsolutePath() + " contains null data");
                } 
                catch (IOException e) 
                {
                    System.err.println("Given image somehow failed to be read via ImageIO.read(File f)");
                    e.printStackTrace();
                }
            }
            else
            {
                System.err.println("Unsupported file " + file.getAbsolutePath() + " found, ignoring for processing");
            }
        }
    }
}
