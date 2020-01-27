package com.grooptown.snorkunking.service.engine.player;

import com.grooptown.snorkunking.service.engine.card.Card;

import java.util.List;

public class PlayerSecret {
    private List<Card> handCards;
    private List<Card> program;

    public PlayerSecret() {
    }

    public PlayerSecret(List<Card> handCards, List<Card> program) {
        this.handCards = handCards;
        this.program = program;
    }

    public List<Card> getProgram() {
        return program;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Card> handCards) {
        this.handCards = handCards;
    }

    public void setProgram(List<Card> program) {
        this.program = program;
    }

    @Override
    public String toString() {
        return "PlayerSecret{" +
                "handCards=" + handCards +
                ", program=" + program +
                '}';
    }
}
