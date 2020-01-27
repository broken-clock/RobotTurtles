// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.springframework.util.Assert;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public abstract class AbstractBeanFactoryPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware
{
    private String adviceBeanName;
    private BeanFactory beanFactory;
    private transient Advice advice;
    private transient volatile Object adviceMonitor;
    
    public AbstractBeanFactoryPointcutAdvisor() {
        this.adviceMonitor = new Object();
    }
    
    public void setAdviceBeanName(final String adviceBeanName) {
        this.adviceBeanName = adviceBeanName;
    }
    
    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    public void setAdvice(final Advice advice) {
        synchronized (this.adviceMonitor) {
            this.advice = advice;
        }
    }
    
    @Override
    public Advice getAdvice() {
        synchronized (this.adviceMonitor) {
            if (this.advice == null && this.adviceBeanName != null) {
                Assert.state(this.beanFactory != null, "BeanFactory must be set to resolve 'adviceBeanName'");
                this.advice = this.beanFactory.getBean(this.adviceBeanName, Advice.class);
            }
            return this.advice;
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": advice bean '" + this.getAdviceBeanName() + "'";
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.adviceMonitor = new Object();
    }
}
