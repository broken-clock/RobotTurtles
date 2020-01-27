// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

public final class LinkedNode<T>
{
    private final T value;
    private LinkedNode<T> next;
    
    public LinkedNode(final T value, final LinkedNode<T> next) {
        this.value = value;
        this.next = next;
    }
    
    public void linkNext(final LinkedNode<T> n) {
        if (this.next != null) {
            throw new IllegalStateException();
        }
        this.next = n;
    }
    
    public LinkedNode<T> next() {
        return this.next;
    }
    
    public T value() {
        return this.value;
    }
    
    public static <ST> boolean contains(LinkedNode<ST> node, final ST value) {
        while (node != null) {
            if (node.value() == value) {
                return true;
            }
            node = node.next();
        }
        return false;
    }
}
