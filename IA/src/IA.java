package src;

import com.grooptown.ia.robotturtles.PlayerConnector;
import com.grooptown.snorkunking.service.engine.player.PlayerSecret;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static com.grooptown.ia.robotturtles.PlayerConnector.getGameStateAsString;
import static com.grooptown.ia.robotturtles.SSLUtil.disableSSLValidation;

public class IA {
    public static void main(String[] args) throws Exception {
        // With JDK inferior to 8u101 you need to disable SSL validation.
//        disableSSLValidation();
        PlayerConnector.baseUrl = "https://robot-turtles.grooptown.com/";

        // Paramètres
        int gameId = 59;
        int playTurnDelayMs = 1000;  // Delay en ms entre les tours de chaque joueur
        String playersFileName = "IA/src/playersInfo.txt";
        boolean createPlayers = false;  // Détermine s'il faut créer de nouveaux joueurs ou utiliser les identités de joueurs déjà créés
        boolean scannerMovesMode = true;
        String[] nomsJoueurs = {"bleubidon", "bleubidu"};
        ArrayList<Player> players = new ArrayList<>();

        // Créer les joueurs si besoin
        if (createPlayers) {
            // Remove file if exists
            File f = new File(playersFileName);
            f.delete();

            PlayerConnector playerConnector = new PlayerConnector(gameId);

            for (int i = 0; i < nomsJoueurs.length; i++) {
                String nomJoueur = nomsJoueurs[i];
                String playerUUID = playerConnector.joinGame(nomJoueur);
                System.out.println(playerUUID);
                String playerData = nomJoueur + ";" + i + ";" + playerUUID + "\n";

                // Write player id and UUID to file
                File file = new File(playersFileName);
                FileWriter fr = new FileWriter(file, true);
                fr.write(playerData);
                fr.close();
            }
        }

        // S'identifier en tant que les joueurs créés
        // Read players data from file
        File file = new File(playersFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String[] playerData;
        while ((st = br.readLine()) != null) {
            playerData = st.split(";");
            players.add(new Player(gameId, Integer.parseInt(playerData[1]), playerData[2]));
        }

        boolean gameOver = false;
        while (!gameOver) {
            for (Player player : players) {
                player.waitUntilItsMyTurn(gameId);
                String gameState = getGameStateAsString(gameId);
                player.secret = player.playerConnector.getPlayerSecret();

                if (scannerMovesMode) {
                    System.out.println("Player " + player.idPlayer);
                    System.out.println(player.secret);
                    System.out.println("your move:");
                    player.playerConnector.playMove(new Scanner(System.in).nextLine());
                } else {
                    player.playerConnector.playMove(getMove(gameState, player.secret));
                    Thread.sleep(playTurnDelayMs);
                }
            }
        }
    }

    public static String getMove(String gameState, PlayerSecret secret) {
        return "3;;";  // Placeholder
    }
}
