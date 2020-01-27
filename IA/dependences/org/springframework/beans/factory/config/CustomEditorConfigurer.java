// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import java.util.Iterator;
import org.apache.commons.logging.LogFactory;
import java.beans.PropertyEditor;
import java.util.Map;
import org.springframework.beans.PropertyEditorRegistrar;
import org.apache.commons.logging.Log;
import org.springframework.core.Ordered;

public class CustomEditorConfigurer implements BeanFactoryPostProcessor, Ordered
{
    protected final Log logger;
    private int order;
    private PropertyEditorRegistrar[] propertyEditorRegistrars;
    private Map<Class<?>, Class<? extends PropertyEditor>> customEditors;
    
    public CustomEditorConfigurer() {
        this.logger = LogFactory.getLog(this.getClass());
        this.order = Integer.MAX_VALUE;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    public void setPropertyEditorRegistrars(final PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = propertyEditorRegistrars;
    }
    
    public void setCustomEditors(final Map<Class<?>, Class<? extends PropertyEditor>> customEditors) {
        this.customEditors = customEditors;
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertyEditorRegistrars != null) {
            for (final PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
                beanFactory.addPropertyEditorRegistrar(propertyEditorRegistrar);
            }
        }
        if (this.customEditors != null) {
            for (final Map.Entry<Class<?>, Class<? extends PropertyEditor>> entry : this.customEditors.entrySet()) {
                final Class<?> requiredType = entry.getKey();
                final Class<? extends PropertyEditor> propertyEditorClass = entry.getValue();
                beanFactory.registerCustomEditor(requiredType, propertyEditorClass);
            }
        }
    }
}
