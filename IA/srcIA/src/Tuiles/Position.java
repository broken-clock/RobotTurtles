package srcIA.src.Tuiles;

public class Position {
    private int x;
    private int y;
    private Orientations orientation;

    public Position(int _x, int _y, Orientations _orientation) {
        this.setX(_x);
        this.setY(_y);
        this.setOrientation(_orientation);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Orientations getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientations orientation) {
        this.orientation = orientation;
    }
}
