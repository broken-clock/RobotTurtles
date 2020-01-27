// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.objenesis;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

public interface Objenesis
{
     <T> T newInstance(final Class<T> p0);
    
     <T> ObjectInstantiator<T> getInstantiatorOf(final Class<T> p0);
}
