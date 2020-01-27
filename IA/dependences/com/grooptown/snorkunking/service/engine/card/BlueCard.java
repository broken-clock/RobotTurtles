// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.card;

import com.grooptown.snorkunking.service.engine.player.Position;
import com.grooptown.snorkunking.service.engine.grid.RubyPanel;
import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.tile.WoodBoxTile;
import com.grooptown.snorkunking.service.engine.tile.WallTile;
import com.grooptown.snorkunking.service.engine.tile.IceTile;
import com.grooptown.snorkunking.service.engine.player.MovementService;
import com.grooptown.snorkunking.service.engine.game.Game;

public class BlueCard extends Card
{
    @Override
    public void play(final Game game) {
        final Player currentPlayer = game.findCurrentPlayer();
        final Position currentPosition = game.getGrid().getPosition(currentPlayer);
        final Position nextPosition = MovementService.getNextPosition(currentPosition, currentPlayer.getDirection());
        game.addMoveDescription("Next Position of turtle should be " + nextPosition + "\n");
        if (game.getGrid().isOutOfBound(nextPosition)) {
            game.addMoveDescription("Turtle is going out of board. It goes back to it's initial Position " + game.findCurrentPlayer().getInitialPosition() + "\n");
            currentPlayer.backToInitialPosition(game.getGrid());
        }
        else if (game.getGrid().getPanel(nextPosition).getClass().equals(IceTile.class) || game.getGrid().getPanel(nextPosition).getClass().equals(WallTile.class) || game.getGrid().getPanel(nextPosition).getClass().equals(WoodBoxTile.class)) {
            currentPlayer.reverseDirection();
            game.addMoveDescription("Turtle has hit a Wall. It's reversing it's direction to : " + game.findCurrentPlayer().getDirection() + "\n");
        }
        else if (game.getGrid().getPanel(nextPosition).getClass().equals(Player.class)) {
            final Player touchedPlayer = (Player)game.getGrid().getPanel(nextPosition);
            touchedPlayer.touchTurtle(game);
            currentPlayer.touchTurtle(game);
            game.addMoveDescription("Turtle has hit turtle " + touchedPlayer.getPlayerName() + "\n");
        }
        else if (game.getGrid().getPanel(nextPosition).getClass().equals(RubyPanel.class)) {
            currentPlayer.setRubyReached(true);
            game.addMoveDescription("Turtle has reached a Ruby ! \n");
            System.out.println("Player " + currentPlayer.getPlayerName() + " has reached a Ruby !");
            game.getGrid().makeCellEmpty(currentPosition);
            game.getLeaderBoard().add(game.findCurrentPlayer());
        }
        else {
            currentPlayer.moveTo(nextPosition, game.getGrid());
        }
    }
}
