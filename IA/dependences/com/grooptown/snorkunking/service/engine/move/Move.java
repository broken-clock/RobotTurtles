// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.move;

import com.grooptown.snorkunking.service.engine.game.Game;

public abstract class Move
{
    protected Game game;
    
    public void setGame(final Game game) {
        this.game = game;
    }
    
    public abstract boolean isValidMove(final String p0);
    
    public abstract void constructMoveFromEntry(final String p0);
    
    public abstract void playMove();
    
    public abstract String entryQuestion();
}
