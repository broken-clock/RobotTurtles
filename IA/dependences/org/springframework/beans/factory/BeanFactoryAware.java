// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

public interface BeanFactoryAware extends Aware
{
    void setBeanFactory(final BeanFactory p0) throws BeansException;
}
