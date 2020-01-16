package src;

import src.Cartes.Carte;
import src.Cartes.TypeCarte;
import src.Interface.*;
import src.Tuiles.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LogiqueDeJeu {
    public Interface monInterface;
    public static int nombreJoueurs;
    public int nombreJoueursGagne = 0;
    public static ArrayList<Joueur> joueurs = new ArrayList();
    public ArrayList<Position> positionsInitialesJoueurs = new ArrayList();
    public ArrayList<Joyau> joyaux = new ArrayList();
    public static Plateau plateau = new Plateau();
    public static int focusJoueur;
    public static char gamemode =  't';
    public static Joueur joueurCourant;
    public boolean gameOver;

    public int getNombreJoueurs() {
        return this.nombreJoueurs;
    }

    public void initialiserPartie() {
        // Choix du type d'interface
        this.monInterface = new InterfaceConsole();

//        this.nombreJoueurs = this.monInterface.demanderNombreJoueurs(this);
        this.nombreJoueurs = 4;

        // Création du nombre adéquat de joueurs et initialisation pour chaque joueur de ses obstacles disponibles et de ses cartesMain initiales
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.joueurs.add(new Joueur(this.nombreJoueurs));
            this.joueurs.get(i).setNumeroJoueur(i);
            this.joueurs.get(i).tortue.setNumeroJoueur(this.joueurs.get(i).getNumeroJoueur());
            this.joueurs.get(i).mursDePierre = 3;
            this.joueurs.get(i).mursDeGlace = 2;
            this.joueurs.get(i).cartesMain.tirerCartesDuDeck(this.joueurs.get(i), 5);
        }

        // En fonction du nombre de joueurs, initialiser les positions des tortues et les joyaux
        Position positionDepart;
        switch (this.nombreJoueurs) {
            case 2:
            default:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(7, 2, Orientations.RIGHT));
                this.positionsInitialesJoueurs.add(new Position(0, 5, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.joueurs.get(0).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(0).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);
//                System.out.println(this.joueurs.get(0).getTortue().positionDepart.y);

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.joueurs.get(1).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(1).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);
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
                this.joueurs.get(0).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(0).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.joueurs.get(1).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(1).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.joueurs.get(2).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(2).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

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
                this.joueurs.get(0).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(0).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.joueurs.get(1).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(1).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.joueurs.get(2).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(2).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

                positionDepart = this.positionsInitialesJoueurs.get(3);
                this.joueurs.get(3).tortue.setPosition(positionDepart.x, positionDepart.y, positionDepart.orientation);
                this.joueurs.get(3).tortue.setPositionDepart(positionDepart.x, positionDepart.y, positionDepart.orientation);

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 2; i++) {
                    this.joyaux.add(new Joyau());
                }
                this.joyaux.get(0).setPosition(7, 1, null);
                this.joyaux.get(1).setPosition(7, 6, null);
                break;
        }

        // Enfin, initialiser le plateau à partir des objets créés précédemment
        this.plateau = new Plateau();
        this.plateau.initPlateau(this);
    }

    public void lancerPartie() {
        this.focusJoueur = this.initFocusJoueur();  // Choisit au hasard le joueur qui jouera en premier
        this.gameOver = false;

        while (!this.gameOver) {
            for (int i = 0; i < nombreJoueurs; i++) {
                if (this.gameOver) break;
                System.out.println("focusJoueur: " + i);
                monInterface.afficherPlateau(this);
                joueurCourant = this.joueurs.get(i);
                joueurCourant.action = this.monInterface.demanderAction(this);
                switch (joueurCourant.action) {
                    case "P":  // Compléter le programme
                        this.monInterface.afficherCartesMain(this);
                        boolean continuerAjouterCartes = true;
                        while (!joueurCourant.cartesMain.empty() && continuerAjouterCartes) {
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
                                Carte carte = joueurCourant.cartesMain.retirerCarte(typeCarte);
                                joueurCourant.completerPrgm(carte);
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
                            murPlaceOk = joueurCourant.placerMur(this, obstacle);
                        } while (!murPlaceOk);
                        break;

                    case "E":  // Exécuter le programme
                        this.joueurCourant.executerPrgm(this);
                        break;
                }
                if (this.gameOver) break;
                this.joueurCourant.choixDefausse = this.monInterface.demanderChoixDefausse();
                this.joueurCourant.terminerTour();
                this.monInterface.afficherCartesMain(this);
            }
        }
        this.monInterface.afficherResultats(this);
    }

    private int initFocusJoueur() {
        return ThreadLocalRandom.current().nextInt(1, this.nombreJoueurs + 1);
    }
}
