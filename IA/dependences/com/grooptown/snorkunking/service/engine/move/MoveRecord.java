// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.move;

import java.util.LinkedList;
import java.util.List;

public class MoveRecord
{
    private int turnNumber;
    private String title;
    private List<String> description;
    private String playerName;
    private int numberOfCardFold;
    
    public MoveRecord() {
        this.description = new LinkedList<String>();
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public List<String> getDescription() {
        return this.description;
    }
    
    public void setDescription(final List<String> description) {
        this.description = description;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public int getTurnNumber() {
        return this.turnNumber;
    }
    
    public void setTurnNumber(final int turnNumber) {
        this.turnNumber = turnNumber;
    }
    
    public int getNumberOfCardFold() {
        return this.numberOfCardFold;
    }
    
    public void setNumberOfCardFold(final int numberOfCardFold) {
        this.numberOfCardFold = numberOfCardFold;
    }
}
