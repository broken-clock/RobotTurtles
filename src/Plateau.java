package src;

import src.Tuiles.Joyau;
import src.Tuiles.Position;
import src.Tuiles.Tortue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class Plateau {
    private int taillePlateau = 8;
    private String[][] plateau;
    private ArrayList<ArrayList<Integer>> casesAccessibles;  // Utile pour la recherche de chemin entre cases du plateau
    // Utile pour la vérification du caractère gênant du placement d'un obstacle
    private ArrayList<Integer> numerosTortuesVerifiees = new ArrayList();
    private boolean aucuneTortueBloquee;  // Sert de flag

    public Plateau() {
        plateau = new String[taillePlateau][taillePlateau];
    }

    public int getTaillePlateau() {
        return taillePlateau;
    }

    public String getCase(int x, int y) {
        return plateau[x][y];
    }

    public void setCase(int x, int y, String contenu) {
        plateau[x][y] = contenu;
    }

    void initPlateau(LogiqueDeJeu logiqueDeJeu) {
        // Remplir la dernière colonne de mur en pierre si besoin
        if (logiqueDeJeu.getNombreJoueurs() != 4) {
            for (int i = 0; i < taillePlateau; i++) {
                this.setCase(i, taillePlateau - 1, "p");
            }
        }

        // Placer les tortues
        for (Joueur joueur : logiqueDeJeu.getJoueurs()) {
            Position position_tortue = joueur.getTortue().getPosition();
            String reprTortue = joueur.getTortue().getReprTortue(joueur.getTortue(), position_tortue.getOrientation());
            this.setCase(position_tortue.getX(), position_tortue.getY(), reprTortue);
        }

        // Placer les joyaux
        for (Joyau joyau : logiqueDeJeu.getJoyaux()) {
            Position position_joyau = joyau.getPosition();
            this.setCase(position_joyau.getX(), position_joyau.getY(), "J");
        }
    }

    // Fonction récursive déterminant l'ensemble des cases accessibles à partir d'une case de départ (d'une liste de cases de départ pouvant être de taille 1)
    // par déplacements haut / bas / gauche / droite sur le plateau
    private ArrayDeque<int[]> getRecursiveCasesAdjacentesLibres(LogiqueDeJeu logiqueDeJeu, ArrayDeque<int[]> arrayCoordonnees, boolean conditionArretTortues) {
        for (int[] coordonnees : arrayCoordonnees) {
            int X = coordonnees[0];
            int Y = coordonnees[1];

            // On ne continue l'exploration que si on n'a pas déjà visité la case courante
            if (!this.casesAccessibles.contains(new ArrayList<>(Arrays.asList(X, Y)))) {
                ArrayDeque<int[]> casesAdjacentesLibres = new ArrayDeque();
                String contenuCase;
                //On ne s'intéresse qu'aux déplacements de type haut / bas / gauche / droite, donc il y a
                // quatre cases adjacentes libres potentielles
                for (int[] xy : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1},}) {
                    int x = xy[0];
                    int y = xy[1];

                    // Coordonnées de la case adjacente potentielle qu'il va peut-être falloir explorer
                    int newX = X - x;
                    int newY = Y - y;
                    // Si la case potentiellement adjacente à tester est à l'intérieur du plateau, alors on l'explore effectivement
                    if (newX >= 0 && newX <= 7 && newY >= 0 && newY <= 7) {
                        contenuCase = this.getCase(X - x, Y - y);
                        if (contenuCase == null) contenuCase = "";
                        // Si la case est considérée libre, i.e. pas occupée par un mur indestructible
                        if (!contenuCase.equals("b") && !contenuCase.equals("p"))
                            // Alors on l'ajoute à la liste des cases à prendre en compte pour la recherche de chemin...
                            casesAdjacentesLibres.add(new int[]{X - x, Y - y});
                    }
                }
                this.casesAccessibles.add(new ArrayList<>(Arrays.asList(X, Y)));  // La case (X, Y) est enregistrée comme accessible depuis la case de départ
                if (conditionArretTortues) {
                    // Si la case qu'on vient d'enregistrer est une tortue, on en prend note
                    String contenuCaseVerifierTortue = this.getCase(X, Y);
                    if (Tortue.isReprTortue(contenuCaseVerifierTortue)) {
                        this.numerosTortuesVerifiees.add(Tortue.getNumeroTortue(contenuCaseVerifierTortue));
                    }

                    // Si on a prouvé que toutes les tortues peuvent accéder au joyau, on quitte la recherche de cases accessibles
                    if (this.numerosTortuesVerifiees.size() == logiqueDeJeu.getNombreJoueurs()) {
                        this.aucuneTortueBloquee = true;
                        break;
                    }
                }

                // ... et ce de manière récursive
                // La terminaison est garantie par la finitude du plateau et par la mémorisation des cases déjà explorées
                getRecursiveCasesAdjacentesLibres(logiqueDeJeu, casesAdjacentesLibres, conditionArretTortues);
            }
        }
        return new ArrayDeque();
    }

    // Fonction de recherche de chemin entre joyaux et tortues
    // Si conditionArretTortues, on arrête la recherche de cases accessibles si on a déterminé que les cases correspondant aux positions de toutes les tortues en faisaient partie
    private ArrayList<ArrayList<Integer>> getCasesAccessibles(LogiqueDeJeu logiqueDeJeu, int[] coordonnees, boolean conditionArretTortues) {
        // Initialisation
        ArrayDeque<int[]> initialArrayDeque = new ArrayDeque();
        initialArrayDeque.add(coordonnees);
        getRecursiveCasesAdjacentesLibres(logiqueDeJeu, initialArrayDeque, conditionArretTortues);
        return this.casesAccessibles;
    }

    // Fonction déterminant si placer un obstacle à ces coordonnées bloquerait l'accès à au moins une tortue à au moins un joyau
    boolean placementBloquant(LogiqueDeJeu logiqueDeJeu, int[] coordonnees) {
        boolean placementBloquant = false;
        // Simulation d'un obstacle indestructible aux coordonnées à tester
        // On sait que ces coordonnées correspondent à une case vide, donc pas besoin de backup le contenu de la case
        this.setCase(coordonnees[0], coordonnees[1], "b");

        // Obtention des positions de tous les joyaux
        ArrayList<Position> positionsJoyaux = new ArrayList();
        for (Joyau joyau : logiqueDeJeu.getJoyaux()) {
            positionsJoyaux.add(joyau.getPosition());
        }
        // On s'assure que toutes les tortues peuvent accéder à chaque position de joyau
        for (Position positionJoyau : positionsJoyaux) {
            // Reinit
            this.casesAccessibles = new ArrayList();
            this.aucuneTortueBloquee = false;
            this.numerosTortuesVerifiees = new ArrayList();

            int x = positionJoyau.getX();
            int y = positionJoyau.getY();
            getCasesAccessibles(logiqueDeJeu, new int[]{x, y}, true);  // Lève le flag aucuneTortueBloquee si toutes les tortues peuvent accéder au joyau
            if (!this.aucuneTortueBloquee) {
                placementBloquant = true;
            }
        }
        // FIN simulation d'un obstacle indestructible aux coordonnées à tester
        this.setCase(coordonnees[0], coordonnees[1], null);

        return placementBloquant;
    }
}
