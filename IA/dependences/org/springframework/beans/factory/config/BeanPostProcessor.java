// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

public interface BeanPostProcessor
{
    Object postProcessBeforeInitialization(final Object p0, final String p1) throws BeansException;
    
    Object postProcessAfterInitialization(final Object p0, final String p1) throws BeansException;
}
