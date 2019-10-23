package src;

import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class GameLogic {
//    Définition de variables globales
    public static Scanner scanner = new Scanner(System.in);
    public static int nombreTortues;
    public static Tortue[] tortues;
    public static int taillePlateau = 8;
    public static char[][] plateau = new char[taillePlateau][taillePlateau];

    public static void main(String[] args) {
//        demanderNombreTortues();
        nombreTortues = 2;
        initTortues();
        initPlateau();
//        afficherPlateau();
        int focusTortue = initFocusTortue();
        // TODO:: comprendre comment est défini l'ordre de passage des joueurs
        //  et modifier en conséquence
        boolean gameOver = false;

        String action;
        while (!gameOver) {
            for (int i = 0; i< nombreTortues; i++) {
                System.out.println("focusTortue: " + i);
                action = demanderAction();
                // TODO: contrôler la validité des entrées utilisateur
                switch (action) {
                    case "P":  // Compléter le programme
                        // Récupérer la liste des cartes
                        boolean quit;
                        ArrayDeque<String> cartes = new ArrayDeque<String>();
                        String carte;
                        do {
                            quit = false;
                            System.out.println(cartes);
                            System.out.println("Indiquer carte (B/J/V/L) ou quitter (Q) si possible: ");
                            carte = scanner.nextLine();
                            if (carte.equals("Q")) {
                                quit = true;
                            }
                            else {
                                cartes.add(carte);
                            }
                        } while ( !(cartes.size() >=1 && (cartes.size() >=5 || quit)) );
                        tortues[i].completerPrgm(cartes);
                        break;

                    case "M":  // Construire un mur
                        int[] coords = new int[2];
                        System.out.println("Quel type de mur ? ");
                        String typeMur = scanner.nextLine();
                        System.out.println("Quelles coordonnées ? ");
                        coords[0] = scanner.nextInt();
                        coords[1] = scanner.nextInt();
                        tortues[i].placerMur(typeMur, coords);
                        break;

                    case "E":  // Exécuter le programme
                        tortues[i].executerPrgm();
                        break;

                }


            }
            // TODO: vérifier si gameOver

        }

    }

    private static void demanderNombreTortues() {
        do {
            System.out.println("Combien de tortues ? (entre 2 et 4): ");
            nombreTortues = scanner.nextInt();
        } while (nombreTortues < 2 || nombreTortues > 4);
    }

    private static void initTortues() {
//        Création du nombre adéquat de tortues
        tortues = new Tortue[nombreTortues];
        for (int i = 0; i< nombreTortues; i++) {
            tortues[i] = new Tortue();
        }
//        Imposer les positions initiales des tortues
        int[] position = new int[2];
        switch (nombreTortues) {
            case 2:
                position[0] = 0;
                position[1] = 1;
                tortues[0].setPosition(position);
                position[1] = 5;
                tortues[1].setPosition(position);
                break;

            case 3:
                position[0] = 0;
                position[1] = 0;
                tortues[0].setPosition(position);
                position[1] = 3;
                tortues[1].setPosition(position);
                position[1] = 6;
                tortues[2].setPosition(position);
                break;

            case 4:
                position[0] = 0;
                position[1] = 0;
                tortues[0].setPosition(position);
                position[1] = 2;
                tortues[1].setPosition(position);
                position[1] = 5;
                tortues[2].setPosition(position);
                position[1] = 7;
                tortues[3].setPosition(position);
                break;
        }

    }

    private static void initPlateau() {
//            Remplir la dernière colonne de caisses en bois si besoin
        if (nombreTortues != 4) {
            for (int i=0; i<taillePlateau; i++) {
                plateau[i][taillePlateau - 1] = 'b';
            }
        }

//            Placer les joyaux
        switch (nombreTortues) {
            case 2:
                plateau[taillePlateau - 1][3] = 'j';
                break;

            case 3:
                plateau[taillePlateau - 1][0] = 'j';
                plateau[taillePlateau - 1][3] = 'j';
                plateau[taillePlateau - 1][6] = 'j';
                break;

            case 4:
                plateau[taillePlateau - 1][1] = 'j';
                plateau[taillePlateau - 1][6] = 'j';
                break;
        }

        //            Placer les tortues
        for (int i = 0; i< nombreTortues; i++) {
            int[] position = tortues[i].position;
            plateau[position[0]][position[1]] = 't';
        }
    }

    private static void afficherPlateau() {
        for (int i=0; i<taillePlateau; i++) {
            for (int j=0; j<taillePlateau; j++) {
                System.out.print(plateau[i][j] != Character.MIN_VALUE ? plateau[i][j] : '.');
                System.out.print("\t");
            }
            System.out.println("");
        }
    }

    private static int initFocusTortue() {
        return ThreadLocalRandom.current().nextInt(1, nombreTortues + 1);
    }

    private static String demanderAction() {
        String action;
        do {
            System.out.println("Action ? (P/M/E (compléter prgm/mur/exécuter prgm): ");
            action = scanner.nextLine();
        } while (!action.equals("P") && !action.equals("M") && !action.equals("E"));
        return action;
    }

}
