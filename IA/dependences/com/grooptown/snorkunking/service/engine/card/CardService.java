// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.card;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

public class CardService
{
    private static final Map<Character, Card> charToCards;
    
    public static List<Card> getNewCards(final String cards) {
        return cards.chars().mapToObj(c -> Character.valueOf((char)c)).map(c -> CardService.charToCards.get(c)).collect((Collector<? super Object, ?, List<Card>>)Collectors.toList());
    }
    
    public static boolean isValidCardsEntry(final String cards) {
        return cards.length() >= 1 && cards.length() <= 5 && isValidCardSet(cards);
    }
    
    private static boolean isValidCardSet(final String cards) {
        for (int i = 0; i < cards.length(); ++i) {
            if (!isValidCard(cards.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isValidCard(final char c) {
        return CardService.charToCards.containsKey(c);
    }
    
    public static boolean hasEnoughCards(final String entryToVerify, final List<Card> setOfCards) {
        final Set<Card> handCards = new HashSet<Card>(setOfCards);
        for (final Card card : getNewCards(entryToVerify)) {
            boolean found = false;
            final Iterator<Card> iterator = handCards.iterator();
            while (iterator.hasNext()) {
                final Card nextCard = iterator.next();
                if (nextCard.getClass().equals(card.getClass())) {
                    found = true;
                    iterator.remove();
                    break;
                }
            }
            if (!found) {
                System.out.println("You don't have enough " + card.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }
    
    public static void removeCardsFromHand(final List<Card> handOfCard, final List<Card> cardToRemove) {
        for (final Card card : cardToRemove) {
            boolean found = false;
            final Iterator<Card> iterator = handOfCard.iterator();
            while (iterator.hasNext()) {
                final Card nextCard = iterator.next();
                if (nextCard.getClass().equals(card.getClass())) {
                    found = true;
                    iterator.remove();
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Can't Remove this card : " + card.getClass().getSimpleName());
            }
        }
    }
    
    static {
        (charToCards = new HashMap<Character, Card>()).put('B', new BlueCard());
        CardService.charToCards.put('L', new LaserCard());
        CardService.charToCards.put('P', new PurpleCard());
        CardService.charToCards.put('Y', new YellowCard());
    }
}
