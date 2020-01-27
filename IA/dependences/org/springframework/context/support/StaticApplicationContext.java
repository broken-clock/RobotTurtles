// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.util.Locale;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class StaticApplicationContext extends GenericApplicationContext
{
    private final StaticMessageSource staticMessageSource;
    
    public StaticApplicationContext() throws BeansException {
        this((ApplicationContext)null);
    }
    
    public StaticApplicationContext(final ApplicationContext parent) throws BeansException {
        super(parent);
        this.staticMessageSource = new StaticMessageSource();
        this.getBeanFactory().registerSingleton("messageSource", this.staticMessageSource);
    }
    
    @Override
    protected void assertBeanFactoryActive() {
    }
    
    public final StaticMessageSource getStaticMessageSource() {
        return this.staticMessageSource;
    }
    
    public void registerSingleton(final String name, final Class<?> clazz) throws BeansException {
        final GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }
    
    public void registerSingleton(final String name, final Class<?> clazz, final MutablePropertyValues pvs) throws BeansException {
        final GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        bd.setPropertyValues(pvs);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }
    
    public void registerPrototype(final String name, final Class<?> clazz) throws BeansException {
        final GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setScope("prototype");
        bd.setBeanClass(clazz);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }
    
    public void registerPrototype(final String name, final Class<?> clazz, final MutablePropertyValues pvs) throws BeansException {
        final GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setScope("prototype");
        bd.setBeanClass(clazz);
        bd.setPropertyValues(pvs);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
    }
    
    public void addMessage(final String code, final Locale locale, final String defaultMessage) {
        this.getStaticMessageSource().addMessage(code, locale, defaultMessage);
    }
}
