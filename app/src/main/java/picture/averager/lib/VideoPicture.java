package picture.averager.lib;

import java.awt.image.BufferedImage;

import java.io.File;

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;

public class VideoPicture extends Picture {
    private long curFrameIndex;
    private int frameCount;
    private BasicPicture curFrame;
    private FFmpegFrameGrabber frameGrabber;

    public VideoPicture(String filename) {
        curFrameIndex = -1;
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
        File video = new File(filename);
        frameGrabber = new FFmpegFrameGrabber(video.getAbsoluteFile());

        try {
            frameGrabber.setOption("-loglevel", "AV_LOG_QUIET");
            frameGrabber.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        width = frameGrabber.getImageWidth();
        height = frameGrabber.getImageHeight();

        curFrame = new BasicPicture(width, height);

        frameCount = frameGrabber.getLengthInVideoFrames();

        int i = frameCount;
        while(i >= 0 && getFrame(i) == null) {
            frameCount--;
            i--;
        }
        frameCount = Math.max(0, frameCount);

        try {
            frameGrabber.setFrameNumber(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Picture getFrame(int frame) {
        frame = Math.min(frame, frameCount - 1);
        if(frame == curFrameIndex)
            return curFrame;
        curFrameIndex = frame;
        try {
            frameGrabber.setVideoFrameNumber(frame);
            Frame videoFrame = frameGrabber.grabImage();
            Java2DFrameConverter tempJ2D = new Java2DFrameConverter();
            BufferedImage bufImg = tempJ2D.convert(videoFrame);
            tempJ2D.close();
            curFrame = new BasicPicture(width, height);
            for(int col = 0; col < width; col++) {
                for(int row = 0; row < height; row++) {
                    int px;
                    try {
                        px = bufImg.getRGB(col, row);
                    } catch(NullPointerException npe) {
                        //npe.printStackTrace();
                        return null;
                    }
                    int pxR = (px & 0xFF0000) >> 16;
                    int pxG = (px & 0x00FF00) >> 8;
                    int pxB = px & 0x0000FF;
                    curFrame.setPixel(col, row, new Pixel(pxR, pxG, pxB));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return curFrame;
    }


    public BufferedImage nextBuffer() {
        Frame videoFrame;
        try {
            videoFrame = frameGrabber.grabImage();
            Java2DFrameConverter tempJ2D = new Java2DFrameConverter();
            BufferedImage bufImg = tempJ2D.convert(videoFrame);
            tempJ2D.close();
            return bufImg;
        } catch (Exception e) {
            return null;
        }
    }

    public Picture nextFrame() {
        try {
            Frame videoFrame = frameGrabber.grabImage();
            Java2DFrameConverter tempJ2D = new Java2DFrameConverter();
            BufferedImage bufImg = tempJ2D.convert(videoFrame);
            tempJ2D.close();

            curFrame = new BasicPicture(width, height);
            for(int col = 0; col < width; col++) {
                for(int row = 0; row < height; row++) {
                    int px;
                    try {
                        px = bufImg.getRGB(col, row);
                    } catch(NullPointerException npe) {
                        //npe.printStackTrace();
                        return null;
                    }
                    int pxR = (px & 0xFF0000) >> 16;
                    int pxG = (px & 0x00FF00) >> 8;
                    int pxB = px & 0x0000FF;
                    curFrame.setPixel(col, row, new Pixel(pxR, pxG, pxB));
                }
            }

            return curFrame;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int totalFrames() {
        return frameCount;
    }

    public void close() {
        try {
            frameGrabber.stop();
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pixel getPixel(int x, int y) {
        return Pixel.BLACK;
    }
}
