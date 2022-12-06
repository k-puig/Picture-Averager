package picture.averager.lib;

public class BasicPicture extends Picture {
    Pixel[][] image;

    public BasicPicture(int width, int height) {
        super(width, height);
        image = new Pixel[width][height];
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                image[col][row] = Pixel.BLACK;
            }
        }
    }

    public BasicPicture(Picture p) {
        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                image[col][row] = p.getPixel(col, row);
            }
        }
    }

    @Override
    public Pixel getPixel(int x, int y) {
        if(x >= width || y >= height)
            return Pixel.BLACK;
        return image[x][y];
    }

    public void setPixel(int x, int y, Pixel p) {
        if(x >= width || y >= height)
            return;
        image[x][y] = p;
    }
}
