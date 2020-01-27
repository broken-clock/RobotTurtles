// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.List;
import java.lang.reflect.Array;

public final class ObjectBuffer
{
    private static final int SMALL_CHUNK = 16384;
    private static final int MAX_CHUNK = 262144;
    private LinkedNode<Object[]> _head;
    private LinkedNode<Object[]> _tail;
    private int _size;
    private Object[] _freeBuffer;
    
    public Object[] resetAndStart() {
        this._reset();
        if (this._freeBuffer == null) {
            return this._freeBuffer = new Object[12];
        }
        return this._freeBuffer;
    }
    
    public Object[] resetAndStart(final Object[] base, final int count) {
        this._reset();
        if (this._freeBuffer == null || this._freeBuffer.length < count) {
            this._freeBuffer = new Object[Math.max(12, count)];
        }
        System.arraycopy(base, 0, this._freeBuffer, 0, count);
        return this._freeBuffer;
    }
    
    public Object[] appendCompletedChunk(final Object[] fullChunk) {
        final LinkedNode<Object[]> next = new LinkedNode<Object[]>(fullChunk, null);
        if (this._head == null) {
            final LinkedNode<Object[]> linkedNode = next;
            this._tail = linkedNode;
            this._head = linkedNode;
        }
        else {
            this._tail.linkNext(next);
            this._tail = next;
        }
        int len = fullChunk.length;
        this._size += len;
        if (len < 16384) {
            len += len;
        }
        else if (len < 262144) {
            len += len >> 2;
        }
        return new Object[len];
    }
    
    public Object[] completeAndClearBuffer(final Object[] lastChunk, final int lastChunkEntries) {
        final int totalSize = lastChunkEntries + this._size;
        final Object[] result = new Object[totalSize];
        this._copyTo(result, totalSize, lastChunk, lastChunkEntries);
        this._reset();
        return result;
    }
    
    public <T> T[] completeAndClearBuffer(final Object[] lastChunk, final int lastChunkEntries, final Class<T> componentType) {
        final int totalSize = lastChunkEntries + this._size;
        final T[] result = (T[])Array.newInstance(componentType, totalSize);
        this._copyTo(result, totalSize, lastChunk, lastChunkEntries);
        this._reset();
        return result;
    }
    
    public void completeAndClearBuffer(final Object[] lastChunk, final int lastChunkEntries, final List<Object> resultList) {
        for (LinkedNode<Object[]> n = this._head; n != null; n = n.next()) {
            final Object[] curr = n.value();
            for (int i = 0, len = curr.length; i < len; ++i) {
                resultList.add(curr[i]);
            }
        }
        for (int j = 0; j < lastChunkEntries; ++j) {
            resultList.add(lastChunk[j]);
        }
        this._reset();
    }
    
    public int initialCapacity() {
        return (this._freeBuffer == null) ? 0 : this._freeBuffer.length;
    }
    
    public int bufferedSize() {
        return this._size;
    }
    
    protected void _reset() {
        if (this._tail != null) {
            this._freeBuffer = this._tail.value();
        }
        final LinkedNode<Object[]> linkedNode = null;
        this._tail = linkedNode;
        this._head = linkedNode;
        this._size = 0;
    }
    
    protected final void _copyTo(final Object resultArray, final int totalSize, final Object[] lastChunk, final int lastChunkEntries) {
        int ptr = 0;
        for (LinkedNode<Object[]> n = this._head; n != null; n = n.next()) {
            final Object[] curr = n.value();
            final int len = curr.length;
            System.arraycopy(curr, 0, resultArray, ptr, len);
            ptr += len;
        }
        System.arraycopy(lastChunk, 0, resultArray, ptr, lastChunkEntries);
        ptr += lastChunkEntries;
        if (ptr != totalSize) {
            throw new IllegalStateException("Should have gotten " + totalSize + " entries, got " + ptr);
        }
    }
}
