package src;

import java.util.ArrayDeque;

import static src.GameLogic.plateau;

public class Tortue {
    public class Deck {
        int cartesBleues = 18;
        int cartesJaunes = 8;
        int cartesViolettes = 8;
        int lasers = 3;
    }

    int[] position = new int[2];
    int mursDePierre = 3;
    int mursDeGlace = 2;
    Deck deck = new Deck();
    ArrayDeque<String> prgm = new ArrayDeque<String>();

    public void setPosition(int[] position) {
        this.position[0] = position[0];
        this.position[1] = position[1];
    }

    public void placerMur(String typeMur, int[] coords) {
//        TODO: Vérifier si le placement de mur demandé est valide
        // La tortue dispose-t-elle d'un tel mur ?
        // Le placement est-il autorisé ?

//        Placer le mur
        switch (typeMur) {
            case "P":  // Mur de pierre
                plateau[coords[0]][coords[1]] = 'p';
                this.mursDePierre --;
                break;

            case "G":  // Mur de glace
                plateau[coords[0]][coords[1]] = 'g';
                this.mursDeGlace --;
                break;
        }
    }

    public void completerPrgm(ArrayDeque<String> cartes) {
        // TODO vérifier la validité
        String carte;
        while (!cartes.isEmpty()) {
            carte = cartes.remove();
            this.prgm.push(carte);
            switch (carte) {
                case "B":
                    this.deck.cartesBleues --;
                    break;
                case "J":
                    this.deck.cartesJaunes --;
                    break;
                case "V":
                    this.deck.cartesViolettes --;
                    break;
                case "L":
                    this.deck.lasers --;
                    break;
            }
        }
        cartes.clear();
    }

    public void executerPrgm() {
        String carte;
        while (!this.prgm.isEmpty()) {
            carte = prgm.pop();
            switch (carte) {
                case "B":
                    this.avancer();
                    break;
                case "J":
                    this.tournerAntiHoraire();
                    break;
                case "V":
                    this.tournerHoraire();
                    break;
                case "L":
                    this.lancerLaser();
                    break;
            }
        }

    }

    // Actions élémentaires des tortues
    private void avancer() {

    }

    private void tournerAntiHoraire() {

    }

    private void tournerHoraire() {

    }

    private void lancerLaser() {

    }


}
