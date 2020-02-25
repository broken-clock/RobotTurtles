package src;

import src.Cartes.Carte;
import src.Cartes.Programme;
import src.Cartes.TypeCarte;
import src.Interface.*;
import src.Tuiles.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class LogiqueDeJeu {
    private int test3alasuite=0;//set 1 pour test 3 a la suite en 2 joueurs
    private Interface monInterface;
    private int nombreJoueurs;
    private ArrayList<Integer> ordreJoueurs;
    private String modeJeu;
    private boolean modeBug;
    private int nombreJoueursGagne = 0;
    private ArrayList<Joueur> joueurs = new ArrayList();
    private ArrayList<Position> positionsInitialesJoueurs = new ArrayList();
    private ArrayList<Joyau> joyaux;
    private Plateau plateau = new Plateau();
    private Joueur joueurCourant;
    private boolean gameOver;
    private Iterator<Integer> iterateurJoueurs;
    private int numeroManche;
    private int[] coordsCaseToucheeParLaser;

    void initialiserPartie() {
        // Choix du type d'interface
        this.setMonInterface(new InterfaceConsole());

        // Musique de fond

        Parametres parametres = this.getMonInterface().parametresMenu();
        System.out.println("modejeu: " + parametres.gameMode);
        this.nombreJoueurs = parametres.getNbJoueurs();
        this.setModeJeu(parametres.getModeJeu());
        this.setModeBug(parametres.getModeBug());

        // Création du nombre adéquat de joueurs et initialisation pour chaque joueur de ses obstacles disponibles et de ses cartesMain initiales
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.getJoueurs().add(new Joueur(this));
            this.getJoueurs().get(i).setNumeroJoueur(i);
            this.getJoueurs().get(i).getTortue().setNumeroJoueur(this.getJoueurs().get(i).getNumeroJoueur());
            this.initialiserAttributsJoueurs(i);

        }
        this.initialiserPositionsPlateauOrdrepassage();
    }

    public int[] getCoordsCaseToucheeParLaser() {
        return coordsCaseToucheeParLaser;
    }

    public void setCoordsCaseToucheeParLaser(int x, int y) {
        this.coordsCaseToucheeParLaser = new int[]{x, y};
    }

    public int getNumeroManche() {
        return numeroManche;
    }

    public void setNumeroManche(int numeroManche) {
        this.numeroManche = numeroManche;
    }

    public int getNombreJoueurs() {
        return this.nombreJoueurs;
    }

    public String getModeJeu() {
        return modeJeu;
    }

    public void setModeJeu(String modeJeu) {
        this.modeJeu = modeJeu;
    }

    public boolean isModeBug() {
        return modeBug;
    }

    public void setModeBug(boolean modeBug) {
        this.modeBug = modeBug;
    }

    public int getNombreJoueursGagne() {
        return nombreJoueursGagne;
    }

    public void setNombreJoueursGagne(int nombreJoueursGagne) {
        this.nombreJoueursGagne = nombreJoueursGagne;
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public void setJoueurCourant(Joueur joueurCourant) {
        this.joueurCourant = joueurCourant;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public ArrayList<Joyau> getJoyaux() {
        return joyaux;
    }

    public Interface getMonInterface() {
        return monInterface;
    }

    public void setMonInterface(Interface interfacee) {
        this.monInterface = interfacee;
    }

    private void initialiserPositionsPlateauOrdrepassage() {
        // Créer les joyaux
        this.joyaux = new ArrayList();

        // En fonction du nombre de joueurs, initialiser les positions des tortues et les joyaux
        Position positionDepart;
        switch (this.nombreJoueurs) {
            case 2:
            default:
                // Initialiser les positions des tortues
                if (test3alasuite==1) {
                    this.positionsInitialesJoueurs.add(new Position(6, 3, Orientations.DOWN));//initialiser J1 a coté du joyau
                }else if (test3alasuite==0){
                    this.positionsInitialesJoueurs.add(new Position(0, 1, Orientations.DOWN));//init J1 normal
                }
                this.positionsInitialesJoueurs.add(new Position(0, 5, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Definir la position des joyaux
                this.getJoyaux().add(new Joyau());
                this.getJoyaux().get(0).setPosition(7, 3, null);
                break;

            case 3:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(0, 0, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(6, 3, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(6, 6, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.getJoueurs().get(2).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(2).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 3; i++) {
                    this.getJoyaux().add(new Joyau());
                }
                this.getJoyaux().get(0).setPosition(7, 0, null);
                this.getJoyaux().get(1).setPosition(7, 3, null);
                this.getJoyaux().get(2).setPosition(7, 6, null);
                break;

            case 4:
                // Initialiser les positions des tortues
                this.positionsInitialesJoueurs.add(new Position(0, 0, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 2, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 5, Orientations.DOWN));
                this.positionsInitialesJoueurs.add(new Position(0, 7, Orientations.DOWN));

                positionDepart = this.positionsInitialesJoueurs.get(0);
                this.getJoueurs().get(0).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(0).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(1);
                this.getJoueurs().get(1).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(1).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(2);
                this.getJoueurs().get(2).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(2).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                positionDepart = this.positionsInitialesJoueurs.get(3);
                this.getJoueurs().get(3).getTortue().setPosition(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());
                this.getJoueurs().get(3).getTortue().setPositionDepart(positionDepart.getX(), positionDepart.getY(), positionDepart.getOrientation());

                // Créer les joyaux et définir leur position
                for (int i = 0; i < 2; i++) {
                    this.getJoyaux().add(new Joyau());
                }
                this.getJoyaux().get(0).setPosition(7, 1, null);
                this.getJoyaux().get(1).setPosition(7, 6, null);
                break;
        }

        // Initialisation du plateau à partir des objets créés précédemment
        this.setPlateau(new Plateau());
        this.getPlateau().initPlateau(this);

        genererOrdrePassageJoueurs();
    }

    private void initialiserAttributsJoueurs(int i) {
        this.getJoueurs().get(i).setFini(false);
        this.getJoueurs().get(i).reInitCartes();
        this.getJoueurs().get(i).setMursDePierre(3);
        this.getJoueurs().get(i).setMursDeGlace(2);
        this.getJoueurs().get(i).getCartesMain().tirerCartesDuDeck(this.getJoueurs().get(i), 5);
        // Pour le mode avec cartes bug
        this.getJoueurs().get(i).setCarteBug(this.isModeBug());
        this.getJoueurs().get(i).setSubiBug(false);
    }

    private void genererOrdrePassageJoueurs() {
        // Génération de l'ordre de passage des joueurs
        int focusJoueur = this.initFocusJoueur();  // Choisit au hasard le joueur qui jouera en premier
        this.ordreJoueurs = new ArrayList();
        this.ordreJoueurs.add(focusJoueur);
        for (int i = focusJoueur + 1; i < nombreJoueurs; i++) this.ordreJoueurs.add(i);
        for (int i = 0; i < focusJoueur; i++) this.ordreJoueurs.add(i);
    }

    private void reInitialiserPartie() {
        // On refait uniquement les initialisations nécessaires pour lancer une nouvelle manche
        this.genererOrdrePassageJoueurs();
        this.setNombreJoueursGagne(0);
        this.setGameOver(false);
        this.initialiserPositionsPlateauOrdrepassage();

        // Réinitialisation attributs (cartes, obstacles) de chaque joueur
        for (int i = 0; i < this.nombreJoueurs; i++) {
            this.initialiserAttributsJoueurs(i);
        }
    }

    private void jouerManche() {
        while (!this.isGameOver()) {
            for (this.iterateurJoueurs = this.ordreJoueurs.iterator(); this.iterateurJoueurs.hasNext(); ) {
                Integer focusJoueur = this.iterateurJoueurs.next();
                if (this.isGameOver()) break;
                System.out.println("focusJoueur: " + focusJoueur);
                setJoueurCourant(this.getJoueurs().get(focusJoueur));
                this.getMonInterface().afficherPlateau(this);
     //           ArrayList<Carte> carteConseil = cartesAJouer(this, this.getJoueurCourant().getTortue().getPositionDepart().getX(), this.getJoueurCourant().getTortue().getPositionDepart().getY(), this.getJoueurCourant().getTortue().getPositionDepart().getOrientation(), this.getJoueurCourant().getTortue().getPosition().getX(), this.getJoueurCourant().getTortue().getPosition().getY(), this.getJoueurCourant().getTortue().getPosition().getOrientation(),this.getJoueurCourant().getCartesMain().getCartesMain());
   //             for (int i=0;i<carteConseil.size();i++) {
  //              	System.out.println(carteConseil.get(i).getTypeCarte().toString());
   //             }
            	getJoueurCourant().setAction(this.getMonInterface().demanderAction(this));
                switch (getJoueurCourant().getAction()) {
                    case "P":  // Compléter le programme
                        this.getMonInterface().afficherCartesMain("completer le programme", this);
                        boolean continuerAjouterCartes = true;
                        while (continuerAjouterCartes) {
                            String carteStr = this.getMonInterface().demanderCarteAAjouterAProgramme();
                            TypeCarte typeCarte = TypeCarte.LASER;  // Placeholder
                            switch (carteStr) {
                                case "CARTE_BLEUE":
                                    typeCarte = TypeCarte.CARTE_BLEUE;
                                    if (this.getMonInterface().getTypeInterface().equals("Affichage"))
                                        System.out.println(1);
                                    break;
                                case "CARTE_JAUNE":
                                    typeCarte = TypeCarte.CARTE_JAUNE;
                                    if (this.getMonInterface().getTypeInterface().equals("Affichage"))
                                        System.out.println(2);

                                    break;
                                case "CARTE_VIOLETTE":
                                    typeCarte = TypeCarte.CARTE_VIOLETTE;
                                    if (this.getMonInterface().getTypeInterface().equals("Affichage"))
                                        System.out.println(3);

                                    break;
                                case "LASER":
                                    typeCarte = TypeCarte.LASER;
                                    if (this.getMonInterface().getTypeInterface().equals("Affichage"))
                                        System.out.println(4);

                                    break;
                                case "NOT_A_CARD":
                                    if (this.getMonInterface().getTypeInterface().equals("Affichage"))
                                        System.out.println(5);
                                    continuerAjouterCartes = false;
                            }

                            if (continuerAjouterCartes) {
                                Carte carte = getJoueurCourant().getCartesMain().retirerCarte(typeCarte);
                                getJoueurCourant().completerPrgm(carte);
                            }
                        }
                        //a voir           this.getMonInterface().afficherProgramme(this);
                        break;

                    case "M":  // Construire un mur
                        Obstacle obstacle;
                        boolean murPlaceOk;
                        do {
                            obstacle = this.getMonInterface().demanderObstacleAPlacer();
                            murPlaceOk = getJoueurCourant().placerMur(this, obstacle);
                        } while (!murPlaceOk);
                        break;

                    case "E":  // Exécuter le programme
                        this.getJoueurCourant().executerPrgm(this);
                        break;

                    case "B":  // Utiliser sa carte bug
                        int numeroJoueurCibleBug = this.getMonInterface().demanderCibleCarteBug(this);
                        if (!getJoueurCourant().isCarteBug())
                            this.getMonInterface().afficherMessage("Refusé: vous n'avez plus de carte bug");
                        else {
                            getJoueurCourant().setCarteBug(false);  // Le joueur courant a consommé sa carte bug
                            this.getJoueurs().get(numeroJoueurCibleBug).subirBug();  // Le joueur cible subit les effets de la carte bug ajoutée à son programme
                        }
                        break;
                }
                if (this.isGameOver()) break;
                if (this.getMonInterface().getTypeInterface().equals("Affichage")) System.out.println("abcdfinsess");

                // Defausse des cartes
                if (!this.joueurCourant.isFini() && !this.joueurCourant.getCartesMain().empty()) {
                    boolean continuerDefausserCartes = true;
                    this.getMonInterface().afficherCartesMain("choissisez les cartes a defausser", this);
                    while (continuerDefausserCartes) {
                        String carteStr = this.getMonInterface().demanderChoixDefausse();
                        TypeCarte typeCarte = TypeCarte.LASER;  // Placeholder
                        switch (carteStr) {
                            case "CARTE_BLEUE":
                                typeCarte = TypeCarte.CARTE_BLEUE;
                                break;
                            case "CARTE_JAUNE":
                                typeCarte = TypeCarte.CARTE_JAUNE;
                                break;
                            case "CARTE_VIOLETTE":
                                typeCarte = TypeCarte.CARTE_VIOLETTE;
                                break;
                            case "LASER":
                                typeCarte = TypeCarte.LASER;
                                break;
                            case "NOT_A_CARD":
                                continuerDefausserCartes = false;
                        }

                        if (continuerDefausserCartes) {
                            Carte carte = getJoueurCourant().getCartesMain().retirerCarte(typeCarte);
                            //          if (carte.getTypeCarte() == TypeCarte.NOT_A_CARD) {
                            //                  this.monInterface.afficherMessage("Refuse: vous ne possedez pas de telle carte");
                            //           }
                        }
                    }
                }
  //              ArrayList<int[]> hugues = distanceJoyau(this, this.getJoueurCourant().getTortue().getPosition().getX(), this.getJoueurCourant().getTortue().getPosition().getY(), this.getJoueurCourant().getTortue().getPosition().getOrientation());
 //               for (int i=0;i<hugues.size();i++) {
  //              	for(int j=0;j<hugues.get(i).length;j++) {
   //                 	System.out.println(hugues.get(i)[j]);
      //          	}
    //            }
                LogiqueDeJeu simulation = new LogiqueDeJeu();
                simulation.setPlateau(this.getPlateau());
                for (int i=1;i<7;i++) {
                	this.getPlateau().setCase(1, i, "p");
                }
                Chemin test = cheminJoyau2(this,this.joueurCourant.getTortue().getPosition().getX(),this.joueurCourant.getTortue().getPosition().getY(), this.joueurCourant.getTortue().getPosition().getOrientation(),this.joueurCourant.getTortue().getPositionDepart().getX(),this.joueurCourant.getTortue().getPositionDepart().getY(),this.joueurCourant.getTortue().getPositionDepart().getOrientation());
                System.out.println(test.nbInstruction);
                this.getJoueurCourant().terminerTour();
                if (this.getMonInterface().getTypeInterface().equals("Affichage")) System.out.println("end turn");
                if (this.getJoueurCourant().isFini()) iterateurJoueurs.remove();
            }
        }
    }

	public Chemin cheminJoyau2(LogiqueDeJeu logiqueDeJeu, int currentX, int currentY, Orientations currentOrien,int xDepart, int yDepart, Orientations orientationDepart) {
		Programme programmeTest = new Programme();
		logiqueDeJeu.getPlateau().setCase(7, 3, ".");
		Joueur test = new Joueur(logiqueDeJeu);
		test.getTortue().setPositionDepart(xDepart, yDepart, orientationDepart);
		test.setNumeroJoueur(10);
		logiqueDeJeu.setJoueurCourant(test);
	   	ArrayList<ArrayList<Carte>> listeC = new ArrayList<ArrayList<Carte>>();
	   	ArrayList<Carte> armand = new ArrayList<Carte>();
	   	armand.add(new Carte(TypeCarte.CARTE_BLEUE));
	   	ArrayList<Carte> armand2 = new ArrayList<Carte>();
	   	armand2.add(new Carte(TypeCarte.CARTE_VIOLETTE));
	   	ArrayList<Carte> armand3 = new ArrayList<Carte>();
	   	armand3.add(new Carte(TypeCarte.CARTE_JAUNE));
	   	listeC.add(armand);
	   	listeC.add(armand2);
	   	listeC.add(armand3);
		boolean accessJoyau = false;
		while (!accessJoyau) {
			System.out.println("zzzzaaahdshha");
    		int x = currentX;
    		int y = currentY;
    		Orientations orientation = currentOrien;
		for (int i=0;i<listeC.size();i++) {
			for (int j=0; j<listeC.get(i).size();j++) {
//				try {
//					Thread.sleep(200);
	//			} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//	//				e.printStackTrace();
//				}
//				System.out.println(listeC.get(i).get(j).getTypeCarte().toString());
    			programmeTest.enfilerCarte(listeC.get(i).get(j));
    			
			}
    			test.getTortue().setPosition(x, y, orientation);
    			test.setProgramme(programmeTest);
    			test.executerPrgm(logiqueDeJeu);
 //   			System.out.println(test.getTortue().getPosition().getX() + " : " + test.getTortue().getPosition().getY());
    			if (test.getTortue().getPosition().getX() == 7 && test.getTortue().getPosition().getY() == 3) {
        			test.getTortue().disparaitre(logiqueDeJeu);
    				return new Chemin(listeC.get(i).size(),listeC.get(i));
    			}
    			test.getTortue().disparaitre(logiqueDeJeu);
		}
		int taille = listeC.size();
		for(int i=0;i<taille+1;i++) {
		ArrayList copy = new ArrayList(listeC.get(i));
		ArrayList copy2 = new ArrayList(listeC.get(i));

		listeC.add(copy);
		listeC.add(copy2);
		listeC.get(i).add(new Carte(TypeCarte.CARTE_BLEUE));
		listeC.get(taille+i*2).add(new Carte (TypeCarte.CARTE_VIOLETTE));
		listeC.get(taille+i*2+1).add(new Carte (TypeCarte.CARTE_JAUNE));
	//	listeC.get(taille+i*3+2).add(new Carte (TypeCarte.LASER));
	}
	}
		return null;
	}

	
	//armand �num�ration
	
	
		public static ArrayList<ArrayList<Integer>> choose(ArrayList<Integer> a, int k) {
        ArrayList<ArrayList<Integer>> allPermutations = new ArrayList<ArrayList<Integer>>();
        enumerate(a, a.size(), k, allPermutations);
        return allPermutations;
		}
	
	   private static void enumerate(ArrayList<Integer> a, int n, int k, ArrayList<ArrayList<Integer>> allPermutations) {
	        if (k == 0) {
	            ArrayList<Integer> singlePermutation = new ArrayList<Integer>();
	            for (int i = n; i < a.size(); i++) {
	                singlePermutation.add(a.get(i));
	            }
	            allPermutations.add(singlePermutation);
	            return;
	        }
	 
	        for (int i = 0; i < n; i++) {
	            swap(a, i, n - 1);
	            enumerate(a, n - 1, k - 1, allPermutations);
	            swap(a, i, n - 1);
	        }
	    }
	 
	    // helper function that swaps a.get(i) and a.get(j)
	    public static void swap(ArrayList<Integer> a, int i, int j) {
	        Integer temp = a.get(i);
	        a.set(i, a.get(j));
	        a.set(j, temp);
	    }
	 
	    public void arrangement(ArrayList<ArrayList<Integer>> OutputList) {
        int n = 5;
        ArrayList<Integer> elements = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            elements.add(i);
        }
        for (int k = 1; k <= n; k++) {
            OutputList.addAll(choose(elements, k));
        }
        System.out.println(elements);
        System.out.println(OutputList);
	    }
	    
	    public ArrayList<Carte> cartesAJouer(LogiqueDeJeu logiqueDeJeu,int xDepart,int yDepart, Orientations orientationDepart, int posX, int posY,Orientations orientation, ArrayList<Carte> main) {
			int distanceIni = cheminJoyau2(logiqueDeJeu,posX,posY,orientation,xDepart,yDepart,orientationDepart).nbInstruction;
			int distanceMin = distanceIni;
			ArrayList<Carte> carteJouable = new ArrayList<Carte>();
	    	ArrayList<ArrayList<Integer>> OutputList = new ArrayList();
	    	arrangement(OutputList);
	    	Programme programmeTest = new Programme();
			Joueur virtuel = new Joueur(logiqueDeJeu);
			virtuel.getTortue().setPositionDepart(xDepart, yDepart, orientationDepart);
			virtuel.setNumeroJoueur(10);
			logiqueDeJeu.setJoueurCourant(virtuel);
	    	for (ArrayList<Integer> jeu : OutputList) {
	    		for (int i=0;i<jeu.size();i++) {
	    			programmeTest.enfilerCarte(main.get(jeu.get(i)));
	    		}
    			virtuel.getTortue().setPosition(posX, posY, orientation);
    			virtuel.setProgramme(programmeTest);
    			virtuel.executerPrgm(logiqueDeJeu);
    			Chemin cheminAct = cheminJoyau2(logiqueDeJeu,virtuel.getTortue().getPosition().getX(),virtuel.getTortue().getPosition().getY(),virtuel.getTortue().getPosition().getOrientation(),xDepart,yDepart,orientationDepart);
    			int distance = cheminAct.nbInstruction;
    			if (distance<distanceMin) {
    				distanceMin = distance;
    				carteJouable = cheminAct.prog;
	    	}
    		virtuel.getTortue().disparaitre(logiqueDeJeu);
	    }
	    	return carteJouable;
	    }
	    
	    

    void lancerPartie() {
//        // Triche init plateau
//        this.plateau.setCase(1, 0, "g");
//        this.plateau.setCase(1, 1, "b");
//        this.plateau.setCase(1, 4, "b");
//        this.plateau.setCase(2, 2, "b");
//        this.plateau.setCase(2, 4, "b");
//        this.plateau.setCase(3, 2, "b");
//        this.plateau.setCase(3, 3, "b");
//        this.plateau.setCase(3, 4, "b");

        this.setGameOver(false);
        switch (this.getModeJeu()) {
            case "normal":
                jouerManche();
                break;
            case "3alasuite":
                for (int i = 0; i < 3; i++) {
                    this.setNumeroManche(i);
                    jouerManche();
                    if (this.getMonInterface().getTypeInterface().equals("Affichage")) System.out.println("abcdMANCHE");
                    this.getMonInterface().afficherFinManche(this);
                    if (this.getMonInterface().getTypeInterface().equals("Affichage")) System.out.println("abcdFIN");
                    this.reInitialiserPartie();
                    if (this.getMonInterface().getTypeInterface().equals("Affichage")) System.out.println("abcdRESET");
                }
                // Calcul du classement de chaque joueur en fonction de son nombre de points gagné durant les 3 manches
                this.getJoueurs().sort(Comparator.comparing(Joueur::getScore));
                for (int i = 0; i < this.nombreJoueurs; i++) {
                    this.getJoueurs().get(i).setClassement(this.nombreJoueurs - i);
                }
                break;
        }
        this.getMonInterface().afficherResultats(this);
        if (this.getMonInterface().getTypeInterface().equals("Affichage")) System.out.println("test");
    }

    private int initFocusJoueur() {
        return ThreadLocalRandom.current().nextInt(0, this.nombreJoueurs);
    }


}
