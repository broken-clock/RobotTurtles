package src.Cartes;

import src.Joueur;

import java.util.ArrayList;

public class CartesMain {
    public ArrayList<Carte> cartesMain = new ArrayList();

    public ArrayList<Carte> getCartesMain() {
        return cartesMain;
    }

    public boolean empty() {
        return cartesMain.isEmpty();
    }

    public void viderCartesMain(Joueur joueur) {
        joueur.cartesMain.cartesMain = new ArrayList();
    }

    public void tirerCartesDuDeck(Joueur joueur, int nombreCartes) {
        for (int i = 0; i < nombreCartes; i++) {
            joueur.cartesMain.ajouterCarte(joueur.deck.donnerUneCarte(joueur));
        }

//        // On triche sur l'initialisation des cartes main
//        // TODO enlever cette triche
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
        Carte carte = this.cartesMain.get(indexToPop);
        this.cartesMain.remove(indexToPop);
        return carte;
    }
}
