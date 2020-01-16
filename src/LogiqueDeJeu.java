package src;

import src.Cartes.Carte;
import src.Cartes.TypeCarte;
import src.Interface.*;
import src.Tuiles.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class LogiqueDeJeu {
    Interface monInterface;
    private int nombreJoueurs;
    private ArrayList<Integer> ordreJoueurs;
    private String modeJeu;
    private boolean modeBug;
    private int nombreJoueursGagne = 0;
    private ArrayList<Joueur> joueurs = new ArrayList();
    private ArrayList<Position> positionsInitialesJoueurs = new ArrayList();
    ArrayList<Joyau> joyaux = new ArrayList();
    private Plateau plateau = new Plateau();
    private Joueur joueurCourant;
    private boolean gameOver;

    public int getNombreJoueurs() {
        return this.nombreJoueurs;
    }

    public String getModeJeu() {
        return modeJeu;
    }

    public void setModeJeu(String modeJeu) {
        this.modeJeu = modeJeu;
    }

    public boolean isModeBug() {
        return modeBug;
    }

    public void setModeBug(boolean modeBug) {
        this.modeBug = modeBug;
    }

    public int getNombreJoueursGagne() {
        return nombreJoueursGagne;
    }

    public void setNombreJoueursGagne(int nombreJoueursGagne) {
        this.nombreJoueursGagne = nombreJoueursGagne;
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public void setJoueurCourant(Joueur joueurCourant) {
        this.joueurCourant = joueurCourant;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    private void initialiserPositionsPlateauOrdrepassage() {
        // En fonction du nombre de joueurs, initialiser les positions des tortues et les joyaux
        Position positionDepart;
        switch (this.nombreJoueurs) {
            case 2:
            default:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(7, 2, Orientations.RIGHT));
                this.positionsInitialesJoueurs.add(new Position(0, 5, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
//                System.out.println(this.joueurs.get(0).getTortue().positionDepart.y);

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
//                System.out.println(this.joueurs.get(1).getTortue().positionDepart.y);

                // Créer les joyaux et définir leur position
                this.joyaux.add(new Joyau());
                this.joyaux.get(0).setPosition(7, 3, null);
                break;

            case 3:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(0, 0, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 3, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 6, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.getJoueurs().get(2).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(2).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 3; i++) {
                    this.joyaux.add(new Joyau());
                }
                this.joyaux.get(0).setPosition(7, 0, null);
                this.joyaux.get(1).setPosition(7, 3, null);
                this.joyaux.get(2).setPosition(7, 6, null);
                break;

            case 4:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(0, 0, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 2, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 5, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 7, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.getJoueurs().get(2).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(2).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(3);
                this.getJoueurs().get(3).tortue.setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(3).tortue.setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 2; i++) {
                    this.joyaux.add(new Joyau());
                }
                this.joyaux.get(0).setPosition(7, 1, null);
                this.joyaux.get(1).setPosition(7, 6, null);
                break;
        }

        // Initialisation du plateau à partir des objets créés précédemment
        this.setPlateau(new Plateau());
        this.getPlateau().initPlateau(this);

        // Génération de l'ordre de passage des joueurs
        int focusJoueur = this.initFocusJoueur();  // Choisit au hasard le joueur qui jouera en premier
        this.ordreJoueurs = new ArrayList();
        this.ordreJoueurs.add(focusJoueur);
        for (int i = focusJoueur + 1; i < nombreJoueurs; i++) this.ordreJoueurs.add(i);
        for (int i = 0; i < focusJoueur; i++) this.ordreJoueurs.add(i);
    }

    private void initialiserAttributsJoueurs(int i) {
        this.getJoueurs().get(i).reInitCartes();
        this.getJoueurs().get(i).mursDePierre = 3;
        this.getJoueurs().get(i).mursDeGlace = 2;
        this.getJoueurs().get(i).getCartesMain().tirerCartesDuDeck(this.getJoueurs().get(i), 5);
        // Pour le mode avec cartes bug
        this.getJoueurs().get(i).carteBug = this.isModeBug();
        this.getJoueurs().get(i).subiBug = false;
    }

    void initialiserPartie() {
        // Choix du type d'interface
        this.monInterface = new InterfaceConsole();
        this.nombreJoueurs = this.monInterface.demanderNombreJoueurs();
        this.setModeJeu(this.monInterface.demanderModeJeu());
        this.setModeBug(this.monInterface.demanderModeCarteBug());

        // Création du nombre adéquat de joueurs et initialisation pour chaque joueur de ses obstacles disponibles et de ses cartesMain initiales
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.getJoueurs().add(new Joueur(this));
            this.getJoueurs().get(i).setNumeroJoueur(i);
            this.getJoueurs().get(i).tortue.setNumeroJoueur(this.getJoueurs().get(i).getNumeroJoueur());
            this.initialiserAttributsJoueurs(i);
        }
        this.initialiserPositionsPlateauOrdrepassage();
    }

    private void reInitialiserPartie() {
        // On refait uniquement les initialisations nécessaires pour lancer une nouvelle manche
        this.setNombreJoueursGagne(0);
        this.setGameOver(false);
        this.initialiserPositionsPlateauOrdrepassage();

        // Réinitialisation attributs (cartes, obstacles) de chaque joueur
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.initialiserAttributsJoueurs(i);
        }
    }

    private void jouerManche() {
        while (!this.isGameOver()) {
            for (int focusJoueur : this.ordreJoueurs) {
                if (this.isGameOver()) break;
                System.out.println("focusJoueur: " + focusJoueur);
                monInterface.afficherPlateau(this);
                setJoueurCourant(this.getJoueurs().get(focusJoueur));
                getJoueurCourant().action = this.monInterface.demanderAction(this);
                switch (getJoueurCourant().action) {
                    case "P":  // Compléter le programme
                        this.monInterface.afficherCartesMain(this);
                        boolean continuerAjouterCartes = true;
                        while (!getJoueurCourant().getCartesMain().empty() && continuerAjouterCartes) {
                            String carteStr = this.monInterface.demanderCarteAAjouterAProgramme();
                            TypeCarte typeCarte = TypeCarte.LASER;  // Placeholder
                            switch (carteStr) {
                                case "B":
                                    typeCarte = TypeCarte.CARTE_BLEUE;
                                    break;
                                case "J":
                                    typeCarte = TypeCarte.CARTE_JAUNE;
                                    break;
                                case "V":
                                    typeCarte = TypeCarte.CARTE_VIOLETTE;
                                    break;
                                case "L":
                                    typeCarte = TypeCarte.LASER;
                                    break;
                                case "none":
                                    continuerAjouterCartes = false;
                            }

                            if (continuerAjouterCartes) {
                                Carte carte = getJoueurCourant().getCartesMain().retirerCarte(typeCarte);
                                getJoueurCourant().completerPrgm(carte);
                            }
                        }
                        this.monInterface.afficherCartesMain(this);
                        this.monInterface.afficherProgramme(this);
                        break;

                    case "M":  // Construire un mur
                        Obstacle obstacle;
                        boolean murPlaceOk;
                        do {
                            String typeObstacle = this.monInterface.demanderTypeObstacleAPlacer();
                            int[] coordsObstacle = this.monInterface.demanderCoordsObstacleAPlacer();
                            obstacle = new Obstacle(typeObstacle, coordsObstacle);
                            murPlaceOk = getJoueurCourant().placerMur(this, obstacle);
                        } while (!murPlaceOk);
                        break;

                    case "E":  // Exécuter le programme
                        this.getJoueurCourant().executerPrgm(this);
                        break;

                    case "B":  // Utiliser sa carte bug
                        int numeroJoueurCibleBug = this.monInterface.demanderCibleCarteBug(this);
                        if (!getJoueurCourant().carteBug)
                            this.monInterface.afficherMessage("Refusé: vous n'avez plus de carte bug");
                        else {
                            getJoueurCourant().carteBug = false;  // Le joueur courant a consommé sa carte bug
                            this.getJoueurs().get(numeroJoueurCibleBug).subirBug();  // Le joueur cible subit les effets de la carte bug ajoutée à son programme
                        }
                        break;
                }
                if (this.isGameOver()) break;
                this.getJoueurCourant().choixDefausse = this.monInterface.demanderChoixDefausse();
                this.getJoueurCourant().terminerTour();
                this.monInterface.afficherCartesMain(this);
            }
        }
    }

    void lancerPartie() {
        this.setGameOver(false);
        switch (this.getModeJeu()) {
            case "normal":
                this.jouerManche();
                break;
            case "3àlasuite":
                for (int i = 0; i < 3; i++) {
                    this.jouerManche();
                    this.monInterface.afficherFinManche(this, i);
                    this.reInitialiserPartie();
                }
                // Calcul du classement de chaque joueur en fonction de son nombre de points gagné durant les 3 manches
                this.getJoueurs().sort(Comparator.comparing(Joueur::getScore));
                for (int i = 0; i < this.nombreJoueurs; i++) {
                    this.getJoueurs().get(i).setClassement(this.nombreJoueurs - i);
                }
                break;
        }
        this.monInterface.afficherResultats(this);
    }

    private int initFocusJoueur() {
        return ThreadLocalRandom.current().nextInt(0, this.nombreJoueurs);
    }
}
