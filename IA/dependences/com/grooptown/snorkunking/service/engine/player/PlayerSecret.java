// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.player;

import com.grooptown.snorkunking.service.engine.card.Card;
import java.util.List;

public class PlayerSecret
{
    private List<Card> handCards;
    private List<Card> program;
    
    public PlayerSecret() {
    }
    
    public PlayerSecret(final List<Card> handCards, final List<Card> program) {
        this.handCards = handCards;
        this.program = program;
    }
    
    public List<Card> getProgram() {
        return this.program;
    }
    
    public List<Card> getHandCards() {
        return this.handCards;
    }
    
    public void setHandCards(final List<Card> handCards) {
        this.handCards = handCards;
    }
    
    public void setProgram(final List<Card> program) {
        this.program = program;
    }
    
    @Override
    public String toString() {
        return "PlayerSecret{handCards=" + this.handCards + ", program=" + this.program + '}';
    }
}
