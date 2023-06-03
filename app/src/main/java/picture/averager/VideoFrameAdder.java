package picture.averager;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;

import picture.averager.lib.PictureProcessor;
import java.awt.image.BufferedImage;

/*
 * Adds frames of a video as BufferedImage to the given PictureProcessor
 */
class VideoFrameAdder 
{
    private PictureProcessor processor;
    private String videoFile;

    public VideoFrameAdder(PictureProcessor processor, String videoFile)
    {
        this.processor = processor;
        this.videoFile = videoFile;
    }

    public void start() throws org.bytedeco.javacv.FrameGrabber.Exception
    {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(this.videoFile);
        Java2DFrameConverter converter = new Java2DFrameConverter();

        grabber.start();
        Frame frame = grabber.grabImage();
        while (frame != null)
        {
            BufferedImage image = converter.convert(frame);
            processor.addImage(image);
            frame = grabber.grabImage();
        }

        grabber.close();
        converter.close();
    }
}
