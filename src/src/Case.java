package src;

import org.jetbrains.annotations.Contract;

public class Case {
	
    int x;
    int y;
    String contenu;

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getContenu() {
        return this.contenu == null ? "." : this.contenu;
    }

    public Case(int x_, int y_, String contenu_) {
        x = x_;
        y = y_;
        contenu = contenu_;
    }
}
