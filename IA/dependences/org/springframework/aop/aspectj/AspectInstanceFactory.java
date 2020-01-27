// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;

public interface AspectInstanceFactory extends Ordered
{
    Object getAspectInstance();
    
    ClassLoader getAspectClassLoader();
}
