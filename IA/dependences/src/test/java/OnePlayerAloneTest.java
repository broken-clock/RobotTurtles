import com.grooptown.ia.robotturtles.PlayerConnector;
import com.grooptown.snorkunking.service.engine.game.Game;
import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.player.PlayerSecret;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Scanner;
import java.util.UUID;

import static com.grooptown.ia.robotturtles.SSLUtil.disableSSLValidation;

/**
 * Connects a Player to a game.
 * Created by thibautdebroca on 09/01/2019.
 */
public class OnePlayerAloneTest {

    public static void main(String[] args) throws Exception {
        // With JDK inferior to 8u101 you need to disable SSL validation.
        disableSSLValidation();
        // PlayerConnector.baseUrl = "http://localhost:8080";
        PlayerConnector.baseUrl = "https://robot-turtles.grooptown.com/";

        int gameId = 43;
        String playerName = "Player-" + UUID.randomUUID();

        PlayerConnector playerConnector = new PlayerConnector(gameId);
        playerConnector.joinGame(playerName);


        while (true) {
            waitUntilItsMyTurn(playerConnector, gameId);

            String gameState = PlayerConnector.getGameStateAsString(gameId);
            System.out.println(gameState);

            PlayerSecret playerSecret = playerConnector.getPlayerSecret();
            System.out.println("Your Secret are : " + playerSecret);
            System.out.println(playerName + ". Please Enter a Move :");

            playerConnector.playMove(new Scanner(System.in).nextLine());
        }

    }


    public static void waitUntilItsMyTurn(PlayerConnector playerConnector, int gameId) throws InterruptedException {
        System.out.println("Waiting for our Turn to play...");
        while (true) {
            try {
                Game game = PlayerConnector.getGameState(gameId);
                if (game.isStarted()
                    && getCurrentPlayer(game).getPlayerName().equals(playerConnector.getPlayer().getPlayerFromInstance(game).getPlayerName())) {
                    return;
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    System.out.println("Game has not started yet.");
                }
            }
            Thread.sleep(500);
        }
    }

    public static Player getCurrentPlayer(Game game) {
        return game.getPlayers().get(game.getCurrentIdPlayerTurn());
    }

}
