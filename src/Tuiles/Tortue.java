package src.Tuiles;

import src.Case;
import src.LogiqueDeJeu;

import java.util.HashMap;

public class Tortue extends Tuile {
    private static HashMap<Orientations, String> reprTortues = new HashMap();
    private Position positionDepart;
    private int numeroJoueur;

    static {
        reprTortues.put(Orientations.UP, "T");
        reprTortues.put(Orientations.DOWN, "⊥");
        reprTortues.put(Orientations.LEFT, "├");
        reprTortues.put(Orientations.RIGHT, "┤");
    }

    public void setNumeroJoueur(int _numeroJoueur) {
        this.numeroJoueur = _numeroJoueur;
    }

    public void setPositionDepart(int x, int y, Orientations orientation) {
        positionDepart = new Position(x, y, orientation);
    }

    public void retourPositionDepart(LogiqueDeJeu logiqueDeJeu) {
        deplacerTortue(logiqueDeJeu, this, new Position(this.positionDepart.x, this.positionDepart.y, this.positionDepart.orientation));
    }

    public void deplacerTortue(LogiqueDeJeu logiqueDeJeu, Tortue tortue, Position position) {
        // On vide la case couramment occupée par la tortue
        logiqueDeJeu.plateau.setCase(tortue.position.x, tortue.position.y, ".");

        // On occupe la case suivante par la tortue
        logiqueDeJeu.plateau.setCase(position.x, position.y, getReprTortue(tortue, position.orientation));
        tortue.setPosition(position.x, position.y, position.orientation);
    }

    public void avancer(LogiqueDeJeu logiqueDeJeu) {
        Case caseDestination = this.getCaseSuivante(logiqueDeJeu);
        System.out.println(caseDestination.getContenu());

        switch (caseDestination.getContenu()) {
            // Si c'est un mur
            case "b":
            case "p":
            case "g":
                this.faireDemiTour(logiqueDeJeu);
                break;

            case ".":  // Si la case est vide
            case "J":
                System.out.print("On déplace la tortue: ");
                System.out.print(caseDestination.getX());
                System.out.print("; ");
                System.out.println(caseDestination.getY());

                deplacerTortue(logiqueDeJeu, this, new Position(caseDestination.getX(), caseDestination.getY(), this.position.orientation));

                // Si la tortue a atteint un joyau
                if (caseDestination.getContenu().equals("J")) {
                    logiqueDeJeu.nombreJoueursGagne++;
                    // S'il ne reste plus qu’un joueur qui n'a pas atteint de joyau
                    if (logiqueDeJeu.nombreJoueursGagne == logiqueDeJeu.nombreJoueurs - 1) {
                        logiqueDeJeu.gameOver = true;
                    }

                    if (logiqueDeJeu.modeJeu.equals("normal")) {
                        // On donne son classement au joueur
                        logiqueDeJeu.joueurs.get(this.numeroJoueur).classement = logiqueDeJeu.nombreJoueursGagne;
                    } else if (logiqueDeJeu.modeJeu.equals("3àlasuite")) {
                        // On met à jour le score du joueur
                        logiqueDeJeu.joueurs.get(this.numeroJoueur).increaseScore(logiqueDeJeu.nombreJoueurs - logiqueDeJeu.nombreJoueursGagne);
                    }
                }
                break;
            default:
                // Si une tortue rencontre une autre tortue
                if (reprTortues.containsValue(String.valueOf(caseDestination.getContenu().charAt(0)))) {
                    int numeroTortueAdverse = Character.getNumericValue(caseDestination.getContenu().charAt(1));
                    Tortue tortueAdverse = logiqueDeJeu.joueurs.get(numeroTortueAdverse).getTortue();
                    Position positionDepartTortueAdverse = tortueAdverse.positionDepart;

                    // Les deux tortues retournent à leurs positions de départ respectives
                    this.retourPositionDepart(logiqueDeJeu);
                    deplacerTortue(logiqueDeJeu, tortueAdverse, new Position(positionDepartTortueAdverse.x, positionDepartTortueAdverse.y, positionDepartTortueAdverse.orientation));
                }
                break;
        }
    }

    public void tournerHoraire(LogiqueDeJeu logiqueDeJeu) {
        this.position.orientation = Orientations.getOrientationPrecedente(this.position.orientation);
        logiqueDeJeu.plateau.setCase(this.position.x, this.position.y, getReprTortue(this, this.position.orientation));
    }

    public void tournerAntiHoraire(LogiqueDeJeu logiqueDeJeu) {
        this.position.orientation = Orientations.getOrientationSuivante(this.position.orientation);
        logiqueDeJeu.plateau.setCase(this.position.x, this.position.y, getReprTortue(this, this.position.orientation));
    }

    public void faireDemiTour(LogiqueDeJeu logiqueDeJeu) {
        this.tournerHoraire(logiqueDeJeu);
        this.tournerHoraire(logiqueDeJeu);
    }

    public void lancerLaser(LogiqueDeJeu logiqueDeJeu) {
        // Déterminer les coordonnées de la case du plateau touchée par le laser
        int x = this.getPosition().x;
        int y = this.getPosition().y;
        Orientations orientation = this.getPosition().orientation;
        String caseLueContenu = null;

        do {
            switch (orientation) {
                case UP:
                    x--;
                    break;
                case DOWN:
                    x++;
                    break;
                case LEFT:
                    y--;
                    break;
                case RIGHT:
                    y++;
                    break;
            }
            try {
                caseLueContenu = logiqueDeJeu.plateau.getCase(x, y);
            } catch (Exception e) {
                if (e.equals("java.lang.ArrayIndexOutOfBoundsException")) break;
                break;  // Permet de sortir de la boucle
            }
        } while (caseLueContenu == null);

        // Agir sur cette case comme il se doit
        switch (caseLueContenu) {
            case "g":
                logiqueDeJeu.plateau.setCase(x, y, null);
                break;
            case "J":
                int nombreJoueurs = logiqueDeJeu.getNombreJoueurs();
                if (nombreJoueurs == 2) {
                    this.faireDemiTour(logiqueDeJeu);
                } else {
                    this.retourPositionDepart(logiqueDeJeu);
                }
                break;
            default:
                if (reprTortues.containsValue(String.valueOf(caseLueContenu.charAt(0)))) {
                    // Le laser a touché une tortue
                    int numeroTortueAdverse = Character.getNumericValue(caseLueContenu.charAt(1));
                    Tortue tortueAdverse = logiqueDeJeu.joueurs.get(numeroTortueAdverse).getTortue();

                    if (logiqueDeJeu.getNombreJoueurs() == 2) {
                        tortueAdverse.faireDemiTour(logiqueDeJeu);
                    } else {
                        Position positionDepartTortueAdverse = tortueAdverse.positionDepart;
                        deplacerTortue(logiqueDeJeu, tortueAdverse, new Position(positionDepartTortueAdverse.x, positionDepartTortueAdverse.y, positionDepartTortueAdverse.orientation));
                    }
                }
        }
    }

    // Représentation dans le plateau des tortues en fonction de leur orientation
    public String getReprTortue(Tortue tortue, Orientations orientation) {
        String reprTortue = reprTortues.get(orientation);
        return reprTortue + tortue.numeroJoueur;
    }
}
