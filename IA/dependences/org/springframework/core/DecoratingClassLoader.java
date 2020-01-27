// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.Iterator;
import org.springframework.util.Assert;
import java.util.HashSet;
import java.util.Set;

public abstract class DecoratingClassLoader extends ClassLoader
{
    private final Set<String> excludedPackages;
    private final Set<String> excludedClasses;
    private final Object exclusionMonitor;
    
    public DecoratingClassLoader() {
        this.excludedPackages = new HashSet<String>();
        this.excludedClasses = new HashSet<String>();
        this.exclusionMonitor = new Object();
    }
    
    public DecoratingClassLoader(final ClassLoader parent) {
        super(parent);
        this.excludedPackages = new HashSet<String>();
        this.excludedClasses = new HashSet<String>();
        this.exclusionMonitor = new Object();
    }
    
    public void excludePackage(final String packageName) {
        Assert.notNull(packageName, "Package name must not be null");
        synchronized (this.exclusionMonitor) {
            this.excludedPackages.add(packageName);
        }
    }
    
    public void excludeClass(final String className) {
        Assert.notNull(className, "Class name must not be null");
        synchronized (this.exclusionMonitor) {
            this.excludedClasses.add(className);
        }
    }
    
    protected boolean isExcluded(final String className) {
        synchronized (this.exclusionMonitor) {
            if (this.excludedClasses.contains(className)) {
                return true;
            }
            for (final String packageName : this.excludedPackages) {
                if (className.startsWith(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
