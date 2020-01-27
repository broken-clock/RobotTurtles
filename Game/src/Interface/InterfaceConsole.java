package src.Interface;

import src.Cartes.Carte;
import src.Joueur;
import src.LogiqueDeJeu;
import src.Tuiles.Obstacle;
import src.Tuiles.Orientations;

import java.util.Scanner;

public class InterfaceConsole implements Interface {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void actualiser() {

    }

    @Override
    public String getTypeInterface() {
        return "Console";
    }

    @Override
    public void afficherMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void afficherPlateau(LogiqueDeJeu logiqueDeJeu) {
        int taillePlateau = logiqueDeJeu.getPlateau().getTaillePlateau();
        for (int i = 0; i < taillePlateau; i++) {
            for (int j = 0; j < taillePlateau; j++) {
                System.out.print(logiqueDeJeu.getPlateau().getCase(i, j) == null ? "." : logiqueDeJeu.getPlateau().getCase(i, j));
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    @Override
    public ParametresInitPartie parametresMenu() {
        int nombreJoueurs;
        do {
            System.out.println("Combien de joueurs ? (entre 2 et 4): ");
            nombreJoueurs = Integer.parseInt(scanner.nextLine().trim());
        } while (nombreJoueurs < 2 || nombreJoueurs > 4);

        String modeJeu;
        do {
            System.out.println("Quel mode de jeu ? ('normal' / '3alasuite'): ");
            modeJeu = scanner.nextLine();
        } while (!modeJeu.equals("normal") && !modeJeu.equals("3alasuite"));

        String modeCarteBug;
        System.out.println("Jouer avec les cartes Bug ? (o/N): ");  // Valeur par defaut = "N"
        modeCarteBug = scanner.nextLine();

        return new ParametresInitPartie(nombreJoueurs, modeJeu, modeCarteBug.equals("o"));
    }

    @Override
    public String demanderAction(LogiqueDeJeu logiqueDeJeu) {
        String action;
        if (!logiqueDeJeu.isModeBug()) {
            do {
                System.out.println("Action ? (P/M/E (completer prgm/mur/executer prgm): ");
                action = scanner.nextLine();
            } while (!action.equals("P") && !action.equals("M") && !action.equals("E"));
        } else {
            do {
                System.out.println("Action ? (P/M/E/B (completer prgm/mur/executer prgm/utiliser carte bug): ");
                action = scanner.nextLine();
            } while (!action.equals("P") && !action.equals("M") && !action.equals("E") && !action.equals("B"));
        }
        return action;
    }

    @Override
    public void afficherCartesMain(String str_, LogiqueDeJeu logiqueDeJeu) {
        System.out.println("Cartes dans votre main:");
        for (Carte carteMain : logiqueDeJeu.getJoueurCourant().getCartesMain().getCartesMain()) {
            System.out.println(carteMain.getTypeCarte());
        }
    }

    @Override
    public void afficherProgramme(LogiqueDeJeu logiqueDeJeu) {
        System.out.println("Votre programme courant:");
        for (Carte cartePrgm : logiqueDeJeu.getJoueurCourant().getProgramme().getProgramme()) {
            System.out.println(cartePrgm.getTypeCarte());
        }
    }

    @Override
    public String demanderCarteAAjouterAProgramme() {
        String carteStr;
        do {
            System.out.println("Indiquer carte (CARTE_BLEUE/CARTE_JAUNE/CARTE_VIOLETTE/LASER/NOT_A_CARD) a ajouter a votre programme");
            carteStr = scanner.nextLine();
        } while (!carteStr.equals("CARTE_BLEUE") && !carteStr.equals("CARTE_JAUNE") && !carteStr.equals("CARTE_VIOLETTE") && !carteStr.equals("LASER") && !carteStr.equals("NOT_A_CARD"));

        return carteStr;
    }

    @Override
    public Obstacle demanderObstacleAPlacer() {
        String typeObstacle;
        do {
            System.out.println("Quel type d'obstacle voulez-vous placer ? (g/p)");
            typeObstacle = scanner.nextLine();
        } while (!typeObstacle.equals("g") && !typeObstacle.equals("p"));

        int[] coordsObstacle = new int[2];
        System.out.println("A quelles coordonees ?");
        coordsObstacle[0] = Integer.parseInt(scanner.nextLine().trim());
        coordsObstacle[1] = Integer.parseInt(scanner.nextLine().trim());
        return new Obstacle(typeObstacle, coordsObstacle);
    }

    @Override
    public int demanderCibleCarteBug(LogiqueDeJeu logiqueDeJeu) {
        int cibleCarteBug;
        do {
            System.out.println("Entrez le numero du joueur a qui vous voulez poser votre carte bug:");
            cibleCarteBug = Integer.parseInt(scanner.nextLine().trim());
        } while (cibleCarteBug < 0 || cibleCarteBug > logiqueDeJeu.getNombreJoueurs() - 1 || cibleCarteBug == logiqueDeJeu.getJoueurCourant().getNumeroJoueur());
        return cibleCarteBug;
    }

    @Override
    public String demanderChoixDefausse() {
        String choixCarteADefausser;
        do {
            System.out.println("Indiquer carte (CARTE_BLEUE/CARTE_JAUNE/CARTE_VIOLETTE/LASER/NOT_A_CARD) a defausser de votre main");
            choixCarteADefausser = scanner.nextLine();
        } while (!choixCarteADefausser.equals("CARTE_BLEUE") && !choixCarteADefausser.equals("CARTE_JAUNE") && !choixCarteADefausser.equals("CARTE_VIOLETTE") && !choixCarteADefausser.equals("LASER") && !choixCarteADefausser.equals("NOT_A_CARD"));

        return choixCarteADefausser;
    }

    @Override
    public void afficherResultats(LogiqueDeJeu logiqueDeJeu) {
        System.out.println("\n////////////////////");
        System.out.println("TERMINE, voici le classement");
        for (Joueur joueur : logiqueDeJeu.getJoueurs()) {
            System.out.print("Joueur " + joueur.getNumeroJoueur() + ": ");
            System.out.print(joueur.getClassement() + "°");
            switch (logiqueDeJeu.getModeJeu()) {
                case "normal":
                    System.out.println();
                    break;
                case "3alasuite":
                    System.out.println(" avec " + joueur.getScore() + " points");
                    break;
            }
        }
    }

    @Override
    public void afficherFinManche(LogiqueDeJeu logiqueDeJeu) {
        System.out.println("\n\\\\\\\\");
        System.out.println("Fin de la manche " + (logiqueDeJeu.getNumeroManche() + 1) + " !");
        for (Joueur joueur : logiqueDeJeu.getJoueurs()) {
            System.out.print("Joueur " + joueur.getNumeroJoueur() + ": score courant = ");
            System.out.println(joueur.getScore());
        }
    }

    @Override
    public void animationLaser(int[] pos, Orientations orient) {
    }

    @Override
    public void stopLaser() {
    }

}
