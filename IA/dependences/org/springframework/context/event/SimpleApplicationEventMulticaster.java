// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import java.util.Iterator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.beans.factory.BeanFactory;
import java.util.concurrent.Executor;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster
{
    private Executor taskExecutor;
    
    public SimpleApplicationEventMulticaster() {
    }
    
    public SimpleApplicationEventMulticaster(final BeanFactory beanFactory) {
        this.setBeanFactory(beanFactory);
    }
    
    public void setTaskExecutor(final Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    protected Executor getTaskExecutor() {
        return this.taskExecutor;
    }
    
    @Override
    public void multicastEvent(final ApplicationEvent event) {
        for (final ApplicationListener listener : this.getApplicationListeners(event)) {
            final Executor executor = this.getTaskExecutor();
            if (executor != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.onApplicationEvent(event);
                    }
                });
            }
            else {
                listener.onApplicationEvent(event);
            }
        }
    }
}
