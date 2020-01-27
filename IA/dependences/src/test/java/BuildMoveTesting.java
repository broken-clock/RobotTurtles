import com.grooptown.snorkunking.service.engine.card.*;
import com.grooptown.snorkunking.service.engine.game.Game;
import com.grooptown.snorkunking.service.engine.move.AllMove;
import com.grooptown.snorkunking.service.engine.move.BuildWallMove;
import com.grooptown.snorkunking.service.engine.move.CompleteMove;
import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.tile.IceTile;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * Connects a Player to a game.
 * Created by thibautdebroca on 09/01/2019.
 */
public class BuildMoveTesting {

    public static void main(String[] args) throws Exception {
        AllMove move = new AllMove();
        BuildWallMove buildWallMove = new BuildWallMove();
        buildWallMove.setLine(4);
        buildWallMove.setColumn(6);
        buildWallMove.setTileToBuild(new IceTile());
        move.setMove(buildWallMove);

        List<Card> cards = new LinkedList<>();
        cards.add(new BlueCard());
        cards.add(new YellowCard());
        move.setCardToFold(cards);

        System.out.println(move.toPlayMoveString());
        Assert.isTrue(move.toPlayMoveString().equals("2;Ice on 4-6;BY"));

        AllMove move2 = new AllMove();
        CompleteMove completeMove = new CompleteMove();

        List<Card> cards3 = new LinkedList<>();
        cards3.add(new PurpleCard());
        cards3.add(new BlueCard());
        cards3.add(new YellowCard());
        completeMove.setCardsToAdd(cards3);
        move2.setMove(completeMove);

        List<Card> cards2 = new LinkedList<>();
        cards2.add(new PurpleCard());
        cards2.add(new LaserCard());
        cards2.add(new PurpleCard());
        move2.setCardToFold(cards2);

        System.out.println(move2.toPlayMoveString());
        Assert.isTrue(move2.toPlayMoveString().equals("1;PBY;PLP"));
    }

}
