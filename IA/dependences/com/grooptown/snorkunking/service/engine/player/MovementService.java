// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.player;

import java.util.Arrays;

public class MovementService
{
    public static final DirectionEnum[] directions;
    private static final int[] directionsX;
    private static final int[] directionsY;
    
    public static Position getNextPosition(final Position previousPosition, final DirectionEnum direction) {
        final int currentIndex = Arrays.asList(MovementService.directions).indexOf(direction);
        return new Position(previousPosition.getLine() + MovementService.directionsY[currentIndex], previousPosition.getColumn() + MovementService.directionsX[currentIndex]);
    }
    
    public static DirectionEnum getNextPosition(final DirectionEnum direction, final boolean clockWise) {
        final int currentIndex = Arrays.asList(MovementService.directions).indexOf(direction);
        int nextPosition = currentIndex + (clockWise ? 1 : -1);
        nextPosition = (nextPosition + 4) % 4;
        return MovementService.directions[nextPosition];
    }
    
    static DirectionEnum getOppositeDirection(final DirectionEnum direction) {
        final int currentIndex = Arrays.asList(MovementService.directions).indexOf(direction);
        final int nextPosition = (currentIndex + 2) % 4;
        return MovementService.directions[nextPosition];
    }
    
    static {
        directions = new DirectionEnum[] { DirectionEnum.WEST, DirectionEnum.NORTH, DirectionEnum.EAST, DirectionEnum.SOUTH };
        directionsX = new int[] { -1, 0, 1, 0 };
        directionsY = new int[] { 0, -1, 0, 1 };
    }
}
