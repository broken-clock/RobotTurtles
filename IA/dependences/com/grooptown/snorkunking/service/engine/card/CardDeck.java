// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.card;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;

public class CardDeck
{
    private LinkedList<Card> cards;
    
    public LinkedList<Card> getCards() {
        return this.cards;
    }
    
    public CardDeck() {
        this.buildDefaultDeck();
    }
    
    public void buildDefaultDeck() {
        this.cards = new LinkedList<Card>();
        for (int i = 0; i < 18; ++i) {
            this.cards.add(new BlueCard());
        }
        for (int i = 0; i < 8; ++i) {
            this.cards.add(new YellowCard());
        }
        for (int i = 0; i < 8; ++i) {
            this.cards.add(new PurpleCard());
        }
        for (int i = 0; i < 3; ++i) {
            this.cards.add(new LaserCard());
        }
        Collections.shuffle(this.cards);
    }
}
