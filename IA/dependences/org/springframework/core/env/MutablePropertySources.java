// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.util.Iterator;
import org.apache.commons.logging.LogFactory;
import java.util.LinkedList;
import org.apache.commons.logging.Log;

public class MutablePropertySources implements PropertySources
{
    static final String NON_EXISTENT_PROPERTY_SOURCE_MESSAGE = "PropertySource named [%s] does not exist";
    static final String ILLEGAL_RELATIVE_ADDITION_MESSAGE = "PropertySource named [%s] cannot be added relative to itself";
    private final Log logger;
    private final LinkedList<PropertySource<?>> propertySourceList;
    
    public MutablePropertySources() {
        this.propertySourceList = new LinkedList<PropertySource<?>>();
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public MutablePropertySources(final PropertySources propertySources) {
        this();
        for (final PropertySource<?> propertySource : propertySources) {
            this.addLast(propertySource);
        }
    }
    
    MutablePropertySources(final Log logger) {
        this.propertySourceList = new LinkedList<PropertySource<?>>();
        this.logger = logger;
    }
    
    @Override
    public boolean contains(final String name) {
        return this.propertySourceList.contains(PropertySource.named(name));
    }
    
    @Override
    public PropertySource<?> get(final String name) {
        final int index = this.propertySourceList.indexOf(PropertySource.named(name));
        return (index == -1) ? null : this.propertySourceList.get(index);
    }
    
    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }
    
    public void addFirst(final PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Adding [%s] PropertySource with highest search precedence", propertySource.getName()));
        }
        this.removeIfPresent(propertySource);
        this.propertySourceList.addFirst(propertySource);
    }
    
    public void addLast(final PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Adding [%s] PropertySource with lowest search precedence", propertySource.getName()));
        }
        this.removeIfPresent(propertySource);
        this.propertySourceList.addLast(propertySource);
    }
    
    public void addBefore(final String relativePropertySourceName, final PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Adding [%s] PropertySource with search precedence immediately higher than [%s]", propertySource.getName(), relativePropertySourceName));
        }
        this.assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        this.removeIfPresent(propertySource);
        final int index = this.assertPresentAndGetIndex(relativePropertySourceName);
        this.addAtIndex(index, propertySource);
    }
    
    public void addAfter(final String relativePropertySourceName, final PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Adding [%s] PropertySource with search precedence immediately lower than [%s]", propertySource.getName(), relativePropertySourceName));
        }
        this.assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        this.removeIfPresent(propertySource);
        final int index = this.assertPresentAndGetIndex(relativePropertySourceName);
        this.addAtIndex(index + 1, propertySource);
    }
    
    public int precedenceOf(final PropertySource<?> propertySource) {
        return this.propertySourceList.indexOf(propertySource);
    }
    
    public PropertySource<?> remove(final String name) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Removing [%s] PropertySource", name));
        }
        final int index = this.propertySourceList.indexOf(PropertySource.named(name));
        return (index == -1) ? null : this.propertySourceList.remove(index);
    }
    
    public void replace(final String name, final PropertySource<?> propertySource) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format("Replacing [%s] PropertySource with [%s]", name, propertySource.getName()));
        }
        final int index = this.assertPresentAndGetIndex(name);
        this.propertySourceList.set(index, propertySource);
    }
    
    public int size() {
        return this.propertySourceList.size();
    }
    
    @Override
    public synchronized String toString() {
        final String[] names = new String[this.size()];
        for (int i = 0; i < this.size(); ++i) {
            names[i] = this.propertySourceList.get(i).getName();
        }
        return String.format("[%s]", StringUtils.arrayToCommaDelimitedString(names));
    }
    
    protected void assertLegalRelativeAddition(final String relativePropertySourceName, final PropertySource<?> propertySource) {
        final String newPropertySourceName = propertySource.getName();
        Assert.isTrue(!relativePropertySourceName.equals(newPropertySourceName), String.format("PropertySource named [%s] cannot be added relative to itself", newPropertySourceName));
    }
    
    protected void removeIfPresent(final PropertySource<?> propertySource) {
        if (this.propertySourceList.contains(propertySource)) {
            this.propertySourceList.remove(propertySource);
        }
    }
    
    private void addAtIndex(final int index, final PropertySource<?> propertySource) {
        this.removeIfPresent(propertySource);
        this.propertySourceList.add(index, propertySource);
    }
    
    private int assertPresentAndGetIndex(final String name) {
        final int index = this.propertySourceList.indexOf(PropertySource.named(name));
        Assert.isTrue(index >= 0, String.format("PropertySource named [%s] does not exist", name));
        return index;
    }
}
