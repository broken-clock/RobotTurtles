// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.xml.sax.EntityResolver;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class XmlWebApplicationContext extends AbstractRefreshableWebApplicationContext
{
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";
    
    @Override
    protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        final XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
        this.initBeanDefinitionReader(beanDefinitionReader);
        this.loadBeanDefinitions(beanDefinitionReader);
    }
    
    protected void initBeanDefinitionReader(final XmlBeanDefinitionReader beanDefinitionReader) {
    }
    
    protected void loadBeanDefinitions(final XmlBeanDefinitionReader reader) throws IOException {
        final String[] configLocations = this.getConfigLocations();
        if (configLocations != null) {
            for (final String configLocation : configLocations) {
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }
    
    @Override
    protected String[] getDefaultConfigLocations() {
        if (this.getNamespace() != null) {
            return new String[] { "/WEB-INF/" + this.getNamespace() + ".xml" };
        }
        return new String[] { "/WEB-INF/applicationContext.xml" };
    }
}
