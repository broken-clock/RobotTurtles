// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import java.lang.reflect.Member;

public class RejectModifierPredicate implements Predicate
{
    private int rejectMask;
    
    public RejectModifierPredicate(final int rejectMask) {
        this.rejectMask = rejectMask;
    }
    
    public boolean evaluate(final Object arg) {
        return (((Member)arg).getModifiers() & this.rejectMask) == 0x0;
    }
}
