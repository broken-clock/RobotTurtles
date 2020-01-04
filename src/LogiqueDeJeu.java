package src;

import src.Interface.*;
import src.Tuiles.Directions;
import src.Tuiles.Joyau;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class LogiqueDeJeu {
    public Interface monInterface;
    public int nombreJoueurs;
    public ArrayList<Joueur> joueurs = new ArrayList();
    public ArrayList<Joyau> joyaux = new ArrayList();
    public Plateau plateau = new Plateau();
    public int focusJoueur;
    public boolean gameOver = false;

    public void initialiserPartie() {
        // Choix du type d'interface
        this.monInterface = new InterfaceConsole();

        // demanderNombreJoueurs();
        this.nombreJoueurs = 2;

        // Création du nombre adéquat de joueurs et initialisation pour chaque joueur de ses obstacles disponibles
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.joueurs.add(new Joueur());
            this.joueurs.get(i).mursDePierre = 3;
            this.joueurs.get(i).mursDeGlace = 2;
        }

        // En fonction du nombre de joueurs, initialiser les positions des tortues et les joyaux
        switch (this.nombreJoueurs) {
            case 2:
                // Initialiser les positions des tortues
                this.joueurs.get(0).tortue.setPosition(0, 1, Directions.DOWN);
                this.joueurs.get(1).tortue.setPosition(0, 5, Directions.DOWN);

                // Créer les joyaux et définir leur position
                this.joyaux.add(new Joyau());
                this.joyaux.get(0).setPosition(7, 3, null);
                break;

            case 3:
                // Initialiser les positions des tortues
                this.joueurs.get(0).tortue.setPosition(0, 0, Directions.DOWN);
                this.joueurs.get(1).tortue.setPosition(0, 3, Directions.DOWN);
                this.joueurs.get(2).tortue.setPosition(0, 6, Directions.DOWN);

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
                this.joueurs.get(0).tortue.setPosition(0, 0, Directions.DOWN);
                this.joueurs.get(1).tortue.setPosition(0, 2, Directions.DOWN);
                this.joueurs.get(2).tortue.setPosition(0, 5, Directions.DOWN);
                this.joueurs.get(3).tortue.setPosition(0, 7, Directions.DOWN);

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

        // Afficher l'état courant du plateau
        this.monInterface.afficherPlateau(this);
    }

//    public void lancerPartie() {
//        int focusTortue = initFocusTortue();
//        // TODO: comprendre comment est défini l'ordre de passage des joueurs
//        //  et modifier en conséquence
//        boolean gameOver = false;
//
//        String action;
//        while (!gameOver) {
//            for (int i = 0; i < nombreJoueurs; i++) {
//                System.out.println("focusTortue: " + i);
//                action = demanderAction();
//                // TODO: contrôler la validité des entrées utilisateur
//                switch (action) {
//                    case "P":  // Compléter le programme
//                        // Récupérer la liste des cartes
//                        boolean quit;
//                        ArrayDeque<String> cartes = new ArrayDeque<>();
//                        String carte;
//                        do {
//                            quit = false;
//                            System.out.println(cartes);
//                            System.out.println("Indiquer carte (B/J/V/L) ou quitter (Q) si possible: ");
//                            carte = scanner.nextLine();
//                            if (carte.equals("Q")) quit = true;
//
//                            else {
//                                cartes.add(carte);
//                            }
//                        } while (!(cartes.size() >= 1 && (cartes.size() >= 5 || quit)));
//                        tortues[i].completerPrgm(cartes);
//                        break;
//
//                    case "M":  // Construire un mur
//                        int[] coords = new int[2];
//                        System.out.println("Quel type de mur ? ");
//                        String typeMur = scanner.nextLine();
//                        System.out.println("Quelles coordonnées ? ");
//                        coords[0] = scanner.nextInt();
//                        coords[1] = scanner.nextInt();
//                        tortues[i].placerMur(typeMur, coords);
//                        break;
//
//                    case "E":  // Exécuter le programme
//                        tortues[i].executerPrgm();
//                        break;
//
//                }
//
//
//            }
//            // TODO: vérifier si gameOver
//
//        }
//
//    }
//
//    private int demanderNombreJoueurs() {
//        do {
//            System.out.println("Combien de tortues ? (entre 2 et 4): ");
//            nombreJoueurs = scanner.nextInt();
//        } while (nombreJoueurs < 2 || nombreJoueurs > 4);
//        return 0;
//    }
//
//    private int initFocusTortue() {
//        return ThreadLocalRandom.current().nextInt(1, nombreJoueurs + 1);
//    }
//
//    private String demanderAction() {
//        String action;
//        do {
//            System.out.println("Action ? (P/M/E (compléter prgm/mur/exécuter prgm): ");
//            action = scanner.nextLine();
//        } while (!action.equals("P") && !action.equals("M") && !action.equals("E"));
//        return action;
//    }
}
