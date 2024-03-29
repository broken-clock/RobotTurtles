package srcIA.src.Cartes;

import srcIA.src.Joueur;

import java.util.ArrayList;

public class CartesMain {
    private ArrayList<Carte> cartesMain = new ArrayList();

    public ArrayList<Carte> getCartesMain() {
        return cartesMain;
    }

    public boolean empty() {
        return cartesMain.isEmpty();
    }

    public void tirerCartesDuDeck(Joueur joueur, int nombreCartes) {
        for (int i = 0; i < nombreCartes; i++) {
            joueur.getCartesMain().ajouterCarte(joueur.getDeck().donnerUneCarte(joueur));
        }

//        // Triche sur l'initialisation des cartes main
//        if (joueur.getNumeroJoueur() == 0) {
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_JAUNE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_BLEUE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_BLEUE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_VIOLETTE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.LASER));
//        } else if (joueur.getNumeroJoueur() == 1) {
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_VIOLETTE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_BLEUE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_BLEUE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.CARTE_BLEUE));
//            joueur.cartesMain.ajouterCarte(new Carte(TypeCarte.LASER));
//        }
    }

    private void ajouterCarte(Carte carte) {
        this.cartesMain.add(carte);
    }

    public Carte retirerCarte(TypeCarte typeCarte) {
        int indexToPop = this.cartesMain.indexOf(new Carte(typeCarte));
        Carte carte = new Carte(TypeCarte.NOT_A_CARD);  // Placeholder
        try {
            carte = this.cartesMain.get(indexToPop);
            this.cartesMain.remove(indexToPop);
        } catch (java.lang.IndexOutOfBoundsException e) {
            return new Carte(TypeCarte.NOT_A_CARD);
        }
        return carte;
    }
}
