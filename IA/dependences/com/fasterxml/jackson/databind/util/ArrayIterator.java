// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T>, Iterable<T>
{
    private final T[] _a;
    private int _index;
    
    public ArrayIterator(final T[] a) {
        this._a = a;
        this._index = 0;
    }
    
    @Override
    public boolean hasNext() {
        return this._index < this._a.length;
    }
    
    @Override
    public T next() {
        if (this._index >= this._a.length) {
            throw new NoSuchElementException();
        }
        return this._a[this._index++];
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
