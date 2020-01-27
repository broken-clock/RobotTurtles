// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext implements BeanNameAware, InitializingBean
{
    private String[] configLocations;
    private boolean setIdCalled;
    
    public AbstractRefreshableConfigApplicationContext() {
        this.setIdCalled = false;
    }
    
    public AbstractRefreshableConfigApplicationContext(final ApplicationContext parent) {
        super(parent);
        this.setIdCalled = false;
    }
    
    public void setConfigLocation(final String location) {
        this.setConfigLocations(StringUtils.tokenizeToStringArray(location, ",; \t\n"));
    }
    
    public void setConfigLocations(final String[] locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; ++i) {
                this.configLocations[i] = this.resolvePath(locations[i]).trim();
            }
        }
        else {
            this.configLocations = null;
        }
    }
    
    protected String[] getConfigLocations() {
        return (this.configLocations != null) ? this.configLocations : this.getDefaultConfigLocations();
    }
    
    protected String[] getDefaultConfigLocations() {
        return null;
    }
    
    protected String resolvePath(final String path) {
        return this.getEnvironment().resolveRequiredPlaceholders(path);
    }
    
    @Override
    public void setId(final String id) {
        super.setId(id);
        this.setIdCalled = true;
    }
    
    @Override
    public void setBeanName(final String name) {
        if (!this.setIdCalled) {
            super.setId(name);
            this.setDisplayName("ApplicationContext '" + name + "'");
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        if (!this.isActive()) {
            this.refresh();
        }
    }
}
