package src.Interface;

import src.LogiqueDeJeu;
import src.Tuiles.Obstacle;
import src.Tuiles.Orientations;

public interface Interface {
    // Identification de l'interface
    String getTypeInterface();

    // Initialisation du jeu
    ParametresInitPartie parametresMenu(); // Demande le nombre de joueurs pour la partie à venir, le mode de jeu: normal ou 3 à la suite, et s'il faut jouer avec les cartes bug

    // Actions dans le jeu
    String demanderAction(LogiqueDeJeu logiqueDeJeu);  // Demande quelle action un joueur veut faire: compléter son programme / placer un mur / exécuter son programme / bugger un autre joueur (si possible)

    String demanderCarteAAjouterAProgramme();  // Demande au joueur en cours quelles cartes il veut transférer de sa main vers son programme

    Obstacle demanderObstacleAPlacer();  // Demande au joueur en cours quel type d'obstacle il veut placer (mur de glace / mur de pierre) et à quelles coordonnées (x, y) il veut placer son obstacle

    int demanderCibleCarteBug(LogiqueDeJeu logiqueDeJeu);  // Demande au joueur en cours le numéro du joueur à qui il veut poser sa carte bug

    String demanderChoixDefausse();  // A la fin de son tour, demande au joueur en cours s'il souhaite défausser sa main et re-piocher 5 cartes

    // Divers utilisables au cours du jeu
    void afficherMessage(String message);  // Notification quelconque

    void afficherCartesMain(String string, LogiqueDeJeu logiqueDeJeu);  // Affiche les cartes présentes dans la main du joueur en cours

    void afficherProgramme(LogiqueDeJeu logiqueDeJeu);  // Affiche le contenu courant du programme du joueur en cours

    void afficherPlateau(LogiqueDeJeu logiqueDeJeu);  // Affiche l'état courant du plateau

    // A la fin d'une manche (pour le mode de jeu 3 à la suite) ou du jeu
    void afficherResultats(LogiqueDeJeu logiqueDeJeu);  // A la fin du jeu, afficher le classement des joueurs
    void stopLaser();
    void afficherFinManche(LogiqueDeJeu logiqueDeJeu);  // Pour le mode de jeu 3 à la suite, afficher les scores courants à la fin de chaque manche
    void animationLaser(int[] pos,Orientations orient);
    void actualiser();
}
