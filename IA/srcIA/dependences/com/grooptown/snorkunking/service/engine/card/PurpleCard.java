package srcIA.dependences.com.grooptown.snorkunking.service.engine.card;

import srcIA.dependences.com.grooptown.snorkunking.service.engine.game.Game;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.player.MovementService;

/**
 * Created by thibautdebroca on 02/11/2019.
 */
public class PurpleCard extends Card {

    @Override
    public void play(Game game) {
        DirectionEnum nextDirection = MovementService.getNextPosition(game.findCurrentPlayer().getDirection(), true);
        game.findCurrentPlayer().setDirection(nextDirection);
        game.addMoveDescription("New Direction of the turtle is " + nextDirection + " \n");
    }
}
