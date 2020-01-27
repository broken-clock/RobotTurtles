// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.io.Serializable;

public final class CompactStringObjectMap implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final CompactStringObjectMap EMPTY;
    private final int _hashMask;
    private final int _spillCount;
    private final Object[] _hashArea;
    
    private CompactStringObjectMap(final int hashMask, final int spillCount, final Object[] hashArea) {
        this._hashMask = hashMask;
        this._spillCount = spillCount;
        this._hashArea = hashArea;
    }
    
    public static <T> CompactStringObjectMap construct(final Map<String, T> all) {
        if (all.isEmpty()) {
            return CompactStringObjectMap.EMPTY;
        }
        final int size = findSize(all.size());
        final int mask = size - 1;
        final int alloc = (size + (size >> 1)) * 2;
        Object[] hashArea = new Object[alloc];
        int spillCount = 0;
        for (final Map.Entry<String, T> entry : all.entrySet()) {
            final String key = entry.getKey();
            final int slot = key.hashCode() & mask;
            int ix = slot + slot;
            if (hashArea[ix] != null) {
                ix = size + (slot >> 1) << 1;
                if (hashArea[ix] != null) {
                    ix = (size + (size >> 1) << 1) + spillCount;
                    spillCount += 2;
                    if (ix >= hashArea.length) {
                        hashArea = Arrays.copyOf(hashArea, hashArea.length + 4);
                    }
                }
            }
            hashArea[ix] = key;
            hashArea[ix + 1] = entry.getValue();
        }
        return new CompactStringObjectMap(mask, spillCount, hashArea);
    }
    
    private static final int findSize(final int size) {
        if (size <= 5) {
            return 8;
        }
        if (size <= 12) {
            return 16;
        }
        int needed;
        int result;
        for (needed = size + (size >> 2), result = 32; result < needed; result += result) {}
        return result;
    }
    
    public Object find(final String key) {
        final int slot = key.hashCode() & this._hashMask;
        final int ix = slot << 1;
        final Object match = this._hashArea[ix];
        if (match == key || key.equals(match)) {
            return this._hashArea[ix + 1];
        }
        return this._find2(key, slot, match);
    }
    
    private final Object _find2(final String key, final int slot, Object match) {
        if (match == null) {
            return null;
        }
        final int hashSize = this._hashMask + 1;
        final int ix = hashSize + (slot >> 1) << 1;
        match = this._hashArea[ix];
        if (key.equals(match)) {
            return this._hashArea[ix + 1];
        }
        if (match != null) {
            for (int i = hashSize + (hashSize >> 1) << 1, end = i + this._spillCount; i < end; i += 2) {
                match = this._hashArea[i];
                if (match == key || key.equals(match)) {
                    return this._hashArea[i + 1];
                }
            }
        }
        return null;
    }
    
    public Object findCaseInsensitive(final String key) {
        for (int i = 0, end = this._hashArea.length; i < end; i += 2) {
            final Object k2 = this._hashArea[i];
            if (k2 != null) {
                final String s = (String)k2;
                if (s.equalsIgnoreCase(key)) {
                    return this._hashArea[i + 1];
                }
            }
        }
        return null;
    }
    
    public List<String> keys() {
        final int end = this._hashArea.length;
        final List<String> keys = new ArrayList<String>(end >> 2);
        for (int i = 0; i < end; i += 2) {
            final Object key = this._hashArea[i];
            if (key != null) {
                keys.add((String)key);
            }
        }
        return keys;
    }
    
    static {
        EMPTY = new CompactStringObjectMap(1, 0, new Object[4]);
    }
}
