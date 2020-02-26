package srcIA.dependences;

import srcIA.dependences.com.grooptown.snorkunking.service.engine.game.Game;
import srcIA.dependences.com.grooptown.snorkunking.service.engine.player.Player;

public class Utils {

    public static Player getCurrentPlayer(Game game) {
        return game.getPlayers().get(game.getCurrentIdPlayerTurn());
    }
}
