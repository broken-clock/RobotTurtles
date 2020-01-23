package src;

import src.Cartes.*;
import src.Interface.InterfaceConsole;
import src.Tuiles.*;

public class Joueur {
    private int numeroJoueur;
    private int classement;
    private boolean carteBug;
    private boolean subiBug;
    private int score = 0;
    private Tortue tortue = new Tortue();
    private Deck deck = new Deck();
    private CartesMain cartesMain = new CartesMain();
    private Programme programme = new Programme();
    private int mursDePierre;
    private int mursDeGlace;
    private String action;  // Action demandÃ©e par le joueur Ã  chaque tour de jeu
    private boolean fini;

    public Joueur(LogiqueDeJeu logiqueDeJeu) {
        this.fini = false;
        this.setClassement(logiqueDeJeu.getNombreJoueurs());  // Classement du dernier joueur. Si ce joueur ne finit pas en dernier, on mettra Ã  jour cet attribut
    }

    public int getClassement() {
        return classement;
    }

    public void setClassement(int classement) {
        this.classement = classement;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public CartesMain getCartesMain() {
        return cartesMain;
    }

    public void setCartesMain(CartesMain cartesMain) {
        this.cartesMain = cartesMain;
    }

    public Programme getProgramme() {
        return programme;
    }

    public void setProgramme(Programme programme) {
        this.programme = programme;
    }

    public int getScore() {
        return this.score;
    }

    public void increaseScore(int i) {
        this.score += i;
    }

    public int getNumeroJoueur() {
        return this.numeroJoueur;
    }

    void setNumeroJoueur(int i) {
        this.numeroJoueur = i;
    }

    public Tortue getTortue() {
        return this.tortue;
    }

    public boolean isCarteBug() {
        return carteBug;
    }

    public void setCarteBug(boolean carteBug) {
        this.carteBug = carteBug;
    }

    public boolean isSubiBug() {
        return subiBug;
    }

    public void setSubiBug(boolean subiBug) {
        this.subiBug = subiBug;
    }

    public int getMursDePierre() {
        return mursDePierre;
    }

    public void setMursDePierre(int mursDePierre) {
        this.mursDePierre = mursDePierre;
    }

    public int getMursDeGlace() {
        return mursDeGlace;
    }

    public void setMursDeGlace(int mursDeGlace) {
        this.mursDeGlace = mursDeGlace;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isFini() {
        return fini;
    }

    public void setFini(boolean fini) {
        this.fini = fini;
    }

    void reInitCartes() {
        this.setDeck(new Deck());
        this.setCartesMain(new CartesMain());
        this.setProgramme(new Programme());
    }

    boolean placerMur(LogiqueDeJeu logiqueDeJeu, Obstacle obstacle) {
        String messagePossedePasObstacle = "Refuse: vous ne disposez pas d'un tel obstacle";
        if (obstacle.getTypeObstacle().equals("p")) {
            if (this.getMursDePierre() <= 0) {
                logiqueDeJeu.getMonInterface().afficherMessage(messagePossedePasObstacle);
                return false;
            }
        } else if (obstacle.getTypeObstacle().equals("g")) {
            if (this.getMursDeGlace() <= 0) {
                logiqueDeJeu.getMonInterface().afficherMessage(messagePossedePasObstacle);
                return false;
            }
        }

        if (logiqueDeJeu.getPlateau().getCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1]) != null) {  // Si la case demandÃ©e est dÃ©jÃ  occupÃ©e
            logiqueDeJeu.getMonInterface().afficherMessage("Refuse: la case demandee est deja  occupee");
            return false;
        }
        // Les murs de glace sont destructibles donc ne peuvent pas bloquer l'acces a un joyau
        else if (!obstacle.getTypeObstacle().equals("g") && logiqueDeJeu.getPlateau().placementBloquant(logiqueDeJeu, obstacle.getCoordsObstacle())) {
            logiqueDeJeu.getMonInterface().afficherMessage("Refuse: placer un obstacle ici bloquerait l'acces a un joyau");
            return false;
        }

        // Le placement du mur demande est valide
        System.out.println("type obs: " + obstacle.getTypeObstacle());
        if (obstacle.getTypeObstacle().equals("p")) {
            logiqueDeJeu.getPlateau().setCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1], "p");
            this.setMursDePierre(this.getMursDePierre() - 1);
        } else if (obstacle.getTypeObstacle().equals("g")) {
            logiqueDeJeu.getPlateau().setCase(obstacle.getCoordsObstacle()[0], obstacle.getCoordsObstacle()[1], "g");
            this.setMursDeGlace(this.getMursDeGlace() - 1);
        }
        return true;
    }

    void completerPrgm(Carte carte) {
        this.getProgramme().enfilerCarte(carte);
    }

    void executerPrgm(LogiqueDeJeu logiqueDeJeu) {
        Carte carte;
        while (!this.getProgramme().empty() && !this.isFini()) {
            carte = this.getProgramme().defilerCarte(this.isSubiBug());
            System.out.print("On execute l'instruction: ");
            System.out.println(carte.getTypeCarte());
            switch (carte.getTypeCarte()) {
                case CARTE_BLEUE:
                    this.getTortue().avancer(logiqueDeJeu);
                    break;
                case CARTE_JAUNE:
                    this.getTortue().tournerAntiHoraire(logiqueDeJeu);
                    break;
                case CARTE_VIOLETTE:
                    this.getTortue().tournerHoraire(logiqueDeJeu);
                    break;
                case LASER:
                    this.getTortue().lancerLaser(logiqueDeJeu);
                    break;
            }
            if (logiqueDeJeu.getMonInterface().getTypeInterface().equals("Affichage")) logiqueDeJeu.getMonInterface().actualiser();
        	logiqueDeJeu.getMonInterface().stopLaser();
        }
        logiqueDeJeu.getMonInterface().afficherPlateau(logiqueDeJeu);
    }

    void subirBug() {
        System.out.println("Le joueur " + this.getNumeroJoueur() + " subit le bug");
        this.setSubiBug(true);
    }

    void terminerTour() {
        // Si besoin, remplir cartesMain jusqu'a  avoir 5 cartes
        this.getCartesMain().tirerCartesDuDeck(this, 5 - this.getCartesMain().getCartesMain().size());
    }
}