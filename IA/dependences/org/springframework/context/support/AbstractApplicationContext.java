// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import java.util.Date;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.ContextStartedEvent;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import java.util.Locale;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.convert.ConversionService;
import java.util.Iterator;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.env.PropertyResolver;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.env.StandardEnvironment;
import java.util.Collection;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.context.ApplicationEvent;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import org.springframework.util.ObjectUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.context.ApplicationListener;
import java.util.Set;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.MessageSource;
import org.springframework.context.LifecycleProcessor;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext, DisposableBean
{
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";
    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    protected final Log logger;
    private String id;
    private String displayName;
    private ApplicationContext parent;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
    private long startupDate;
    private boolean active;
    private boolean closed;
    private final Object activeMonitor;
    private final Object startupShutdownMonitor;
    private Thread shutdownHook;
    private ResourcePatternResolver resourcePatternResolver;
    private LifecycleProcessor lifecycleProcessor;
    private MessageSource messageSource;
    private ApplicationEventMulticaster applicationEventMulticaster;
    private Set<ApplicationListener<?>> applicationListeners;
    private ConfigurableEnvironment environment;
    
    public AbstractApplicationContext() {
        this.logger = LogFactory.getLog(this.getClass());
        this.id = ObjectUtils.identityToString(this);
        this.displayName = ObjectUtils.identityToString(this);
        this.beanFactoryPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
        this.active = false;
        this.closed = false;
        this.activeMonitor = new Object();
        this.startupShutdownMonitor = new Object();
        this.applicationListeners = new LinkedHashSet<ApplicationListener<?>>();
        this.resourcePatternResolver = this.getResourcePatternResolver();
    }
    
    public AbstractApplicationContext(final ApplicationContext parent) {
        this();
        this.setParent(parent);
    }
    
    @Override
    public void setId(final String id) {
        this.id = id;
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public String getApplicationName() {
        return "";
    }
    
    public void setDisplayName(final String displayName) {
        Assert.hasLength(displayName, "Display name must not be empty");
        this.displayName = displayName;
    }
    
    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    
    @Override
    public ApplicationContext getParent() {
        return this.parent;
    }
    
    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }
        return this.environment;
    }
    
    @Override
    public void setEnvironment(final ConfigurableEnvironment environment) {
        this.environment = environment;
    }
    
    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return this.getBeanFactory();
    }
    
    @Override
    public long getStartupDate() {
        return this.startupDate;
    }
    
    @Override
    public void publishEvent(final ApplicationEvent event) {
        Assert.notNull(event, "Event must not be null");
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Publishing event in " + this.getDisplayName() + ": " + event);
        }
        this.getApplicationEventMulticaster().multicastEvent(event);
        if (this.parent != null) {
            this.parent.publishEvent(event);
        }
    }
    
    ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - call 'refresh' before multicasting events via the context: " + this);
        }
        return this.applicationEventMulticaster;
    }
    
    LifecycleProcessor getLifecycleProcessor() {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
        }
        return this.lifecycleProcessor;
    }
    
    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }
    
    @Override
    public void setParent(final ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            final Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                this.getEnvironment().merge((ConfigurableEnvironment)parentEnvironment);
            }
        }
    }
    
    @Override
    public void addBeanFactoryPostProcessor(final BeanFactoryPostProcessor beanFactoryPostProcessor) {
        this.beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }
    
    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }
    
    @Override
    public void addApplicationListener(final ApplicationListener<?> listener) {
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
        else {
            this.applicationListeners.add(listener);
        }
    }
    
    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }
    
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }
    
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            this.prepareRefresh();
            final ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            this.prepareBeanFactory(beanFactory);
            try {
                this.postProcessBeanFactory(beanFactory);
                this.invokeBeanFactoryPostProcessors(beanFactory);
                this.registerBeanPostProcessors(beanFactory);
                this.initMessageSource();
                this.initApplicationEventMulticaster();
                this.onRefresh();
                this.registerListeners();
                this.finishBeanFactoryInitialization(beanFactory);
                this.finishRefresh();
            }
            catch (BeansException ex) {
                this.destroyBeans();
                this.cancelRefresh(ex);
                throw ex;
            }
        }
    }
    
    protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        synchronized (this.activeMonitor) {
            this.active = true;
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Refreshing " + this);
        }
        this.initPropertySources();
        this.getEnvironment().validateRequiredProperties();
    }
    
    protected void initPropertySources() {
    }
    
    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        this.refreshBeanFactory();
        final ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Bean factory for " + this.getDisplayName() + ": " + beanFactory);
        }
        return beanFactory;
    }
    
    protected void prepareBeanFactory(final ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(this.getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, this.getEnvironment()));
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);
        if (beanFactory.containsBean("loadTimeWeaver")) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
        if (!beanFactory.containsLocalBean("environment")) {
            beanFactory.registerSingleton("environment", this.getEnvironment());
        }
        if (!beanFactory.containsLocalBean("systemProperties")) {
            beanFactory.registerSingleton("systemProperties", this.getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean("systemEnvironment")) {
            beanFactory.registerSingleton("systemEnvironment", this.getEnvironment().getSystemEnvironment());
        }
    }
    
    protected void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) {
    }
    
    protected void invokeBeanFactoryPostProcessors(final ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, this.getBeanFactoryPostProcessors());
    }
    
    protected void registerBeanPostProcessors(final ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }
    
    protected void initMessageSource() {
        final ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean("messageSource")) {
            this.messageSource = beanFactory.getBean("messageSource", MessageSource.class);
            if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
                final HierarchicalMessageSource hms = (HierarchicalMessageSource)this.messageSource;
                if (hms.getParentMessageSource() == null) {
                    hms.setParentMessageSource(this.getInternalParentMessageSource());
                }
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MessageSource [" + this.messageSource + "]");
            }
        }
        else {
            final DelegatingMessageSource dms = new DelegatingMessageSource();
            dms.setParentMessageSource(this.getInternalParentMessageSource());
            beanFactory.registerSingleton("messageSource", this.messageSource = dms);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate MessageSource with name 'messageSource': using default [" + this.messageSource + "]");
            }
        }
    }
    
    protected void initApplicationEventMulticaster() {
        final ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean("applicationEventMulticaster")) {
            this.applicationEventMulticaster = beanFactory.getBean("applicationEventMulticaster", ApplicationEventMulticaster.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
            }
        }
        else {
            beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory));
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate ApplicationEventMulticaster with name 'applicationEventMulticaster': using default [" + this.applicationEventMulticaster + "]");
            }
        }
    }
    
    protected void initLifecycleProcessor() {
        final ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean("lifecycleProcessor")) {
            this.lifecycleProcessor = beanFactory.getBean("lifecycleProcessor", LifecycleProcessor.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
            }
        }
        else {
            final DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
            defaultProcessor.setBeanFactory(beanFactory);
            beanFactory.registerSingleton("lifecycleProcessor", this.lifecycleProcessor = defaultProcessor);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate LifecycleProcessor with name 'lifecycleProcessor': using default [" + this.lifecycleProcessor + "]");
            }
        }
    }
    
    protected void onRefresh() throws BeansException {
    }
    
    protected void registerListeners() {
        for (final ApplicationListener<?> listener : this.getApplicationListeners()) {
            this.getApplicationEventMulticaster().addApplicationListener(listener);
        }
        final String[] beanNamesForType;
        final String[] listenerBeanNames = beanNamesForType = this.getBeanNamesForType(ApplicationListener.class, true, false);
        for (final String lisName : beanNamesForType) {
            this.getApplicationEventMulticaster().addApplicationListenerBean(lisName);
        }
    }
    
    protected void finishBeanFactoryInitialization(final ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean("conversionService") && beanFactory.isTypeMatch("conversionService", ConversionService.class)) {
            beanFactory.setConversionService(beanFactory.getBean("conversionService", ConversionService.class));
        }
        final String[] beanNamesForType;
        final String[] weaverAwareNames = beanNamesForType = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        for (final String weaverAwareName : beanNamesForType) {
            this.getBean(weaverAwareName);
        }
        beanFactory.setTempClassLoader(null);
        beanFactory.freezeConfiguration();
        beanFactory.preInstantiateSingletons();
    }
    
    protected void finishRefresh() {
        this.initLifecycleProcessor();
        this.getLifecycleProcessor().onRefresh();
        this.publishEvent(new ContextRefreshedEvent(this));
        LiveBeansView.registerApplicationContext(this);
    }
    
    protected void cancelRefresh(final BeansException ex) {
        synchronized (this.activeMonitor) {
            this.active = false;
        }
    }
    
    @Override
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread() {
                @Override
                public void run() {
                    AbstractApplicationContext.this.doClose();
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }
    
    @Override
    public void destroy() {
        this.close();
    }
    
    @Override
    public void close() {
        synchronized (this.startupShutdownMonitor) {
            this.doClose();
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                }
                catch (IllegalStateException ex) {}
            }
        }
    }
    
    protected void doClose() {
        final boolean actuallyClose;
        synchronized (this.activeMonitor) {
            actuallyClose = (this.active && !this.closed);
            this.closed = true;
        }
        if (actuallyClose) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Closing " + this);
            }
            LiveBeansView.unregisterApplicationContext(this);
            try {
                this.publishEvent(new ContextClosedEvent(this));
            }
            catch (Throwable ex) {
                this.logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
            }
            try {
                this.getLifecycleProcessor().onClose();
            }
            catch (Throwable ex) {
                this.logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
            }
            this.destroyBeans();
            this.closeBeanFactory();
            this.onClose();
            synchronized (this.activeMonitor) {
                this.active = false;
            }
        }
    }
    
    protected void destroyBeans() {
        this.getBeanFactory().destroySingletons();
    }
    
    protected void onClose() {
    }
    
    @Override
    public boolean isActive() {
        synchronized (this.activeMonitor) {
            return this.active;
        }
    }
    
    protected void assertBeanFactoryActive() {
        synchronized (this.activeMonitor) {
            if (!this.active) {
                if (this.closed) {
                    throw new IllegalStateException(this.getDisplayName() + " has been closed already");
                }
                throw new IllegalStateException(this.getDisplayName() + " has not been refreshed yet");
            }
        }
    }
    
    @Override
    public Object getBean(final String name) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name);
    }
    
    @Override
    public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name, requiredType);
    }
    
    @Override
    public <T> T getBean(final Class<T> requiredType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(requiredType);
    }
    
    @Override
    public Object getBean(final String name, final Object... args) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name, args);
    }
    
    @Override
    public boolean containsBean(final String name) {
        return this.getBeanFactory().containsBean(name);
    }
    
    @Override
    public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isSingleton(name);
    }
    
    @Override
    public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isPrototype(name);
    }
    
    @Override
    public boolean isTypeMatch(final String name, final Class<?> targetType) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isTypeMatch(name, targetType);
    }
    
    @Override
    public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getType(name);
    }
    
    @Override
    public String[] getAliases(final String name) {
        return this.getBeanFactory().getAliases(name);
    }
    
    @Override
    public boolean containsBeanDefinition(final String beanName) {
        return this.getBeanFactory().containsBeanDefinition(beanName);
    }
    
    @Override
    public int getBeanDefinitionCount() {
        return this.getBeanFactory().getBeanDefinitionCount();
    }
    
    @Override
    public String[] getBeanDefinitionNames() {
        return this.getBeanFactory().getBeanDefinitionNames();
    }
    
    @Override
    public String[] getBeanNamesForType(final Class<?> type) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type);
    }
    
    @Override
    public String[] getBeanNamesForType(final Class<?> type, final boolean includeNonSingletons, final boolean allowEagerInit) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansOfType(type);
    }
    
    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }
    
    @Override
    public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> annotationType) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForAnnotation(annotationType);
    }
    
    @Override
    public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansWithAnnotation(annotationType);
    }
    
    @Override
    public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }
    
    @Override
    public BeanFactory getParentBeanFactory() {
        return this.getParent();
    }
    
    @Override
    public boolean containsLocalBean(final String name) {
        return this.getBeanFactory().containsLocalBean(name);
    }
    
    protected BeanFactory getInternalParentBeanFactory() {
        return (this.getParent() instanceof ConfigurableApplicationContext) ? ((ConfigurableApplicationContext)this.getParent()).getBeanFactory() : this.getParent();
    }
    
    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
        return this.getMessageSource().getMessage(code, args, defaultMessage, locale);
    }
    
    @Override
    public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
        return this.getMessageSource().getMessage(code, args, locale);
    }
    
    @Override
    public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException {
        return this.getMessageSource().getMessage(resolvable, locale);
    }
    
    private MessageSource getMessageSource() throws IllegalStateException {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized - call 'refresh' before accessing messages via the context: " + this);
        }
        return this.messageSource;
    }
    
    protected MessageSource getInternalParentMessageSource() {
        return (this.getParent() instanceof AbstractApplicationContext) ? ((AbstractApplicationContext)this.getParent()).messageSource : this.getParent();
    }
    
    @Override
    public Resource[] getResources(final String locationPattern) throws IOException {
        return this.resourcePatternResolver.getResources(locationPattern);
    }
    
    @Override
    public void start() {
        this.getLifecycleProcessor().start();
        this.publishEvent(new ContextStartedEvent(this));
    }
    
    @Override
    public void stop() {
        this.getLifecycleProcessor().stop();
        this.publishEvent(new ContextStoppedEvent(this));
    }
    
    @Override
    public boolean isRunning() {
        return this.getLifecycleProcessor().isRunning();
    }
    
    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;
    
    protected abstract void closeBeanFactory();
    
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getDisplayName());
        sb.append(": startup date [").append(new Date(this.getStartupDate()));
        sb.append("]; ");
        final ApplicationContext parent = this.getParent();
        if (parent == null) {
            sb.append("root of context hierarchy");
        }
        else {
            sb.append("parent: ").append(parent.getDisplayName());
        }
        return sb.toString();
    }
    
    static {
        ContextClosedEvent.class.getName();
    }
}
