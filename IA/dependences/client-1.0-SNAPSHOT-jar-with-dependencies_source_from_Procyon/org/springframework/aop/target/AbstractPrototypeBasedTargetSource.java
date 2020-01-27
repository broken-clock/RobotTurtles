// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.target;

import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;

public abstract class AbstractPrototypeBasedTargetSource extends AbstractBeanFactoryBasedTargetSource
{
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        if (!beanFactory.isPrototype(this.getTargetBeanName())) {
            throw new BeanDefinitionStoreException("Cannot use prototype-based TargetSource against non-prototype bean with name '" + this.getTargetBeanName() + "': instances would not be independent");
        }
    }
    
    protected Object newPrototypeInstance() throws BeansException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating new instance of bean '" + this.getTargetBeanName() + "'");
        }
        return this.getBeanFactory().getBean(this.getTargetBeanName());
    }
    
    protected void destroyPrototypeInstance(final Object target) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Destroying instance of bean '" + this.getTargetBeanName() + "'");
        }
        if (this.getBeanFactory() instanceof ConfigurableBeanFactory) {
            ((ConfigurableBeanFactory)this.getBeanFactory()).destroyBean(this.getTargetBeanName(), target);
        }
        else if (target instanceof DisposableBean) {
            try {
                ((DisposableBean)target).destroy();
            }
            catch (Throwable ex) {
                this.logger.error("Couldn't invoke destroy method of bean with name '" + this.getTargetBeanName() + "'", ex);
            }
        }
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("A prototype-based TargetSource itself is not deserializable - just a disconnected SingletonTargetSource is");
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Disconnecting TargetSource [" + this + "]");
        }
        try {
            return new SingletonTargetSource(this.getTarget());
        }
        catch (Exception ex) {
            this.logger.error("Cannot get target for disconnecting TargetSource [" + this + "]", ex);
            throw new NotSerializableException("Cannot get target for disconnecting TargetSource [" + this + "]: " + ex);
        }
    }
}
