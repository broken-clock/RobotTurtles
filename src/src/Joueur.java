package src;

import src.Cartes.*;
import src.Tuiles.*;

public class Joueur {
    private int numeroJoueur;
    public int classement;
    Tortue tortue = new Tortue();
    public Deck deck = new Deck();
    public CartesMain cartesMain = new CartesMain();
    public Programme programme = new Programme();
    public int mursDePierre;
    public int mursDeGlace;
    String action;  // Action demandée par le joueur à chaque tour de jeu
    boolean choixDefausse;  // Le joueur veut-il défausser sa main  et re-piocher 5 cartes ? (Demandé à la fin de chaque tour)

    public Joueur(int nombreJoueurs) {
        this.classement = nombreJoueurs;  // Classement du dernier joueur. Si ce joueur ne finit pas en dernier, on mettra à jour cet attribut
    }

    public int getNumeroJoueur() {
        return this.numeroJoueur;
    }

    public void setNumeroJoueur(int i) {
        this.numeroJoueur = i;
    }

    public Tortue getTortue() {
        return this.tortue;
    }

    public boolean placerMur(LogiqueDeJeu logiqueDeJeu, Obstacle obstacle) {
        switch (obstacle.getTypeObstacle()) {
            case "P":  // Mur de pierre
                if (this.mursDePierre <= 0) {
                    logiqueDeJeu.monInterface.afficherMessage("Vous ne disposez pas d'un tel obstacle");
                    return false;
                } else if (logiqueDeJeu.plateau.getCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1]) != null) {  // Si la case demandée est déjà occupée
                    logiqueDeJeu.monInterface.afficherMessage("La case demandée est déjà occupée");
                    return false;
                } else if (logiqueDeJeu.plateau.placementBloquant(obstacle.getCoordsObstacle())) {
                    logiqueDeJeu.monInterface.afficherMessage("Placer un obstacle ici bloquerait l'accès à un joyau");
                    return false;
                }
                // Le placement du mur demandé est valide
                logiqueDeJeu.plateau.setCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1], "p");
                this.mursDePierre--;
                return true;

            case "G":  // Mur de glace
                if (this.mursDeGlace <= 0) {
                    logiqueDeJeu.monInterface.afficherMessage("Vous ne disposez pas d'un tel obstacle");
                    return false;
                } else if (logiqueDeJeu.plateau.getCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1]) != null) {  // Si la case demandée est déjà occupée
                    logiqueDeJeu.monInterface.afficherMessage("Il n'est pas possible de placer un obstacle à cet endroit");
                    return false;
                }
                // Le placement du mur demandé est valide
                logiqueDeJeu.plateau.setCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1], "g");
                this.mursDeGlace--;
                return true;
        }
        return false;
    }

    public void completerPrgm(Carte carte) {
        this.programme.enfilerCarte(carte);
    }

    public void executerPrgm(LogiqueDeJeu logiqueDeJeu) {
        Carte carte;
        while (!this.programme.empty()) {
            carte = this.programme.defilerCarte();
            System.out.print("On exécute l'instruction: ");
            System.out.println(carte.getTypeCarte());
            switch (carte.getTypeCarte()) {
                case CARTE_BLEUE:
                    this.tortue.avancer(logiqueDeJeu);
                    break;
                case CARTE_JAUNE:
                    this.tortue.tournerAntiHoraire(logiqueDeJeu);
                    break;
                case CARTE_VIOLETTE:
                    this.tortue.tournerHoraire(logiqueDeJeu);
                    break;
                case LASER:
                    this.tortue.lancerLaser(logiqueDeJeu);
                    break;
            }
        }
    }

    public void terminerTour() {
        if (this.choixDefausse) {
            this.cartesMain.viderCartesMain(this);
            this.cartesMain.tirerCartesDuDeck(this, 5);
        }
    }
}
