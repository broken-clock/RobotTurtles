package src.Cartes;

import java.util.HashMap;

public class Deck {
    HashMap<Carte, Integer> deck = new HashMap();

    public Deck() {
        deck.put(new Carte(TypeCarte.CARTE_BLEUE), 18);
        deck.put(new Carte(TypeCarte.CARTE_JAUNE), 8);
        deck.put(new Carte(TypeCarte.CARTE_VIOLETTE), 8);
        deck.put(new Carte(TypeCarte.LASER), 3);
    }
}
