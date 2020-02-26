package srcIA;

import srcIA.dependences.com.grooptown.ia.robotturtles.PlayerConnector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static srcIA.dependences.com.grooptown.ia.robotturtles.PlayerConnector.getGameStateAsString;

public class IARun {
    public static void main(String[] args) throws Exception {
        // With JDK inferior to 8u101 you need to disable SSL validation.
//        disableSSLValidation();
        PlayerConnector.baseUrl = "https://robot-turtles.grooptown.com/";

        // Paramètres
        int gameId = 136;
        int playTurnDelayMs = 1000;  // Delai en ms entre les tours de chaque joueur (quand on est pas en mode scanner)
        String playersFileName = "IA/srcIA/playersInfo.txt";
//        boolean createPlayers = false;  // Détermine s'il faut créer de nouveaux joueurs ou utiliser les identités de joueurs déjà créés
        boolean scannerMovesMode = false;
        String[] nomsJoueurs = {"bleubidon", "bleubidu"};
        ArrayList<Player> players = new ArrayList<>();

        // Créer les joueurs si besoin
//        if (createPlayers) {
//            // Remove file if exists
//            File f = new File(playersFileName);
//            f.delete();
//
//            PlayerConnector playerConnector = new PlayerConnector(gameId);
//
//            for (int i = 0; i < nomsJoueurs.length; i++) {
//                String nomJoueur = nomsJoueurs[i];
//                String playerUUID = playerConnector.joinGame(nomJoueur);
//                System.out.println(playerUUID);
//                String playerData = nomJoueur + ";" + i + ";" + playerUUID + "\n";
//
//                // Write player id and UUID to file
//                File file = new File(playersFileName);
//                FileWriter fr = new FileWriter(file, true);
//                fr.write(playerData);
//                fr.close();
//            }
//        }

        // S'identifier en tant que les joueurs créés
        // Read players data from file
        File file = new File(playersFileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String[] playerData;
        while ((st = br.readLine()) != null) {
            playerData = st.split(";");
            players.add(new Player(gameId, playerData[0], Integer.parseInt(playerData[1]), playerData[2]));
        }

        boolean gameOver = false;
        while (!gameOver) {
            for (Player player : players) {
                if (player.playerName.equals("bleubidon")) continue;
                player.waitUntilItsMyTurn(gameId);

                // Recover useful data
                String gameState = getGameStateAsString(gameId);
                File gameStateFile = new File("gameState.txt");
                FileWriter fr = new FileWriter(gameStateFile, true);
                fr.write(gameState);
                fr.close();

                player.secret = player.playerConnector.getPlayerSecret();

                if (scannerMovesMode) {
                    System.out.println(gameState);
                    System.out.print("Player " + player.idPlayer);
                    System.out.println(" (" + player.playerName + ")");
                    System.out.println(player.secret);
                    System.out.println("your move:");
                    player.playerConnector.playMove(new Scanner(System.in).nextLine());
                } else {
                    String move;
                    move = IAProfiles.myIAProfile(gameState, player.secret);
                    player.playerConnector.playMove(move);
                    Thread.sleep(playTurnDelayMs);
                }
            }
        }
    }
}
