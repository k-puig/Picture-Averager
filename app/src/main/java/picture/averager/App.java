package picture.averager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import picture.averager.lib.*;

public class App {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        if(args.length == 0 || args[0].equals("--help") || args[0].equals("-h")) {
            printHelp();
            return;
        }

        LongExposureType expType = LongExposureType.AVERAGE;
        boolean doBrighten = false;
        boolean doBrightenInteractive = false;
        double brightenNumber = -1;
        int threadCount = 1;

        String outputName = "a";
        String inputName = "";

        try {
            for(int i = 0; i < args.length; i++) {
                if(args[i].equals("--average")) 
                    expType = LongExposureType.AVERAGE;
                else if(args[i].equals("--altaverage"))
                    expType = LongExposureType.ALTAVERAGE;
                else if(args[i].equals("--brightest"))
                    expType = LongExposureType.BRIGHTEST;
                else if(args[i].equals("--darkest"))
                    expType = LongExposureType.DARKEST;
                else if(args[i].startsWith("--brighten")) {
                    doBrighten = true;
                    if(args[i].startsWith("--brighten=")) {
                        try {
                            brightenNumber = Integer.parseInt(args[i].substring("--brighten=".length()));
                        } catch(NumberFormatException nfe) {
                            if(args[i].substring("--brighten=".length()).equals("interactive"))
                                doBrightenInteractive = true;
                        }
                    }
                }
                else if(args[i].equals("-o") || args[i].equals("--output")) 
                    outputName = args[++i];
                else if(args[i].equals("-t") || args[i].equals("--threads"))
                    threadCount = Integer.parseInt(args[++i]);
                else {
                    inputName += args[i] + " ";
                }
            }
        } catch(Exception e) {
            printHelp();
            return;
        }

        if(expType == LongExposureType.ALTAVERAGE) {
            System.out.println("Thread count ignored for picture type altaverage");
            threadCount = 1;
        }

        int width = 0;
        int height = 0;
        // Cut out trailing spaces from input file name
        while(inputName.charAt(inputName.length() - 1) == ' ') {
            inputName = inputName.substring(0, inputName.length() - 1);
        }
        File input = new File(inputName);
        if(input.exists()) {
            if(input.isFile()) {
                VideoPicture tempVP = new VideoPicture(input.getAbsolutePath());
                width = tempVP.getWidth();
                height = tempVP.getHeight();
                threadCount = Math.min(tempVP.totalFrames(), threadCount);
            }
            else if(input.isDirectory()) {
                FilePicture tempFP;
                for(File f : input.listFiles()) {
                    tempFP = new FilePicture(f.getAbsolutePath());
                    if(tempFP != null) {
                        width = tempFP.getWidth();
                        height = tempFP.getHeight();
                        break;
                    }
                }
                threadCount = Math.min(input.listFiles().length, threadCount);
            }

            if(width <= 0 || height <= 0) {
                printHelp();
                return;
            }
        }
        else {
            System.out.println(inputName);
            printHelp();
            return;
        }

        FrameScheduler frameScheduler = new FrameScheduler(input.getAbsolutePath(), threadCount);
        frameScheduler.start();
        
        AppThread[] threads = new AppThread[threadCount];
        for(int i = 0; i < threadCount; i++) {
            threads[i] = new AppThread(frameScheduler, width, height, expType, doBrighten, doBrightenInteractive, brightenNumber, threadCount, i, input);
            threads[i].start();
        }

        frameScheduler.join();

        System.out.println("Done!");

        // Done?
        LongExposurePicture finalLep;
        switch(expType) {
            default:
            case AVERAGE:
                finalLep = new PictureAverager(width, height);
                break;
            
            case ALTAVERAGE:
                finalLep = new BrighteningAveragePicture(width, height);
                break;

            case BRIGHTEST: 
                finalLep = new BrightestPicture(width, height);
                break;

            case DARKEST: 
                finalLep = new DarkestPicture(width, height);
                break;
        }

        System.out.println("Combining images");

        for(int i = 0; i < threadCount; i++) {
            finalLep.addImage(threads[i].getLep());
        }

        if(doBrighten) {
            if(doBrightenInteractive) {
                Scanner myScanner = new Scanner(System.in);
                String line = "";
                while(!line.equals("q")) {
                    System.out.print("Enter q (quit), m (max bright), or a number (1-255 preferred, 0-INF actual): ");
                    line = myScanner.nextLine();
                    if(line.equals("m")) {
                        new FilePicture(new PictureAverager(finalLep).brighten()).saveImage(outputName);
                        System.out.println("Saved to " + outputName + ".png");
                    }
                    else {
                        try {
                            brightenNumber = Double.parseDouble(line);
                            new FilePicture(new PictureAverager(finalLep).brighten(brightenNumber)).saveImage(outputName);
                            System.out.println("Saved to " + outputName + ".png");
                        } catch(NumberFormatException nfe) {

                        }
                    }
                }
                myScanner.close();
            }
            else {
                if(brightenNumber >= 0) {
                    finalLep = new PictureAverager(finalLep).brighten(brightenNumber);
                }
                else {
                    finalLep = new PictureAverager(finalLep).brighten();
                }
                new FilePicture(finalLep).saveImage(outputName);
                System.out.println("Saved to " + outputName + ".png");
            }
        }
        else {
            new FilePicture(finalLep).saveImage(outputName);
            System.out.println("Saved to " + outputName + ".png");
        }
    }

    private static void printHelp() throws NullPointerException {
        
        System.out.println("arg yarrrr!");
        System.out.println("");
        throw new NullPointerException();
    }
}
