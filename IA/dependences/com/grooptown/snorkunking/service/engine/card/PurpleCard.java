// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.card;

import com.grooptown.snorkunking.service.engine.player.DirectionEnum;
import com.grooptown.snorkunking.service.engine.player.MovementService;
import com.grooptown.snorkunking.service.engine.game.Game;

public class PurpleCard extends Card
{
    @Override
    public void play(final Game game) {
        final DirectionEnum nextDirection = MovementService.getNextPosition(game.findCurrentPlayer().getDirection(), true);
        game.findCurrentPlayer().setDirection(nextDirection);
        game.addMoveDescription("New Direction of the turtle is " + nextDirection + " \n");
    }
}
