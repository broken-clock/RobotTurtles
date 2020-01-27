// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework.autoproxy.target;

import org.springframework.aop.target.PrototypeTargetSource;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.aop.target.CommonsPoolTargetSource;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;

public class QuickTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator
{
    public static final String PREFIX_COMMONS_POOL = ":";
    public static final String PREFIX_THREAD_LOCAL = "%";
    public static final String PREFIX_PROTOTYPE = "!";
    
    @Override
    protected final AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(final Class<?> beanClass, final String beanName) {
        if (beanName.startsWith(":")) {
            final CommonsPoolTargetSource cpts = new CommonsPoolTargetSource();
            cpts.setMaxSize(25);
            return cpts;
        }
        if (beanName.startsWith("%")) {
            return new ThreadLocalTargetSource();
        }
        if (beanName.startsWith("!")) {
            return new PrototypeTargetSource();
        }
        return null;
    }
}
