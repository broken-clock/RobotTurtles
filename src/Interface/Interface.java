package src.Interface;

import src.LogiqueDeJeu;

public interface Interface {
    // Initialisation du jeu
    int demanderNombreJoueurs();  // Demande le nombre de joueurs pour la partie à venir

    String demanderModeJeu();  // Demande le mode de jeu: normal ou 3 à la suite

    boolean demanderModeCarteBug();  // Demande s'il faut jouer avec les cartes bug

    // Actions dans le jeu
    String demanderAction(LogiqueDeJeu logiqueDeJeu);  // Demande quelle action un joueur veut faire: compléter son programme / placer un mur / exécuter son programme / bugger un autre joueur (si possible)

    String demanderCarteAAjouterAProgramme();  // Demande au joueur en cours quelles cartes il veut transférer de sa main vers son programme

    String demanderTypeObstacleAPlacer();  // Demande au joueur en cours quel type d'obstacle il veut placer (mur de glace / mur de pierre)

    int[] demanderCoordsObstacleAPlacer();  // Demande au joueur en cours à quelles coordonnées (x, y) il veut placer son obstacle

    int demanderCibleCarteBug(LogiqueDeJeu logiqueDeJeu);  // Demande au joueur en cours le numéro du joueur à qui il veut poser sa carte bug

    boolean demanderChoixDefausse();  // A la fin de son tour, demande au joueur en cours s'il souhaite défausser sa main et re-piocher 5 cartes

    // Divers utilisables au cours du jeu
    void afficherMessage(String message);  // Notification quelconque

    void afficherCartesMain(LogiqueDeJeu logiqueDeJeu);  // Affiche les cartes présentes dans la main du joueur en cours

    void afficherProgramme(LogiqueDeJeu logiqueDeJeu);  // Affiche le contenu courant du programme du joueur en cours

    void afficherPlateau(LogiqueDeJeu logiqueDeJeu);  // Affiche l'état courant du plateau

    // A la fin d'une manche (pour le mode de jeu 3 à la suite) ou du jeu
    void afficherResultats(LogiqueDeJeu logiqueDeJeu);  // A la fin du jeu, afficher le classement des joueurs

    void afficherFinManche(LogiqueDeJeu logiqueDeJeu, int i);  // Pour le mode de jeu 3 à la suite, afficher les scores courants à la fin de chaque manche
}
