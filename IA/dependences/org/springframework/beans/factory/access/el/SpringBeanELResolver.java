// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.access.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.PropertyNotWritableException;
import javax.el.ELException;
import org.springframework.beans.factory.BeanFactory;
import javax.el.ELContext;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import javax.el.ELResolver;

public abstract class SpringBeanELResolver extends ELResolver
{
    protected final Log logger;
    
    public SpringBeanELResolver() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public Object getValue(final ELContext elContext, final Object base, final Object property) throws ELException {
        if (base == null) {
            final String beanName = property.toString();
            final BeanFactory bf = this.getBeanFactory(elContext);
            if (bf.containsBean(beanName)) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Successfully resolved variable '" + beanName + "' in Spring BeanFactory");
                }
                elContext.setPropertyResolved(true);
                return bf.getBean(beanName);
            }
        }
        return null;
    }
    
    public Class<?> getType(final ELContext elContext, final Object base, final Object property) throws ELException {
        if (base == null) {
            final String beanName = property.toString();
            final BeanFactory bf = this.getBeanFactory(elContext);
            if (bf.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return bf.getType(beanName);
            }
        }
        return null;
    }
    
    public void setValue(final ELContext elContext, final Object base, final Object property, final Object value) throws ELException {
        if (base == null) {
            final String beanName = property.toString();
            final BeanFactory bf = this.getBeanFactory(elContext);
            if (bf.containsBean(beanName)) {
                throw new PropertyNotWritableException("Variable '" + beanName + "' refers to a Spring bean which by definition is not writable");
            }
        }
    }
    
    public boolean isReadOnly(final ELContext elContext, final Object base, final Object property) throws ELException {
        if (base == null) {
            final String beanName = property.toString();
            final BeanFactory bf = this.getBeanFactory(elContext);
            if (bf.containsBean(beanName)) {
                return true;
            }
        }
        return false;
    }
    
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext elContext, final Object base) {
        return null;
    }
    
    public Class<?> getCommonPropertyType(final ELContext elContext, final Object base) {
        return Object.class;
    }
    
    protected abstract BeanFactory getBeanFactory(final ELContext p0);
}
