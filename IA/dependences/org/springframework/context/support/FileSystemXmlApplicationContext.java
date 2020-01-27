// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext
{
    public FileSystemXmlApplicationContext() {
    }
    
    public FileSystemXmlApplicationContext(final ApplicationContext parent) {
        super(parent);
    }
    
    public FileSystemXmlApplicationContext(final String configLocation) throws BeansException {
        this(new String[] { configLocation }, true, null);
    }
    
    public FileSystemXmlApplicationContext(final String... configLocations) throws BeansException {
        this(configLocations, true, null);
    }
    
    public FileSystemXmlApplicationContext(final String[] configLocations, final ApplicationContext parent) throws BeansException {
        this(configLocations, true, parent);
    }
    
    public FileSystemXmlApplicationContext(final String[] configLocations, final boolean refresh) throws BeansException {
        this(configLocations, refresh, null);
    }
    
    public FileSystemXmlApplicationContext(final String[] configLocations, final boolean refresh, final ApplicationContext parent) throws BeansException {
        super(parent);
        this.setConfigLocations(configLocations);
        if (refresh) {
            this.refresh();
        }
    }
    
    @Override
    protected Resource getResourceByPath(String path) {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return new FileSystemResource(path);
    }
}
