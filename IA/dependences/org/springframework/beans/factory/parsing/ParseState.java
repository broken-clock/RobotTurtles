// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.parsing;

import java.util.Stack;

public final class ParseState
{
    private static final char TAB = '\t';
    private final Stack<Entry> state;
    
    public ParseState() {
        this.state = new Stack<Entry>();
    }
    
    private ParseState(final ParseState other) {
        this.state = (Stack<Entry>)other.state.clone();
    }
    
    public void push(final Entry entry) {
        this.state.push(entry);
    }
    
    public void pop() {
        this.state.pop();
    }
    
    public Entry peek() {
        return this.state.empty() ? null : this.state.peek();
    }
    
    public ParseState snapshot() {
        return new ParseState(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int x = 0; x < this.state.size(); ++x) {
            if (x > 0) {
                sb.append('\n');
                for (int y = 0; y < x; ++y) {
                    sb.append('\t');
                }
                sb.append("-> ");
            }
            sb.append(this.state.get(x));
        }
        return sb.toString();
    }
    
    public interface Entry
    {
    }
}
