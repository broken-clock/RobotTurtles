package src.Cartes;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Deck {
    ArrayList<Carte> deck = new ArrayList();

    public Deck() {
        for (int i = 0; i < 18; i++) {
            deck.add(new Carte(TypeCarte.CARTE_BLEUE));
        }
        for (int i = 0; i < 8; i++) {
            deck.add(new Carte(TypeCarte.CARTE_JAUNE));
        }
        for (int i = 0; i < 8; i++) {
            deck.add(new Carte(TypeCarte.CARTE_VIOLETTE));
        }
        for (int i = 0; i < 3; i++) {
            deck.add(new Carte(TypeCarte.LASER));
        }
    }

    public Carte donnerUneCarte() {
        // Tirer une carte au hasard parmi les cartes présentes dans le deck
        int indiceCarte = ThreadLocalRandom.current().nextInt(this.deck.size());

        // Supprimer cette carte du deck avant de la renvoyer
        Carte carte = this.deck.get(indiceCarte);
        this.deck.remove(indiceCarte);
        return carte;
    }
}
