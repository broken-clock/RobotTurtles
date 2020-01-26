#### Il y a deux classes:

- ```Player```: instancié autant de fois qu'il y a de joueurs
- ```IA```: fait jouer plusieurs joueurs à tour de rôle dans une même partie. Il faut renseigner l'id de la partie, et pour chaque joueur son ID (numéro du joueur, entre 0 et le nombre de joueurs - 1) et son UUID (obtenu lors de la création d'un joueur avec ```playerConnector.joinGame(playerName)```).
- La méthode ```IA.getMove()``` renvoie le move à exécuter sous forme de String, en fonction du gameState et du PlayerSecret relatif au joueur courant. C'est cette méthode qui doit a priori contenir l'algorithme de l'IA.