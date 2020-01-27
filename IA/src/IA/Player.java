//package IA;
//
//import IA.dependences.Utils;
//import com.grooptown.ia.robotturtles.PlayerConnector;
//import com.grooptown.snorkunking.service.engine.connector.PlayerInstance;
//import com.grooptown.snorkunking.service.engine.game.Game;
//import com.grooptown.snorkunking.service.engine.player.PlayerSecret;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.client.HttpClientErrorException;
//
///**
// * Connects a Player to a game.
// * Created by thibautdebroca on 09/01/2019.
// */
//public class Player {
//    int idPlayer;
//    PlayerConnector playerConnector;
//    PlayerSecret secret;
//
//    public Player(int idGame, int idPlayer, String userId) {
//        this.idPlayer = idPlayer;
//        PlayerInstance myPlayerInstance = new PlayerInstance(idGame, idPlayer, userId);
//        this.playerConnector = new PlayerConnector(idGame);
//        this.playerConnector.setPlayer(myPlayerInstance);
//    }
//
//    public void waitUntilItsMyTurn(int gameId) throws InterruptedException {
//        System.out.println("Waiting for our Turn to play...");
//        while (true) {
//            try {
//                Game game = PlayerConnector.getGameState(gameId);
//                if (game.isStarted()
//                        && Utils.getCurrentPlayer(game).getPlayerName().equals(this.playerConnector.getPlayer().getPlayerFromInstance(game).getPlayerName())) {
//                    return;
//                }
//            } catch (HttpClientErrorException e) {
//                if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//                    System.out.println("Game has not started yet.");
//                }
//            }
//            Thread.sleep(500);
//        }
//    }
//}
