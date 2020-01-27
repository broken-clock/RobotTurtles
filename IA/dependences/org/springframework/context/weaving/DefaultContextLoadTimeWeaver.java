// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.weaving;

import java.lang.instrument.ClassFileTransformer;
import org.springframework.instrument.classloading.websphere.WebSphereLoadTimeWeaver;
import org.springframework.instrument.classloading.jboss.JBossLoadTimeWeaver;
import org.springframework.instrument.classloading.tomcat.TomcatLoadTimeWeaver;
import org.springframework.instrument.classloading.glassfish.GlassFishLoadTimeWeaver;
import org.springframework.instrument.classloading.weblogic.WebLogicLoadTimeWeaver;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.instrument.classloading.LoadTimeWeaver;

public class DefaultContextLoadTimeWeaver implements LoadTimeWeaver, BeanClassLoaderAware, DisposableBean
{
    protected final Log logger;
    private LoadTimeWeaver loadTimeWeaver;
    
    public DefaultContextLoadTimeWeaver() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    public DefaultContextLoadTimeWeaver(final ClassLoader beanClassLoader) {
        this.logger = LogFactory.getLog(this.getClass());
        this.setBeanClassLoader(beanClassLoader);
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        final LoadTimeWeaver serverSpecificLoadTimeWeaver = this.createServerSpecificLoadTimeWeaver(classLoader);
        if (serverSpecificLoadTimeWeaver != null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Determined server-specific load-time weaver: " + serverSpecificLoadTimeWeaver.getClass().getName());
            }
            this.loadTimeWeaver = serverSpecificLoadTimeWeaver;
        }
        else if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
            this.logger.info("Found Spring's JVM agent for instrumentation");
            this.loadTimeWeaver = new InstrumentationLoadTimeWeaver(classLoader);
        }
        else {
            try {
                this.loadTimeWeaver = new ReflectiveLoadTimeWeaver(classLoader);
                this.logger.info("Using a reflective load-time weaver for class loader: " + this.loadTimeWeaver.getInstrumentableClassLoader().getClass().getName());
            }
            catch (IllegalStateException ex) {
                throw new IllegalStateException(ex.getMessage() + " Specify a custom LoadTimeWeaver or start your " + "Java virtual machine with Spring's agent: -javaagent:org.springframework.instrument.jar");
            }
        }
    }
    
    protected LoadTimeWeaver createServerSpecificLoadTimeWeaver(final ClassLoader classLoader) {
        final String name = classLoader.getClass().getName();
        try {
            if (name.startsWith("weblogic")) {
                return new WebLogicLoadTimeWeaver(classLoader);
            }
            if (name.startsWith("org.glassfish")) {
                return new GlassFishLoadTimeWeaver(classLoader);
            }
            if (name.startsWith("org.apache.catalina")) {
                return new TomcatLoadTimeWeaver(classLoader);
            }
            if (name.startsWith("org.jboss")) {
                return new JBossLoadTimeWeaver(classLoader);
            }
            if (name.startsWith("com.ibm")) {
                return new WebSphereLoadTimeWeaver(classLoader);
            }
        }
        catch (IllegalStateException ex) {
            this.logger.info("Could not obtain server-specific LoadTimeWeaver: " + ex.getMessage());
        }
        return null;
    }
    
    @Override
    public void destroy() {
        if (this.loadTimeWeaver instanceof InstrumentationLoadTimeWeaver) {
            this.logger.info("Removing all registered transformers for class loader: " + this.loadTimeWeaver.getInstrumentableClassLoader().getClass().getName());
            ((InstrumentationLoadTimeWeaver)this.loadTimeWeaver).removeTransformers();
        }
    }
    
    @Override
    public void addTransformer(final ClassFileTransformer transformer) {
        this.loadTimeWeaver.addTransformer(transformer);
    }
    
    @Override
    public ClassLoader getInstrumentableClassLoader() {
        return this.loadTimeWeaver.getInstrumentableClassLoader();
    }
    
    @Override
    public ClassLoader getThrowawayClassLoader() {
        return this.loadTimeWeaver.getThrowawayClassLoader();
    }
}
