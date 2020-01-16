package src.Interface;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import src.Cartes.Carte;
import src.Cartes.TypeCarte;
import src.Tuiles.Orientations;
import src.Tuiles.Position;
import src.Joueur;
import src.LogiqueDeJeu;

public class Affichage extends JFrame {
	public Affichage fenetre = this;
	String joueur1;
	String joueur2;
	Image[] img = new Image[20];
	Image[] imgSkins = new Image[4];
	String[] personnage = {"Non","pieuvre", "requin", "grenouille","tortue","plongeur"};
	menuDeroulant liste1 = new menuDeroulant(personnage,0,650,0);
	menuDeroulant liste2 = new menuDeroulant(personnage,305,650,1);
	menuDeroulant liste3 = new menuDeroulant(personnage,610,650,2);
	menuDeroulant liste4 = new menuDeroulant(personnage,915,650,3);
	public int cell;
	public static final int CELL_SIZE =70;
	public JPanel ecran;

	public String[] choix = new String[4];
	  public Affichage() {
		  
			img[0] = (new ImageIcon("src/images/pieuvreAccueil.png")).getImage();
			img[1] = (new ImageIcon("src/images/requinAccueil.png")).getImage();
			img[2] = (new ImageIcon("src/images/grenouilleAccueil.png")).getImage();
			img[3] = (new ImageIcon("src/images/tortueAccueil.png")).getImage();
			img[4] = (new ImageIcon("src/images/plongeurAccueil.png")).getImage();
			img[5] = (new ImageIcon("src/images/noAccueil.png")).getImage();
			img[6] = (new ImageIcon("src/images/test1.jpg")).getImage();
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



		  	this.setVisible(true);
	        this.setResizable(true);
	        this.setSize(1280, 960);
	        this.setTitle("Robot Turtles");
	        this.setLocationRelativeTo(null);
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        ecran = new Menu();
	        this.setContentPane(ecran);
	        ecran.setVisible(true);


	        }


	  public class Menu extends JPanel {
		  public Menu panelMenu = this;

			public Menu() {
				add(liste1);
				add(liste2);
				add(liste3);
				add(liste4);

				toggleButton toggleButtonTroisALaSuite = new toggleButton("Mode trois à la suite",100,400);
				toggleButton toggleButtonCarteBug = new toggleButton("Carte Bug",400,400);
				add(toggleButtonTroisALaSuite);
				add(toggleButtonCarteBug);
				System.out.println(toggleButtonTroisALaSuite.isSelected());
		        JButton valide = new JButton("valider");
			    valide.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					lancementJeu(panelMenu);
				}
			    });
		        add(valide);


			}
			public void paintComponent(Graphics g) {
				g.drawImage(img[6],0,0,this);
				g.setFont(new Font("Anton Bold DB", Font.PLAIN, 200)); 
				g.setColor(Color.GREEN);
				g.drawString("Robot Turtle ",100, 200);
				g.setFont(new Font("Anton Bold DB", Font.ITALIC, 60)); 
				g.setColor(Color.red);
				g.drawString("DELUXE EDITION",400, 270);

				g.setFont(new Font("Anton Bold DB", Font.BOLD, 40)); 
				g.setColor(Color.black);
				g.drawString("Veuillez choisir les joueurs :",15, 580);

				for(int i=0;i<4;i++) {
				if(choix[i] == "pieuvre") {
					g.drawImage(img[0], 60 + 305*i, 600  , this); 
				}
				else if(choix[i]== "requin") {
					g.drawImage(img[1], 60 +305*i, 600  , this); 
				}
				else if(choix[i]== "grenouille") {
					g.drawImage(img[2], 60 +305*i, 600  , this); 
				}
				else if(choix[i]== "tortue") {
					g.drawImage(img[3], 60 +305*i, 600  , this); 
				}
				else if(choix[i]== "plongeur") {
					g.drawImage(img[4], 60 +305*i, 600  , this); 
				}
				else {
					g.drawImage(img[5], 60 +305*i, 600  , this); 

				}

			}
			}
		}
	  //Classe créant les toggleButtons
	  private class toggleButton extends JToggleButton {
			 private String texte;
			 private int posx;
			 private int posy;


			public toggleButton(String str,int posx, int posy) {
				//on prend en parametre le nom du bouton, et sa position
				    super();
				    this.posx= posx;
				    this.posy = posy;
				    this.texte = str;
				    ItemListener itemListener = new ItemListener() {
						@Override
				        public void itemStateChanged(ItemEvent itemEvent) {
				            int state = itemEvent.getStateChange();
				            if (state == ItemEvent.SELECTED) {
				                System.out.println("Selected"); // show your message here
				            } else {
				                System.out.println("Deselected"); // remove your message
				            }
				        }

				    };
				    this.addItemListener(itemListener);

	  }
			  public void paintComponent(Graphics g){
				  	g.setColor(Color.black);
				  	this.setFocusPainted(false);
				  	this.setText(this.texte);
					this.setLocation(posx, posy);
					this.setSize(150, 50);
					
					g.drawString(texte, posx, posy);
				  }
	  }
	  //Classe créant nos menu déroulants
	  private class menuDeroulant extends JComboBox implements ActionListener {
			 private String[] personnage;
			 private int posx;
			 private int posy;
			 private int compteur;	//compteur permet de savoir quel menu représente quel joueur 

			 
			public menuDeroulant(String[] str,int posx, int posy,int compteur) {
				//on prend en parametre le nom du bouton, et sa position
					
				    super();
				    this.posx= posx;
				    this.posy = posy;
				    this.compteur=compteur;
				    for(int i=0;i<str.length;i++)
				    this.addItem(str[i]);
				    this.setSelectedIndex(1);
				    addActionListener(this);

			}
			  public void paintComponent(Graphics g){
				  	g.setColor(Color.black);
					this.setLocation(posx, posy);
					this.setSize(345, 260);
				
				  }
			    public void actionPerformed(ActionEvent e) {
			        String personnage = (String)this.getSelectedItem();
			        System.out.println(personnage);
			        choix[compteur] = personnage;
			       	repaint();
	  
	    
	  }
}
	  private void lancementJeu(JPanel panel) {
		  ArrayList<Integer> perso = new ArrayList();
		  perso.add(liste1.getSelectedIndex());
		  perso.add(liste2.getSelectedIndex());
		  perso.add(liste3.getSelectedIndex());
		  perso.add(liste4.getSelectedIndex());
		  if(perso.get(0) != perso.get(1) && perso.get(1) != perso.get(2) &&  perso.get(2) != perso.get(3))   {
		  imgSkins[0] = img[(liste1.getSelectedIndex()+12)];
		  imgSkins[1] = img[(liste2.getSelectedIndex()+12)];
		  imgSkins[2] = img[(liste3.getSelectedIndex()+12)];
		  imgSkins[3] = img[(liste4.getSelectedIndex()+12)];

		  panel.removeAll();
		  ecran = new Jeu();
		  fenetre.setContentPane(ecran);
			fenetre.revalidate();
			fenetre.repaint(); }
		  else {
				    JFrame frame = new JFrame("showMessageDialog");
				    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				    JOptionPane.showMessageDialog(frame,
				            "Veuillez prendre des personnages différents afin de pouvoir commencer une partie",
				            "Erreur",
				            JOptionPane.ERROR_MESSAGE);
				}
		  LogiqueDeJeu.nombreJoueurs = 4;
		  }
		 
		  // panel.setVisible(false);

	  //Dessine le jeu lorsqu'une partie est en cours
	  public class Jeu extends JPanel {
		  
		  public Jeu() {
			  
			  //On ajoute les boutons de controle
				Bouton boutonC = new Bouton("Completer",900,560,new Color(255,36,25));
				Bouton boutonE = new Bouton(" Exécuter",1090,560,new Color(233,76,54));
				Bouton boutonB = new Bouton("  Bloquer",710,560, new Color(254,99,155));
				add(boutonC);
				add(boutonE);
				add(boutonB);


		  }
		  
		  public void paintComponent(Graphics g) {
			  Graphics2D g2d =(Graphics2D) g;
			  g.drawImage(img[11],0,0,this);

				//dessine les cases en fonctions de leurs valeurs
				for (int i=0; i< 8;i++) {
					for (int j=0; j<8;j++) {
						String valeurCase = LogiqueDeJeu.plateau.getCase(i, j);
						if (valeurCase == "p") {	//p correspond au mur de pierre
							cell = 8;
						}
						else if (valeurCase =="g") { //g correspond au mur de glace
							cell =7;
						}

						else {
							cell =16;
						}
						g.drawImage(img[cell], (j * CELL_SIZE +360),
							    (i * CELL_SIZE), this);

					}
				}
				  for(int i=0;i<LogiqueDeJeu.nombreJoueurs-1;i++) {
					int positionX =LogiqueDeJeu.joueurs.get(i).getTortue().getPosition().x;
					int positionY =LogiqueDeJeu.joueurs.get(i).getTortue().getPosition().y;
					int orientation = 180 ;
					if (LogiqueDeJeu.joueurs.get(i).getTortue().getPosition().orientation == Orientations.UP) {
						orientation = 180;  
					}
					else if (LogiqueDeJeu.joueurs.get(i).getTortue().getPosition().orientation == Orientations.LEFT) {
						orientation = 90;
					}
					else if (LogiqueDeJeu.joueurs.get(i).getTortue().getPosition().orientation == Orientations.RIGHT) {
						orientation = 270;
					}
					g2d.rotate(orientation, (positionX+70)/2, (positionY+70)/2);	//On effectue la rotation au centre de notre image
					g.drawImage(imgSkins[i],360 + CELL_SIZE * positionX, CELL_SIZE*positionY,this);
					g2d.rotate(-orientation, (positionX+70)/2, (positionY+70)/2);	//On effectue la rotation au centre de notre image

				  	}
				//dessine la main
					System.out.println("a");
			        for (int i=0; i<5;i++) {
			        	Carte carteMain = LogiqueDeJeu.joueurCourant.cartesMain.getCartesMain().get(i);
						if (carteMain.getTypeCarte() == TypeCarte.CARTE_BLEUE) {
							g.setColor(Color.blue);
						}
						else if (carteMain.getTypeCarte() == TypeCarte.CARTE_JAUNE) {
							g.setColor(Color.yellow);
						}
						else if (carteMain.getTypeCarte() == TypeCarte.CARTE_VIOLETTE) {
							g.setColor(Color.magenta);
						}
						else if (carteMain.getTypeCarte() == TypeCarte.LASER) {
							g.setColor(Color.CYAN);
			        }
					g.fillRect(25+(135*i), 600, 130, 360);
			        }
					//Rectangle de gauche affichant l'Ã©tat de la partie
					g.setColor(Color.black);
					g.setFont(new Font("TimesRoman", Font.PLAIN, 16)); 
					if(LogiqueDeJeu.gamemode == 't') { 
						g.drawString("Mode de jeu : Trois Ã  la suite - Partie 2/3", 10, 20);
					}
					else {
						g.drawString("Mode de jeu : Partie normale", 10, 20);
					}
					//On affiche les donnÃ©es correspondantes aux autres joueurs
					int j = 0;
					for(int i=0;i<LogiqueDeJeu.nombreJoueurs;i++) {
						if(LogiqueDeJeu.focusJoueur != i) {
					//	if(joueur[i] != joueurActif)
						g.setFont(new Font("TimesRoman", Font.PLAIN, 40)); 
						g.drawString("Requin", 15, 60+i*200);
						
						g.drawImage(img[7], 10, 75 + j * 200, this); //le mur de glace
						g.drawString("x" + LogiqueDeJeu.joueurs.get(i).mursDeGlace, 65, 110 + j*200);		//le nombre de murs de glace
						g.drawImage(img[8], 115, 75 + j * 200, this); //le mur de pierre
						g.drawString("x" + LogiqueDeJeu.joueurs.get(i).mursDePierre, 170, 110 + j*200);		//le nombre de murs de pierre
						g.drawImage(img[10], 210, 75 + j * 200, this); //l'instruction
						g.drawString("x" + LogiqueDeJeu.joueurs.get(i).programme.programme.size(), 265, 110 + j*200);		//le nombre d'instructions
						g.drawImage(img[9], 10, 140 + j * 200, this); //Joyau
						g.drawString("3 points", 65, 175 + j*200);		//le nombre de murs de pierre
						j++;
					//On affiche les donnÃ©es correspondantes au joueur actif
					}
						else {
					g.setFont(new Font("TimesRoman", Font.PLAIN, 60)); 
					g.drawString("Tortue", 950, 50); //On affiche le nom du joueur
					g.drawImage(img[7], 950, 100, this); //le mur de glace
					g.drawString("x" + LogiqueDeJeu.joueurCourant.mursDeGlace, 1025, 135);		//le nombre de murs de glace
					g.drawImage(img[8], 950, 200, this); //le mur de pierre
					g.drawString("x" + LogiqueDeJeu.joueurCourant.mursDePierre, 1025, 235);		//le nombre de murs de pierre
					g.drawImage(img[10], 950, 300, this); //Instruction
					g.drawString("x" + LogiqueDeJeu.joueurs.get(i).programme.programme.size(), 1025, 335);		//Le nombre d'instructions
					g.drawImage(img[9], 950, 400, this); //joyau
					g.drawString("x2", 1025, 435);		//Le nombre de points
						}
		  }
	  }
	  
	  //Classe responsable de la création des boutons de choix d'action
	  	 private class Bouton extends JButton implements MouseListener {
	  	 private String name;
	  	 private int posx;
	  	 private int posy;
	  	 private Color color;
	  	 
	  	public Bouton(String str,int posx, int posy, Color color) {
	  		//on prend en parametre le nom du bouton, et sa position
	  		    super(str);
	  		    this.posx= posx;
	  		    this.posy = posy;
	  		    this.name = str;
	  		    this.color = color;
	  		    addMouseListener(this);
	  	}
	  	  public void paintComponent(Graphics g){
	  			this.setSize(190, 400);
	  			this.setLocation(posx, posy);
	  			g.setColor(color);
	  		    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	  		    g.setColor(Color.BLACK);
	  		    g.drawString(this.name, (this.getWidth()/2) -25 , (this.getHeight() / 2) + 5);
	  		    
	  		    
	  		  }
	  	@Override
	  	public void mouseClicked(MouseEvent arg0) {
	  		if (this.name == "  Bloquer") {
	  				bloquer();
	  		}
	  		else if (this.name == " Executer") {
	  			LogiqueDeJeu.joueurCourant.executerPrgm(null);
	  	}
	  		else if (this.name == "Completer") {
	  			completer();
	  	}
	  		
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
	  }
	  //La fonction qui se lance quand on appuie sur compléter
	  	 private void completer() {
	  		JFrame fenetreCompleter = new JFrame();
	  		fenetreCompleter.setVisible(true);
	        fenetreCompleter.setResizable(true);
	        fenetreCompleter.setSize(800, 600);
	        fenetreCompleter.setTitle("Compléter le programme");
	        fenetreCompleter.setLocationRelativeTo(null);
	        fenetreCompleter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        fenetreCompleter.setLayout(new FlowLayout());
	        ArrayList<BoutonCompleter> boutonsCartes = new ArrayList<BoutonCompleter>(); //Liste dans laquelle on stock les boutons

	        for (int i=0; i<5;i++) {
	        boutonsCartes.add(i,  new BoutonCompleter(fenetreCompleter,LogiqueDeJeu.joueurCourant.cartesMain.getCartesMain().get(i)));
	        fenetreCompleter.add(boutonsCartes.get(i));
	        }
	        JButton valide = new JButton("valider");
	        valide.setSize(fenetreCompleter.getWidth(), (int) (fenetreCompleter.getWidth()*0.15)); 
		    valide.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				fenetreCompleter.dispose();
				finDuTour();

			}
		    });
	        fenetreCompleter.add(valide);
	        	  	 }
	  	 //La fonction qui se lance quand on appuie sur bloquer
	  	 
	  	 private void bloquer() {
	  		JFrame fenetreBloquer = new JFrame();
	  		fenetreBloquer.setVisible(true);
	        fenetreBloquer.setResizable(true);
	        fenetreBloquer.setSize(800, 600);
	        fenetreBloquer.setTitle("Compléter le programme");
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
			choixMur.repaint();
		    fenetreBloquer.add(choixMur);
		    String[] position = {"1","2","3","4","5","6","7","8"};

		    JComboBox positionX = new JComboBox(position) ;
		    JComboBox<int[]> positionY = new JComboBox(position) ;
		    positionX.setMaximumSize(new Dimension(300, 150));
		    fenetreBloquer.add(positionX);
		   	fenetreBloquer.add(positionY);
		   	JButton valide = new JButton("valider");
		    valide.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				fenetreBloquer.dispose();
				String typeMur = "g";
				if(choixMur.getText() == "Mur de pierre") {
					typeMur = "p";
				}
				LogiqueDeJeu.plateau.setCase(positionX.getSelectedIndex(), positionY.getSelectedIndex(),typeMur);
				finDuTour();
			}
		    });
	        fenetreBloquer.add(valide);
	  	 }

	  	 private void finDuTour() {
				fenetre.revalidate();
				fenetre.repaint();
				int choixDefausser = JOptionPane.showConfirmDialog(null, 
				        "Souhaitez-vous défausser votre main et repiocher 5 cartes ?", 
				        "Choix utilisateur", 
				        JOptionPane.YES_NO_OPTION, 
				        JOptionPane.QUESTION_MESSAGE);
				if(choixDefausser == 0) {
					LogiqueDeJeu.joueurCourant.cartesMain.viderCartesMain(LogiqueDeJeu.joueurCourant);
					LogiqueDeJeu.joueurCourant.cartesMain.tirerCartesDuDeck(LogiqueDeJeu.joueurCourant, 5);
				}
				
	  	 }
	  	 
		  //Classe responsable de la création des boutons de choix d'action
	  	 private class BoutonCompleter extends JButton implements MouseListener {
	  	 BoutonCompleter ceBouton;
	  	 Carte carte;
	  	 private String name;
	  	 private boolean visible;
	  	 private JFrame fenetre;

	  	 
	  	public BoutonCompleter(JFrame fenetre,Carte carte) {
	  		//on prend en parametre le nom du bouton
	  		    super();
	  		    ceBouton = this;
	  		    this.carte = carte;
	  		    this.name = carte.getTypeCarte().toString();;
	  		    visible = true;
	  		    this.fenetre = fenetre;
	  		    addMouseListener(this);
	  		    this.setName(name);
	  		    int hauteur = (int) (fenetre.getHeight()*0.8);
	  		    int longueur = (int) (fenetre.getWidth()*0.185);
	  		    this.setPreferredSize(new Dimension(longueur , hauteur ));
	  	}
	  	  public void paintComponent(Graphics g){

	  		  if (name == "CARTE_BLEUE") {
					g.setColor(Color.blue);
				}
				if (name == "CARTE_JAUNE") {
					g.setColor(Color.yellow);
				}
				if (name == "CARTE_VIOLETTE") {
					g.setColor(Color.magenta);
				}
				if (name == "LASER") {
					g.setColor(Color.CYAN);
	        }
	  		    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	  		    g.setColor(Color.BLACK);
	  		    g.drawString(this.name, (this.getWidth()/2) -25 , (this.getHeight() / 2) + 5);
	  		    
	  		    
	  		  }
	  	@Override
	  	public void mouseClicked(MouseEvent arg0) {
	  		System.out.println("test");
	  		LogiqueDeJeu.joueurCourant.programme.enfilerCarte(carte);
	  		fenetre.remove(ceBouton);
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
}
