// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis.strategy;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

public interface InstantiatorStrategy
{
     <T> ObjectInstantiator<T> newInstantiatorOf(final Class<T> p0);
}
