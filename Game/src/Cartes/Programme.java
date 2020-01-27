package src.Cartes;

import java.util.ArrayDeque;

public class Programme {
    private ArrayDeque<Carte> programme = new ArrayDeque();

    public boolean empty() {
        return programme.isEmpty();
    }

    public ArrayDeque<Carte> getProgramme() {
        return programme;

    }

    public void enfilerCarte(Carte carte) {
        this.programme.add(carte);
    }

    public Carte defilerCarte(boolean bug) {
        if (!bug) return this.programme.remove();
        return this.programme.removeLast();  // Si bug, on lit le programme à l'envers
    }
}
