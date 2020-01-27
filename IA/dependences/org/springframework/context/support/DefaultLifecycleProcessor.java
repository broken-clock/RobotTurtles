// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.util.concurrent.TimeUnit;
import java.util.LinkedHashSet;
import org.springframework.context.Phased;
import org.springframework.beans.factory.BeanFactoryUtils;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.springframework.context.ApplicationContextException;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.Lifecycle;
import java.util.Map;
import java.util.HashMap;
import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.LifecycleProcessor;

public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware
{
    private final Log logger;
    private volatile long timeoutPerShutdownPhase;
    private volatile boolean running;
    private volatile ConfigurableListableBeanFactory beanFactory;
    
    public DefaultLifecycleProcessor() {
        this.logger = LogFactory.getLog(this.getClass());
        this.timeoutPerShutdownPhase = 30000L;
    }
    
    public void setTimeoutPerShutdownPhase(final long timeoutPerShutdownPhase) {
        this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }
    
    @Override
    public void start() {
        this.startBeans(false);
        this.running = true;
    }
    
    @Override
    public void stop() {
        this.stopBeans();
        this.running = false;
    }
    
    @Override
    public void onRefresh() {
        this.startBeans(true);
        this.running = true;
    }
    
    @Override
    public void onClose() {
        this.stopBeans();
        this.running = false;
    }
    
    @Override
    public boolean isRunning() {
        return this.running;
    }
    
    private void startBeans(final boolean autoStartupOnly) {
        final Map<String, Lifecycle> lifecycleBeans = this.getLifecycleBeans();
        final Map<Integer, LifecycleGroup> phases = new HashMap<Integer, LifecycleGroup>();
        for (final Map.Entry<String, ? extends Lifecycle> entry : lifecycleBeans.entrySet()) {
            final Lifecycle bean = (Lifecycle)entry.getValue();
            if (!autoStartupOnly || (bean instanceof SmartLifecycle && ((SmartLifecycle)bean).isAutoStartup())) {
                final int phase = this.getPhase(bean);
                LifecycleGroup group = phases.get(phase);
                if (group == null) {
                    group = new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
                    phases.put(phase, group);
                }
                group.add(entry.getKey(), bean);
            }
        }
        if (phases.size() > 0) {
            final List<Integer> keys = new ArrayList<Integer>(phases.keySet());
            Collections.sort(keys);
            for (final Integer key : keys) {
                phases.get(key).start();
            }
        }
    }
    
    private void doStart(final Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName, final boolean autoStartupOnly) {
        final Lifecycle bean = (Lifecycle)lifecycleBeans.remove(beanName);
        if (bean != null && !this.equals(bean)) {
            final String[] dependenciesForBean2;
            final String[] dependenciesForBean = dependenciesForBean2 = this.beanFactory.getDependenciesForBean(beanName);
            for (final String dependency : dependenciesForBean2) {
                this.doStart(lifecycleBeans, dependency, autoStartupOnly);
            }
            if (!bean.isRunning() && (!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle)bean).isAutoStartup())) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Starting bean '" + beanName + "' of type [" + bean.getClass() + "]");
                }
                try {
                    bean.start();
                }
                catch (Throwable ex) {
                    throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Successfully started bean '" + beanName + "'");
                }
            }
        }
    }
    
    private void stopBeans() {
        final Map<String, Lifecycle> lifecycleBeans = this.getLifecycleBeans();
        final Map<Integer, LifecycleGroup> phases = new HashMap<Integer, LifecycleGroup>();
        for (final Map.Entry<String, Lifecycle> entry : lifecycleBeans.entrySet()) {
            final Lifecycle bean = entry.getValue();
            final int shutdownOrder = this.getPhase(bean);
            LifecycleGroup group = phases.get(shutdownOrder);
            if (group == null) {
                group = new LifecycleGroup(shutdownOrder, this.timeoutPerShutdownPhase, lifecycleBeans, false);
                phases.put(shutdownOrder, group);
            }
            group.add(entry.getKey(), bean);
        }
        if (phases.size() > 0) {
            final List<Integer> keys = new ArrayList<Integer>(phases.keySet());
            Collections.sort(keys, Collections.reverseOrder());
            for (final Integer key : keys) {
                phases.get(key).stop();
            }
        }
    }
    
    private void doStop(final Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName, final CountDownLatch latch, final Set<String> countDownBeanNames) {
        final Lifecycle bean = (Lifecycle)lifecycleBeans.remove(beanName);
        if (bean != null) {
            final String[] dependentBeans2;
            final String[] dependentBeans = dependentBeans2 = this.beanFactory.getDependentBeans(beanName);
            for (final String dependentBean : dependentBeans2) {
                this.doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
            }
            try {
                if (bean.isRunning()) {
                    if (bean instanceof SmartLifecycle) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Asking bean '" + beanName + "' of type [" + bean.getClass() + "] to stop");
                        }
                        countDownBeanNames.add(beanName);
                        ((SmartLifecycle)bean).stop(new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                                countDownBeanNames.remove(beanName);
                                if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                                    DefaultLifecycleProcessor.this.logger.debug("Bean '" + beanName + "' completed its stop procedure");
                                }
                            }
                        });
                    }
                    else {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Stopping bean '" + beanName + "' of type [" + bean.getClass() + "]");
                        }
                        bean.stop();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Successfully stopped bean '" + beanName + "'");
                        }
                    }
                }
                else if (bean instanceof SmartLifecycle) {
                    latch.countDown();
                }
            }
            catch (Throwable ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Failed to stop bean '" + beanName + "'", ex);
                }
            }
        }
    }
    
    protected Map<String, Lifecycle> getLifecycleBeans() {
        final Map<String, Lifecycle> beans = new LinkedHashMap<String, Lifecycle>();
        final String[] beanNamesForType;
        final String[] beanNames = beanNamesForType = this.beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
        for (final String beanName : beanNamesForType) {
            final String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
            final boolean isFactoryBean = this.beanFactory.isFactoryBean(beanNameToRegister);
            final String beanNameToCheck = isFactoryBean ? ("&" + beanName) : beanName;
            if ((this.beanFactory.containsSingleton(beanNameToRegister) && (!isFactoryBean || Lifecycle.class.isAssignableFrom(this.beanFactory.getType(beanNameToCheck)))) || SmartLifecycle.class.isAssignableFrom(this.beanFactory.getType(beanNameToCheck))) {
                final Lifecycle bean = this.beanFactory.getBean(beanNameToCheck, Lifecycle.class);
                if (bean != this) {
                    beans.put(beanNameToRegister, bean);
                }
            }
        }
        return beans;
    }
    
    protected int getPhase(final Lifecycle bean) {
        return (bean instanceof Phased) ? ((Phased)bean).getPhase() : 0;
    }
    
    private class LifecycleGroup
    {
        private final List<LifecycleGroupMember> members;
        private final int phase;
        private final long timeout;
        private final Map<String, ? extends Lifecycle> lifecycleBeans;
        private final boolean autoStartupOnly;
        private volatile int smartMemberCount;
        
        public LifecycleGroup(final int phase, final long timeout, final Map<String, ? extends Lifecycle> lifecycleBeans, final boolean autoStartupOnly) {
            this.members = new ArrayList<LifecycleGroupMember>();
            this.phase = phase;
            this.timeout = timeout;
            this.lifecycleBeans = lifecycleBeans;
            this.autoStartupOnly = autoStartupOnly;
        }
        
        public void add(final String name, final Lifecycle bean) {
            if (bean instanceof SmartLifecycle) {
                ++this.smartMemberCount;
            }
            this.members.add(new LifecycleGroupMember(name, bean));
        }
        
        public void start() {
            if (this.members.isEmpty()) {
                return;
            }
            if (DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                DefaultLifecycleProcessor.this.logger.info("Starting beans in phase " + this.phase);
            }
            Collections.sort(this.members);
            for (final LifecycleGroupMember member : this.members) {
                if (this.lifecycleBeans.containsKey(member.name)) {
                    DefaultLifecycleProcessor.this.doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
                }
            }
        }
        
        public void stop() {
            if (this.members.isEmpty()) {
                return;
            }
            if (DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                DefaultLifecycleProcessor.this.logger.info("Stopping beans in phase " + this.phase);
            }
            Collections.sort(this.members, Collections.reverseOrder());
            final CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
            final Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet<String>());
            for (final LifecycleGroupMember member : this.members) {
                if (this.lifecycleBeans.containsKey(member.name)) {
                    DefaultLifecycleProcessor.this.doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
                }
                else {
                    if (!(member.bean instanceof SmartLifecycle)) {
                        continue;
                    }
                    latch.countDown();
                }
            }
            try {
                latch.await(this.timeout, TimeUnit.MILLISECONDS);
                if (latch.getCount() > 0L && !countDownBeanNames.isEmpty() && DefaultLifecycleProcessor.this.logger.isWarnEnabled()) {
                    DefaultLifecycleProcessor.this.logger.warn("Failed to shut down " + countDownBeanNames.size() + " bean" + ((countDownBeanNames.size() > 1) ? "s" : "") + " with phase value " + this.phase + " within timeout of " + this.timeout + ": " + countDownBeanNames);
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private class LifecycleGroupMember implements Comparable<LifecycleGroupMember>
    {
        private final String name;
        private final Lifecycle bean;
        
        LifecycleGroupMember(final String name, final Lifecycle bean) {
            this.name = name;
            this.bean = bean;
        }
        
        @Override
        public int compareTo(final LifecycleGroupMember other) {
            final int thisOrder = DefaultLifecycleProcessor.this.getPhase(this.bean);
            final int otherOrder = DefaultLifecycleProcessor.this.getPhase(other.bean);
            return (thisOrder == otherOrder) ? 0 : ((thisOrder < otherOrder) ? -1 : 1);
        }
    }
}
