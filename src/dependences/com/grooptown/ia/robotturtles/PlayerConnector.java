package com.grooptown.ia.robotturtles;


import com.grooptown.snorkunking.service.engine.connector.MessageResponse;
import com.grooptown.snorkunking.service.engine.connector.PlayerInstance;
import com.grooptown.snorkunking.service.engine.game.Game;
import com.grooptown.snorkunking.service.engine.player.PlayerSecret;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Data
public class PlayerConnector {

    private int gameId;

    public PlayerInstance player;

    // public static String baseUrl = "https://domi-nation.grooptown.com";
    public static String baseUrl = "https://localhost:8080";

    private static RestTemplate restTemplate = getRestTemplate();

    public PlayerConnector(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Constructor for RestTemplate.
     * @return An instance of a RestTemplate.
     */
    private static RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }

    /**
     * Creates a Game on the server.
     * @param playerCount Counts number of players.
     * @return The Game created with its uuid.
     */
    public static Game createNewGame(int playerCount) {
        return restTemplate.postForObject(
                baseUrl + "/new-games/?playerCount=" + playerCount,
                null, Game.class
        );
    }

    /**
     * Make one player joining the game. com.grooptown.ia.robotturtles.PlayerConnector will store the secret uuid of the player.
     * @param playerName The player name who want to join the game.
     */
    public String joinGame(String playerName) {
        player = restTemplate.getForObject(
                baseUrl+ "/api/iaconnector/addPlayer?playerName="
                        + playerName + "&idGame=" + gameId,
                PlayerInstance.class
        );
        if (player == null) {
            throw new RuntimeException("Error while joining Game. It's either already started, or have reach max num of players.");
        }
        return player.getUUID();
    }

    /**
     * Gets game state as a String.
     * @param gameId Id Of the game.
     * @return Game represented as a String.
     */
    public static String getGameStateAsString(int gameId) {
        return restTemplate.getForEntity(
                baseUrl + "/api/iaconnector/game/" + gameId,
                String.class
        ).getBody();
    }


    /**
     * Gets Game State as a Java Object GameState.
     * @param gameId ID Of the game we want to get.
     * @return State of the game.
     */
    public static Game getGameState(int gameId) {
        return restTemplate.getForEntity(
                baseUrl + "/api/iaconnector/game/" + gameId,
                Game.class
        ).getBody();
    }


    /**
     * The players will play move number "moveNumber".
     * @param move Number of the Move.
     * @return State of the Game.
     */
    public MessageResponse playMove(String move) {
        System.out.println("Playing Move " + move + " for player " + player.getUUID());
        try {
            return restTemplate.getForObject(
                    baseUrl + "/api/iaconnector/sendMove?playerUUID=" + player.getUUID() + "&move=" + move,
                    MessageResponse.class
            );
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public PlayerSecret getPlayerSecret() {
        return restTemplate.getForObject(
                baseUrl + "/api/iaconnector/player/secrets/" + player.getUUID(),
                PlayerSecret.class
        );
    }


}
