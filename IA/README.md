## Comment importer les dépendences ?
- Créer un projet de type **Maven** et installer les dépendences spécifiées dans **pom.xml**.
- Ne pas inclure **client-1.0-SNAPSHOT-jar-with-dependencies.jar**.

## Comment fonctionne le module IA ?
#### Il y a 3 classes:

- ```Player```: instancié autant de fois qu'il y a de joueurs
- ```IARun```: fait jouer plusieurs joueurs à tour de rôle dans une même partie. Il faut renseigner l'id de la partie, et pour chaque joueur son ID (numéro du joueur, entre 0 et le nombre de joueurs - 1) et son UUID (obtenu lors de la création d'un joueur avec ```playerConnector.joinGame(playerName)```).
-  ```IAProfiles```: contient les différents profils d'IA (les algos). Chaque profil est une fonction qui renvoie le move à exécuter sous forme de String, en fonction du gameState et du PlayerSecret relatif au joueur courant. C'est à ```IARun``` d'associer à chaque joueur un profil d'IA.

#### IARun.createPlayers
- Ce flag permet de choisir s'il faut créer des joueurs (```true```) ou s'il faut utiliser l'identité de joueurs précédemment créés (```false```)
- Pour chaque nouvelle partie, créer des joueurs puis renseigner les ```playerId``` et ```playerUUID``` dans ```IA.java``` et repasser le flag à ```false```.

#### IARun.scannerMovesMode
Ce flag permet de choisir si les moves sont envoyés via le ```Scanner``` (```true```) ou via le résultat d'un profil d'IA.

#####Credit to https://github.com/tdebroc/robot-turtles-ia-client
