// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.card;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import com.grooptown.snorkunking.service.engine.game.Game;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Card
{
    public abstract void play(final Game p0);
    
    public static String cardsToString(final List<Card> cards) {
        return cards.stream().map(c -> c.getClass().getSimpleName().replace("Card", "")).collect((Collector<? super Object, ?, String>)Collectors.joining(", "));
    }
    
    public String getCardName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public String toString() {
        return this.getCardName();
    }
}
