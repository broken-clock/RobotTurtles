// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.weaving;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.lang.instrument.ClassFileTransformer;
import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.core.Ordered;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

public class AspectJWeavingEnabler implements BeanFactoryPostProcessor, BeanClassLoaderAware, LoadTimeWeaverAware, Ordered
{
    private ClassLoader beanClassLoader;
    private LoadTimeWeaver loadTimeWeaver;
    public static final String ASPECTJ_AOP_XML_RESOURCE = "META-INF/aop.xml";
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }
    
    @Override
    public void setLoadTimeWeaver(final LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }
    
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
    
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        enableAspectJWeaving(this.loadTimeWeaver, this.beanClassLoader);
    }
    
    public static void enableAspectJWeaving(LoadTimeWeaver weaverToUse, final ClassLoader beanClassLoader) {
        if (weaverToUse == null) {
            if (!InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
                throw new IllegalStateException("No LoadTimeWeaver available");
            }
            weaverToUse = new InstrumentationLoadTimeWeaver(beanClassLoader);
        }
        weaverToUse.addTransformer(new AspectJClassBypassingClassFileTransformer((ClassFileTransformer)new ClassPreProcessorAgentAdapter()));
    }
    
    private static class AspectJClassBypassingClassFileTransformer implements ClassFileTransformer
    {
        private final ClassFileTransformer delegate;
        
        public AspectJClassBypassingClassFileTransformer(final ClassFileTransformer delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className.startsWith("org.aspectj") || className.startsWith("org/aspectj")) {
                return classfileBuffer;
            }
            return this.delegate.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
    }
}
