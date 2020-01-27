// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.wiring;

import org.springframework.util.Assert;

public class BeanWiringInfo
{
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    private String beanName;
    private boolean isDefaultBeanName;
    private int autowireMode;
    private boolean dependencyCheck;
    
    public BeanWiringInfo() {
        this.beanName = null;
        this.isDefaultBeanName = false;
        this.autowireMode = 0;
        this.dependencyCheck = false;
    }
    
    public BeanWiringInfo(final String beanName) {
        this(beanName, false);
    }
    
    public BeanWiringInfo(final String beanName, final boolean isDefaultBeanName) {
        this.beanName = null;
        this.isDefaultBeanName = false;
        this.autowireMode = 0;
        this.dependencyCheck = false;
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
        this.isDefaultBeanName = isDefaultBeanName;
    }
    
    public BeanWiringInfo(final int autowireMode, final boolean dependencyCheck) {
        this.beanName = null;
        this.isDefaultBeanName = false;
        this.autowireMode = 0;
        this.dependencyCheck = false;
        if (autowireMode != 1 && autowireMode != 2) {
            throw new IllegalArgumentException("Only constants AUTOWIRE_BY_NAME and AUTOWIRE_BY_TYPE supported");
        }
        this.autowireMode = autowireMode;
        this.dependencyCheck = dependencyCheck;
    }
    
    public boolean indicatesAutowiring() {
        return this.beanName == null;
    }
    
    public String getBeanName() {
        return this.beanName;
    }
    
    public boolean isDefaultBeanName() {
        return this.isDefaultBeanName;
    }
    
    public int getAutowireMode() {
        return this.autowireMode;
    }
    
    public boolean getDependencyCheck() {
        return this.dependencyCheck;
    }
}
