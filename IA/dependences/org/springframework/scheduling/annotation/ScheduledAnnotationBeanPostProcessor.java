// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.context.ApplicationEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Map;
import org.springframework.scheduling.TaskScheduler;
import java.util.HashMap;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronTrigger;
import java.util.TimeZone;
import org.springframework.util.StringUtils;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.reflect.Method;
import org.springframework.util.ReflectionUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringValueResolver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ScheduledAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, EmbeddedValueResolverAware, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>, DisposableBean
{
    private Object scheduler;
    private StringValueResolver embeddedValueResolver;
    private ApplicationContext applicationContext;
    private final ScheduledTaskRegistrar registrar;
    
    public ScheduledAnnotationBeanPostProcessor() {
        this.registrar = new ScheduledTaskRegistrar();
    }
    
    public void setScheduler(final Object scheduler) {
        this.scheduler = scheduler;
    }
    
    @Override
    public void setEmbeddedValueResolver(final StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }
    
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
                for (final Scheduled scheduled : AnnotationUtils.getRepeatableAnnotation(method, Schedules.class, Scheduled.class)) {
                    ScheduledAnnotationBeanPostProcessor.this.processScheduled(scheduled, method, bean);
                }
            }
        });
        return bean;
    }
    
    protected void processScheduled(final Scheduled scheduled, Method method, final Object bean) {
        try {
            Assert.isTrue(Void.TYPE.equals(method.getReturnType()), "Only void-returning methods may be annotated with @Scheduled");
            Assert.isTrue(method.getParameterTypes().length == 0, "Only no-arg methods may be annotated with @Scheduled");
            if (AopUtils.isJdkDynamicProxy(bean)) {
                try {
                    method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                }
                catch (SecurityException ex) {
                    ReflectionUtils.handleReflectionException(ex);
                }
                catch (NoSuchMethodException ex3) {
                    throw new IllegalStateException(String.format("@Scheduled method '%s' found on bean target class '%s', but not found in any interface(s) for bean JDK proxy. Either pull the method up to an interface or switch to subclass (CGLIB) proxies by setting proxy-target-class/proxyTargetClass attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()));
                }
            }
            final Runnable runnable = new ScheduledMethodRunnable(bean, method);
            boolean processedSchedule = false;
            final String errorMessage = "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required";
            long initialDelay = scheduled.initialDelay();
            String initialDelayString = scheduled.initialDelayString();
            if (StringUtils.hasText(initialDelayString)) {
                Assert.isTrue(initialDelay < 0L, "Specify 'initialDelay' or 'initialDelayString', not both");
                if (this.embeddedValueResolver != null) {
                    initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
                }
                try {
                    initialDelay = Integer.parseInt(initialDelayString);
                }
                catch (NumberFormatException ex4) {
                    throw new IllegalArgumentException("Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into integer");
                }
            }
            String cron = scheduled.cron();
            if (StringUtils.hasText(cron)) {
                Assert.isTrue(initialDelay == -1L, "'initialDelay' not supported for cron triggers");
                processedSchedule = true;
                String zone = scheduled.zone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                TimeZone timeZone;
                if (StringUtils.hasText(zone)) {
                    timeZone = StringUtils.parseTimeZoneString(zone);
                }
                else {
                    timeZone = TimeZone.getDefault();
                }
                this.registrar.addCronTask(new CronTask(runnable, new CronTrigger(cron, timeZone)));
            }
            if (initialDelay < 0L) {
                initialDelay = 0L;
            }
            long fixedDelay = scheduled.fixedDelay();
            if (fixedDelay >= 0L) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                this.registrar.addFixedDelayTask(new IntervalTask(runnable, fixedDelay, initialDelay));
            }
            String fixedDelayString = scheduled.fixedDelayString();
            if (StringUtils.hasText(fixedDelayString)) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                try {
                    fixedDelay = Integer.parseInt(fixedDelayString);
                }
                catch (NumberFormatException ex5) {
                    throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into integer");
                }
                this.registrar.addFixedDelayTask(new IntervalTask(runnable, fixedDelay, initialDelay));
            }
            long fixedRate = scheduled.fixedRate();
            if (fixedRate >= 0L) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                this.registrar.addFixedRateTask(new IntervalTask(runnable, fixedRate, initialDelay));
            }
            String fixedRateString = scheduled.fixedRateString();
            if (StringUtils.hasText(fixedRateString)) {
                Assert.isTrue(!processedSchedule, errorMessage);
                processedSchedule = true;
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                try {
                    fixedRate = Integer.parseInt(fixedRateString);
                }
                catch (NumberFormatException ex6) {
                    throw new IllegalArgumentException("Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into integer");
                }
                this.registrar.addFixedRateTask(new IntervalTask(runnable, fixedRate, initialDelay));
            }
            Assert.isTrue(processedSchedule, errorMessage);
        }
        catch (IllegalArgumentException ex2) {
            throw new IllegalStateException("Encountered invalid @Scheduled method '" + method.getName() + "': " + ex2.getMessage());
        }
    }
    
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (event.getApplicationContext() != this.applicationContext) {
            return;
        }
        if (this.scheduler != null) {
            this.registrar.setScheduler(this.scheduler);
        }
        final Map<String, SchedulingConfigurer> configurers = this.applicationContext.getBeansOfType(SchedulingConfigurer.class);
        for (final SchedulingConfigurer configurer : configurers.values()) {
            configurer.configureTasks(this.registrar);
        }
        if (this.registrar.hasTasks() && this.registrar.getScheduler() == null) {
            final Map<String, ? super Object> schedulers = new HashMap<String, Object>();
            schedulers.putAll(this.applicationContext.getBeansOfType((Class<?>)TaskScheduler.class));
            schedulers.putAll(this.applicationContext.getBeansOfType((Class<?>)ScheduledExecutorService.class));
            if (schedulers.size() != 0) {
                if (schedulers.size() == 1) {
                    this.registrar.setScheduler(schedulers.values().iterator().next());
                }
                else if (schedulers.size() >= 2) {
                    throw new IllegalStateException("More than one TaskScheduler and/or ScheduledExecutorService  exist within the context. Remove all but one of the beans; or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback. Found the following beans: " + schedulers.keySet());
                }
            }
        }
        this.registrar.afterPropertiesSet();
    }
    
    @Override
    public void destroy() {
        this.registrar.destroy();
    }
}
