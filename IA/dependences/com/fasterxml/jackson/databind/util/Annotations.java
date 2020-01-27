// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.lang.annotation.Annotation;

public interface Annotations
{
     <A extends Annotation> A get(final Class<A> p0);
    
    boolean has(final Class<?> p0);
    
    boolean hasOneOf(final Class<? extends Annotation>[] p0);
    
    int size();
}
