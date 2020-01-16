package src.Cartes;

import java.util.ArrayDeque;

public class Programme {
    public ArrayDeque<Carte> programme = new ArrayDeque();

    public boolean empty() {
        return programme.isEmpty();
    }

    public ArrayDeque<Carte> getProgramme() {
        return programme;

    }

    public void enfilerCarte(Carte carte) {
        this.programme.add(carte);
    }

    public Carte defilerCarte() {
        return this.programme.pop();
    }
}
