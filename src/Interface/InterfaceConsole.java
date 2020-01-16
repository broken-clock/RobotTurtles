package src.Interface;

import src.Cartes.Carte;
import src.Joueur;
import src.LogiqueDeJeu;

import java.util.Scanner;

public class InterfaceConsole implements Interface {
    Scanner scanner = new Scanner(System.in);

    @Override
    public void afficherMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void afficherPlateau(LogiqueDeJeu logiqueDeJeu) {
        int taillePlateau = logiqueDeJeu.plateau.getTaillePlateau();
        for (int i = 0; i < taillePlateau; i++) {
            for (int j = 0; j < taillePlateau; j++) {
                System.out.print(logiqueDeJeu.plateau.getCase(i, j) == null ? "." : logiqueDeJeu.plateau.getCase(i, j));
                System.out.print("\t");
            }
            System.out.println("");
        }
    }

    @Override
    public int demanderNombreJoueurs(LogiqueDeJeu logiqueDeJeu) {
        int nombreJoueurs;
        do {
            System.out.println("Combien de joueurs ? (entre 2 et 4): ");
            nombreJoueurs = scanner.nextInt();
        } while (nombreJoueurs < 2 || nombreJoueurs > 4);
        return nombreJoueurs;
    }

    @Override
    public String demanderModeJeu() {
        String modeJeu;
        do {
            System.out.println("Quel mode de jeu ? ('normal' / '3àlasuite'): ");
            modeJeu = scanner.nextLine();
        } while (!modeJeu.equals("normal") && !modeJeu.equals("3àlasuite"));
        return modeJeu;
    }

    @Override
    public boolean demanderModeCarteBug() {
        String modeCarteBug;
        System.out.println("Jouer avec les cartes Bug ? (o/N): ");  // Valeur par défaut = "N"
        modeCarteBug = scanner.nextLine();
        return modeCarteBug.equals("o");
    }

    @Override
    public String demanderAction(LogiqueDeJeu logiqueDeJeu) {
        String action;
        if (!logiqueDeJeu.modeBug) {
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
    public void afficherCartesMain(LogiqueDeJeu logiqueDeJeu) {
        System.out.println("Cartes dans votre main:");
        for (Carte carteMain : logiqueDeJeu.joueurCourant.cartesMain.getCartesMain()) {
            System.out.println(carteMain.getTypeCarte());
        }
    }

    @Override
    public void afficherProgramme(LogiqueDeJeu logiqueDeJeu) {
        System.out.println("Votre programme courant:");
        for (Carte cartePrgm : logiqueDeJeu.joueurCourant.programme.getProgramme()) {
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
    public String demanderTypeObstacleAPlacer() {
        String typeObstacle;
        do {
            System.out.println("Quel type d'obstacle voulez-vous placer ? (G/P)");
            typeObstacle = scanner.nextLine();
        } while (!typeObstacle.equals("G") && !typeObstacle.equals("P"));
        return typeObstacle;
    }

    @Override
    public int[] demanderCoordsObstacleAPlacer() {
        int[] coordsObstacle = new int[2];
        System.out.println("A quelles coordonées ?");
        coordsObstacle[0] = scanner.nextInt();
        coordsObstacle[1] = scanner.nextInt();
        return coordsObstacle;
    }

    @Override
    public int demanderCibleCarteBug(LogiqueDeJeu logiqueDeJeu) {
        int cibleCarteBug;
        do {
            System.out.println("Entrez le numéro du joueur à qui vous voulez poser votre carte bug:");
            cibleCarteBug = scanner.nextInt();
        } while (cibleCarteBug < 0 || cibleCarteBug > logiqueDeJeu.nombreJoueurs - 1 || cibleCarteBug == logiqueDeJeu.joueurCourant.getNumeroJoueur());
        return cibleCarteBug;
    }

    @Override
    public boolean demanderChoixDefausse() {
        String choixDefausse;
        System.out.println("Voulez-vous défausser votre main et re-piocher 5 cartes ? (O/n)");  // Valeur par défaut = "O"
        choixDefausse = scanner.nextLine();
        return !choixDefausse.equals("n");
    }

    @Override
    public void afficherResultats(LogiqueDeJeu logiqueDeJeu) {
        System.out.println("\n////////////////////");
        System.out.println("TERMINE, voici le classement");
        for (Joueur joueur : logiqueDeJeu.joueurs) {
            System.out.print("Joueur " + joueur.getNumeroJoueur() + ": ");
            System.out.print(joueur.classement + "°");
            switch (logiqueDeJeu.modeJeu) {
                case "normal":
                    System.out.println("");
                    break;
                case "3àlasuite":
                    System.out.println(" avec " + joueur.getScore() + " points");
                    break;
            }
        }
    }

    @Override
    public void afficherFinManche(LogiqueDeJeu logiqueDeJeu, int i) {
        System.out.println("\n\\\\\\\\");
        System.out.println("Fin de la manche " + (i + 1) + " !");
        for (Joueur joueur : logiqueDeJeu.joueurs) {
            System.out.print("Joueur " + joueur.getNumeroJoueur() + ": score courant = ");
            System.out.println(joueur.getScore());
        }
    }
}
