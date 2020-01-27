// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

public class AdviceEntry implements ParseState.Entry
{
    private final String kind;
    
    public AdviceEntry(final String kind) {
        this.kind = kind;
    }
    
    @Override
    public String toString() {
        return "Advice (" + this.kind + ")";
    }
}
