// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext
{
    private Resource[] configResources;
    
    public ClassPathXmlApplicationContext() {
    }
    
    public ClassPathXmlApplicationContext(final ApplicationContext parent) {
        super(parent);
    }
    
    public ClassPathXmlApplicationContext(final String configLocation) throws BeansException {
        this(new String[] { configLocation }, true, null);
    }
    
    public ClassPathXmlApplicationContext(final String... configLocations) throws BeansException {
        this(configLocations, true, null);
    }
    
    public ClassPathXmlApplicationContext(final String[] configLocations, final ApplicationContext parent) throws BeansException {
        this(configLocations, true, parent);
    }
    
    public ClassPathXmlApplicationContext(final String[] configLocations, final boolean refresh) throws BeansException {
        this(configLocations, refresh, null);
    }
    
    public ClassPathXmlApplicationContext(final String[] configLocations, final boolean refresh, final ApplicationContext parent) throws BeansException {
        super(parent);
        this.setConfigLocations(configLocations);
        if (refresh) {
            this.refresh();
        }
    }
    
    public ClassPathXmlApplicationContext(final String path, final Class<?> clazz) throws BeansException {
        this(new String[] { path }, clazz);
    }
    
    public ClassPathXmlApplicationContext(final String[] paths, final Class<?> clazz) throws BeansException {
        this(paths, clazz, null);
    }
    
    public ClassPathXmlApplicationContext(final String[] paths, final Class<?> clazz, final ApplicationContext parent) throws BeansException {
        super(parent);
        Assert.notNull(paths, "Path array must not be null");
        Assert.notNull(clazz, "Class argument must not be null");
        this.configResources = new Resource[paths.length];
        for (int i = 0; i < paths.length; ++i) {
            this.configResources[i] = new ClassPathResource(paths[i], clazz);
        }
        this.refresh();
    }
    
    @Override
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
}
