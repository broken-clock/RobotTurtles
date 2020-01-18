package src;

import src.Cartes.Carte;
import src.Cartes.TypeCarte;
import src.Interface.*;
import src.Tuiles.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class LogiqueDeJeu {
    private Affichage monInterface;
    private int nombreJoueurs;
    private ArrayList<Integer> ordreJoueurs;
    private String modeJeu;
    private boolean modeBug;
    private int nombreJoueursGagne = 0;
    private ArrayList<Joueur> joueurs = new ArrayList();
    private ArrayList<Position> positionsInitialesJoueurs = new ArrayList();
    private ArrayList<Joyau> joyaux = new ArrayList();
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

    public ArrayList<Joyau> getJoyaux() {
        return joyaux;
    }

    public Affichage getMonInterface() {
        return monInterface;
    }

    public void setMonInterface(Affichage affichage) {
        this.monInterface = affichage;
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
                this.getJoueurs().get(0).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                this.getJoyaux().add(new Joyau());
                this.getJoyaux().get(0).setPosition(7, 3, null);
                break;

            case 3:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(0, 0, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 3, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 6, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.getJoueurs().get(2).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(2).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 3; i++) {
                    this.getJoyaux().add(new Joyau());
                }
                this.getJoyaux().get(0).setPosition(7, 0, null);
                this.getJoyaux().get(1).setPosition(7, 3, null);
                this.getJoyaux().get(2).setPosition(7, 6, null);
                break;

            case 4:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(0, 0, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 2, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 5, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 7, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.getJoueurs().get(2).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(2).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(3);
                this.getJoueurs().get(3).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(3).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 2; i++) {
                    this.getJoyaux().add(new Joyau());
                }
                this.getJoyaux().get(0).setPosition(7, 1, null);
                this.getJoyaux().get(1).setPosition(7, 6, null);
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
        this.getJoueurs().get(i).setMursDePierre(3);
        this.getJoueurs().get(i).setMursDeGlace(2);
        this.getJoueurs().get(i).getCartesMain().tirerCartesDuDeck(this.getJoueurs().get(i), 5);
        // Pour le mode avec cartes bug
        this.getJoueurs().get(i).setCarteBug(this.isModeBug());
        this.getJoueurs().get(i).setSubiBug(false);
    }

    void initialiserPartie() {
        // Choix du type d'interface
        this.setMonInterface(new Affichage());
        Parametres parametre = this.getMonInterface().parametresMenu();
        this.nombreJoueurs = parametre.getNbJoueurs();
        this.setModeJeu(parametre.getModeJeu());
        this.setModeBug(parametre.getModeBug());

        // Création du nombre adéquat de joueurs et initialisation pour chaque joueur de ses obstacles disponibles et de ses cartesMain initiales
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.getJoueurs().add(new Joueur(this));
            this.getJoueurs().get(i).setNumeroJoueur(i);
            this.getJoueurs().get(i).getTortue().setNumeroJoueur(this.getJoueurs().get(i).getNumeroJoueur());
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
                setJoueurCourant(this.getJoueurs().get(focusJoueur));
            	this.getMonInterface().afficherPlateauJeu(this);
                getJoueurCourant().setAction(this.getMonInterface().demanderAction());
                switch (getJoueurCourant().getAction()) {
                    case "P":  // Compléter le programme
                        this.getMonInterface().afficherCartesMain("compl�ter le programme",this.getJoueurCourant().getCartesMain().getCartesMain());
                        boolean continuerAjouterCartes = true;
                        while (continuerAjouterCartes) {

                            String carteStr = this.getMonInterface().selectionnerCarte(this.getJoueurCourant().getCartesMain().getCartesMain());
                            System.out.println(carteStr);

                            TypeCarte typeCarte = TypeCarte.LASER;  // Placeholder
                            switch (carteStr) {
                                case "CARTE_BLEUE":
                                    typeCarte = TypeCarte.CARTE_BLEUE;
                                    System.out.println(1);
                                    break;
                                case "CARTE_JAUNE":
                                    typeCarte = TypeCarte.CARTE_JAUNE;
                                    System.out.println(2);

                                    break;
                                case "CARTE_VIOLETTE":
                                    typeCarte = TypeCarte.CARTE_VIOLETTE;
                                    System.out.println(3);

                                    break;
                                case "LASER":
                                    typeCarte = TypeCarte.LASER;            
                                    System.out.println(4);

                                    break;
                                case "NOT_A_CARD":
                                    System.out.println(5);

                                    continuerAjouterCartes = false;
                            }

                            if (continuerAjouterCartes) {
                                Carte carte = getJoueurCourant().getCartesMain().retirerCarte(typeCarte);
                                getJoueurCourant().completerPrgm(carte);
                            }
                        }
             //a voir           this.getMonInterface().afficherProgramme(this);
                        break;

                    case "M":  // Construire un mur
                        Obstacle obstacle;
                        boolean murPlaceOk;
                        do {
                            obstacle = this.getMonInterface().demanderObstacleAPlacer();
                            System.out.println(obstacle.getCoordsObstacle()[0] +obstacle.getCoordsObstacle()[1] );

                            murPlaceOk = getJoueurCourant().placerMur(this, obstacle);
                        } while (!murPlaceOk);
                        break;

                    case "E":  // Exécuter le programme

                        this.getJoueurCourant().executerPrgm(this);
                        break;

         //           case "B":  // Utiliser sa carte bug
         //               int numeroJoueurCibleBug = this.getMonInterface().demanderCibleCarteBug(this);
        //                if (!getJoueurCourant().isCarteBug())
        //                    this.getMonInterface().afficherMessage("Refusé: vous n'avez plus de carte bug");
        //                else {
        //                    getJoueurCourant().setCarteBug(false);  // Le joueur courant a consommé sa carte bug
       //                     this.getJoueurs().get(numeroJoueurCibleBug).subirBug();  // Le joueur cible subit les effets de la carte bug ajoutée à son programme
       //                 }
       //                 break;
                }
                if (this.isGameOver()) break;
                System.out.println("abcdfinsess");

                boolean continuerDefausserCartes = true;
                this.getMonInterface().afficherCartesMain("choissisez les cartes � d�fausser",this.getJoueurCourant().getCartesMain().getCartesMain());
                while (continuerDefausserCartes) {
                	String carteStr = this.getMonInterface().selectionnerCarte(this.getJoueurCourant().getCartesMain().getCartesMain());     
                	TypeCarte typeCarte = TypeCarte.LASER;  // Placeholder
                    switch (carteStr) {
                        case "CARTE_BLEUE":
                            typeCarte = TypeCarte.CARTE_BLEUE;
                            break;
                        case "CARTE_JAUNE":
                            typeCarte = TypeCarte.CARTE_JAUNE;
                            break;
                        case "CARTE_VIOLETTE":
                            typeCarte = TypeCarte.CARTE_VIOLETTE;
                            break;
                        case "LASER":
                            typeCarte = TypeCarte.LASER;
                            break;
                        case "NOT_A_CARD":
                            continuerDefausserCartes = false;
                    }

                    if (continuerDefausserCartes) {
                        Carte carte = getJoueurCourant().getCartesMain().retirerCarte(typeCarte);
              //          if (carte.getTypeCarte() == TypeCarte.NOT_A_CARD) {
          //                  this.monInterface.afficherMessage("Refus�: vous ne poss�dez pas de telle carte");
             //           }
                    	}
                }

                this.getJoueurCourant().terminerTour();
                System.out.println("end turn");
            }
        }
    }

    void lancerPartie() {
//        // Triche init plateau
//        this.plateau.setCase(1, 0, "g");
//        this.plateau.setCase(1, 1, "b");
//        this.plateau.setCase(1, 4, "b");
//        this.plateau.setCase(2, 2, "b");
//        this.plateau.setCase(2, 4, "b");
//        this.plateau.setCase(3, 2, "b");
//        this.plateau.setCase(3, 3, "b");
//        this.plateau.setCase(3, 4, "b");

        this.setGameOver(false);
        switch (this.getModeJeu()) {
            case "normal":
                jouerManche();
                break;
            case "3 a la suite":
                for (int i = 0; i < 3; i++) {
                    jouerManche();
                    System.out.println("abcdMANCHE");
                    this.getMonInterface().afficherFinManche(this, i);
                    System.out.println("abcdFIN");
                    this.reInitialiserPartie();
                    System.out.println("abcdRESET");
                }
                // Calcul du classement de chaque joueur en fonction de son nombre de points gagné durant les 3 manches
                this.getJoueurs().sort(Comparator.comparing(Joueur::getScore));
                for (int i = 0; i < this.nombreJoueurs; i++) {
                    this.getJoueurs().get(i).setClassement(this.nombreJoueurs - i);
                }
                break;
        }
        this.getMonInterface().afficherResultats(this);
        System.out.println("test");
    }

    private int initFocusJoueur() {
        return ThreadLocalRandom.current().nextInt(0, this.nombreJoueurs);
    }
}
