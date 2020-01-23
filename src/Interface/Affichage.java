package src.Interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import src.Cartes.Carte;
import src.Cartes.TypeCarte;
import src.Interface.Affichage.Jeu;
import src.Tuiles.Joyau;
import src.Tuiles.Obstacle;
import src.Tuiles.Orientations;
import src.Joueur;
import src.LogiqueDeJeu;
import src.Parametres;
import src.Plateau;

public class Affichage extends JFrame implements Interface {
    private String action = "";
    private Carte carteSelectionnee = null;
    private Obstacle ObstacleSelectionne = null;
    private int cible = 10;

    private final Affichage fenetre = this;
    String joueur1;
    String joueur2;
    private final Image[] img = new Image[35];
    private final Image[] imgSkins = new Image[4];
    private final String[] noms = new String[4];
    private final String[] personnage = {"Non", "Pieuvre", "Requin", "Grenouille", "Tortue"};
    private final menuDeroulant liste1 = new menuDeroulant(personnage, 0, 380, 0);
    private final menuDeroulant liste2 = new menuDeroulant(personnage, 305, 380, 1);
    private final menuDeroulant liste3 = new menuDeroulant(personnage, 610, 380, 2);
    private final menuDeroulant liste4 = new menuDeroulant(personnage, 915, 380, 3);
    private final toggleButton toggleButtonTroisALaSuite = new toggleButton("Mode trois a la suite", 85, 660);
    private final toggleButton toggleButtonCarteBug = new toggleButton("     Carte Bug", 570, 660);
    private static final int CELL_SIZE = 70;
    private JPanel ecran;
    private final Menu menu;


    @Override
    public String getTypeInterface() {
        return "Affichage";
    }

    private final String[] choix = new String[4];

    public Affichage() {

        img[0] = (new ImageIcon("src/images/pieuvreAccueil.png")).getImage();
        img[1] = (new ImageIcon("src/images/requinAccueil.png")).getImage();
        img[2] = (new ImageIcon("src/images/grenouilleAccueil.png")).getImage();
        img[3] = (new ImageIcon("src/images/tortueAccueil.png")).getImage();
        img[4] = (new ImageIcon("src/images/plongeurAccueil.png")).getImage();
        img[5] = (new ImageIcon("src/images/noAccueil.png")).getImage();
        img[6] = (new ImageIcon("src/images/image de fond menu.png")).getImage();
        img[7] = (new ImageIcon("src/images/ICE.png")).getImage();
        img[8] = (new ImageIcon("src/images/WALL.png")).getImage();
        img[9] = (new ImageIcon("src/images/RUBY.png")).getImage();
        img[10] = (new ImageIcon("src/images/engrenages.png")).getImage();
        img[11] = (new ImageIcon("src/images/test2.png")).getImage();
        img[12] = (new ImageIcon("src/images/avatarPieuvre.png")).getImage();
        img[13] = (new ImageIcon("src/images/avatarRequin.png")).getImage();
        img[14] = (new ImageIcon("src/images/avatarGrenouille.png")).getImage();
        img[15] = (new ImageIcon("src/images/avatarTortue.png")).getImage();
        img[16] = (new ImageIcon("src/images/cellule2.png")).getImage();
        img[17] = (new ImageIcon("src/images/RUBYgrand.png")).getImage();
        img[18] = (new ImageIcon("src/images/carteAvancer.png")).getImage();
        img[19] = (new ImageIcon("src/images/carteGauche.png")).getImage();
        img[20] = (new ImageIcon("src/images/carteDroite.png")).getImage();
        img[21] = (new ImageIcon("src/images/carteLaser.png")).getImage();
        img[22] = (new ImageIcon("src/images/designToggleButtonON.png")).getImage();
        img[23] = (new ImageIcon("src/images/designToggleButtonOFF.png")).getImage();
        img[24] = (new ImageIcon("src/images/boutonValider.png")).getImage();
        img[25] = (new ImageIcon("src/images/victoire.png")).getImage();
        img[26] = (new ImageIcon("src/images/poissonBug.png")).getImage();
        img[28] = (new ImageIcon("src/images/poissonExecuter.png")).getImage();
        img[29] = (new ImageIcon("src/images/poissonCompleter.png")).getImage();
        img[30] = (new ImageIcon("src/images/poissonBloquer.png")).getImage();
        img[31] = (new ImageIcon("src/images/murPlateau.png")).getImage();
        img[32] = (new ImageIcon("src/images/murGlacePlateau.png")).getImage();
        img[33] = (new ImageIcon("src/images/poissonExecuterNormal.png")).getImage();
        img[34] = (new ImageIcon("src/images/poissonCompleterNormal.png")).getImage();
        img[35] = (new ImageIcon("src/images/poissonBloquerNormal.png")).getImage();

        this.setIconImage(new ImageIcon("src/images/icon.png").getImage());
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(1299, 825);
        this.setTitle("Robot Turtles");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu = new Menu();

    }


    public class Menu extends JPanel {
        public Menu panelMenu = this;
        private String modeJeu;
        private boolean modeBug;
        private boolean validation;


        Menu() {
            validation = false;
            modeJeu = "normal";
            modeBug = false;
            add(liste1);
            add(liste2);
            add(liste3);
            add(liste4);

            add(toggleButtonTroisALaSuite);
            add(toggleButtonCarteBug);
            Bouton valider = new Bouton("valider", 300, 250, 950, 620, Color.blue);
            add(valider);
            System.out.println(toggleButtonTroisALaSuite.isSelected());


        }

        public void paintComponent(Graphics g) {
            g.drawImage(img[6], 0, 0, this);
            g.setFont(new Font("Anton Bold DB", Font.PLAIN, 200));
            g.setColor(Color.GREEN);
            g.drawString("Robot Turtles ", 40, 200);
            g.setFont(new Font("Anton Bold DB", Font.ITALIC, 60));
            g.setColor(Color.red);
            g.drawString("DELUXE EDITION SPECIAL 2002", 200, 270);

            g.setFont(new Font("Anton Bold DB", Font.BOLD, 40));
            g.setColor(Color.white);
            g.drawString("Veuillez choisir les joueurs :", 15, 340);
            for (int i = 0; i < 4; i++) {
                if (Objects.equals(choix[i], "Pieuvre")) {
                    g.drawImage(img[0], 60 + 305 * i, 380, this);
                } else if (Objects.equals(choix[i], "Requin")) {
                    g.drawImage(img[1], 60 + 305 * i, 360, this);
                } else if (Objects.equals(choix[i], "Grenouille")) {
                    g.drawImage(img[2], 60 + 305 * i, 390, this);
                } else if (Objects.equals(choix[i], "Tortue")) {
                    g.drawImage(img[3], 60 + 305 * i, 370, this);
                } else {
                    g.drawImage(img[5], 60 + 305 * i, 360, this);

                }
            }
            toggleButtonTroisALaSuite.repaint();

        }

        void setModeDeJeu(String ModeJeu) {
            this.modeJeu = ModeJeu;
        }

        void setCarteBug(boolean bug) {
            this.modeBug = bug;
        }

        boolean getCarteBug() {
            return modeBug;
        }

        String getModeDeJeu() {
            return modeJeu;
        }

        void setValidation(boolean valide) {
            this.validation = valide;
        }

        boolean getValidation() {
            return validation;
        }

    }

    //Classe cr�ant les toggleButtons
    private class toggleButton extends JToggleButton {
        private final String texte;
        private final int posx;
        private final int posy;
        private int state;


        toggleButton(String str, int posx, int posy) {
            //on prend en parametre le nom du bouton, et sa position
            super();
            this.posx = posx;
            this.posy = posy;
            this.texte = str;

            ItemListener itemListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    state = itemEvent.getStateChange();
                    if (state == ItemEvent.SELECTED) {
                        if (Objects.equals(texte, "Mode trois a la suite")) {
                            menu.setModeDeJeu("3alasuite");
                            System.out.println("3ala");
                        } else {
                            menu.setCarteBug(true);
                            System.out.println("bug");

                        }
                    } else {
                        if (Objects.equals(texte, "Mode trois � la suite")) {
                            System.out.println("!3ala");
                            menu.setModeDeJeu("normal");
                        } else {
                            menu.setCarteBug(false);
                            System.out.println("!bug");

                        }
                    }
                    fenetre.repaint();
                }
            };

            this.addItemListener(itemListener);
        }

        public void paintComponent(Graphics g) {
            g.setColor(Color.white);
            if (state == ItemEvent.SELECTED) {
                g.drawImage(img[22], 0, 0, null);
                g.drawString(texte + " ON", 65, 30);

            } else {
                g.drawImage(img[23], 0, 0, null);
                g.drawString(texte + " OFF", 65, 30);

            }
            this.setLocation(posx, posy);
            this.setSize(250, 70);
        }
    }

    //Classe cr�ant nos menu d�roulants
    private class menuDeroulant extends JComboBox implements ActionListener {
        private String[] personnage;
        private final int posx;
        private final int posy;
        private final int compteur;    //compteur permet de savoir quel menu repr�sente quel joueur


        menuDeroulant(String[] str, int posx, int posy, int compteur) {
            //on prend en parametre le nom du bouton, et sa position

            super();
            this.posx = posx;
            this.posy = posy;
            this.compteur = compteur;
            for (int i = 0; i < str.length; i++)
                this.addItem(str[i]);
            this.setSelectedIndex(0);
            addActionListener(this);
            this.setUI(ui);
        }

        public void paintComponent(Graphics g) {

            g.setColor(Color.black);
            this.setLocation(posx, posy);
            this.setSize(345, 180);

        }

        public void actionPerformed(ActionEvent e) {
            fenetre.repaint();
            String personnage = (String) this.getSelectedItem();
            System.out.println(personnage);
            choix[compteur] = personnage;


        }
    }

    private boolean persoDiff(ArrayList<Integer> personnageJeu) {
        for (int i = 1; i < personnage.length; i++) {
            if (Collections.frequency(personnageJeu, i) > 1) {
                return false;
            }
        }
        return true;
    }

    public Parametres parametresMenu() {
        ArrayList<Integer> perso = new ArrayList<>();
        perso.add(liste1.getSelectedIndex());
        perso.add(liste2.getSelectedIndex());
        perso.add(liste3.getSelectedIndex());
        perso.add(liste4.getSelectedIndex());

        int nbJoueurs = 0;
        fenetre.setContentPane(menu);
        System.out.println("1");
        while (!menu.getValidation() || !persoDiff(perso)) {
            try {                                    //sans mettre d'instructions dans le while �a fonctionne pas donc je met un thread sleep
                Thread.sleep(20);
                perso.set(0, liste1.getSelectedIndex());
                perso.set(1, liste2.getSelectedIndex());
                perso.set(2, liste3.getSelectedIndex());
                perso.set(3, liste4.getSelectedIndex());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("3");


        Collections.sort(perso, Collections.reverseOrder());

        int i = 0;
        while (i < 4 && perso.get(i) != 0) {
            System.out.println(perso.get(i));
            imgSkins[i] = img[(perso.get(i) + 11)];
            noms[i] = personnage[perso.get(i)];

            i++;
            nbJoueurs++;
        }
        if (nbJoueurs < 2) {
            JOptionPane.showMessageDialog(null, "Vous ne pouvez pas lancer une partie avec moins de 2 joueurs", "Alerte", JOptionPane.ERROR_MESSAGE);
            System.exit(2);
        }
        Parametres parametres = new Parametres(nbJoueurs, menu.getModeDeJeu(), menu.getCarteBug());
        menu.removeAll();

        return parametres;
    }

    public void afficherPlateau(LogiqueDeJeu etatDuJeu) {
        ecran = new Jeu(etatDuJeu.isModeBug(), etatDuJeu.getPlateau(), etatDuJeu.getJoueurs(), etatDuJeu.getJoueurCourant(), etatDuJeu.getModeJeu(), etatDuJeu.getNombreJoueurs(), etatDuJeu.getJoyaux());

        fenetre.setContentPane(ecran);
        fenetre.revalidate();
        fenetre.repaint();
    }

    public String demanderAction(LogiqueDeJeu LogiqueDeJeu) {
        while (Objects.equals(action, "")) {
            try {                                    //sans mettre d'instructions dans le while �a fonctionne pas
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String actionEnvoye = action;
        action = "";
        return actionEnvoye;
    }

    //Dessine le jeu lorsqu'une partie est en cours
    class Jeu extends JPanel {
        Plateau plateau;
        ArrayList<Joueur> joueurs;
        Joueur joueurCourant;
        String modeJeu;
        int nbJoueurs;
        final boolean bug;
        ArrayList<Joyau> joyaux;


        Jeu(boolean bug, Plateau plateau, ArrayList<Joueur> joueurs, Joueur joueurCourant, String modeJeu, int nbJoueurs, ArrayList<Joyau> joyaux) {
            this.plateau = plateau;
            this.joueurs = joueurs;
            this.joueurCourant = joueurCourant;
            this.modeJeu = modeJeu;
            this.nbJoueurs = nbJoueurs;
            this.joyaux = joyaux;
            this.bug = bug;
            //On ajoute les boutons de controle
            if (bug) {
                Bouton boutonBug = new Bouton("Bug", 285, 120, 710, 680, new Color(255, 36, 25));
                Bouton boutonC = new Bouton("Completer", 285, 120, 995, 560, new Color(255, 36, 25));
                Bouton boutonE = new Bouton("Executer", 285, 120, 995, 680, new Color(233, 76, 54));
                Bouton boutonB = new Bouton("Bloquer", 285, 120, 710, 560, new Color(254, 99, 155));
                add(boutonC);
                add(boutonE);
                add(boutonB);
                add(boutonBug);
            } else {
                Bouton boutonC = new Bouton("Completer", 190, 240, 900, 560, new Color(255, 36, 25));
                Bouton boutonE = new Bouton("Executer", 190, 240, 1090, 560, new Color(233, 76, 54));
                Bouton boutonB = new Bouton("Bloquer", 190, 240, 710, 560, new Color(254, 99, 155));
                add(boutonC);
                add(boutonE);
                add(boutonB);
            }
        }

        public Plateau getPlateau() {
            return plateau;
        }

        public void setPlateau(Plateau plateau) {
            this.plateau = plateau;
        }

        public ArrayList<Joueur> getJoueurs() {
            return joueurs;
        }

        public void setJoueurs(ArrayList<Joueur> joueurs) {
            this.joueurs = joueurs;
        }

        public Joueur getJoueurCourant() {
            return joueurCourant;
        }

        public void setJoueurCourant(Joueur joueurCourant) {
            this.joueurCourant = joueurCourant;
        }

        public String getModeJeu() {
            return modeJeu;
        }

        public void setModeJeu(String modeJeu) {
            this.modeJeu = modeJeu;
        }

        public int getNbJoueurs() {
            return nbJoueurs;
        }

        public void setNbJoueurs(int nbJoueurs) {
            this.nbJoueurs = nbJoueurs;
        }


        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g.drawImage(img[11], 0, 0, this);


            //dessine les cases en fonctions de leurs valeurs
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    String valeurCase = plateau.getCase(i, j);
                    int cell;
                    if (Objects.equals(valeurCase, "p")) {    //p correspond au mur de pierre
                        cell = 31;
                    } else if (Objects.equals(valeurCase, "g")) { //g correspond au mur de glace
                        cell = 32;
                    } else {
                        cell = 16;
                    }
                    g.drawImage(img[cell], (j * CELL_SIZE + 360),
                            (i * CELL_SIZE), this);

                }

            }
            for (int i = 0; i < nbJoueurs; i++) {
                if (!joueurs.get(i).isFini()) {
                    int positionX = joueurs.get(i).getTortue().getPosition().getX();
                    int positionY = joueurs.get(i).getTortue().getPosition().getY();
                    System.out.println(positionX);
                    System.out.println(positionY);
                    System.out.println(noms[i]);
                    double orientation = Math.PI;
                    if (joueurs.get(i).getTortue().getPosition().getOrientation() == Orientations.UP) {
                        orientation = 0;
                    } else if (joueurs.get(i).getTortue().getPosition().getOrientation() == Orientations.LEFT) {
                        orientation = 3 * (Math.PI) / 2;
                    } else if (joueurs.get(i).getTortue().getPosition().getOrientation() == Orientations.RIGHT) {
                        orientation = (Math.PI) / 2;
                    }
                    System.out.println(orientation);
                    AffineTransform rotation = new AffineTransform();
                    rotation.translate(360 + (CELL_SIZE * positionY), CELL_SIZE * positionX);
                    rotation.rotate(orientation, CELL_SIZE / 2, CELL_SIZE / 2);
                    g2d.drawImage(imgSkins[i], rotation, null);
                }

                //rotation.rotate(-orientation, (360 + CELL_SIZE * positionY+70)/2, (CELL_SIZE*positionX+70)/2);
                //	rotation.translate(-(360 + CELL_SIZE * positionY),-( CELL_SIZE*positionX));
                //g2d.rotate(orientation, (360 + CELL_SIZE * positionY+70)/2, (CELL_SIZE*positionX+70)/2);
                //g.drawImage(imgSkins[i],360 + CELL_SIZE * positionY, CELL_SIZE*positionX,ecran);
                //g2d.rotate(2*Math.PI-orientation, (360 + CELL_SIZE * positionY+70)/2, (CELL_SIZE*positionX+70)/2);


            }
            //dessine joyaux
            for (int i = 0; i < joyaux.size(); i++) {
                int positionX = joyaux.get(i).getPosition().getX();
                int positionY = joyaux.get(i).getPosition().getY();
                g.drawImage(img[17], 360 + CELL_SIZE * positionY, CELL_SIZE * positionX, ecran);

            }
            //dessine la main
            for (int i = 0; i < joueurCourant.getCartesMain().getCartesMain().size(); i++) {
                int dessin = 0;
                Carte carteMain = joueurCourant.getCartesMain().getCartesMain().get(i);
                if (carteMain.getTypeCarte() == TypeCarte.CARTE_BLEUE) {
                    dessin = 18;
                } else if (carteMain.getTypeCarte() == TypeCarte.CARTE_JAUNE) {
                    dessin = 19;
                } else if (carteMain.getTypeCarte() == TypeCarte.CARTE_VIOLETTE) {
                    dessin = 20;
                } else if (carteMain.getTypeCarte() == TypeCarte.LASER) {
                    dessin = 21;
                }
                g.drawImage(img[dessin], 25 + (135 * i), 600, this);
            }
            //Rectangle de gauche affichant l'état de la partie
            g.setColor(Color.black);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
            if (Objects.equals(modeJeu, "3alasuite")) {
                g.drawString("Mode de jeu : Trois a la suite - Partie 1/3", 10, 20);
            } else {
                g.drawString("Mode de jeu : Partie normale", 10, 20);
            }
            //On affiche les données correspondantes aux autres joueurs
            int j = 0;
            for (int i = 0; i < nbJoueurs; i++) {
                if (joueurCourant.getNumeroJoueur() != i) {
                    //	if(joueur[i] != joueurActif)
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 40));
                    g.drawString(noms[i], 15, 60 + j * 200);

                    g.drawImage(img[7], 10, 75 + j * 200, this); //le mur de glace
                    g.drawString("x" + joueurs.get(i).getMursDeGlace(), 65, 110 + j * 200);        //le nombre de murs de glace
                    g.drawImage(img[8], 115, 75 + j * 200, this); //le mur de pierre
                    g.drawString("x" + joueurs.get(i).getMursDePierre(), 170, 110 + j * 200);        //le nombre de murs de pierre
                    g.drawImage(img[10], 210, 75 + j * 200, this); //l'instruction
                    g.drawString("x" + joueurs.get(i).getProgramme().getProgramme().size(), 265, 110 + j * 200);        //le nombre d'instructions
                    if (Objects.equals(modeJeu, "3alasuite")) {
                        g.drawImage(img[9], 10, 140 + j * 200, this); //Joyau
                        g.drawString(joueurs.get(i).getScore() + "points", 65, 175 + j * 200);        //le nombre de murs de pierre
                    }
                    j++;
                    //On affiche les données correspondantes au joueur actif
                } else {
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
                    g.drawString(noms[j], 950, 50); //On affiche le nom du joueur
                    g.drawImage(img[7], 950, 100, this); //le mur de glace
                    g.drawString("x" + joueurCourant.getMursDeGlace(), 1025, 135);        //le nombre de murs de glace
                    g.drawImage(img[8], 950, 200, this); //le mur de pierre
                    g.drawString("x" + joueurCourant.getMursDePierre(), 1025, 235);        //le nombre de murs de pierre
                    g.drawImage(img[10], 950, 300, this); //Instruction
                    g.drawString("x" + joueurCourant.getProgramme().getProgramme().size(), 1025, 335);        //Le nombre d'instructions
                    if (Objects.equals(modeJeu, "3alasuite")) {
                        g.drawImage(img[9], 950, 400, this); //joyau
                        g.drawString(joueurs.get(j).getScore() + "points", 1025, 435);        //Le nombre de points

                    }
                }
            }
        }

        //Classe responsable de la cr�ation des boutons de choix d'action

    }

    class Bouton extends JButton implements MouseListener {
        private final String name;
        private final int posx;
        private final int sizex;
        private final int sizey;
        private final int posy;
        private final Color color;

        Bouton(String str, int sizex, int sizey, int posx, int posy, Color color) {
            //on prend en parametre le nom du bouton, et sa position
            super(str);
            this.posx = posx;
            this.posy = posy;
            this.name = str;
            this.color = color;
            this.sizex = sizex;
            this.sizey = sizey;
            addMouseListener(this);
        }

        public void paintComponent(Graphics g) {
            // refaire cette fonction correctement
            this.setSize(sizex, sizey);
            this.setLocation(posx, posy);
            g.setColor(color);
            if (Objects.equals(this.name, "valider")) {
                this.setBorderPainted(false);
                g.drawImage(img[24], 0, 0, null);
            }
            if (sizex == 285) {
                if (Objects.equals(this.name, "Bloquer")) {
                    g.drawImage(img[30], 0, 0, null);
                } else if (Objects.equals(this.name, "Executer")) {
                    g.drawImage(img[29], 0, 0, null);
                } else if (Objects.equals(this.name, "Completer")) {
                    g.drawImage(img[28], 0, 0, null);
                } else if (Objects.equals(this.name, "Bug")) {
                    g.drawImage(img[26], 0, 0, null);
                }
            } else if (sizex == 190) {
                g.setColor(color);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                g.setColor(Color.BLACK);
                g.drawString(this.name, (this.getWidth() / 2) - 25, (this.getHeight() / 2) + 5);
            }
        }


        @Override
        public void mouseClicked(MouseEvent arg0) {
            if (Objects.equals(this.name, "Bloquer")) {
                action = "M";
            } else if (Objects.equals(this.name, "Executer")) {
                action = "E";
            } else if (Objects.equals(this.name, "Completer")) {
                action = "P";
            } else if (Objects.equals(this.name, "Bug")) {
                action = "B";
            } else if (Objects.equals(this.name, "valider")) {
                menu.setValidation(true);
            }
            fenetre.repaint();
        }

        // TODO Auto-generated method stub


        @Override
        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub
            fenetre.repaint();
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            // TODO Auto-generated method stub
            fenetre.repaint();
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            // TODO Auto-generated method stub
            fenetre.repaint();

        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            // TODO Auto-generated method stub
            fenetre.repaint();

        }

    }

    //La fonction qui se lance quand on appuie sur completer
    public void afficherCartesMain(String title, LogiqueDeJeu logiqueDeJeu) {
        ArrayList<Carte> cartesMain = logiqueDeJeu.getJoueurCourant().getCartesMain().getCartesMain();
        JFrame fenetreCompleter = new JFrame();
        fenetreCompleter.setVisible(true);
        fenetreCompleter.setResizable(true);
        fenetreCompleter.setSize(800, 300);
        fenetreCompleter.setTitle(title);
        fenetreCompleter.setLocationRelativeTo(null);
        fenetreCompleter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fenetreCompleter.setLayout(new FlowLayout());
        ArrayList<BoutonCompleter> boutonsCartes = new ArrayList<>(); //Liste dans laquelle on stock les boutons

        for (int i = 0; i < cartesMain.size(); i++) {
            boutonsCartes.add(i, new BoutonCompleter(fenetreCompleter, cartesMain.get(i)));
            fenetreCompleter.add(boutonsCartes.get(i));
        }
        JButton valide = new JButton("valider");
        valide.setSize(fenetreCompleter.getWidth(), (int) (fenetreCompleter.getWidth() * 0.15));
        valide.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                carteSelectionnee = new Carte(TypeCarte.NOT_A_CARD);
                fenetreCompleter.dispose();

            }
        });
        fenetreCompleter.add(valide);
    }

    private String selectionnerCarte() {
        while (carteSelectionnee == null) {
            try {                                    //sans mettre d'instructions dans le while sa fonctionne pas donc je met un thread sleep
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String renvoie = carteSelectionnee.getTypeCarte().toString();
        carteSelectionnee = null;
        return renvoie;
    }
    //La fonction qui se lance quand on appuie sur bloquer

    public Obstacle demanderObstacleAPlacer() {
        JFrame fenetreBloquer = new JFrame();
        fenetreBloquer.setVisible(true);
        fenetreBloquer.setResizable(true);
        fenetreBloquer.setSize(800, 200);
        fenetreBloquer.setTitle("Completer le programme");
        fenetreBloquer.setLocationRelativeTo(null);
        fenetreBloquer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fenetreBloquer.setLayout(new FlowLayout());
        JToggleButton choixMur = new JToggleButton("Mur de glace");
        ItemListener itemListenerChoixMur = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    choixMur.setText("Mur de pierre"); // show your message here
                    System.out.println("bouton pressedd");
                } else {
                    choixMur.setText("Mur de glace"); // show your message here
                }
                choixMur.repaint();
            }
        };
        choixMur.addItemListener(itemListenerChoixMur);
        choixMur.setSize(200, 200);
        fenetreBloquer.add(choixMur);
        String[] position = {"1", "2", "3", "4", "5", "6", "7", "8"};

        JComboBox<String> positionX = new JComboBox<>(position);
        JComboBox<String> positionY = new JComboBox<>(position);
        positionX.setMaximumSize(new Dimension(300, 150));
        fenetreBloquer.add(positionX);
        fenetreBloquer.add(positionY);
        JButton valide = new JButton("valider");
        valide.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                String typeMur = "g";
                int[] coord = {positionX.getSelectedIndex(), positionY.getSelectedIndex()};
                if (Objects.equals(choixMur.getText(), "Mur de pierre")) {
                    typeMur = "p";
                }
                ObstacleSelectionne = new Obstacle(typeMur, coord);
                fenetreBloquer.dispose();


            }

        });
        fenetreBloquer.add(valide);
        fenetreBloquer.repaint();
        while (ObstacleSelectionne == null) {
            try {                                    //sans mettre d'instructions dans le while �a fonctionne pas donc je met un thread sleep
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Obstacle ObstacleRetour = ObstacleSelectionne;
        ObstacleSelectionne = null;
        return ObstacleRetour;

    }

    public int demanderCibleCarteBug(LogiqueDeJeu logiqueDeJeu) {
        cible = 10;
        JFrame fenetreBug = new JFrame();
        fenetreBug.setVisible(true);
        fenetreBug.setResizable(true);
        fenetreBug.setSize(85 * (logiqueDeJeu.getNombreJoueurs() - 1), 150);
        fenetreBug.setTitle("Cible de la carte Bug");
        fenetreBug.setLocationRelativeTo(null);
        fenetreBug.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fenetreBug.setLayout(new FlowLayout());
        int j = 0;
        for (int i = 0; i < logiqueDeJeu.getNombreJoueurs(); i++) {
            if (i != logiqueDeJeu.getJoueurCourant().getNumeroJoueur()) {
                fenetreBug.add(new BoutonBug(fenetreBug, j * 75, logiqueDeJeu.getNombreJoueurs(), i, noms[i]));
                j++;
            }
        }
        while (cible == 10) {
            try {                                    //sans mettre d'instructions dans le while �a fonctionne pas
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fenetreBug.dispose();
        return cible;
    }
    //ArrayList<BoutonBug> listeBug = new ArrayList<BoutonBug>();
    // listeBug.add(new BoutonBug(0, logiqueDeJeu.getNombreJoueurs(), 0, noms[0]));
    //listeBug.add(new BoutonBug(0, logiqueDeJeu.getNombreJoueurs(), 0, noms[0]));
    //listeBug.add(new BoutonBug(0, logiqueDeJeu.getNombreJoueurs(), 0, noms[0]));
    // listeBug.add(new BoutonBug(0, logiqueDeJeu.getNombreJoueurs(), 0, noms[0]));

    // for (int i = 0; i < logiqueDeJeu.getNombreJoueurs(); i++) {
    //     if (i != logiqueDeJeu.getJoueurCourant().getNumeroJoueur()) {
    //    	listeBug.get(i).setPosx(j*300);
    //    	listeBug.get(i).setIdJoueur(i);
    //   	listeBug.get(i).setName(noms[i]);
    //       j++;
    //       listeBug.get(i).repaint();
    //   }


    private class BoutonBug extends JButton implements MouseListener {
        private String name;
        private int idJoueur;
        int posx;
        private final JFrame fenetreBug;

        BoutonBug(JFrame fenetreBug, int posx, int nbJoueurs, int id, String name) {
            System.out.println(name);
            this.posx = posx;
            this.name = name;
            this.idJoueur = id;
            this.fenetreBug = fenetreBug;
            addMouseListener(this);
        }

        public void paintComponent(Graphics g) {
            this.setSize(70, 70);
            this.setLocation(posx, 10);
            this.setName(name);
            g.drawImage(imgSkins[idJoueur], 0, 0, null);
            this.setBorderPainted(false);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(cible);
            cible = getIdJoueur();
            System.out.println(cible);
            fenetreBug.setVisible(false);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            System.out.println(this.name);
            fenetreBug.repaint();
            // TODO Auto-generated method stub
        }

        @Override
        public void mouseExited(MouseEvent e) {
            fenetreBug.repaint();

        }

        @Override
        public void mousePressed(MouseEvent e) {
            fenetreBug.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            fenetreBug.repaint();

        }

        int getIdJoueur() {
            return idJoueur;
        }

        public void setIdJoueur(int idJoueur) {
            this.idJoueur = idJoueur;
        }

        public void setPosx(int posX) {
            this.posx = posX;
        }

        public void setName(String nom) {
            this.name = nom;
        }

        public String getName() {
            return name;
        }

        public int getPosx() {
            return posx;
        }
    }

    //Classe responsable de la creation des boutons de choix d'action
    private class BoutonCompleter extends JButton implements MouseListener {
        final BoutonCompleter ceBouton;
        final Carte carte;
        private final String name;
        private final JFrame fenetreC;


        BoutonCompleter(JFrame fenetreC, Carte carte) {
            //on prend en parametre le nom du bouton
            super();
            ceBouton = this;
            this.carte = carte;
            this.name = carte.getTypeCarte().toString();
            boolean visible = true;
            this.fenetreC = fenetreC;
            addMouseListener(this);
            this.setName(name);
            int hauteur = (int) (fenetreC.getHeight() * 0.75);
            int longueur = (int) (fenetreC.getWidth() * 0.185);
            this.setPreferredSize(new Dimension(longueur, hauteur));
        }

        public void paintComponent(Graphics g) {
            int dessin = 0;
            this.setSize(130, 200);
            if (Objects.equals(name, "CARTE_BLEUE")) {
                dessin = 18;
            }
            if (Objects.equals(name, "CARTE_JAUNE")) {
                dessin = 19;
            }
            if (Objects.equals(name, "CARTE_VIOLETTE")) {
                dessin = 20;
            }
            if (Objects.equals(name, "LASER")) {
                dessin = 21;
            }

            g.drawImage(img[dessin], 0, 0, this);

        }

        @Override
        public void mouseClicked(MouseEvent arg0) {
            carteSelectionnee = carte;
            fenetreC.remove(ceBouton);
            fenetreC.revalidate();
            fenetreC.repaint();
        }

        // TODO Auto-generated method stub


        @Override
        public void mouseEntered(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            // TODO Auto-generated method stub

        }
    }

    public void afficherFinManche(LogiqueDeJeu logiqueDeJeu) {
        Victoire ecranVictoire = new Victoire(logiqueDeJeu);
        fenetre.setContentPane(ecranVictoire);
    }

    class Victoire extends JPanel {
        final LogiqueDeJeu logiqueDeJeu;

        Victoire(LogiqueDeJeu logiqueDeJeu) {
            this.logiqueDeJeu = logiqueDeJeu;

        }

        public void paintComponent(Graphics g) {
            g.drawImage(img[25], 0, 0, this);
            g.setFont(new Font("Anton Bold DB", Font.PLAIN, 100));
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < logiqueDeJeu.getNombreJoueurs(); i++) {
                g.drawString(noms[logiqueDeJeu.getJoueurs().get(i).getNumeroJoueur()], 50, 100 + 200 * i);
                g.drawImage(imgSkins[logiqueDeJeu.getJoueurs().get(i).getNumeroJoueur()], 1100, 50 + 200 * i, null);
                g.drawString("place :" + logiqueDeJeu.getJoueurs().get(i).getClassement(), 600, 100 + 200 * i);
            }
        }
    }

    public void afficherResultats(LogiqueDeJeu a) {
        // 		ecran.removeAll();
        Victoire ecranVictoire = new Victoire(a);
        fenetre.setContentPane(ecranVictoire);
        fenetre.revalidate();
        fenetre.repaint();

    }

    public void afficherMessage(String str) {
        JOptionPane.showMessageDialog(null, str, "Alerte", JOptionPane.ERROR_MESSAGE);
    }


    @Override
    public String demanderCarteAAjouterAProgramme() {
        return selectionnerCarte();
    }


    @Override
    public String demanderChoixDefausse() {
        return selectionnerCarte();
    }


    @Override
    public void afficherProgramme(LogiqueDeJeu logiqueDeJeu) {
        // TODO Auto-generated method stub

    }

    public void actualiser() {
        fenetre.revalidate();
        fenetre.repaint();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void afficherFinManche(LogiqueDeJeu logiqueDeJeu, int i) {
        // TODO Auto-generated method stub

    }
}

