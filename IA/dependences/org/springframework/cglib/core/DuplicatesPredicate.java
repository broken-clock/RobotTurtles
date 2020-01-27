// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class DuplicatesPredicate implements Predicate
{
    private Set unique;
    
    public DuplicatesPredicate() {
        this.unique = new HashSet();
    }
    
    public boolean evaluate(final Object arg) {
        return this.unique.add(MethodWrapper.create((Method)arg));
    }
}
