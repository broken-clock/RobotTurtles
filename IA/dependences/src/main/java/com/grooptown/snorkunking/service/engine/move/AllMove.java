package com.grooptown.snorkunking.service.engine.move;

import com.grooptown.snorkunking.service.engine.card.Card;
import com.grooptown.snorkunking.service.engine.card.CardService;
import com.grooptown.snorkunking.service.engine.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.grooptown.snorkunking.service.engine.card.CardService.hasEnoughCards;
import static com.grooptown.snorkunking.service.engine.card.CardService.isValidCardsEntry;

/**
 * Created by thibautdebroca on 08/11/2019.
 */
public class AllMove {
    private Move move;

    private List<Card> CardToFold = new ArrayList<>();

    public List<Card> getCardToFold() {
        return CardToFold;
    }

    public void setCardToFold(List<Card> cardToFold) {
        CardToFold = cardToFold;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public boolean areCardToFoldValid(String cardToFoldParam, Game game) {
        List<Card> playerHands = new LinkedList<>(game.findCurrentPlayer().handCards());
        if (this.getMove().getClass().equals(CompleteMove.class)) {
            CardService.removeCardsFromHand(playerHands, ((CompleteMove) (this.getMove())).getCardsToAdd());
        }

        return cardToFoldParam.isEmpty() ||
            (isValidCardsEntry(cardToFoldParam) && hasEnoughCards(cardToFoldParam, playerHands));
    }

    public String toPlayMoveString() {
        List<String> movesClassNames = Arrays.asList(Game.getNewPossiblesMoves())
                .stream()
                .map(m -> m != null ? m.getClass().getName(): "null")
                .collect(Collectors.toList());
        int moveIndex = movesClassNames.indexOf(this.getMove().getClass().getName());

        String fullMove = moveIndex + ";";
        if (this.getMove().getClass().equals(CompleteMove.class)) {
            List<Card> cardsToAdd = ((CompleteMove) this.getMove()).getCardsToAdd();
            fullMove += getCardsChars(cardsToAdd);
        } else if (this.getMove().getClass().equals(BuildWallMove.class)) {
            BuildWallMove buildWallMove = (BuildWallMove) (this.getMove());
            String tileName = buildWallMove.getTileToBuild().toAscii().replaceAll("\\s+", "");
            fullMove += tileName + " on "
                    + buildWallMove.getLine() + "-"
                    + buildWallMove.getColumn();
        }
        fullMove += ";";
        if (this.getCardToFold() != null) {
            fullMove += getCardsChars(this.getCardToFold());
        }
        return fullMove;
    }

    public String getCardsChars(List<Card> cards) {
        return cards
                .stream()
                .map(c -> c.getCardName().charAt(0) + "")
                .collect(Collectors.joining(""));
    }



}
