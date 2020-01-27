// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class CompositeIterator<E> implements Iterator<E>
{
    private List<Iterator<E>> iterators;
    private boolean inUse;
    
    public CompositeIterator() {
        this.iterators = new LinkedList<Iterator<E>>();
        this.inUse = false;
    }
    
    public void add(final Iterator<E> iterator) {
        Assert.state(!this.inUse, "You can no longer add iterator to a composite iterator that's already in use");
        if (this.iterators.contains(iterator)) {
            throw new IllegalArgumentException("You cannot add the same iterator twice");
        }
        this.iterators.add(iterator);
    }
    
    @Override
    public boolean hasNext() {
        this.inUse = true;
        final Iterator<Iterator<E>> it = this.iterators.iterator();
        while (it.hasNext()) {
            if (it.next().hasNext()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public E next() {
        this.inUse = true;
        for (final Iterator<E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        throw new NoSuchElementException("Exhausted all iterators");
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported");
    }
}
