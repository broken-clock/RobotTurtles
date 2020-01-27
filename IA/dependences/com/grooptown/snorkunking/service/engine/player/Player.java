// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grooptown.snorkunking.service.engine.card.CardService;
import com.grooptown.snorkunking.service.engine.grid.PanelEnum;
import com.grooptown.snorkunking.service.engine.game.Game;
import com.grooptown.snorkunking.service.engine.grid.Grid;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import com.grooptown.snorkunking.service.engine.tile.IceTile;
import com.grooptown.snorkunking.service.engine.tile.WallTile;
import java.util.ArrayList;
import com.grooptown.snorkunking.service.engine.card.CardDeck;
import com.grooptown.snorkunking.service.engine.card.Card;
import com.grooptown.snorkunking.service.engine.tile.Tile;
import java.util.List;
import com.grooptown.snorkunking.service.engine.grid.Panel;

public class Player implements Panel
{
    public static final int MAX_CARD_ALLOWED_IN_HAND = 5;
    private String playerName;
    private List<Tile> tiles;
    private final List<Card> handCards;
    private final List<Card> program;
    private final List<Card> discarded;
    private CardDeck cardDeck;
    private DirectionEnum direction;
    private Position initialPosition;
    private boolean rubyReached;
    
    public Player() {
        this.tiles = new ArrayList<Tile>();
        this.handCards = new ArrayList<Card>();
        this.program = new ArrayList<Card>();
        this.discarded = new ArrayList<Card>();
        this.direction = DirectionEnum.SOUTH;
    }
    
    public Player(final String playerName) {
        this.tiles = new ArrayList<Tile>();
        this.handCards = new ArrayList<Card>();
        this.program = new ArrayList<Card>();
        this.discarded = new ArrayList<Card>();
        this.direction = DirectionEnum.SOUTH;
        this.playerName = playerName;
    }
    
    public void initPlayerTiles() {
        for (int j = 0; j < 3; ++j) {
            this.tiles.add(new WallTile());
        }
        for (int j = 0; j < 2; ++j) {
            this.tiles.add(new IceTile());
        }
    }
    
    public void setTiles(final List<Tile> tiles) {
        this.tiles = tiles;
    }
    
    public List<Tile> getTiles() {
        return this.tiles;
    }
    
    public List<Card> handCards() {
        return this.handCards;
    }
    
    @Override
    public String toAscii() {
        return " T" + this.playerName.substring(0, 2) + " ";
    }
    
    public void setCardDeck(final CardDeck cardDeck) {
        this.cardDeck = cardDeck;
    }
    
    public void pickCardInDeck() {
        if (this.cardDeck.getCards().size() == 0) {
            Collections.shuffle(this.discarded);
            this.cardDeck.getCards().addAll(this.discarded);
            this.discarded.clear();
        }
        if (this.handCards.size() >= 5) {
            throw new RuntimeException("Already 5 handCards ...");
        }
        this.handCards.add(this.cardDeck.getCards().pollFirst());
    }
    
    public void displayPlayer() {
        System.out.println("###### Player " + this.playerName + " id in Direction " + this.direction + " and has : ######");
        System.out.println("  - " + this.tiles.size() + " Tiles : " + this.tiles);
        System.out.println("  - " + this.handCards.size() + " Cards : " + Card.cardsToString(this.handCards));
        System.out.println("  - Program is composed of " + this.program.size() + " Cards : " + Card.cardsToString(this.program));
    }
    
    public void foldProgramCards() {
        final Iterator<Card> iterator = this.program.iterator();
        while (iterator.hasNext()) {
            final Card card = iterator.next();
            this.addToDiscarded(card);
            iterator.remove();
        }
    }
    
    public boolean hasTile(final Tile tile) {
        return this.getTiles().stream().anyMatch(t -> t.getClass().equals(tile.getClass()));
    }
    
    public void removeTile(final Tile tile) {
        final Iterator<Tile> tilesIt = this.tiles.iterator();
        while (tilesIt.hasNext()) {
            final Tile currentTile = tilesIt.next();
            if (currentTile.getClass().equals(tile.getClass())) {
                tilesIt.remove();
                return;
            }
        }
        System.err.println("Error, should have tile to be removed...");
    }
    
    public void addCardsToProgram(final List<Card> newCards) {
        this.program.addAll(newCards);
    }
    
    public List<Card> program() {
        return this.program;
    }
    
    public DirectionEnum getDirection() {
        return this.direction;
    }
    
    public void setDirection(final DirectionEnum direction) {
        this.direction = direction;
    }
    
    public void setInitialPosition(final Position initialPosition) {
        this.setDirection(DirectionEnum.SOUTH);
        this.initialPosition = initialPosition;
    }
    
    public void backToInitialPosition(final Grid grid) {
        this.moveTo(this.initialPosition, grid);
        this.setDirection(DirectionEnum.SOUTH);
    }
    
    public void moveTo(final Position nextPosition, final Grid grid) {
        final Position oldPosition = grid.getPosition(this);
        grid.makeCellEmpty(oldPosition);
        grid.placePlayer(nextPosition, this);
    }
    
    public void touchTurtle(final Game game) {
        this.backToInitialPosition(game.getGrid());
    }
    
    public void touchLaser(final Game game) {
        if (game.getPlayers().size() > 2) {
            this.backToInitialPosition(game.getGrid());
        }
        else {
            this.reverseDirection();
        }
    }
    
    public void reverseDirection() {
        final DirectionEnum oppositeDirection = MovementService.getOppositeDirection(this.getDirection());
        this.setDirection(oppositeDirection);
    }
    
    public boolean isRubyReached() {
        return this.rubyReached;
    }
    
    public void setRubyReached(final boolean rubyReached) {
        this.rubyReached = rubyReached;
    }
    
    @Override
    public PanelEnum getPanelName() {
        return PanelEnum.PLAYER;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void removeCardsFromHand(final List<Card> cardsToAdd) {
        CardService.removeCardsFromHand(this.handCards(), cardsToAdd);
    }
    
    public Position getInitialPosition() {
        return this.initialPosition;
    }
    
    @JsonIgnore
    public PlayerSecret getSecrets() {
        return new PlayerSecret(this.handCards, this.program);
    }
    
    public void clearSecrets() {
        this.handCards.clear();
        this.program.clear();
    }
    
    public void addToDiscarded(final Card card) {
        this.discarded.add(card);
    }
}
