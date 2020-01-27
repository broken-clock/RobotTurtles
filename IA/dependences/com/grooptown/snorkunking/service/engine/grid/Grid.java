// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.grid;

import com.grooptown.snorkunking.service.engine.player.Player;
import com.grooptown.snorkunking.service.engine.player.Position;

public class Grid
{
    private Panel[][] grid;
    
    public Grid() {
    }
    
    public Grid(final int size) {
        this.grid = new Panel[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                this.grid[i][j] = new EmptyPanel();
            }
        }
    }
    
    public void setGrid(final Panel[][] grid) {
        this.grid = grid;
    }
    
    public Panel[][] getGrid() {
        return this.grid;
    }
    
    public void displayGrid() {
        System.out.print("   ");
        for (int i = 0; i < this.getGrid()[0].length; ++i) {
            System.out.print(" " + i + "  ");
        }
        System.out.println();
        for (int i = 0; i < this.getGrid().length; ++i) {
            System.out.print(i + " |");
            for (int j = 0; j < this.getGrid()[0].length; ++j) {
                System.out.print(this.getGrid()[i][j].toAscii() + '|');
            }
            System.out.println();
        }
    }
    
    public void placePlayer(final Position position, final Player player) {
        this.grid[position.getLine()][position.getColumn()] = player;
    }
    
    public void makeCellEmpty(final Position position) {
        this.grid[position.getLine()][position.getColumn()] = new EmptyPanel();
    }
    
    public Position getPosition(final Player player) {
        for (int line = 0; line < this.grid.length; ++line) {
            for (int column = 0; column < this.grid[0].length; ++column) {
                if (this.grid[line][column].equals(player)) {
                    return new Position(line, column);
                }
            }
        }
        throw new RuntimeException("Didn't find the Player : " + player);
    }
    
    public boolean isOutOfBound(final Position position) {
        return position.getColumn() < 0 || position.getLine() < 0 || position.getLine() >= this.getGrid().length || position.getColumn() >= this.getGrid()[0].length;
    }
    
    public Panel getPanel(final Position position) {
        return this.grid[position.getLine()][position.getColumn()];
    }
    
    public Grid cloneGrid() {
        final Grid newGrid = new Grid(this.grid.length);
        for (int i = 0; i < this.grid.length; ++i) {
            System.arraycopy(this.grid[i], 0, newGrid.getGrid()[i], 0, this.grid[0].length);
        }
        return newGrid;
    }
}
