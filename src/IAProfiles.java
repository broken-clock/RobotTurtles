package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.grooptown.snorkunking.service.engine.player.PlayerSecret;

import src.Cartes.Carte;
import src.Cartes.Programme;
import src.Cartes.TypeCarte;
import src.Tuiles.Orientations;

public class IAProfiles {
    Joueur joueur;
    LogiqueDeJeu logiqueDeJeu;
    String title;
    List<Carte> carteEnMain;
   public String action(Joueur joueur,LogiqueDeJeu logiqueDeJeu,int nbMurPierre,ArrayList<Carte> main) {
   	String carteAJouer = "";
   	String carteADefausser = "";
   	ArrayList<Carte> mainCopie = new ArrayList<Carte>();
   	for (int i=0;i<5;i++) {
   		mainCopie.add(main.get(i));
   	}
   	ArrayList<Carte> ilFautJouer = cartesAJouer(logiqueDeJeu,joueur.getTortue().getPositionDepart().getX(),joueur.getTortue().getPositionDepart().getY(), joueur.getTortue().getPositionDepart().getOrientation(), joueur.getTortue().getPosition().getX(), joueur.getTortue().getPosition().getY(),joueur.getTortue().getPosition().getOrientation(),mainCopie);
   	for (int i=0;i<ilFautJouer.size();i++) {
        switch (ilFautJouer.get(i).getTypeCarte()) {
        case CARTE_BLEUE:
            carteAJouer.concat("B");
            main.remove(mainCopie.get(i));
            break;
        case CARTE_JAUNE:
            carteAJouer.concat("J");
            main.remove(mainCopie.get(i));
            break;
        case CARTE_VIOLETTE:
            carteAJouer.concat("V");
            main.remove(mainCopie.get(i));
            break;
    }
   	}
   	for (int i=0;i<5-ilFautJouer.size();i++) {
        switch (main.get(i).getTypeCarte()) {
        case CARTE_BLEUE:
            carteADefausser.concat("B");
            main.remove(mainCopie.get(i));
            break;
        case CARTE_JAUNE:
            carteADefausser.concat("J");
            main.remove(mainCopie.get(i));
            break;
        case CARTE_VIOLETTE:
            carteADefausser.concat("V");
            main.remove(mainCopie.get(i));
            break;
    }
   	}
	if (executionVictorieuse(joueur, logiqueDeJeu) {
        return "3;;";
    }
    else if (adversaireCompleteSonProgramme(title) && onADesMurs) {
    	int[] coord = cheminJoyau(logiqueDeJeu,joueur.getTortue().getPosition().getX(),joueur.getTortue().getPosition().getY(),joueur.getTortue().getPosition().getOrientation(),joueur.getTortue().getPositionDepart().getX(),joueur.getTortue().getPositionDepart().getY(),joueur.getTortue().getPositionDepart().getOrientation()).get(1);
    	int murx = coord[0];
    	int mury = coord[1];
    	String mur = "Wall";
    	if (nbMurPierre>0)  mur = "Ice";
    	return ("2;" + mur + " on " + murx + "-" + mury + ";" + carteADefausser);
    	
        //poser mur devant joueur adverse sur le chemin le plus court en nombre d'instruction
        //=> méthode d'Hugues qui nous donne le chemin, on pose le mur sur la premiére case de ce chemin
       
    }
    else if (joueur.getProgramme().getProgramme().size() > 5) {
        return "3;;" + carteADefausser;
    }
    
    else {
		return "1;" + carteAJouer + ";" + carteADefausser;
    	}
   }

private boolean executionVictorieuse(Joueur joueur, LogiqueDeJeu logiqueDeJeu) {
    int[] position = new int[] {joueur.getTortue().getPosition().getX(), joueur.getTortue().getPosition().getY()};
    Orientations orientation = joueur.getTortue().getPosition().getOrientation();
   
    for (Carte carte : joueur.getProgramme().getProgramme()) {

        switch (carte.getTypeCarte()) {
        case CARTE_BLEUE:
            joueur.getTortue().avancer(logiqueDeJeu);
            break;
        case CARTE_JAUNE:
            joueur.getTortue().tournerAntiHoraire(logiqueDeJeu);
            break;
        case CARTE_VIOLETTE:
            joueur.getTortue().tournerHoraire(logiqueDeJeu);
            break;
        case LASER:
            joueur.getTortue().lancerLaser(logiqueDeJeu);
            break;
        }
       
       
        if (joueur.getTortue().getPosition().getX() == 7 && joueur.getTortue().getPosition().getY() == 3) {
            joueur.getTortue().setPosition(position[0], position[1], orientation);
            return true;
        }
    }

    joueur.getTortue().setPosition(position[0], position[1], orientation);
    return false;
}

private boolean adversaireCompleteSonProgramme(String title) {
    return title == "CompleteMove";
}        return "3;;"; 
   }

// Ex�cute son programme
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
	while (!accessJoyau && listeC.get(0).size()<10) {
		System.out.println("zzzzaaahdshha");
		int x = currentX;
		int y = currentY;
		Orientations orientation = currentOrien;
	for (int i=0;i<listeC.size();i++) {
		for (int j=0; j<listeC.get(i).size();j++) {
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace();
//			}
//			System.out.println(listeC.get(i).get(j).getTypeCarte().toString());
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
	return new Chemin(100,new ArrayList<Carte>());
}

public List<int[]> cheminJoyau(LogiqueDeJeu logiqueDeJeu, int currentX, int currentY, Orientations currentOrien,int xDepart, int yDepart, Orientations orientationDepart) {
	 
    logiqueDeJeu.getPlateau().setCase(1, 5, "p");
    logiqueDeJeu.getPlateau().setCase(1, 1, "p");
    List<int[]> hugues = distanceJoyau(logiqueDeJeu, logiqueDeJeu.getJoueurCourant().getTortue().getPosition().getX(), logiqueDeJeu.getJoueurCourant().getTortue().getPosition().getY(), logiqueDeJeu.getJoueurCourant().getTortue().getPosition().getOrientation());
      for (int[] caseAParcourir : hugues) {
          System.out.println("x: "+caseAParcourir[0] + " y:"+caseAParcourir[1]);
      }
    return hugues;
}
public List<int[]> distanceJoyau(LogiqueDeJeu logiqueDeJeu, int positionX, int positionY, Orientations orientation) {
    boolean cheminOK = false;
    int[] caseActuelle = {positionX,positionY};
    int[] caseParent = {-1,-1};
    boolean existeDeja;
    ArrayList<int[]> parcours = new ArrayList<int[]>();
    HashMap<int[],int[]> chemin = new HashMap<>();
    HashMap<int[],Integer> aParcourir = new HashMap<>();
   
    do {
        //On retire la case que l'on utilise de la liste à parcourir
        aParcourir.remove(caseActuelle);

        System.out.println("chemin " + caseActuelle[0]+" "+ caseActuelle[1]);
        System.out.println("cheminP " + caseParent[0]+" "+ caseParent[1]);
        chemin.put(caseActuelle, caseParent);
        parcours.add(caseActuelle);
        HashMap<int[],Integer> test_TEST = caseVoisine(logiqueDeJeu,caseActuelle[0],caseActuelle[1],orientation, parcours);
        for (int[] caseVoisin : test_TEST.keySet()) {
            existeDeja = false;
            System.out.println("test: "+caseVoisin[0]+" "+caseVoisin[1]);
            for (int[] caseParcouru : aParcourir.keySet()) {
                existeDeja = false;
                    if (caseParcouru[0] == caseVoisin[0] && caseParcouru[1] == caseVoisin[0]) {
                        existeDeja = true;
                        break;
                    }
            }
           
            if (!existeDeja) {
                System.out.println("test2: "+caseVoisin[0]+" "+caseVoisin[1]);
                aParcourir.put(caseVoisin, test_TEST.get(caseVoisin));
            }
        }
       
       
        for (int[] test : aParcourir.keySet()) {
            System.out.println("parcourir"+test[0]+" "+test[1]);
        }
       
        //On prend la case dans la liste des cases à parcourir avec un cout minimal
        caseParent = caseActuelle;
        caseActuelle = coutMinimum(aParcourir);
       
        if (caseActuelle[0] == 7 && caseActuelle[1] == 3) {
            cheminOK = true;
            chemin.put(caseActuelle, caseParent);
            parcours.add(caseActuelle);

        }
       
        System.out.println("CaseActuelle: "+caseActuelle[0] + " "+ caseActuelle[1]);
       
    } while (!aParcourir.isEmpty() && !cheminOK);
   
    List<int[]> cheminFinal = new ArrayList<>();
   
    System.out.println("Chemin final :");
    int[] caseTest = null;
    for (int[] test : chemin.keySet()) {
        if (test[0] == 7 && test[1] == 3) {
            caseTest = chemin.get(test);
            cheminFinal.add(test);
        }
    }
   
    while (caseTest[0] != -1 && caseTest[1] != -1) {
        System.out.println(caseTest[0]+" "+caseTest[1]);
        cheminFinal.add(caseTest);
        caseTest = chemin.get(caseTest);
    }
    System.out.println("___FIN___");
    Collections.reverse(cheminFinal);
    return cheminFinal;
}




public int[] coutMinimum(HashMap<int[], Integer> aParcourir) {
    int[] caseMin = null;
    int min = Integer.MAX_VALUE;
    for (int[] caseDansParcouru : aParcourir.keySet()) {
            if (min > aParcourir.get(caseDansParcouru)) {
                caseMin = caseDansParcouru;
                min = aParcourir.get(caseDansParcouru);
            }
        System.out.println("cout "+caseDansParcouru[0]+" "+caseDansParcouru[1]+" = "+ aParcourir.get(caseDansParcouru));
    }
    return caseMin;
}




public HashMap<int[],Integer> caseVoisine(LogiqueDeJeu logiqueDeJeu, int posX, int posY, Orientations orientation,ArrayList<int[]> parcours) {
    HashMap<int[],Integer> casesVois = new HashMap<int[],Integer>();
    if (posX > 0 && logiqueDeJeu.getPlateau().getCase(posX-1, posY) != "p" && testSiPasPresent(new int[] {posX-1,posY}, parcours)) {
        casesVois.put(new int[] {posX-1,posY} , calculCout(logiqueDeJeu, posX-1,posY,orientation,Orientations.UP));
    }
    if (posX < 7 && logiqueDeJeu.getPlateau().getCase(posX+1, posY) != "p" && testSiPasPresent(new int[] {posX+1,posY}, parcours)) {
        casesVois.put(new int[] {posX+1,posY} , calculCout(logiqueDeJeu, posX+1,posY,orientation,Orientations.DOWN));
    }
    if (posY > 0 && logiqueDeJeu.getPlateau().getCase(posX, posY-1) != "p" && testSiPasPresent(new int[] {posX,posY-1}, parcours)) {
        casesVois.put(new int[] {posX,posY-1} , calculCout(logiqueDeJeu, posX,posY-1,orientation,Orientations.LEFT));
    }

    if (posY < 7 && logiqueDeJeu.getPlateau().getCase(posX, posY+1) != "p" && testSiPasPresent(new int[] {posX,posY+1}, parcours)) {
        casesVois.put(new int[] {posX,posY+1} , calculCout(logiqueDeJeu, posX,posY+1,orientation,Orientations.RIGHT));
        //System.out.println("TOOOOOOOOOOOOOOOTOOOOOOOOOO");
    }
   
    return casesVois;
}
public boolean testSiPasPresent(int[] position, ArrayList<int[]> parcours) {
    for (int[] i : parcours) {
        if (i[0] == position[0] && i[1] == position[1]) return false;
    }
    return true;
}


public int calculCout(LogiqueDeJeu logiqueDeJeu,int posX, int posY, Orientations orientation, Orientations casse) {
    int distance = (7-posX) + Math.abs(3-posY);
    int cout = 0;
    if (orientation == casse) cout += 0;
    else if (orientation.getOrientationSuivante(orientation) == casse) cout += 1;
    else if (orientation.getOrientationPrecedente(orientation) == casse) cout += 1;
    else cout += 2;    
    if (logiqueDeJeu.getPlateau().getCase(posX, posY) == "g") cout++;
    return cout + distance;
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
		ArrayList<Carte> mainCopie = new ArrayList<Carte>();
    	for (ArrayList<Integer> jeu : OutputList) {
    		mainCopie.clear();
    		for (int h=0;h<5;h++) {
    			mainCopie.add(main.get(h));
    			System.out.println(main.get(h).getTypeCarte().toString() + main.size());
    		}
    		for (int i=0;i<jeu.size();i++) {
    			programmeTest.enfilerCarte(mainCopie.get(jeu.get(i)));
    		}
			virtuel.getTortue().setPosition(posX, posY, orientation);
			virtuel.setProgramme(programmeTest);
			virtuel.executerPrgm(logiqueDeJeu);
			Chemin cheminAct = cheminJoyau2(logiqueDeJeu,virtuel.getTortue().getPosition().getX(),virtuel.getTortue().getPosition().getY(),virtuel.getTortue().getPosition().getOrientation(),xDepart,yDepart,orientationDepart);
			int distance = cheminAct.nbInstruction;
			System.out.println(distance);
			if (distance<distanceMin) {
				distanceMin = distance;
				carteJouable.clear();
				for (int j=0;j<jeu.size();j++) {
					carteJouable.add(main.get(jeu.get(j)));
				}
    	}
		virtuel.getTortue().disparaitre(logiqueDeJeu);
    }
    	return carteJouable;
    }
    
    }
