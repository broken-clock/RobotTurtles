package srcIA.dependences.com.grooptown.snorkunking.service.engine.player;

import java.util.Arrays;

import static srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum.*;

public class MovementService {
    public final static srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum[] directions = new srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum[] {
            WEST, NORTH, EAST, SOUTH
    };
    private final static int[] directionsX = {-1, 0, 1, 0};
    private final static int[] directionsY = {0, -1, 0, 1};
    public static srcIA.dependences.com.grooptown.snorkunking.service.engine.player.Position getNextPosition(srcIA.dependences.com.grooptown.snorkunking.service.engine.player.Position previousPosition, srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum direction) {
        int currentIndex = Arrays.asList(directions).indexOf(direction);
        return new Position(
                previousPosition.getLine() + directionsY[currentIndex],
                previousPosition.getColumn() + directionsX[currentIndex]
        );
    }

    public static srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum getNextPosition(srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum direction, boolean clockWise) {
        int currentIndex = Arrays.asList(directions).indexOf(direction);
        int nextPosition = currentIndex + (clockWise ? 1 : -1);
        nextPosition = (nextPosition + 4) % 4;
        return directions[nextPosition];
    }

    static srcIA.dependences.com.grooptown.snorkunking.service.engine.player.DirectionEnum getOppositeDirection(DirectionEnum direction) {
        int currentIndex = Arrays.asList(directions).indexOf(direction);
        int nextPosition = (currentIndex + 2) % 4;
        return directions[nextPosition];
    }
}
