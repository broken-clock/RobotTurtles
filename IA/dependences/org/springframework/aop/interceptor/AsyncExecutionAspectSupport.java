// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeansException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanFactory;
import java.util.concurrent.Executor;
import org.springframework.core.task.AsyncTaskExecutor;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.factory.BeanFactoryAware;

public abstract class AsyncExecutionAspectSupport implements BeanFactoryAware
{
    private final Map<Method, AsyncTaskExecutor> executors;
    private Executor defaultExecutor;
    private BeanFactory beanFactory;
    
    public AsyncExecutionAspectSupport(final Executor defaultExecutor) {
        this.executors = new ConcurrentHashMap<Method, AsyncTaskExecutor>(16);
        this.defaultExecutor = defaultExecutor;
    }
    
    public void setExecutor(final Executor defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    
    protected AsyncTaskExecutor determineAsyncExecutor(final Method method) {
        AsyncTaskExecutor executor = this.executors.get(method);
        if (executor == null) {
            Executor executorToUse = this.defaultExecutor;
            final String qualifier = this.getExecutorQualifier(method);
            if (StringUtils.hasLength(qualifier)) {
                Assert.notNull(this.beanFactory, "BeanFactory must be set on " + this.getClass().getSimpleName() + " to access qualified executor '" + qualifier + "'");
                executorToUse = BeanFactoryAnnotationUtils.qualifiedBeanOfType(this.beanFactory, Executor.class, qualifier);
            }
            else if (executorToUse == null) {
                return null;
            }
            executor = ((executorToUse instanceof AsyncTaskExecutor) ? ((AsyncTaskExecutor)executorToUse) : new TaskExecutorAdapter(executorToUse));
            this.executors.put(method, executor);
        }
        return executor;
    }
    
    protected abstract String getExecutorQualifier(final Method p0);
}
