// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.util.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.beans.factory.FactoryBean;

public class TaskExecutorFactoryBean implements FactoryBean<TaskExecutor>, BeanNameAware, InitializingBean, DisposableBean
{
    private String poolSize;
    private Integer queueCapacity;
    private Object rejectedExecutionHandler;
    private Integer keepAliveSeconds;
    private String beanName;
    private TaskExecutor target;
    
    public void setPoolSize(final String poolSize) {
        this.poolSize = poolSize;
    }
    
    public void setQueueCapacity(final int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
    
    public void setRejectedExecutionHandler(final Object rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }
    
    public void setKeepAliveSeconds(final int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
    
    @Override
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        final BeanWrapper bw = new BeanWrapperImpl(ThreadPoolTaskExecutor.class);
        this.determinePoolSizeRange(bw);
        if (this.queueCapacity != null) {
            bw.setPropertyValue("queueCapacity", this.queueCapacity);
        }
        if (this.keepAliveSeconds != null) {
            bw.setPropertyValue("keepAliveSeconds", this.keepAliveSeconds);
        }
        if (this.rejectedExecutionHandler != null) {
            bw.setPropertyValue("rejectedExecutionHandler", this.rejectedExecutionHandler);
        }
        if (this.beanName != null) {
            bw.setPropertyValue("threadNamePrefix", this.beanName + "-");
        }
        this.target = (TaskExecutor)bw.getWrappedInstance();
        if (this.target instanceof InitializingBean) {
            ((InitializingBean)this.target).afterPropertiesSet();
        }
    }
    
    private void determinePoolSizeRange(final BeanWrapper bw) {
        if (StringUtils.hasText(this.poolSize)) {
            try {
                final int separatorIndex = this.poolSize.indexOf(45);
                int corePoolSize;
                int maxPoolSize;
                if (separatorIndex != -1) {
                    corePoolSize = Integer.valueOf(this.poolSize.substring(0, separatorIndex));
                    maxPoolSize = Integer.valueOf(this.poolSize.substring(separatorIndex + 1, this.poolSize.length()));
                    if (corePoolSize > maxPoolSize) {
                        throw new IllegalArgumentException("Lower bound of pool-size range must not exceed the upper bound");
                    }
                    if (this.queueCapacity == null) {
                        if (corePoolSize != 0) {
                            throw new IllegalArgumentException("A non-zero lower bound for the size range requires a queue-capacity value");
                        }
                        bw.setPropertyValue("allowCoreThreadTimeOut", true);
                        corePoolSize = maxPoolSize;
                    }
                }
                else {
                    final Integer value = Integer.valueOf(this.poolSize);
                    corePoolSize = value;
                    maxPoolSize = value;
                }
                bw.setPropertyValue("corePoolSize", corePoolSize);
                bw.setPropertyValue("maxPoolSize", maxPoolSize);
            }
            catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid pool-size value [" + this.poolSize + "]: only single " + "maximum integer (e.g. \"5\") and minimum-maximum range (e.g. \"3-5\") are supported", ex);
            }
        }
    }
    
    @Override
    public TaskExecutor getObject() {
        return this.target;
    }
    
    @Override
    public Class<? extends TaskExecutor> getObjectType() {
        return (this.target != null) ? this.target.getClass() : ThreadPoolTaskExecutor.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() throws Exception {
        if (this.target instanceof DisposableBean) {
            ((DisposableBean)this.target).destroy();
        }
    }
}
