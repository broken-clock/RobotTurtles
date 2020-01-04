package src;

import src.Cartes.*;
import src.Tuiles.*;

public class Joueur {
    Tortue tortue = new Tortue();
    Deck deck = new Deck();
    CartesMain cartesMain = new CartesMain();
    Programme programme = new Programme();
    int mursDePierre;
    int mursDeGlace;


//    public void placerMur(Obstacle obstacle, int x, int y) {
//        // TODO: Vérifier si le placement de mur demandé est valide:
//        //  La tortue dispose-t-elle d'un tel mur ?
//        //  Le placement est-il autorisé ?
//
//        // Placer le mur
//        switch (typeMur) {
//            case "P":  // Mur de pierre
//                plateau.setCase(coords[0], coords[1], 'p');
//                this.mursDePierre --;
//                break;
//
//            case "G":  // Mur de glace
//                plateau.setCase(coords[0], coords[1], 'g');
//                this.mursDeGlace --;
//                break;
//        }
//    }
//
//    public void completerPrgm(Programme programme, CartesMain cartesMain) {
//        // TODO vérifier la validité
//        String carte;
//        while (!cartes.isEmpty()) {
//            carte = cartes.remove();
//            this.prgm.push(carte);
//            switch (carte) {
//                case "B":
//                    this.deck.cartesBleues --;
//                    break;
//                case "J":
//                    this.deck.cartesJaunes --;
//                    break;
//                case "V":
//                    this.deck.cartesViolettes --;
//                    break;
//                case "L":
//                    this.deck.lasers --;
//                    break;
//            }
//        }
//    }
//
//    public void executerPrgm(Programme programme) {
//        String carte;
//        while (!this.prgm.isEmpty()) {
//            carte = prgm.pop();
//            switch (carte) {
//                    case "B":
//                    this.avancer();
//                    break;
//                case "J":
//                    this.tournerAntiHoraire();
//                    break;
//                case "V":
//                    this.tournerHoraire();
//                    break;
//                case "L":
//                    this.lancerLaser();
//                    break;
//            }
//        }
//
//    }
}
