// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.move;

import java.util.Iterator;
import com.grooptown.snorkunking.service.engine.card.Card;

public class ExecuteMove extends Move
{
    @Override
    public boolean isValidMove(final String entry) {
        return entry.isEmpty();
    }
    
    @Override
    public void constructMoveFromEntry(final String entry) {
    }
    
    @Override
    public void playMove() {
        for (final Card card : this.game.findCurrentPlayer().program()) {
            this.game.addMoveDescription(" - Playing " + card.getCardName() + "\n");
            card.play(this.game);
            this.game.findCurrentPlayer().addToDiscarded(card);
            if (this.game.findCurrentPlayer().isRubyReached()) {
                break;
            }
        }
        this.game.findCurrentPlayer().foldProgramCards();
    }
    
    @Override
    public String entryQuestion() {
        return null;
    }
}
