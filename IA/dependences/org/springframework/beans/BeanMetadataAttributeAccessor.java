// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import org.springframework.core.AttributeAccessorSupport;

public class BeanMetadataAttributeAccessor extends AttributeAccessorSupport implements BeanMetadataElement
{
    private Object source;
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public Object getSource() {
        return this.source;
    }
    
    public void addMetadataAttribute(final BeanMetadataAttribute attribute) {
        super.setAttribute(attribute.getName(), attribute);
    }
    
    public BeanMetadataAttribute getMetadataAttribute(final String name) {
        return (BeanMetadataAttribute)super.getAttribute(name);
    }
    
    @Override
    public void setAttribute(final String name, final Object value) {
        super.setAttribute(name, new BeanMetadataAttribute(name, value));
    }
    
    @Override
    public Object getAttribute(final String name) {
        final BeanMetadataAttribute attribute = (BeanMetadataAttribute)super.getAttribute(name);
        return (attribute != null) ? attribute.getValue() : null;
    }
    
    @Override
    public Object removeAttribute(final String name) {
        final BeanMetadataAttribute attribute = (BeanMetadataAttribute)super.removeAttribute(name);
        return (attribute != null) ? attribute.getValue() : null;
    }
}
