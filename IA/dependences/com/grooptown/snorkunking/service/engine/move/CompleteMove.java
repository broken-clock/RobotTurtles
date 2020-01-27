// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.move;

import com.grooptown.snorkunking.service.engine.card.CardService;
import com.grooptown.snorkunking.service.engine.card.Card;
import java.util.List;

public class CompleteMove extends Move
{
    private List<Card> cardsToAdd;
    
    @Override
    public boolean isValidMove(final String entry) {
        if (!CardService.isValidCardsEntry(entry)) {
            System.out.println("Entry is not Valid");
            return false;
        }
        if (this.game.findCurrentPlayer().program().size() + entry.length() > 5) {
            System.out.println("You'll have too many Card in your Program !");
            return false;
        }
        return CardService.hasEnoughCards(entry, this.game.findCurrentPlayer().handCards());
    }
    
    @Override
    public void constructMoveFromEntry(final String entry) {
        this.cardsToAdd = CardService.getNewCards(entry);
    }
    
    @Override
    public void playMove() {
        this.game.addMoveDescription(" - Player added " + this.cardsToAdd.size() + " cards to it's program \n");
        this.game.findCurrentPlayer().removeCardsFromHand(this.cardsToAdd);
        this.game.findCurrentPlayer().addCardsToProgram(this.cardsToAdd);
    }
    
    @Override
    public String entryQuestion() {
        return "What cards (1 to 5) do you want to add ? ( i.e.: BBLYP (for Blue, Blue, Laser, Yellow, Purple)";
    }
    
    public List<Card> getCardsToAdd() {
        return this.cardsToAdd;
    }
    
    public void setCardsToAdd(final List<Card> cardsToAdd) {
        this.cardsToAdd = cardsToAdd;
    }
}
