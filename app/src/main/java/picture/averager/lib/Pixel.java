package picture.averager.lib;

public class Pixel {
    public static final Pixel RED = new Pixel(255, 0, 0);
    public static final Pixel GREEN = new Pixel(0, 255, 0);
    public static final Pixel BLUE = new Pixel(0, 0, 255);

    public static final Pixel YELLOW = new Pixel(255, 255, 0);
    public static final Pixel MAGENTA = new Pixel(255, 0, 255);
    public static final Pixel CYAN = new Pixel(255, 255, 0);

    public static final Pixel BLACK = new Pixel();
    public static final Pixel WHITE = new Pixel(255, 255, 255);

    private short red;
    private short green;
    private short blue;

    public Pixel() {
        red = 0;
        green = 0;
        blue = 0;
    }

    public Pixel(Pixel p) {
        red = p.getRed();
        green = p.getGreen();
        blue = p.getBlue();
    }

    public Pixel(short r, short g, short b) { 
        red = r;
        green = g;
        blue = b;
    }

    public Pixel(int r, int g, int b) {
        red = (short) r;
        green = (short) g;
        blue = (short) b;
    }

    public Pixel add(Pixel p) {
        Pixel newPixel = 
        new Pixel (
            this.getRed() + p.getRed(), 
            this.getGreen() + p.getGreen(), 
            this.getBlue() + p.getBlue()
        );
        
        return newPixel;
    }

    public Pixel sub(Pixel p) {
        Pixel newPixel = 
        new Pixel (
            this.getRed() - p.getRed(), 
            this.getGreen() - p.getGreen(), 
            this.getBlue() - p.getBlue()
        );
        
        return newPixel;
    }

    public Pixel div(Pixel p) {
        Pixel newPixel = 
        new Pixel (
            this.getRed() / p.getRed(), 
            this.getGreen() / p.getGreen(), 
            this.getBlue() / p.getBlue()
        );
        
        return newPixel;
    }

    public Pixel div(long p) {
        Pixel newPixel = 
        new Pixel (
            (short) (this.getRed() / p), 
            (short) (this.getGreen() / p), 
            (short) (this.getBlue() / p)
        );
        
        return newPixel;
    }

    public Pixel mult(Pixel p) {
        Pixel newPixel = 
        new Pixel (
            this.getRed() * p.getRed(), 
            this.getGreen() * p.getGreen(), 
            this.getBlue() * p.getBlue()
        );
        
        return newPixel;
    }

    public short getRed() {
        return red;
    }

    public short getGreen() {
        return green;
    }

    public short getBlue() {
        return blue;
    }

    public short[] getPixelAsArray() {
        short[] bigIntArr = new short[3];
        bigIntArr[0] = red;
        bigIntArr[1] = green;
        bigIntArr[2] = blue;
        return bigIntArr;
    }

    public int getPixelAsRGBInt() {
        int r = (int) red;
        int g = (int) green;
        int b = (int) blue;

        int color = ((r % 256) << 16) +
                    ((g % 256) << 8) +
                    (b % 256);

        return color;
    }

    // Returns number [0.0, 1.0] based on the average of all pixel values
    public float getBrightness() {
        return (red + blue + green) / 765.0f;
    }

    public static Pixel max(Pixel p1, Pixel p2) {
        if(p1.getBrightness() > p2.getBrightness())
            return p1;
        return p2;
    }

    public static Pixel max(Pixel p, Pixel...pn) {
        if(pn.length == 1)
            return max(p, pn[0]);
        
        Pixel[] newpn = new Pixel[pn.length - 1];
        for(int i = 1; i < pn.length; i++) {
            newpn[i - 1] = pn[i];
        }

        return max(max(p, pn[0]), newpn);
    }

    public static Pixel min(Pixel p1, Pixel p2) {
        if(p1.getBrightness() < p2.getBrightness())
            return p1;
        return p2;
    }

    public static Pixel min(Pixel p, Pixel...pn) {
        if(pn.length == 1)
            return min(p, pn[0]);
        
        Pixel[] newpn = new Pixel[pn.length - 1];
        for(int i = 1; i < pn.length; i++) {
            newpn[i - 1] = pn[i];
        }

        return min(min(p, pn[0]), newpn);
    }

    @Override
    public String toString() {
        return "Pixel[R:%s G:%s B:%s]".formatted(red, green, blue);
    }

}
