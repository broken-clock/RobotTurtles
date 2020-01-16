package src;

import src.Tuiles.Joyau;
import src.Tuiles.Position;

public class Plateau {
    private int taillePlateau = 8;
    private String[][] plateau;

    public Plateau() {
        plateau = new String[taillePlateau][taillePlateau];
    }

    void initPlateau(LogiqueDeJeu logiqueDeJeu) {
        // Remplir la dernière colonne de caisses en bois si besoin
        if (logiqueDeJeu.getNombreJoueurs() != 4) {
            for (int i = 0; i < taillePlateau; i++) {
                this.setCase(i, taillePlateau - 1, "b");
            }
        }

        // Placer les tortues
        for (Joueur joueur : logiqueDeJeu.getJoueurs()) {
            Position position_tortue = joueur.tortue.getPosition();
            String reprTortue = joueur.tortue.getReprTortue(joueur.tortue, position_tortue.getOrientation());
            this.setCase(position_tortue.getX(), position_tortue.getY(), reprTortue);
        }

        // Placer les joyaux
        for (Joyau joyau : logiqueDeJeu.joyaux) {
            Position position_joyau = joyau.getPosition();
            this.setCase(position_joyau.getX(), position_joyau.getY(), "J");
        }
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

//    public ArrayList<Case> getCasesAdjacentesLibres(Case Case) {
//        ArrayList<int[]> casesAdjacentesLibres = new ArrayList();
//        casesAdjacentesLibres.add(new int[]{0, 1});
//
//    }
//
//    public boolean placementBloquant(int[] coordonnees) {
//        ArrayList<ArrayList<Case>> casesCheminAcces = new ArrayList();
//        if (true) return false;  // TODO
//        for (Case caseLibre : getCasesAdjacentesLibres(coordonnees)) {
//            casesCheminAcces.add(getCasesAdjacentesLibres(caseLibre));
//        }
//        return false;
//    }

    boolean placementBloquant(int[] coordonnees) {
        return false;
    }
}
