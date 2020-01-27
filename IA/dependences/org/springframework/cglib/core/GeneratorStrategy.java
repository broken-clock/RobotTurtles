// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

public interface GeneratorStrategy
{
    byte[] generate(final ClassGenerator p0) throws Exception;
    
    boolean equals(final Object p0);
}
