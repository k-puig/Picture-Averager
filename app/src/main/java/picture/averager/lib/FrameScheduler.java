package picture.averager.lib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class FrameScheduler extends Thread {
    private enum QueueType {
        FILES,
        VIDEO
    }

    private LinkedList<BufferedImage> queue;
    int imagesAdded;
    int imagesRemoved;
    int totalImages;
    int index;
    int maxQueueSize;
    File inputFile;
    
    boolean currentlyFilling = false;

    QueueType queueType;

    VideoPicture vPic;

    long startTime;

    public FrameScheduler(String input, int threads) throws FileNotFoundException {
        queue = new LinkedList<>();
        index = 0;
        imagesAdded = 0;
        imagesRemoved = 0;
        maxQueueSize = Math.max(threads * 3 / 2, 2);

        inputFile = new File(input);

        File inputFile = new File(input);
        if(inputFile.exists()) {
            if(inputFile.isFile()) {
                vPic = new VideoPicture(inputFile.getAbsolutePath());
                totalImages = vPic.totalFrames();
                queueType = QueueType.VIDEO;
            }
            else if(inputFile.isDirectory()) {
                totalImages = inputFile.listFiles().length;
                queueType = QueueType.FILES;
            }
        } 
        else {
            throw new FileNotFoundException("FrameScheduler input " + input + " not valid!");
        }
    }

    public synchronized BufferedImage getFrame() {
        if(currentlyFilling)
            return null;
        BufferedImage p = queue.pollLast();
        if(p != null)
            imagesRemoved++;
        return p;
    }

    @Override
    public void run() {
        // Continually run push() until length is desired length
        startTime = System.nanoTime();
        while(imagesAdded != totalImages) {
            /*System.out.println("index " + index);
            System.out.println("imagesAdded " + imagesAdded);
            System.out.println("imagesRemoved " + imagesRemoved);
            System.out.println("totalImages " + totalImages);
            System.out.println("queue length " + queue.size());
            System.out.println();*/
            System.out.print("\r");
            System.out.print("Progress:\t\t" + imagesRemoved + "/" + totalImages + " Elapsed: " + ((double) (System.nanoTime() - startTime) / 1_000_000_000) + "                              ");
            fillToMax();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print("\r");
        System.out.print("Progress:\t\t" + imagesRemoved + "/" + totalImages + " Elapsed: " + ((double) (System.nanoTime() - startTime) / 1_000_000_000) + "                              ");
    }

    private void fillToMax() {
        while(queue.size() != maxQueueSize && imagesAdded != totalImages) {
            try {
                //Picture curPic = pictureFromIndex(index++);
                BufferedImage curBuf = bufferFromIndex(index++);
                currentlyFilling = true;
                queue.push(curBuf);
                currentlyFilling = false;
                System.out.print("\r");
                System.out.print("Progress:\t\t" + imagesRemoved + "/" + totalImages + " Elapsed: " + ((double) (System.nanoTime() - startTime) / 1_000_000_000) + "                              ");
                
                imagesAdded++;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        currentlyFilling = false;
    }

    @Deprecated
    private Picture pictureFromIndex(int i) throws Exception {
        if(i == totalImages)
            throw new IndexOutOfBoundsException();
        switch(queueType) {
            default:
                throw new Exception("FrameScheduler invalid queue type");
            case VIDEO:
                return vPic.nextFrame();
            case FILES:
                return new FilePicture(inputFile.listFiles()[i].getAbsolutePath());
        }
    }

    private BufferedImage bufferFromIndex(int i) throws Exception {
        if(i == totalImages)
            throw new IndexOutOfBoundsException();
        switch(queueType) {
            default:
                throw new Exception("FrameScheduler invalid queue type");
            case VIDEO:
                return vPic.nextBuffer();
            case FILES:
                //return new FilePicture(inputFile.listFiles()[i].getAbsolutePath());
                BufferedImage bufImage = null;
                bufImage = ImageIO.read(inputFile.listFiles()[i]);
                return bufImage;
        }
    }
}
