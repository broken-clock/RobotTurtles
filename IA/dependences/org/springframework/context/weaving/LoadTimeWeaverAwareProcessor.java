// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.weaving;

import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware
{
    private LoadTimeWeaver loadTimeWeaver;
    private BeanFactory beanFactory;
    
    public LoadTimeWeaverAwareProcessor() {
    }
    
    public LoadTimeWeaverAwareProcessor(final LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }
    
    public LoadTimeWeaverAwareProcessor(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof LoadTimeWeaverAware) {
            LoadTimeWeaver ltw = this.loadTimeWeaver;
            if (ltw == null) {
                Assert.state(this.beanFactory != null, "BeanFactory required if no LoadTimeWeaver explicitly specified");
                ltw = this.beanFactory.getBean("loadTimeWeaver", LoadTimeWeaver.class);
            }
            ((LoadTimeWeaverAware)bean).setLoadTimeWeaver(ltw);
        }
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String name) {
        return bean;
    }
}
