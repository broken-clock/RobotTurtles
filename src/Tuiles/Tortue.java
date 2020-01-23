package src.Tuiles;

import src.Case;
import src.LogiqueDeJeu;

import java.util.HashMap;

public class Tortue extends Tuile {
    private static HashMap<Orientations, String> reprTortues = new HashMap();
    private Position positionDepart;
    private int numeroJoueur;

    // Initialisation (exécuté dès le chargement de Tortue en mémoire)
    static {
        reprTortues.put(Orientations.UP, "^");
        reprTortues.put(Orientations.DOWN, "V");
        reprTortues.put(Orientations.LEFT, "<");
        reprTortues.put(Orientations.RIGHT, ">");
    }

    // Permet de déterminer si le contenu d'une case du plateau correspond à la représentation d'une tortue
    public static boolean isReprTortue(String contenuCase) {
        if (contenuCase == null) return false;
        return reprTortues.containsValue(String.valueOf(contenuCase.charAt(0)));
    }

    // Permet de déterminer le numéro de joueur correspondant à une tortue à partie du contenu d'une case du plateau qui correspond à la représentation d'une tortue
    public static int getNumeroTortue(String contenuCase) {
        return Integer.parseInt(String.valueOf(contenuCase.charAt(1)));
    }

    public void setNumeroJoueur(int _numeroJoueur) {
        this.numeroJoueur = _numeroJoueur;
    }

    public void setPositionDepart(int x, int y, Orientations orientation) {
        positionDepart = new Position(x, y, orientation);
    }

    private void retourPositionDepart(LogiqueDeJeu logiqueDeJeu) {
        deplacerTortue(logiqueDeJeu, this, new Position(this.positionDepart.getX(), this.positionDepart.getY(), this.positionDepart.getOrientation()));
    }

    private void deplacerTortue(LogiqueDeJeu logiqueDeJeu, Tortue tortue, Position position) {
        // On vide la case couramment occupée par la tortue
        logiqueDeJeu.getPlateau().setCase(tortue.position.getX(), tortue.position.getY(), ".");

        // On occupe la case suivante par la tortue
        logiqueDeJeu.getPlateau().setCase(position.getX(), position.getY(), getReprTortue(tortue, position.getOrientation()));
        tortue.setPosition(position.getX(), position.getY(), position.getOrientation());
    }

    public void avancer(LogiqueDeJeu logiqueDeJeu) {
        Case caseDestination = this.getCaseSuivante(logiqueDeJeu);
        System.out.println(caseDestination.getContenu());

        switch (caseDestination.getContenu()) {
            // Si la tortue rencontre un bord de plateau
            case "bord":
                // Alors elle retourne à sa position initiale
                this.retourPositionDepart(logiqueDeJeu);
                break;
            // Si c'est un mur
            case "b":
            case "p":
            case "g":
                this.faireDemiTour(logiqueDeJeu);
                break;

            case ".":  // Si la case est vide
            case "J":
                if (caseDestination.getContenu().equals(".")) {
                    System.out.print("On deplace la tortue: ");
                    System.out.print(caseDestination.getX());
                    System.out.print("; ");
                    System.out.println(caseDestination.getY());
                    deplacerTortue(logiqueDeJeu, this, new Position(caseDestination.getX(), caseDestination.getY(), this.position.getOrientation()));
                }

                // Si la tortue a atteint un joyau
                else if (caseDestination.getContenu().equals("J")) {
                    this.disparaitre(logiqueDeJeu);
                    logiqueDeJeu.setNombreJoueursGagne(logiqueDeJeu.getNombreJoueursGagne() + 1);
                    // S'il ne reste plus qu?un joueur qui n'a pas atteint de joyau
                    if (logiqueDeJeu.getNombreJoueursGagne() == logiqueDeJeu.getNombreJoueurs() - 1) {
                        logiqueDeJeu.setGameOver(true);
                    }

                    // Ce joueur doit ne plus jouer jusqu'à la prochaine manche / jusqu'à la fin
                    logiqueDeJeu.getJoueurs().get(this.numeroJoueur).setFini(true);

                    if (logiqueDeJeu.getModeJeu().equals("normal")) {
                        // On donne son classement au joueur
                        logiqueDeJeu.getJoueurs().get(this.numeroJoueur).setClassement(logiqueDeJeu.getNombreJoueursGagne());
                    } else if (logiqueDeJeu.getModeJeu().equals("3alasuite")) {
                        // On met à jour le score du joueur
                        logiqueDeJeu.getJoueurs().get(this.numeroJoueur).increaseScore(logiqueDeJeu.getNombreJoueurs() - logiqueDeJeu.getNombreJoueursGagne());
                    }
                }
                break;
            default:
                // Si une tortue rencontre une autre tortue
                if (isReprTortue(caseDestination.getContenu())) {
                    int numeroTortueAdverse = Character.getNumericValue(caseDestination.getContenu().charAt(1));
                    Tortue tortueAdverse = logiqueDeJeu.getJoueurs().get(numeroTortueAdverse).getTortue();
                    Position positionDepartTortueAdverse = tortueAdverse.positionDepart;

                    // Les deux tortues retournent à leurs positions de départ respectives
                    this.retourPositionDepart(logiqueDeJeu);
                    deplacerTortue(logiqueDeJeu, tortueAdverse, new Position(positionDepartTortueAdverse.getX(), positionDepartTortueAdverse.getY(), positionDepartTortueAdverse.getOrientation()));
                }
                break;
        }
    }

    private void disparaitre(LogiqueDeJeu logiqueDeJeu) {
        // On vide la case couramment occupée par la tortue
        logiqueDeJeu.getPlateau().setCase(this.position.getX(), this.position.getY(), ".");
    }

    public void tournerHoraire(LogiqueDeJeu logiqueDeJeu) {
        this.position.setOrientation(Orientations.getOrientationPrecedente(this.position.getOrientation()));
        logiqueDeJeu.getPlateau().setCase(this.position.getX(), this.position.getY(), getReprTortue(this, this.position.getOrientation()));
    }

    public void tournerAntiHoraire(LogiqueDeJeu logiqueDeJeu) {
        this.position.setOrientation(Orientations.getOrientationSuivante(this.position.getOrientation()));
        logiqueDeJeu.getPlateau().setCase(this.position.getX(), this.position.getY(), getReprTortue(this, this.position.getOrientation()));
    }

    private void faireDemiTour(LogiqueDeJeu logiqueDeJeu) {
        this.tournerHoraire(logiqueDeJeu);
        this.tournerHoraire(logiqueDeJeu);
    }

    public void lancerLaser(LogiqueDeJeu logiqueDeJeu) {
        // Déterminer les coordonnées de la case du plateau touchée par le laser
        int x = this.getPosition().getX();
        int y = this.getPosition().getY();
        Orientations orientation = this.getPosition().getOrientation();
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
                caseLueContenu = logiqueDeJeu.getPlateau().getCase(x, y);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                break;
            }
        } while (caseLueContenu == null);

        // Agir sur cette case comme il se doit
        // Si caseLueContenu == null, alors le laser n'a rien touché, donc on ne fait rien
        logiqueDeJeu.setCoordsCaseToucheeParLaser(x, y);  // Pour l'interface graphique
        if (caseLueContenu != null) {
            switch (caseLueContenu) {
                case "g":
                    logiqueDeJeu.getPlateau().setCase(x, y, null);
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
                    if (isReprTortue(caseLueContenu)) {
                        // Le laser a touché une tortue
                        int numeroTortueAdverse = Character.getNumericValue(caseLueContenu.charAt(1));
                        Tortue tortueAdverse = logiqueDeJeu.getJoueurs().get(numeroTortueAdverse).getTortue();

                        if (logiqueDeJeu.getNombreJoueurs() == 2) {
                            tortueAdverse.faireDemiTour(logiqueDeJeu);
                        } else {
                            Position positionDepartTortueAdverse = tortueAdverse.positionDepart;
                            deplacerTortue(logiqueDeJeu, tortueAdverse, new Position(positionDepartTortueAdverse.getX(), positionDepartTortueAdverse.getY(), positionDepartTortueAdverse.getOrientation()));
                        }
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
