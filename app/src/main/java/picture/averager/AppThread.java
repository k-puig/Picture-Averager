package picture.averager;

import java.io.File;

import picture.averager.lib.*;

public class AppThread extends Thread {
    int width, height;
    LongExposureType expType;
    boolean doBrighten;
    boolean doBrightenInteractive;
    double brightenNumber;
    int threadCount;
    int threadIndex;
    File input;

    FrameScheduler frameScheduler;
    
    LongExposurePicture lep;

    int progress;
    int len;

    public AppThread(FrameScheduler frameScheduler, int width, int height, LongExposureType expType, boolean doBrighten, 
    boolean doBrightenInteractive, double brightenNumber, int threadCount, int threadIndex, File input) {
        this.frameScheduler = frameScheduler;
        this.width = width;
        this.height = height;
        this.expType = expType;
        this.doBrighten = doBrighten;
        this.doBrightenInteractive = doBrightenInteractive;
        this.brightenNumber = brightenNumber;
        this.threadCount = threadCount;
        this.threadIndex = threadIndex;
        this.input = input;

        this.progress = 0;

        switch(expType) {
            default:
            case AVERAGE:
                lep = new PictureAverager(width, height);
                break;
            case ALTAVERAGE:
                lep = new BrighteningAveragePicture(width, height);
                break;
            case BRIGHTEST: 
                lep = new BrightestPicture(width, height);
                break;
            case DARKEST: 
                lep = new DarkestPicture(width, height);
                break;
        }
    }

    public LongExposurePicture getLep() {
        return lep;
    }

    public int[] getProgress() {
        int[] res = new int[2];
        res[0] = progress;
        res[1] = len;
        return res;
    }

    @Override
    public void run() {
        //System.out.println("Starting " + threadIndex);
        /*if(input.exists()) {
            if(input.isFile()) {
                VideoPicture vPic = new VideoPicture(input.getAbsolutePath());
                len = vPic.totalFrames();
                System.out.println("The great barrier has been crossed in thread " + threadIndex);
                for(int i = threadIndex; i < vPic.totalFrames(); i += threadCount) {
                    lep.addImage(vPic.getFrame(i));
                    progress++;
                }
            }
            else if(input.isDirectory()) {
                len = input.listFiles().length;
                for(int i = threadIndex; i < input.listFiles().length; i += threadCount) {
                    FilePicture fPic = new FilePicture(input.listFiles()[i].getAbsolutePath());
                    lep.addImage(fPic);
                    progress++;
                }
            }
        }*/
        while(frameScheduler.isAlive()) {
            lep.addImage(frameScheduler.getFrame());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Done " + threadIndex);
    }
}
