package srcIA.src;

public class Case {
    private int x;
    private int y;
    private String contenu;

    public Case(int x_, int y_, String contenu_) {
        x = x_;
        y = y_;
        contenu = contenu_;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getContenu() {
        return this.contenu == null ? "." : this.contenu;
    }
}
