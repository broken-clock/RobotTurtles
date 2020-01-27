// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.ia.robotturtles;

import com.grooptown.snorkunking.service.engine.player.PlayerSecret;
import com.grooptown.snorkunking.service.engine.connector.MessageResponse;
import com.grooptown.snorkunking.service.engine.game.Game;
import java.util.Arrays;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.grooptown.snorkunking.service.engine.connector.PlayerInstance;

public class PlayerConnector
{
    private int gameId;
    public PlayerInstance player;
    public static String baseUrl;
    private static RestTemplate restTemplate;
    
    public PlayerConnector(final int gameId) {
        this.gameId = gameId;
    }
    
    private static RestTemplate getRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        return restTemplate;
    }
    
    public static Game createNewGame(final int playerCount) {
        return PlayerConnector.restTemplate.postForObject(PlayerConnector.baseUrl + "/new-games/?playerCount=" + playerCount, null, Game.class, new Object[0]);
    }
    
    public void joinGame(final String playerName) {
        this.player = PlayerConnector.restTemplate.getForObject(PlayerConnector.baseUrl + "/api/iaconnector/addPlayer?playerName=" + playerName + "&idGame=" + this.gameId, PlayerInstance.class, new Object[0]);
        if (this.player == null) {
            throw new RuntimeException("Error while joining Game. It's either already started, or have reach max num of players.");
        }
        System.out.println("Your secret UUI is : " + this.player.getUUID());
    }
    
    public static String getGameStateAsString(final int gameId) {
        return PlayerConnector.restTemplate.getForEntity(PlayerConnector.baseUrl + "/api/iaconnector/game/" + gameId, String.class, new Object[0]).getBody();
    }
    
    public static Game getGameState(final int gameId) {
        return PlayerConnector.restTemplate.getForEntity(PlayerConnector.baseUrl + "/api/iaconnector/game/" + gameId, Game.class, new Object[0]).getBody();
    }
    
    public MessageResponse playMove(final String move) {
        System.out.println("Playing Move " + move + " for player " + this.player.getUUID());
        try {
            return PlayerConnector.restTemplate.getForObject(PlayerConnector.baseUrl + "/api/iaconnector/sendMove?playerUUID=" + this.player.getUUID() + "&move=" + move, MessageResponse.class, new Object[0]);
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
    public PlayerSecret getPlayerSecret() {
        return PlayerConnector.restTemplate.getForObject(PlayerConnector.baseUrl + "/api/iaconnector/player/secrets/" + this.player.getUUID(), PlayerSecret.class, new Object[0]);
    }
    
    public int getGameId() {
        return this.gameId;
    }
    
    public PlayerInstance getPlayer() {
        return this.player;
    }
    
    public void setGameId(final int gameId) {
        this.gameId = gameId;
    }
    
    public void setPlayer(final PlayerInstance player) {
        this.player = player;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerConnector)) {
            return false;
        }
        final PlayerConnector other = (PlayerConnector)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getGameId() != other.getGameId()) {
            return false;
        }
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null) {
            if (other$player == null) {
                return true;
            }
        }
        else if (this$player.equals(other$player)) {
            return true;
        }
        return false;
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof PlayerConnector;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getGameId();
        final Object $player = this.getPlayer();
        result = result * 59 + (($player == null) ? 43 : $player.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return "PlayerConnector(gameId=" + this.getGameId() + ", player=" + this.getPlayer() + ")";
    }
    
    static {
        PlayerConnector.baseUrl = "https://localhost:8080";
        PlayerConnector.restTemplate = getRestTemplate();
    }
}
