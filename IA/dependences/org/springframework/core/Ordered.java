// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public interface Ordered
{
    public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
    public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE;
    
    int getOrder();
}
