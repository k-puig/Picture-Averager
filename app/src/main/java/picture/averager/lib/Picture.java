package picture.averager.lib;

abstract public class Picture {
    // Image of arbitrary size
    protected int width, height;

    public Picture() {
        this.width = 0;
        this.height = 0;
    }

    public Picture(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    abstract public Pixel getPixel(int x, int y);

}
