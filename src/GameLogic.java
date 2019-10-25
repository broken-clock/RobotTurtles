package src;

import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class GameLogic {
    // Définition de variables globales
    public static Scanner scanner = new Scanner(System.in);
    public static int nombreTortues;
    public static Tortue[] tortues;
    public static Plateau plateau;

    public static void main(String[] args) {
        // demanderNombreTortues();
        nombreTortues = 2;
        initTortues();
        plateau.initPlateau(nombreTortues);
        // plateau.afficherPlateau();
        int focusTortue = initFocusTortue();
        // TODO: comprendre comment est défini l'ordre de passage des joueurs
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
                        ArrayDeque<String> cartes = new ArrayDeque<>();
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
        // Création du nombre adéquat de tortues
        tortues = new Tortue[nombreTortues];
        for (int i = 0; i< nombreTortues; i++) {
            tortues[i] = new Tortue();
        }
        // Imposer les positions initiales des tortues
        switch (nombreTortues) {
            case 2:
                tortues[0].setPosition(0, 1);
                tortues[1].setPosition(0, 5);
                break;

            case 3:
                tortues[0].setPosition(0, 0);
                tortues[1].setPosition(0, 3);
                tortues[2].setPosition(0, 6);
                break;

            case 4:
                tortues[0].setPosition(0, 0);
                tortues[1].setPosition(0, 2);
                tortues[2].setPosition(0, 5);
                tortues[3].setPosition(0, 7);
                break;
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
