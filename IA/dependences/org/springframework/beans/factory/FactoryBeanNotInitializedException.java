// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class FactoryBeanNotInitializedException extends FatalBeanException
{
    public FactoryBeanNotInitializedException() {
        super("FactoryBean is not fully initialized yet");
    }
    
    public FactoryBeanNotInitializedException(final String msg) {
        super(msg);
    }
}
