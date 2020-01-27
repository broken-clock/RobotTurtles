// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.ApplicationListener;
import org.springframework.beans.factory.support.RootBeanDefinition;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.util.Iterator;
import java.util.Set;
import org.springframework.core.Ordered;
import java.util.Collection;
import org.springframework.core.OrderComparator;
import org.springframework.core.PriorityOrdered;
import java.util.ArrayList;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import java.util.LinkedList;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import java.util.HashSet;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

class PostProcessorRegistrationDelegate
{
    public static void invokeBeanFactoryPostProcessors(final ConfigurableListableBeanFactory beanFactory, final List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
        final Set<String> processedBeans = new HashSet<String>();
        if (beanFactory instanceof BeanDefinitionRegistry) {
            final BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
            final List<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList<BeanFactoryPostProcessor>();
            final List<BeanDefinitionRegistryPostProcessor> registryPostProcessors = new LinkedList<BeanDefinitionRegistryPostProcessor>();
            for (final BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    final BeanDefinitionRegistryPostProcessor registryPostProcessor = (BeanDefinitionRegistryPostProcessor)postProcessor;
                    registryPostProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryPostProcessors.add(registryPostProcessor);
                }
                else {
                    regularPostProcessors.add(postProcessor);
                }
            }
            String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            final List<BeanDefinitionRegistryPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanDefinitionRegistryPostProcessor>();
            for (final String ppName : postProcessorNames) {
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }
            OrderComparator.sort(priorityOrderedPostProcessors);
            registryPostProcessors.addAll(priorityOrderedPostProcessors);
            invokeBeanDefinitionRegistryPostProcessors(priorityOrderedPostProcessors, registry);
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            final List<BeanDefinitionRegistryPostProcessor> orderedPostProcessors = new ArrayList<BeanDefinitionRegistryPostProcessor>();
            for (final String ppName2 : postProcessorNames) {
                if (!processedBeans.contains(ppName2) && beanFactory.isTypeMatch(ppName2, Ordered.class)) {
                    orderedPostProcessors.add(beanFactory.getBean(ppName2, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName2);
                }
            }
            OrderComparator.sort(orderedPostProcessors);
            registryPostProcessors.addAll(orderedPostProcessors);
            invokeBeanDefinitionRegistryPostProcessors(orderedPostProcessors, registry);
            boolean reiterate = true;
            while (reiterate) {
                reiterate = false;
                final String[] beanNamesForType;
                postProcessorNames = (beanNamesForType = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false));
                for (final String ppName3 : beanNamesForType) {
                    if (!processedBeans.contains(ppName3)) {
                        final BeanDefinitionRegistryPostProcessor pp = beanFactory.getBean(ppName3, BeanDefinitionRegistryPostProcessor.class);
                        registryPostProcessors.add(pp);
                        processedBeans.add(ppName3);
                        pp.postProcessBeanDefinitionRegistry(registry);
                        reiterate = true;
                    }
                }
            }
            invokeBeanFactoryPostProcessors(registryPostProcessors, beanFactory);
            invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        }
        else {
            invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
        }
        final String[] postProcessorNames2 = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        final List<BeanFactoryPostProcessor> priorityOrderedPostProcessors2 = new ArrayList<BeanFactoryPostProcessor>();
        final List<String> orderedPostProcessorNames = new ArrayList<String>();
        final List<String> nonOrderedPostProcessorNames = new ArrayList<String>();
        for (final String ppName4 : postProcessorNames2) {
            if (!processedBeans.contains(ppName4)) {
                if (beanFactory.isTypeMatch(ppName4, PriorityOrdered.class)) {
                    priorityOrderedPostProcessors2.add(beanFactory.getBean(ppName4, BeanFactoryPostProcessor.class));
                }
                else if (beanFactory.isTypeMatch(ppName4, Ordered.class)) {
                    orderedPostProcessorNames.add(ppName4);
                }
                else {
                    nonOrderedPostProcessorNames.add(ppName4);
                }
            }
        }
        OrderComparator.sort(priorityOrderedPostProcessors2);
        invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors2, beanFactory);
        final List<BeanFactoryPostProcessor> orderedPostProcessors2 = new ArrayList<BeanFactoryPostProcessor>();
        for (final String postProcessorName : orderedPostProcessorNames) {
            orderedPostProcessors2.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }
        OrderComparator.sort(orderedPostProcessors2);
        invokeBeanFactoryPostProcessors(orderedPostProcessors2, beanFactory);
        final List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
        for (final String postProcessorName2 : nonOrderedPostProcessorNames) {
            nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName2, BeanFactoryPostProcessor.class));
        }
        invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
    }
    
    public static void registerBeanPostProcessors(final ConfigurableListableBeanFactory beanFactory, final AbstractApplicationContext applicationContext) {
        final String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        final int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
        final List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
        final List<BeanPostProcessor> internalPostProcessors = new ArrayList<BeanPostProcessor>();
        final List<String> orderedPostProcessorNames = new ArrayList<String>();
        final List<String> nonOrderedPostProcessorNames = new ArrayList<String>();
        for (final String ppName : postProcessorNames) {
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                final BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (pp instanceof MergedBeanDefinitionPostProcessor) {
                    internalPostProcessors.add(pp);
                }
            }
            else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
            }
            else {
                nonOrderedPostProcessorNames.add(ppName);
            }
        }
        OrderComparator.sort(priorityOrderedPostProcessors);
        registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
        final List<BeanPostProcessor> orderedPostProcessors = new ArrayList<BeanPostProcessor>();
        for (final String ppName2 : orderedPostProcessorNames) {
            final BeanPostProcessor pp2 = beanFactory.getBean(ppName2, BeanPostProcessor.class);
            orderedPostProcessors.add(pp2);
            if (pp2 instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp2);
            }
        }
        OrderComparator.sort(orderedPostProcessors);
        registerBeanPostProcessors(beanFactory, orderedPostProcessors);
        final List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
        for (final String ppName : nonOrderedPostProcessorNames) {
            final BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }
        registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
        OrderComparator.sort(internalPostProcessors);
        registerBeanPostProcessors(beanFactory, internalPostProcessors);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }
    
    private static void invokeBeanDefinitionRegistryPostProcessors(final Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, final BeanDefinitionRegistry registry) {
        for (final BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }
    }
    
    private static void invokeBeanFactoryPostProcessors(final Collection<? extends BeanFactoryPostProcessor> postProcessors, final ConfigurableListableBeanFactory beanFactory) {
        for (final BeanFactoryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanFactory(beanFactory);
        }
    }
    
    private static void registerBeanPostProcessors(final ConfigurableListableBeanFactory beanFactory, final List<BeanPostProcessor> postProcessors) {
        for (final BeanPostProcessor postProcessor : postProcessors) {
            beanFactory.addBeanPostProcessor(postProcessor);
        }
    }
    
    private static class BeanPostProcessorChecker implements BeanPostProcessor
    {
        private static final Log logger;
        private final ConfigurableListableBeanFactory beanFactory;
        private final int beanPostProcessorTargetCount;
        
        public BeanPostProcessorChecker(final ConfigurableListableBeanFactory beanFactory, final int beanPostProcessorTargetCount) {
            this.beanFactory = beanFactory;
            this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
        }
        
        @Override
        public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
            return bean;
        }
        
        @Override
        public Object postProcessAfterInitialization(final Object bean, final String beanName) {
            if (bean != null && !(bean instanceof BeanPostProcessor) && this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount && BeanPostProcessorChecker.logger.isInfoEnabled()) {
                BeanPostProcessorChecker.logger.info("Bean '" + beanName + "' of type [" + bean.getClass() + "] is not eligible for getting processed by all BeanPostProcessors " + "(for example: not eligible for auto-proxying)");
            }
            return bean;
        }
        
        static {
            logger = LogFactory.getLog(BeanPostProcessorChecker.class);
        }
    }
    
    private static class ApplicationListenerDetector implements MergedBeanDefinitionPostProcessor, DestructionAwareBeanPostProcessor
    {
        private static final Log logger;
        private final AbstractApplicationContext applicationContext;
        private final Map<String, Boolean> singletonNames;
        
        public ApplicationListenerDetector(final AbstractApplicationContext applicationContext) {
            this.singletonNames = new ConcurrentHashMap<String, Boolean>(64);
            this.applicationContext = applicationContext;
        }
        
        @Override
        public void postProcessMergedBeanDefinition(final RootBeanDefinition beanDefinition, final Class<?> beanType, final String beanName) {
            if (beanDefinition.isSingleton()) {
                this.singletonNames.put(beanName, Boolean.TRUE);
            }
        }
        
        @Override
        public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
            return bean;
        }
        
        @Override
        public Object postProcessAfterInitialization(final Object bean, final String beanName) {
            if (bean instanceof ApplicationListener) {
                final Boolean flag = this.singletonNames.get(beanName);
                if (Boolean.TRUE.equals(flag)) {
                    this.applicationContext.addApplicationListener((ApplicationListener<?>)bean);
                }
                else if (flag == null) {
                    if (ApplicationListenerDetector.logger.isWarnEnabled() && !this.applicationContext.containsBean(beanName)) {
                        ApplicationListenerDetector.logger.warn("Inner bean '" + beanName + "' implements ApplicationListener interface " + "but is not reachable for event multicasting by its containing ApplicationContext " + "because it does not have singleton scope. Only top-level listener beans are allowed " + "to be of non-singleton scope.");
                    }
                    this.singletonNames.put(beanName, Boolean.FALSE);
                }
            }
            return bean;
        }
        
        @Override
        public void postProcessBeforeDestruction(final Object bean, final String beanName) {
            if (bean instanceof ApplicationListener) {
                final ApplicationEventMulticaster multicaster = this.applicationContext.getApplicationEventMulticaster();
                multicaster.removeApplicationListener((ApplicationListener<?>)bean);
                multicaster.removeApplicationListenerBean(beanName);
            }
        }
        
        static {
            logger = LogFactory.getLog(ApplicationListenerDetector.class);
        }
    }
}
