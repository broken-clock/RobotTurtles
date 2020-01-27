// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.beans;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.AbstractSet;

public class FixedKeySet extends AbstractSet
{
    private Set set;
    private int size;
    
    public FixedKeySet(final String[] keys) {
        this.size = keys.length;
        this.set = Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList(keys)));
    }
    
    public Iterator iterator() {
        return this.set.iterator();
    }
    
    public int size() {
        return this.size;
    }
}
