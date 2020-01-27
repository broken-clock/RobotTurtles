// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.connector;

import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.game.Game;
import java.util.Date;

public class PlayerInstance
{
    private int idGame;
    private String UUID;
    private int idPlayer;
    private Date timeStart;
    
    private PlayerInstance() {
    }
    
    public PlayerInstance(final int idGame, final int idPlayer, final String userId) {
        this.idPlayer = idPlayer;
        this.idGame = idGame;
        this.UUID = userId;
        this.timeStart = new Date();
    }
    
    public int getIdGame() {
        return this.idGame;
    }
    
    public void setIdGame(final int idGame) {
        this.idGame = idGame;
    }
    
    public String getUUID() {
        return this.UUID;
    }
    
    public void setUUID(final String UUID) {
        this.UUID = UUID;
    }
    
    public int getIdPlayer() {
        return this.idPlayer;
    }
    
    public void setIdPlayer(final int idPlayer) {
        this.idPlayer = idPlayer;
    }
    
    public Date getTimeStart() {
        return this.timeStart;
    }
    
    public void setTimeStart(final Date timeStart) {
        this.timeStart = timeStart;
    }
    
    public Player getPlayerFromInstance(final Game game) {
        return game.getPlayers().get(this.getIdPlayer());
    }
}
