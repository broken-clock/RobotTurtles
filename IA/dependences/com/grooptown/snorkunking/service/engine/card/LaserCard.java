// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.card;

import com.grooptown.snorkunking.service.engine.player.DirectionEnum;
import com.grooptown.snorkunking.service.engine.player.Position;
import com.grooptown.snorkunking.service.engine.grid.RubyPanel;
import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.tile.WallTile;
import com.grooptown.snorkunking.service.engine.tile.IceTile;
import com.grooptown.snorkunking.service.engine.player.MovementService;
import com.grooptown.snorkunking.service.engine.game.Game;

public class LaserCard extends Card
{
    @Override
    public void play(final Game game) {
        final Player currentPlayer = game.findCurrentPlayer();
        final Position position = game.getGrid().getPosition(currentPlayer);
        final DirectionEnum laserDirection = currentPlayer.getDirection();
        for (Position nextLaserPosition = MovementService.getNextPosition(position, laserDirection); !game.getGrid().isOutOfBound(nextLaserPosition); nextLaserPosition = MovementService.getNextPosition(nextLaserPosition, laserDirection)) {
            if (game.getGrid().getPanel(nextLaserPosition).getClass().equals(IceTile.class)) {
                game.addMoveDescription("Laser has hit an Ice Wall on " + nextLaserPosition + ". Ice Wall has been destroyed. \n");
                game.getGrid().makeCellEmpty(nextLaserPosition);
                return;
            }
            if (game.getGrid().getPanel(nextLaserPosition).getClass().equals(WallTile.class)) {
                game.addMoveDescription("Laser has hit a Brick Wall on " + nextLaserPosition + ". Ice Wall has no effect. \n");
                return;
            }
            if (game.getGrid().getPanel(nextLaserPosition).getClass().equals(Player.class)) {
                final Player touchedPlayer = (Player)game.getGrid().getPanel(nextLaserPosition);
                touchedPlayer.touchLaser(game);
                game.addMoveDescription("Laser has hit a turtle : " + touchedPlayer.getPlayerName() + " on " + nextLaserPosition + ". \n");
                return;
            }
            if (game.getGrid().getPanel(nextLaserPosition).getClass().equals(RubyPanel.class)) {
                game.addMoveDescription("Laser has hit a ruby. Laser is reflected and go back to current player. \n");
                game.findCurrentPlayer().touchLaser(game);
                return;
            }
        }
    }
}
