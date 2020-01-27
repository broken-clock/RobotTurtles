// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.move;

import com.grooptown.snorkunking.service.engine.player.DirectionEnum;
import com.grooptown.snorkunking.service.engine.grid.Panel;
import com.grooptown.snorkunking.service.engine.player.MovementService;
import com.grooptown.snorkunking.service.engine.grid.RubyPanel;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.tile.WallTile;
import com.grooptown.snorkunking.service.engine.player.Position;
import com.grooptown.snorkunking.service.engine.grid.EmptyPanel;
import com.grooptown.snorkunking.service.engine.tile.TileService;
import java.util.regex.Pattern;
import com.grooptown.snorkunking.service.engine.tile.Tile;

public class BuildWallMove extends Move
{
    private Tile tileToBuild;
    private int line;
    private int column;
    
    @Override
    public boolean isValidMove(final String entry) {
        if (!Pattern.compile("[Ice|Wa{0,1}l{2}] on [0-7]-[0-7]").matcher(entry).find()) {
            return false;
        }
        final String[] entrySplit = entry.split(" ");
        this.tileToBuild = TileService.getTile(entrySplit[0]);
        if (!this.game.findCurrentPlayer().hasTile(this.tileToBuild)) {
            System.out.println("Vous n'avez pas de " + entrySplit[0]);
            return false;
        }
        this.line = Integer.parseInt(entrySplit[2].split("-")[0]);
        this.column = Integer.parseInt(entrySplit[2].split("-")[1]);
        if (!this.game.getGrid().getGrid()[this.line][this.column].getClass().equals(EmptyPanel.class)) {
            System.out.println("Cette case est d\u00e9j\u00e0 occup\u00e9.");
            return false;
        }
        final Position position = new Position(this.line, this.column);
        return !this.tileToBuild.getClass().equals(WallTile.class) || !this.isBlockingRuby(position);
    }
    
    private boolean isBlockingRuby(final Position newWallPosition) {
        boolean isBlocked = false;
        for (final Player player : this.game.getPlayers()) {
            final Position playerPosition = this.game.getGrid().getPosition(player);
            final Set<Position> visited = new HashSet<Position>();
            final boolean hasAccessToRuby = this.hasAccessToRuby(playerPosition, newWallPosition, visited);
            if (!hasAccessToRuby) {
                isBlocked = true;
            }
        }
        return isBlocked;
    }
    
    private boolean hasAccessToRuby(final Position currentPosition, final Position newWallPosition, final Set<Position> visited) {
        if (visited.contains(currentPosition)) {
            return false;
        }
        visited.add(currentPosition);
        if (this.game.getGrid().isOutOfBound(currentPosition)) {
            return false;
        }
        final Class<? extends Panel> currentPanelType = this.game.getGrid().getPanel(currentPosition).getClass();
        if (currentPanelType.equals(RubyPanel.class)) {
            return true;
        }
        if (currentPanelType.equals(WallTile.class) || currentPosition.equals(newWallPosition)) {
            return false;
        }
        for (final DirectionEnum direction : MovementService.directions) {
            final Position nextPosition = MovementService.getNextPosition(currentPosition, direction);
            final boolean hasAccessToRuby = this.hasAccessToRuby(nextPosition, newWallPosition, visited);
            if (hasAccessToRuby) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void constructMoveFromEntry(final String entry) {
    }
    
    @Override
    public void playMove() {
        this.game.addMoveDescription(" - Player added a Wall of type " + this.tileToBuild.toAscii() + " in cell [" + this.line + "," + this.column + "] \n");
        this.game.getGrid().getGrid()[this.line][this.column] = this.tileToBuild;
        this.game.findCurrentPlayer().removeTile(this.tileToBuild);
    }
    
    @Override
    public String entryQuestion() {
        return "Which wall do you want to build and where ? (i.e.: 'Ice on 0-3' for line 0 and column 3, or 'Wall on 4-2')";
    }
    
    public Tile getTileToBuild() {
        return this.tileToBuild;
    }
    
    public void setTileToBuild(final Tile tileToBuild) {
        this.tileToBuild = tileToBuild;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public void setColumn(final int column) {
        this.column = column;
    }
}
