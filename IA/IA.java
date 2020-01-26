package IA;

import com.grooptown.ia.robotturtles.PlayerConnector;

import java.util.ArrayList;

import static com.grooptown.ia.robotturtles.PlayerConnector.getGameStateAsString;
import static com.grooptown.ia.robotturtles.SSLUtil.disableSSLValidation;

public class IA {
    public static void main(String[] args) throws Exception {
        // With JDK inferior to 8u101 you need to disable SSL validation.
        disableSSLValidation();
        PlayerConnector.baseUrl = "https://robot-turtles.grooptown.com/";

        int gameId = 55;
        int playTurnDelayMs = 1000;

        int player1Id = 0;
        String player1UUID = "c2e5bc98-3c4e-4480-8a91-39bfd83cd5bb";

        int player2Id = 1;
        String player2UUID = "faa2f4b5-8a00-4aaf-89fe-04b785fd8040";

        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(gameId, player1Id, player1UUID));
        players.add(new Player(gameId, player2Id, player2UUID));

        boolean gameOver = false;
        while (!gameOver) {
            for (Player player : players) {
                player.waitUntilItsMyTurn(gameId);
                String gameState = getGameStateAsString(gameId);
                player.secret = player.playerConnector.getPlayerSecret();
                player.playerConnector.playMove(getMove(gameState, player.idPlayer));
                Thread.sleep(playTurnDelayMs);
            }
        }
    }

    public static String getMove(String gameState, int playerId) {
        return "3;;";
    }
}
