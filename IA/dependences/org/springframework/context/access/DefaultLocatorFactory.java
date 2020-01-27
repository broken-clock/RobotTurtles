// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.access;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.access.BeanFactoryLocator;

public class DefaultLocatorFactory
{
    public static BeanFactoryLocator getInstance() throws FatalBeanException {
        return ContextSingletonBeanFactoryLocator.getInstance();
    }
    
    public static BeanFactoryLocator getInstance(final String selector) throws FatalBeanException {
        return ContextSingletonBeanFactoryLocator.getInstance(selector);
    }
}
