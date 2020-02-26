package srcIA.dependences.com.grooptown.snorkunking.service.engine.card;

import srcIA.dependences.com.grooptown.snorkunking.service.engine.game.Game;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.grid.RubyPanel;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.player.Player;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.player.Position;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.tile.IceTile;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.tile.WallTile;

import static srcIA.dependences.com.grooptown.snorkunking.service.engine.player.MovementService.getNextPosition;

/**
 * Created by thibautdebroca on 02/11/2019.
 */
public class LaserCard extends Card {
    @Override
    public void play(Game game) {
        Player currentPlayer = game.findCurrentPlayer();
        Position position = game.getGrid().getPosition(currentPlayer);
        DirectionEnum laserDirection = currentPlayer.getDirection();
        Position nextLaserPosition = getNextPosition(position, laserDirection);
        while (!game.getGrid().isOutOfBound(nextLaserPosition)) {
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
                Player touchedPlayer = (Player) game.getGrid().getPanel(nextLaserPosition);
                touchedPlayer.touchLaser(game);
                game.addMoveDescription("Laser has hit a turtle : " + touchedPlayer.getPlayerName() + " on " + nextLaserPosition + ". \n");
                return;
            }
            if (game.getGrid().getPanel(nextLaserPosition).getClass().equals(RubyPanel.class)) {
                game.addMoveDescription("Laser has hit a ruby. Laser is reflected and go back to current player. \n");
                game.findCurrentPlayer().touchLaser(game);
                return;
            }
            nextLaserPosition = getNextPosition(nextLaserPosition, laserDirection);
        }
    }
}
