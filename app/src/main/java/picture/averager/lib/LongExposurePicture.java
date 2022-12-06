package picture.averager.lib;

import java.awt.image.BufferedImage;

abstract public class LongExposurePicture extends Picture {
    public LongExposurePicture(int width, int height) {
        super(width, height);
    }

    abstract public void addImage(Picture pic);
    abstract public void addImage(BufferedImage buf);

    public long getImageCount() {
        return 1;
    }
}
