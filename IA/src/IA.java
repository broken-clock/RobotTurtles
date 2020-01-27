package IA;

import com.grooptown.ia.robotturtles.PlayerConnector;
import com.grooptown.snorkunking.service.engine.player.PlayerSecret;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static com.grooptown.ia.robotturtles.PlayerConnector.getGameStateAsString;
import static com.grooptown.ia.robotturtles.SSLUtil.disableSSLValidation;

public class IA {
    public static void main(String[] args) throws Exception {
        // With JDK inferior to 8u101 you need to disable SSL validation.
        disableSSLValidation();
        PlayerConnector.baseUrl = "https://robot-turtles.grooptown.com/";

        // Paramètres
        int gameId = 500;
        int playTurnDelayMs = 1000;  // Delay en ms entre les tours de chaque joueur
        boolean createPlayers = true;  // Détermine s'il faut créer de nouveaux joueurs ou utiliser les identités de joueurs déjà créés
        boolean scannerMovesMode = true;
        String[] nomsJoueurs = {"bleubidon", "bleubidu"};
        ArrayList<Player> players = new ArrayList<>();

        // Créer les joueurs si besoin
        if (createPlayers) {
            PlayerConnector playerConnector = new PlayerConnector(gameId);
            for (String nomJoueur : nomsJoueurs) {
                playerConnector.joinGame(nomJoueur);
                // Write player id and UUID to file
//                try {
//                    Files.write(Paths.get("playersInfo.txt"), "data".getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }

//        int player1Id = 0;
//        String player1UUID = "c2e5bc98-3c4e-4480-8a91-39bfd83cd5bb";
//
//        int player2Id = 1;
//        String player2UUID = "faa2f4b5-8a00-4aaf-89fe-04b785fd8040";

        // S'identifier en tant que les joueurs créés
        // TODO read from file
//        for () {
//            players.add(new Player(gameId, playerId, playerUUID));
//        }
//
//        boolean gameOver = false;
//        while (!gameOver) {
//            for (Player player : players) {
//                player.waitUntilItsMyTurn(gameId);
//                String gameState = getGameStateAsString(gameId);
//                player.secret = player.playerConnector.getPlayerSecret();
//
//                if (scannerMovesMode) {
//                    System.out.println("Player " + player.idPlayer + " :your move: ");
//                    player.playerConnector.playMove(new Scanner(System.in).nextLine());
//                } else {
//                    player.playerConnector.playMove(getMove(gameState, player.secret));
//                    Thread.sleep(playTurnDelayMs);
//                }
//            }
//        }
    }

    public static String getMove(String gameState, PlayerSecret secret) {
        return "3;;";  // Placeholder
    }
}
