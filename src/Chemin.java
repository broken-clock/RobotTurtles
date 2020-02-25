package src;

import java.util.ArrayList;

import src.Cartes.Carte;

public class Chemin {

	public int nbInstruction;

	public ArrayList<Carte> prog;

		public Chemin(int a,ArrayList<Carte> b) {

			nbInstruction = a;

			prog = b;

		}

}
