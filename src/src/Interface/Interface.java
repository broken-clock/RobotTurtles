package src.Interface;
import src.LogiqueDeJeu;

public interface Interface {
    void afficherMessage(String message);  // Notification quelconque
    void afficherPlateau(LogiqueDeJeu logiqueDeJeu);  // Affiche l'état courant du plateau
    int demanderNombreJoueurs(LogiqueDeJeu logiqueDeJeu);  // Demande le nombre de joueurs pour la partie à venir
    String demanderAction(LogiqueDeJeu logiqueDeJeu);  // Demande quelle action un joueur veut faire: compléter son programme / placer un mur / exécuter son programme
    void afficherCartesMain(LogiqueDeJeu logiqueDeJeu);  // Affiche les cartes présentes dans la main du joueur en cours
    void afficherProgramme(LogiqueDeJeu logiqueDeJeu);  // Affiche le contenu courant du programme du joueur en cours
    String demanderCarteAAjouterAProgramme();  // Demande au joueur en cours quelles cartes il veut transférer de sa main vers son programme
    String demanderTypeObstacleAPlacer();  // Demande au joueur en cours quel type d'obstacle il veut placer (mur de glace / mur de pierre)
    int[] demanderCoordsObstacleAPlacer();  // Demande au joueur en cours à quelles coordonnées (x, y) il veut placer son obstacle
    boolean demanderChoixDefausse();  // A la fin de son tour, demande au joueur en cours s'il souhaite défausser sa main et re-piocher 5 cartes
    void afficherResultats(LogiqueDeJeu logiqueDeJeu);  // A la fin du jeu, afficher le classement des joueurs
}
