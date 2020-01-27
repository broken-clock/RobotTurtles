// 
// Decompiled by Procyon v0.5.36
// 

package com.grooptown.snorkunking.service.engine.player;

import java.util.Objects;

public class Position
{
    private int line;
    private int column;
    
    public Position() {
    }
    
    public Position(final int line, final int column) {
        this.column = column;
        this.line = line;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public void setColumn(final int column) {
        this.column = column;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public int getLine() {
        return this.line;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Position position = (Position)o;
        return this.line == position.line && this.column == position.column;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.line, this.column);
    }
    
    @Override
    public String toString() {
        return "Position{" + this.line + ", " + this.column + '}';
    }
}
