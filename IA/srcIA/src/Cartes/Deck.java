package srcIA.src.Cartes;

import srcIA.src.Joueur;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Deck {
    private ArrayList<Carte> deck = new ArrayList();

    public Deck() {
        for (int i = 0; i < 18; i++) {  // 18
            deck.add(new Carte(TypeCarte.CARTE_BLEUE));
        }
        for (int i = 0; i < 8; i++) {  //8
            deck.add(new Carte(TypeCarte.CARTE_JAUNE));
        }
        for (int i = 0; i < 8; i++) {  //8
            deck.add(new Carte(TypeCarte.CARTE_VIOLETTE));
        }
        for (int i = 0; i < 3; i++) {  // 3
            deck.add(new Carte(TypeCarte.LASER));
        }
    }

    Carte donnerUneCarte(Joueur joueur) {
        // Si le deck est vide, on le re-génère, i.e. on mélange la défausse
        if (joueur.getDeck().deck.isEmpty()) {
            System.out.println("On régénère votre deck à partie de la défausse...");
            joueur.setDeck(new Deck());
        }

        // Tirer une carte au hasard parmi les cartes présentes dans le deck
        int indiceCarte = ThreadLocalRandom.current().nextInt(joueur.getDeck().deck.size());

        // Supprimer cette carte du deck avant de la renvoyer
        Carte carte = joueur.getDeck().deck.get(indiceCarte);
        joueur.getDeck().deck.remove(indiceCarte);
        return carte;
    }
}
