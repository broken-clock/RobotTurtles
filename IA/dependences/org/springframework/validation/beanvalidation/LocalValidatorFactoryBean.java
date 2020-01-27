// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import javax.validation.ValidatorContext;
import org.springframework.util.Assert;
import javax.validation.Validator;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ClassUtils;
import java.util.Iterator;
import javax.validation.Configuration;
import java.io.IOException;
import javax.validation.Validation;
import org.springframework.util.CollectionUtils;
import java.util.Properties;
import org.springframework.context.MessageSource;
import java.util.HashMap;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.context.ApplicationContext;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.ParameterNameDiscoverer;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.TraversableResolver;
import javax.validation.MessageInterpolator;
import java.lang.reflect.Method;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;
import javax.validation.ValidatorFactory;

public class LocalValidatorFactoryBean extends SpringValidatorAdapter implements ValidatorFactory, ApplicationContextAware, InitializingBean, DisposableBean
{
    private static final Method closeMethod;
    private Class providerClass;
    private MessageInterpolator messageInterpolator;
    private TraversableResolver traversableResolver;
    private ConstraintValidatorFactory constraintValidatorFactory;
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private Resource[] mappingLocations;
    private final Map<String, String> validationPropertyMap;
    private ApplicationContext applicationContext;
    private ValidatorFactory validatorFactory;
    
    public LocalValidatorFactoryBean() {
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.validationPropertyMap = new HashMap<String, String>();
    }
    
    public void setProviderClass(final Class providerClass) {
        this.providerClass = providerClass;
    }
    
    public void setMessageInterpolator(final MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }
    
    public void setValidationMessageSource(final MessageSource messageSource) {
        this.messageInterpolator = HibernateValidatorDelegate.buildMessageInterpolator(messageSource);
    }
    
    public void setTraversableResolver(final TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }
    
    public void setConstraintValidatorFactory(final ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }
    
    public void setParameterNameDiscoverer(final ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }
    
    public void setMappingLocations(final Resource... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }
    
    public void setValidationProperties(final Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap(jpaProperties, this.validationPropertyMap);
    }
    
    public void setValidationPropertyMap(final Map<String, String> validationProperties) {
        if (validationProperties != null) {
            this.validationPropertyMap.putAll(validationProperties);
        }
    }
    
    public Map<String, String> getValidationPropertyMap() {
        return this.validationPropertyMap;
    }
    
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    public void afterPropertiesSet() {
        final Configuration<?> configuration = (Configuration<?>)((this.providerClass != null) ? Validation.byProvider(this.providerClass).configure() : Validation.byDefaultProvider().configure());
        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = configuration.getDefaultMessageInterpolator();
        }
        configuration.messageInterpolator((MessageInterpolator)new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            configuration.traversableResolver(this.traversableResolver);
        }
        ConstraintValidatorFactory targetConstraintValidatorFactory = this.constraintValidatorFactory;
        if (targetConstraintValidatorFactory == null && this.applicationContext != null) {
            targetConstraintValidatorFactory = (ConstraintValidatorFactory)new SpringConstraintValidatorFactory(this.applicationContext.getAutowireCapableBeanFactory());
        }
        if (targetConstraintValidatorFactory != null) {
            configuration.constraintValidatorFactory(targetConstraintValidatorFactory);
        }
        if (this.parameterNameDiscoverer != null) {
            this.configureParameterNameProviderIfPossible(configuration);
        }
        if (this.mappingLocations != null) {
            for (final Resource location : this.mappingLocations) {
                try {
                    configuration.addMapping(location.getInputStream());
                }
                catch (IOException ex) {
                    throw new IllegalStateException("Cannot read mapping resource: " + location);
                }
            }
        }
        for (final Map.Entry<String, String> entry : this.validationPropertyMap.entrySet()) {
            configuration.addProperty((String)entry.getKey(), (String)entry.getValue());
        }
        this.postProcessConfiguration(configuration);
        this.validatorFactory = configuration.buildValidatorFactory();
        this.setTargetValidator(this.validatorFactory.getValidator());
    }
    
    private void configureParameterNameProviderIfPossible(final Configuration<?> configuration) {
        try {
            final Class<?> parameterNameProviderClass = ClassUtils.forName("javax.validation.ParameterNameProvider", this.getClass().getClassLoader());
            final Method parameterNameProviderMethod = Configuration.class.getMethod("parameterNameProvider", parameterNameProviderClass);
            final Object defaultProvider = ReflectionUtils.invokeMethod(Configuration.class.getMethod("getDefaultParameterNameProvider", (Class<?>[])new Class[0]), configuration);
            final ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
            final Object parameterNameProvider = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { parameterNameProviderClass }, new InvocationHandler() {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                    if (method.getName().equals("getParameterNames")) {
                        String[] result = null;
                        if (args[0] instanceof Constructor) {
                            result = discoverer.getParameterNames((Constructor<?>)args[0]);
                        }
                        else if (args[0] instanceof Method) {
                            result = discoverer.getParameterNames((Method)args[0]);
                        }
                        if (result != null) {
                            return Arrays.asList(result);
                        }
                        try {
                            return method.invoke(defaultProvider, args);
                        }
                        catch (InvocationTargetException ex) {
                            throw ex.getTargetException();
                        }
                    }
                    try {
                        return method.invoke(this, args);
                    }
                    catch (InvocationTargetException ex2) {
                        throw ex2.getTargetException();
                    }
                }
            });
            ReflectionUtils.invokeMethod(parameterNameProviderMethod, configuration, parameterNameProvider);
        }
        catch (Exception ex) {}
    }
    
    protected void postProcessConfiguration(final Configuration<?> configuration) {
    }
    
    public javax.validation.Validator getValidator() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getValidator();
    }
    
    public ValidatorContext usingContext() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.usingContext();
    }
    
    public MessageInterpolator getMessageInterpolator() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getMessageInterpolator();
    }
    
    public TraversableResolver getTraversableResolver() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getTraversableResolver();
    }
    
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        Assert.notNull(this.validatorFactory, "No target ValidatorFactory set");
        return this.validatorFactory.getConstraintValidatorFactory();
    }
    
    public void close() {
        if (LocalValidatorFactoryBean.closeMethod != null && this.validatorFactory != null) {
            ReflectionUtils.invokeMethod(LocalValidatorFactoryBean.closeMethod, this.validatorFactory);
        }
    }
    
    public void destroy() {
        this.close();
    }
    
    static {
        closeMethod = ClassUtils.getMethodIfAvailable(ValidatorFactory.class, "close", (Class<?>[])new Class[0]);
    }
    
    private static class HibernateValidatorDelegate
    {
        public static MessageInterpolator buildMessageInterpolator(final MessageSource messageSource) {
            return (MessageInterpolator)new ResourceBundleMessageInterpolator((ResourceBundleLocator)new MessageSourceResourceBundleLocator(messageSource));
        }
    }
}
