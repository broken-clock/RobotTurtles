// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.springframework.util.Assert;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.io.AbstractResource;

class BeanDefinitionResource extends AbstractResource
{
    private final BeanDefinition beanDefinition;
    
    public BeanDefinitionResource(final BeanDefinition beanDefinition) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        this.beanDefinition = beanDefinition;
    }
    
    public final BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }
    
    @Override
    public boolean exists() {
        return false;
    }
    
    @Override
    public boolean isReadable() {
        return false;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException("Resource cannot be opened because it points to " + this.getDescription());
    }
    
    @Override
    public String getDescription() {
        return "BeanDefinition defined in " + this.beanDefinition.getResourceDescription();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof BeanDefinitionResource && ((BeanDefinitionResource)obj).beanDefinition.equals(this.beanDefinition));
    }
    
    @Override
    public int hashCode() {
        return this.beanDefinition.hashCode();
    }
}
