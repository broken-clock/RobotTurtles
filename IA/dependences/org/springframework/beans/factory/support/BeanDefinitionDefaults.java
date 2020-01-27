// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.StringUtils;

public class BeanDefinitionDefaults
{
    private boolean lazyInit;
    private int dependencyCheck;
    private int autowireMode;
    private String initMethodName;
    private String destroyMethodName;
    
    public BeanDefinitionDefaults() {
        this.dependencyCheck = 0;
        this.autowireMode = 0;
    }
    
    public void setLazyInit(final boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    public boolean isLazyInit() {
        return this.lazyInit;
    }
    
    public void setDependencyCheck(final int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }
    
    public int getDependencyCheck() {
        return this.dependencyCheck;
    }
    
    public void setAutowireMode(final int autowireMode) {
        this.autowireMode = autowireMode;
    }
    
    public int getAutowireMode() {
        return this.autowireMode;
    }
    
    public void setInitMethodName(final String initMethodName) {
        this.initMethodName = (StringUtils.hasText(initMethodName) ? initMethodName : null);
    }
    
    public String getInitMethodName() {
        return this.initMethodName;
    }
    
    public void setDestroyMethodName(final String destroyMethodName) {
        this.destroyMethodName = (StringUtils.hasText(destroyMethodName) ? destroyMethodName : null);
    }
    
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }
}
