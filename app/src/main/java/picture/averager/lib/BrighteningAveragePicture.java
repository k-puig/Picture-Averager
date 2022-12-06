package picture.averager.lib;

import java.awt.image.BufferedImage;

public class BrighteningAveragePicture extends LongExposurePicture {
    protected double[][] imageR;
    protected double[][] imageG;
    protected double[][] imageB;
    protected long imageCount;

    protected double brightnessCoefficient = 1.0;

    private double newImageCoefficient = 1.2;

    public BrighteningAveragePicture(int width, int height) {
        super(width, height);
        imageCount = 0;
        imageR = new double[width][height];
        imageG = new double[width][height];
        imageB = new double[width][height];
    }

    public BrighteningAveragePicture(Picture p) {
        super(p.getWidth(), p.getHeight());
        imageCount = 1;
        imageR = new double[width][height];
        imageG = new double[width][height];
        imageB = new double[width][height];

        if(p instanceof PictureAverager) {
            PictureAverager pa = (PictureAverager) p;
            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    imageR[col][row] = pa.getDoublePixel(col, row)[0];
                    imageG[col][row] = pa.getDoublePixel(col, row)[1];
                    imageB[col][row] = pa.getDoublePixel(col, row)[2];
                }
            }
            return;
        }

        if(p instanceof BrighteningAveragePicture) {
            BrighteningAveragePicture pa = (BrighteningAveragePicture) p;
            for(int row = 0; row < height; row++) {
                for(int col = 0; col < width; col++) {
                    imageR[col][row] = pa.getDoublePixel(col, row)[0];
                    imageG[col][row] = pa.getDoublePixel(col, row)[1];
                    imageB[col][row] = pa.getDoublePixel(col, row)[2];
                }
            }
            return;
        }

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                imageR[col][row] = p.getPixel(col, row).getRed();
                imageG[col][row] = p.getPixel(col, row).getGreen();
                imageB[col][row] = p.getPixel(col, row).getBlue();
            }
        }
    }

    // n++; mean += (newval - mean) / n
    @Override
    public void addImage(Picture pic) {
        if(pic == null) return;

        imageCount++;
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                imageR[col][row] += pic.getPixel(col, row).getRed();
                imageG[col][row] += pic.getPixel(col, row).getGreen();
                imageB[col][row] += pic.getPixel(col, row).getBlue();

                imageR[col][row] /= 2.0 / newImageCoefficient;
                imageG[col][row] /= 2.0 / newImageCoefficient;
                imageB[col][row] /= 2.0 / newImageCoefficient;
            }
        }
    }

    @Override
    public void addImage(BufferedImage buf) {
        if(buf == null) return;

        imageCount++;
        int rgb;
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                rgb = buf.getRGB(col, row);
                imageR[col][row] += ((rgb & 0xFF0000) >> 16);
                imageG[col][row] += ((rgb & 0x00FF00) >> 8);
                imageB[col][row] += (rgb & 0x0000FF);

                imageR[col][row] /= 2.0 / newImageCoefficient;
                imageG[col][row] /= 2.0 / newImageCoefficient;
                imageB[col][row] /= 2.0 / newImageCoefficient;
            }
        }

        return;
    }

    public BrighteningAveragePicture brighten() {
        double maxBrightness = 0.0;
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                double brightness = Math.max(imageR[col][row], Math.max(imageG[col][row], imageB[col][row]));
                if(brightness > maxBrightness)
                    maxBrightness = brightness;
            }
        }
        maxBrightness = Math.min(255.0, maxBrightness);
        brightnessCoefficient = 255.0 / maxBrightness;
        return this;
    }

    public BrighteningAveragePicture brighten(boolean keepChanges) {
        if(!keepChanges) {
            BrighteningAveragePicture newPA = new BrighteningAveragePicture(this);
            return newPA.brighten();
        }

        double maxBrightness = 0.0;
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                double brightness = Math.max(imageR[col][row], Math.max(imageG[col][row], imageB[col][row]));
                if(brightness > maxBrightness)
                    maxBrightness = brightness;
            }
        }
        brightnessCoefficient = 255.0 / maxBrightness;
        return this;
    }

    // Variable cap is the brightness at which a color component will exceed 255
    public BrighteningAveragePicture brighten(double cap) {
        brightnessCoefficient = 255.0 / cap;
        return this;
    }

    public BrighteningAveragePicture brighten(double cap, boolean keepChanges) {
        if(!keepChanges) {
            BrighteningAveragePicture newPA = new BrighteningAveragePicture(this);
            return newPA.brighten(cap);
        }
        brightnessCoefficient = 255.0 / cap;
        return this;
    }

    public double getBrightnessCoefficient() {
        return brightnessCoefficient;
    }
    
    @Override
    public long getImageCount() {
        return imageCount;
    }

    @Override
    public Pixel getPixel(int x, int y) {
        if(imageCount == 0 || x >= width || y >= height)
            return Pixel.BLACK;
        
        Pixel p = new Pixel(
            (short) Math.min(brightnessCoefficient * imageR[x][y], 255.0),
            (short) Math.min(brightnessCoefficient * imageG[x][y], 255.0),
            (short) Math.min(brightnessCoefficient * imageB[x][y], 255.0)
        );

        return p;
    }

    public double[] getDoublePixel(int x, int y) {
        double[] pix = new double[3];
        if(x >= width || y >= height)
            return pix;
        pix[0] = Math.min(brightnessCoefficient * imageR[x][y], 255.0);
        pix[1] = Math.min(brightnessCoefficient * imageG[x][y], 255.0);
        pix[2] = Math.min(brightnessCoefficient * imageB[x][y], 255.0);
        return pix;
    }
}
