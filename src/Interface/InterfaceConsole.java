package src.Interface;

import src.Cartes.Carte;
import src.Joueur;
import src.LogiqueDeJeu;
import src.Parametres;
import src.Tuiles.Obstacle;

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
    public Parametres parametresMenu() {
        int nombreJoueurs;
        do {
            System.out.println("Combien de joueurs ? (entre 2 et 4): ");
            nombreJoueurs = scanner.nextInt();
        } while (nombreJoueurs < 2 || nombreJoueurs > 4);

        String modeJeu;
        do {
            System.out.println("Quel mode de jeu ? ('normal' / '3alasuite'): ");
            modeJeu = scanner.nextLine();
        } while (!modeJeu.equals("normal") && !modeJeu.equals("3alasuite"));

        String modeCarteBug;
        System.out.println("Jouer avec les cartes Bug ? (o/N): ");  // Valeur par défaut = "N"
        modeCarteBug = scanner.nextLine();

        return new Parametres(nombreJoueurs, modeJeu, modeCarteBug.equals("o"));
    }

    @Override
    public String demanderAction(LogiqueDeJeu logiqueDeJeu) {
        String action;
        if (!logiqueDeJeu.isModeBug()) {
            do {
                System.out.println("Action ? (P/M/E (compléter prgm/mur/exécuter prgm): ");
                action = scanner.nextLine();
            } while (!action.equals("P") && !action.equals("M") && !action.equals("E"));
        } else {
            do {
                System.out.println("Action ? (P/M/E/B (compléter prgm/mur/exécuter prgm/utiliser carte bug): ");
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
            System.out.println("Indiquer carte (B/J/V/L/none) à ajouter à votre programme");
            carteStr = scanner.nextLine();
        } while (!carteStr.equals("B") && !carteStr.equals("J") && !carteStr.equals("V") && !carteStr.equals("L") && !carteStr.equals("none"));

        return carteStr;
    }

    @Override
    public Obstacle demanderObstacleAPlacer() {
        String typeObstacle;
        do {
            System.out.println("Quel type d'obstacle voulez-vous placer ? (G/P)");
            typeObstacle = scanner.nextLine();
        } while (!typeObstacle.equals("G") && !typeObstacle.equals("P"));

        int[] coordsObstacle = new int[2];
        System.out.println("A quelles coordonées ?");
        coordsObstacle[0] = scanner.nextInt();
        coordsObstacle[1] = scanner.nextInt();
        return new Obstacle(typeObstacle, coordsObstacle);
    }

    @Override
    public int demanderCibleCarteBug(LogiqueDeJeu logiqueDeJeu) {
        int cibleCarteBug;
        do {
            System.out.println("Entrez le numéro du joueur à qui vous voulez poser votre carte bug:");
            cibleCarteBug = scanner.nextInt();
        } while (cibleCarteBug < 0 || cibleCarteBug > logiqueDeJeu.getNombreJoueurs() - 1 || cibleCarteBug == logiqueDeJeu.getJoueurCourant().getNumeroJoueur());
        return cibleCarteBug;
    }

    @Override
    public String demanderChoixDefausse() {
        String choixCarteADefausser;
        do {
            System.out.println("Indiquer carte (B/J/V/L/none) à défausser de votre main");
            choixCarteADefausser = scanner.nextLine();
        } while (!choixCarteADefausser.equals("B") && !choixCarteADefausser.equals("J") && !choixCarteADefausser.equals("V") && !choixCarteADefausser.equals("L") && !choixCarteADefausser.equals("none"));

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
    public void afficherFinManche(LogiqueDeJeu logiqueDeJeu, int i) {
        System.out.println("\n\\\\\\\\");
        System.out.println("Fin de la manche " + (i + 1) + " !");
        for (Joueur joueur : logiqueDeJeu.getJoueurs()) {
            System.out.print("Joueur " + joueur.getNumeroJoueur() + ": score courant = ");
            System.out.println(joueur.getScore());
        }
    }
}
