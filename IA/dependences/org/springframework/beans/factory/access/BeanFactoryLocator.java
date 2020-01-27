// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.access;

import org.springframework.beans.BeansException;

public interface BeanFactoryLocator
{
    BeanFactoryReference useBeanFactory(final String p0) throws BeansException;
}
