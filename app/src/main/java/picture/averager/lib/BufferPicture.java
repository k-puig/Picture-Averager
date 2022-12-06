package picture.averager.lib;

import java.awt.image.BufferedImage;

public class BufferPicture extends Picture {
    BufferedImage buf;

    public BufferPicture(BufferedImage buf) {
        this.buf = buf;
        width = buf.getWidth();
        height = buf.getHeight();
    }

    public int getRGB(int x, int y) {
        if(x >= width || y >= height)
            return 0;
            
        return buf.getRGB(x, y);
    }

    @Override
    public Pixel getPixel(int x, int y) {
        if(x >= width || y >= height)
            return Pixel.BLACK;
        
        return new Pixel( 
            ((short) ((buf.getRGB(x, y) & 0xFF0000) >> 16)),
            ((short) ((buf.getRGB(x, y) & 0x00FF00) >> 8)),
            ((short) (buf.getRGB(x, y) & 0x0000FF))
        );
    }
}
