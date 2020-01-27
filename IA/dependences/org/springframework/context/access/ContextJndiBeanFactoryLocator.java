// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.access;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import javax.naming.NamingException;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.jndi.JndiLocatorSupport;

public class ContextJndiBeanFactoryLocator extends JndiLocatorSupport implements BeanFactoryLocator
{
    public static final String BEAN_FACTORY_PATH_DELIMITERS = ",; \t\n";
    
    @Override
    public BeanFactoryReference useBeanFactory(final String factoryKey) throws BeansException {
        try {
            final String beanFactoryPath = this.lookup(factoryKey, String.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Bean factory path from JNDI environment variable [" + factoryKey + "] is: " + beanFactoryPath);
            }
            final String[] paths = StringUtils.tokenizeToStringArray(beanFactoryPath, ",; \t\n");
            return this.createBeanFactory(paths);
        }
        catch (NamingException ex) {
            throw new BootstrapException("Define an environment variable [" + factoryKey + "] containing " + "the class path locations of XML bean definition files", ex);
        }
    }
    
    protected BeanFactoryReference createBeanFactory(final String[] resources) throws BeansException {
        final ApplicationContext ctx = this.createApplicationContext(resources);
        return new ContextBeanFactoryReference(ctx);
    }
    
    protected ApplicationContext createApplicationContext(final String[] resources) throws BeansException {
        return new ClassPathXmlApplicationContext(resources);
    }
}
