// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.StandardEnvironment;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.core.io.Resource;
import java.util.Collection;
import java.io.IOException;
import org.springframework.util.Assert;
import org.springframework.core.env.PropertyResolver;
import org.apache.commons.logging.Log;
import java.beans.PropertyEditorSupport;

public class ResourceArrayPropertyEditor extends PropertyEditorSupport
{
    private static final Log logger;
    private final ResourcePatternResolver resourcePatternResolver;
    private PropertyResolver propertyResolver;
    private final boolean ignoreUnresolvablePlaceholders;
    
    public ResourceArrayPropertyEditor() {
        this(new PathMatchingResourcePatternResolver(), null, true);
    }
    
    @Deprecated
    public ResourceArrayPropertyEditor(final ResourcePatternResolver resourcePatternResolver) {
        this(resourcePatternResolver, null, true);
    }
    
    @Deprecated
    public ResourceArrayPropertyEditor(final ResourcePatternResolver resourcePatternResolver, final boolean ignoreUnresolvablePlaceholders) {
        this(resourcePatternResolver, null, ignoreUnresolvablePlaceholders);
    }
    
    public ResourceArrayPropertyEditor(final ResourcePatternResolver resourcePatternResolver, final PropertyResolver propertyResolver) {
        this(resourcePatternResolver, propertyResolver, true);
    }
    
    public ResourceArrayPropertyEditor(final ResourcePatternResolver resourcePatternResolver, final PropertyResolver propertyResolver, final boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(resourcePatternResolver, "ResourcePatternResolver must not be null");
        this.resourcePatternResolver = resourcePatternResolver;
        this.propertyResolver = propertyResolver;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }
    
    @Override
    public void setAsText(final String text) {
        final String pattern = this.resolvePath(text).trim();
        try {
            this.setValue(this.resourcePatternResolver.getResources(pattern));
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not resolve resource location pattern [" + pattern + "]: " + ex.getMessage());
        }
    }
    
    @Override
    public void setValue(final Object value) throws IllegalArgumentException {
        if (value instanceof Collection || (value instanceof Object[] && !(value instanceof Resource[]))) {
            final Collection<?> input = (Collection<?>)((value instanceof Collection) ? ((Collection)value) : Arrays.asList((Object[])value));
            final List<Resource> merged = new ArrayList<Resource>();
            for (final Object element : input) {
                if (element instanceof String) {
                    final String pattern = this.resolvePath((String)element).trim();
                    try {
                        final Resource[] resources2;
                        final Resource[] resources = resources2 = this.resourcePatternResolver.getResources(pattern);
                        for (final Resource resource : resources2) {
                            if (!merged.contains(resource)) {
                                merged.add(resource);
                            }
                        }
                    }
                    catch (IOException ex) {
                        if (!ResourceArrayPropertyEditor.logger.isDebugEnabled()) {
                            continue;
                        }
                        ResourceArrayPropertyEditor.logger.debug("Could not retrieve resources for pattern '" + pattern + "'", ex);
                    }
                }
                else {
                    if (!(element instanceof Resource)) {
                        throw new IllegalArgumentException("Cannot convert element [" + element + "] to [" + Resource.class.getName() + "]: only location String and Resource object supported");
                    }
                    final Resource resource2 = (Resource)element;
                    if (merged.contains(resource2)) {
                        continue;
                    }
                    merged.add(resource2);
                }
            }
            super.setValue(merged.toArray(new Resource[merged.size()]));
        }
        else {
            super.setValue(value);
        }
    }
    
    protected String resolvePath(final String path) {
        if (this.propertyResolver == null) {
            this.propertyResolver = new StandardEnvironment();
        }
        return this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) : this.propertyResolver.resolveRequiredPlaceholders(path);
    }
    
    static {
        logger = LogFactory.getLog(ResourceArrayPropertyEditor.class);
    }
}
