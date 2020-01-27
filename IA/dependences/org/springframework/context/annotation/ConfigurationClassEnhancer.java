// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.ReflectionUtils;
import java.util.Arrays;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.reflect.Field;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.transform.ClassTransformer;
import org.springframework.cglib.transform.TransformingClassGenerator;
import org.springframework.asm.Type;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.transform.ClassEmitterTransformer;
import org.springframework.cglib.core.ClassGenerator;
import java.lang.reflect.Method;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.core.GeneratorStrategy;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;
import org.apache.commons.logging.Log;
import org.springframework.cglib.core.DefaultGeneratorStrategy;
import org.springframework.cglib.proxy.Callback;

class ConfigurationClassEnhancer
{
    private static final Callback[] CALLBACKS;
    private static final ConditionalCallbackFilter CALLBACK_FILTER;
    private static final DefaultGeneratorStrategy GENERATOR_STRATEGY;
    private static final String BEAN_FACTORY_FIELD = "$$beanFactory";
    private static final Log logger;
    
    public Class<?> enhance(final Class<?> configClass) {
        if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
            if (ConfigurationClassEnhancer.logger.isDebugEnabled()) {
                ConfigurationClassEnhancer.logger.debug(String.format("Ignoring request to enhance %s as it has already been enhanced. This usually indicates that more than one ConfigurationClassPostProcessor has been registered (e.g. via <context:annotation-config>). This is harmless, but you may want check your configuration and remove one CCPP if possible", configClass.getName()));
            }
            return configClass;
        }
        final Class<?> enhancedClass = this.createClass(this.newEnhancer(configClass));
        if (ConfigurationClassEnhancer.logger.isDebugEnabled()) {
            ConfigurationClassEnhancer.logger.debug(String.format("Successfully enhanced %s; enhanced class name is: %s", configClass.getName(), enhancedClass.getName()));
        }
        return enhancedClass;
    }
    
    private Enhancer newEnhancer(final Class<?> superclass) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(superclass);
        enhancer.setInterfaces(new Class[] { EnhancedConfiguration.class });
        enhancer.setUseFactory(false);
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        enhancer.setStrategy(ConfigurationClassEnhancer.GENERATOR_STRATEGY);
        enhancer.setCallbackFilter(ConfigurationClassEnhancer.CALLBACK_FILTER);
        enhancer.setCallbackTypes(ConfigurationClassEnhancer.CALLBACK_FILTER.getCallbackTypes());
        return enhancer;
    }
    
    private Class<?> createClass(final Enhancer enhancer) {
        final Class<?> subclass = (Class<?>)enhancer.createClass();
        Enhancer.registerStaticCallbacks(subclass, ConfigurationClassEnhancer.CALLBACKS);
        return subclass;
    }
    
    static {
        CALLBACKS = new Callback[] { new BeanMethodInterceptor(), new DisposableBeanMethodInterceptor(), new BeanFactoryAwareMethodInterceptor(), NoOp.INSTANCE };
        CALLBACK_FILTER = new ConditionalCallbackFilter(ConfigurationClassEnhancer.CALLBACKS);
        GENERATOR_STRATEGY = new BeanFactoryAwareGeneratorStrategy();
        logger = LogFactory.getLog(ConfigurationClassEnhancer.class);
    }
    
    private static class ConditionalCallbackFilter implements CallbackFilter
    {
        private final Callback[] callbacks;
        private final Class<?>[] callbackTypes;
        
        public ConditionalCallbackFilter(final Callback[] callbacks) {
            this.callbacks = callbacks;
            this.callbackTypes = (Class<?>[])new Class[callbacks.length];
            for (int i = 0; i < callbacks.length; ++i) {
                this.callbackTypes[i] = callbacks[i].getClass();
            }
        }
        
        @Override
        public int accept(final Method method) {
            for (int i = 0; i < this.callbacks.length; ++i) {
                if (!(this.callbacks[i] instanceof ConditionalCallback) || ((ConditionalCallback)this.callbacks[i]).isMatch(method)) {
                    return i;
                }
            }
            throw new IllegalStateException("No callback available for method " + method.getName());
        }
        
        public Class<?>[] getCallbackTypes() {
            return this.callbackTypes;
        }
    }
    
    private static class BeanFactoryAwareGeneratorStrategy extends DefaultGeneratorStrategy
    {
        @Override
        protected ClassGenerator transform(final ClassGenerator cg) throws Exception {
            final ClassEmitterTransformer transformer = new ClassEmitterTransformer() {
                @Override
                public void end_class() {
                    this.declare_field(1, "$$beanFactory", Type.getType(BeanFactory.class), null);
                    super.end_class();
                }
            };
            return new TransformingClassGenerator(cg, transformer);
        }
    }
    
    private static class BeanFactoryAwareMethodInterceptor implements MethodInterceptor, ConditionalCallback
    {
        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
            final Field field = obj.getClass().getDeclaredField("$$beanFactory");
            Assert.state(field != null, "Unable to find generated BeanFactory field");
            field.set(obj, args[0]);
            if (BeanFactoryAware.class.isAssignableFrom(obj.getClass().getSuperclass())) {
                return proxy.invokeSuper(obj, args);
            }
            return null;
        }
        
        @Override
        public boolean isMatch(final Method candidateMethod) {
            return candidateMethod.getName().equals("setBeanFactory") && candidateMethod.getParameterTypes().length == 1 && candidateMethod.getParameterTypes()[0].equals(BeanFactory.class) && BeanFactoryAware.class.isAssignableFrom(candidateMethod.getDeclaringClass());
        }
    }
    
    private static class BeanMethodInterceptor implements MethodInterceptor, ConditionalCallback
    {
        @Override
        public Object intercept(final Object enhancedConfigInstance, final Method beanMethod, final Object[] beanMethodArgs, final MethodProxy cglibMethodProxy) throws Throwable {
            final ConfigurableBeanFactory beanFactory = this.getBeanFactory(enhancedConfigInstance);
            String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);
            final Scope scope = AnnotationUtils.findAnnotation(beanMethod, Scope.class);
            if (scope != null && scope.proxyMode() != ScopedProxyMode.NO) {
                final String scopedBeanName = ScopedProxyCreator.getTargetBeanName(beanName);
                if (beanFactory.isCurrentlyInCreation(scopedBeanName)) {
                    beanName = scopedBeanName;
                }
            }
            if (this.factoryContainsBean(beanFactory, "&" + beanName) && this.factoryContainsBean(beanFactory, beanName)) {
                final Object factoryBean = beanFactory.getBean("&" + beanName);
                if (!(factoryBean instanceof ScopedProxyFactoryBean)) {
                    return this.enhanceFactoryBean(factoryBean.getClass(), beanFactory, beanName);
                }
            }
            if (this.isCurrentlyInvokedFactoryMethod(beanMethod) && !beanFactory.containsSingleton(beanName)) {
                if (BeanFactoryPostProcessor.class.isAssignableFrom(beanMethod.getReturnType())) {
                    ConfigurationClassEnhancer.logger.warn(String.format("@Bean method %s.%s is non-static and returns an object assignable to Spring's BeanFactoryPostProcessor interface. This will result in a failure to process annotations such as @Autowired, @Resource and @PostConstruct within the method's declaring @Configuration class. Add the 'static' modifier to this method to avoid these container lifecycle issues; see @Bean Javadoc for complete details", beanMethod.getDeclaringClass().getSimpleName(), beanMethod.getName()));
                }
                return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
            }
            final boolean alreadyInCreation = beanFactory.isCurrentlyInCreation(beanName);
            try {
                if (alreadyInCreation) {
                    beanFactory.setCurrentlyInCreation(beanName, false);
                }
                return beanFactory.getBean(beanName);
            }
            finally {
                if (alreadyInCreation) {
                    beanFactory.setCurrentlyInCreation(beanName, true);
                }
            }
        }
        
        private boolean factoryContainsBean(final ConfigurableBeanFactory beanFactory, final String beanName) {
            return beanFactory.containsBean(beanName) && !beanFactory.isCurrentlyInCreation(beanName);
        }
        
        private boolean isCurrentlyInvokedFactoryMethod(final Method method) {
            final Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
            return currentlyInvoked != null && method.getName().equals(currentlyInvoked.getName()) && Arrays.equals(method.getParameterTypes(), currentlyInvoked.getParameterTypes());
        }
        
        private Object enhanceFactoryBean(final Class<?> fbClass, final ConfigurableBeanFactory beanFactory, final String beanName) throws InstantiationException, IllegalAccessException {
            final Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(fbClass);
            enhancer.setUseFactory(false);
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
                    if (method.getName().equals("getObject") && args.length == 0) {
                        return beanFactory.getBean(beanName);
                    }
                    return proxy.invokeSuper(obj, args);
                }
            });
            return enhancer.create();
        }
        
        private ConfigurableBeanFactory getBeanFactory(final Object enhancedConfigInstance) {
            final Field field = ReflectionUtils.findField(enhancedConfigInstance.getClass(), "$$beanFactory");
            Assert.state(field != null, "Unable to find generated bean factory field");
            final Object beanFactory = ReflectionUtils.getField(field, enhancedConfigInstance);
            Assert.state(beanFactory != null, "BeanFactory has not been injected into @Configuration class");
            Assert.state(beanFactory instanceof ConfigurableBeanFactory, "Injected BeanFactory is not a ConfigurableBeanFactory");
            return (ConfigurableBeanFactory)beanFactory;
        }
        
        @Override
        public boolean isMatch(final Method candidateMethod) {
            return BeanAnnotationHelper.isBeanAnnotated(candidateMethod);
        }
    }
    
    private static class DisposableBeanMethodInterceptor implements MethodInterceptor, ConditionalCallback
    {
        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
            Enhancer.registerStaticCallbacks(obj.getClass(), null);
            if (DisposableBean.class.isAssignableFrom(obj.getClass().getSuperclass())) {
                return proxy.invokeSuper(obj, args);
            }
            return null;
        }
        
        @Override
        public boolean isMatch(final Method candidateMethod) {
            return candidateMethod.getName().equals("destroy") && candidateMethod.getParameterTypes().length == 0 && DisposableBean.class.isAssignableFrom(candidateMethod.getDeclaringClass());
        }
    }
    
    private interface ConditionalCallback extends Callback
    {
        boolean isMatch(final Method p0);
    }
    
    public interface EnhancedConfiguration extends DisposableBean, BeanFactoryAware
    {
    }
}
