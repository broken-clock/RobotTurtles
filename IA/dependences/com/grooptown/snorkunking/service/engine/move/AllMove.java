// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.move;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.grooptown.snorkunking.service.engine.card.CardService;
import java.util.Collection;
import java.util.LinkedList;
import com.grooptown.snorkunking.service.engine.game.Game;
import java.util.ArrayList;
import com.grooptown.snorkunking.service.engine.card.Card;
import java.util.List;

public class AllMove
{
    private Move move;
    private List<Card> CardToFold;
    
    public AllMove() {
        this.CardToFold = new ArrayList<Card>();
    }
    
    public List<Card> getCardToFold() {
        return this.CardToFold;
    }
    
    public void setCardToFold(final List<Card> cardToFold) {
        this.CardToFold = cardToFold;
    }
    
    public Move getMove() {
        return this.move;
    }
    
    public void setMove(final Move move) {
        this.move = move;
    }
    
    public boolean areCardToFoldValid(final String cardToFoldParam, final Game game) {
        final List<Card> playerHands = new LinkedList<Card>(game.findCurrentPlayer().handCards());
        if (this.getMove().getClass().equals(CompleteMove.class)) {
            CardService.removeCardsFromHand(playerHands, ((CompleteMove)this.getMove()).getCardsToAdd());
        }
        return cardToFoldParam.isEmpty() || (CardService.isValidCardsEntry(cardToFoldParam) && CardService.hasEnoughCards(cardToFoldParam, playerHands));
    }
    
    public String toPlayMoveString() {
        final List<String> movesClassNames = Arrays.asList(Game.getNewPossiblesMoves()).stream().map(m -> (m != null) ? m.getClass().getName() : "null").collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        final int moveIndex = movesClassNames.indexOf(this.getMove().getClass().getName());
        String fullMove = moveIndex + ";";
        if (this.getMove().getClass().equals(CompleteMove.class)) {
            final List<Card> cardsToAdd = ((CompleteMove)this.getMove()).getCardsToAdd();
            fullMove += this.getCardsChars(cardsToAdd);
        }
        else if (this.getMove().getClass().equals(BuildWallMove.class)) {
            final BuildWallMove buildWallMove = (BuildWallMove)this.getMove();
            final String tileName = buildWallMove.getTileToBuild().toAscii().replaceAll("\\s+", "");
            fullMove = fullMove + tileName + " on " + buildWallMove.getLine() + "-" + buildWallMove.getColumn();
        }
        fullMove += ";";
        if (this.getCardToFold() != null) {
            fullMove += this.getCardsChars(this.getCardToFold());
        }
        return fullMove;
    }
    
    public String getCardsChars(final List<Card> cards) {
        return cards.stream().map(c -> c.getCardName().charAt(0) + "").collect((Collector<? super Object, ?, String>)Collectors.joining(""));
    }
}
