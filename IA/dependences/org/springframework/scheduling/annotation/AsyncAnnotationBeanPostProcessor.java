// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import java.util.concurrent.Executor;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;

public class AsyncAnnotationBeanPostProcessor extends AbstractAdvisingBeanPostProcessor implements BeanFactoryAware
{
    private Class<? extends Annotation> asyncAnnotationType;
    private Executor executor;
    
    public AsyncAnnotationBeanPostProcessor() {
        this.setBeforeExistingAdvisors(true);
    }
    
    public void setAsyncAnnotationType(final Class<? extends Annotation> asyncAnnotationType) {
        Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
        this.asyncAnnotationType = asyncAnnotationType;
    }
    
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        final AsyncAnnotationAdvisor advisor = (this.executor != null) ? new AsyncAnnotationAdvisor(this.executor) : new AsyncAnnotationAdvisor();
        if (this.asyncAnnotationType != null) {
            advisor.setAsyncAnnotationType(this.asyncAnnotationType);
        }
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }
}
