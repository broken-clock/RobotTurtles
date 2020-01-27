// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import java.util.Iterator;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Collections;
import org.springframework.util.Assert;
import org.springframework.context.ApplicationContext;
import javax.management.MBeanServer;
import org.springframework.context.ApplicationContextException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.Set;
import org.springframework.context.ApplicationContextAware;

public class LiveBeansView implements LiveBeansViewMBean, ApplicationContextAware
{
    public static final String MBEAN_DOMAIN_PROPERTY_NAME = "spring.liveBeansView.mbeanDomain";
    public static final String MBEAN_APPLICATION_KEY = "application";
    private static final Set<ConfigurableApplicationContext> applicationContexts;
    private ConfigurableApplicationContext applicationContext;
    
    static void registerApplicationContext(final ConfigurableApplicationContext applicationContext) {
        final String mbeanDomain = applicationContext.getEnvironment().getProperty("spring.liveBeansView.mbeanDomain");
        if (mbeanDomain != null) {
            synchronized (LiveBeansView.applicationContexts) {
                if (LiveBeansView.applicationContexts.isEmpty()) {
                    try {
                        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                        server.registerMBean(new LiveBeansView(), new ObjectName(mbeanDomain, "application", applicationContext.getApplicationName()));
                    }
                    catch (Exception ex) {
                        throw new ApplicationContextException("Failed to register LiveBeansView MBean", ex);
                    }
                }
                LiveBeansView.applicationContexts.add(applicationContext);
            }
        }
    }
    
    static void unregisterApplicationContext(final ConfigurableApplicationContext applicationContext) {
        synchronized (LiveBeansView.applicationContexts) {
            if (LiveBeansView.applicationContexts.remove(applicationContext) && LiveBeansView.applicationContexts.isEmpty()) {
                try {
                    final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    final String mbeanDomain = applicationContext.getEnvironment().getProperty("spring.liveBeansView.mbeanDomain");
                    server.unregisterMBean(new ObjectName(mbeanDomain, "application", applicationContext.getApplicationName()));
                }
                catch (Exception ex) {
                    throw new ApplicationContextException("Failed to unregister LiveBeansView MBean", ex);
                }
            }
        }
    }
    
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext, "ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext)applicationContext;
    }
    
    @Override
    public String getSnapshotAsJson() {
        Set<ConfigurableApplicationContext> contexts;
        if (this.applicationContext != null) {
            contexts = Collections.singleton(this.applicationContext);
        }
        else {
            contexts = this.findApplicationContexts();
        }
        return this.generateJson(contexts);
    }
    
    protected Set<ConfigurableApplicationContext> findApplicationContexts() {
        synchronized (LiveBeansView.applicationContexts) {
            return new LinkedHashSet<ConfigurableApplicationContext>(LiveBeansView.applicationContexts);
        }
    }
    
    protected String generateJson(final Set<ConfigurableApplicationContext> contexts) {
        final StringBuilder result = new StringBuilder("[\n");
        final Iterator<ConfigurableApplicationContext> it = contexts.iterator();
        while (it.hasNext()) {
            final ConfigurableApplicationContext context = it.next();
            result.append("{\n\"context\": \"").append(context.getId()).append("\",\n");
            if (context.getParent() != null) {
                result.append("\"parent\": \"").append(context.getParent().getId()).append("\",\n");
            }
            else {
                result.append("\"parent\": null,\n");
            }
            result.append("\"beans\": [\n");
            final ConfigurableListableBeanFactory bf = context.getBeanFactory();
            final String[] beanNames = bf.getBeanDefinitionNames();
            boolean elementAppended = false;
            for (final String beanName : beanNames) {
                final BeanDefinition bd = bf.getBeanDefinition(beanName);
                if (this.isBeanEligible(beanName, bd, bf)) {
                    if (elementAppended) {
                        result.append(",\n");
                    }
                    result.append("{\n\"bean\": \"").append(beanName).append("\",\n");
                    String scope = bd.getScope();
                    if (!StringUtils.hasText(scope)) {
                        scope = "singleton";
                    }
                    result.append("\"scope\": \"").append(scope).append("\",\n");
                    final Class<?> beanType = bf.getType(beanName);
                    if (beanType != null) {
                        result.append("\"type\": \"").append(beanType.getName()).append("\",\n");
                    }
                    else {
                        result.append("\"type\": null,\n");
                    }
                    final String resource = StringUtils.replace(bd.getResourceDescription(), "\\", "/");
                    result.append("\"resource\": \"").append(resource).append("\",\n");
                    result.append("\"dependencies\": [");
                    final String[] dependencies = bf.getDependenciesForBean(beanName);
                    if (dependencies.length > 0) {
                        result.append("\"");
                    }
                    result.append(StringUtils.arrayToDelimitedString(dependencies, "\", \""));
                    if (dependencies.length > 0) {
                        result.append("\"");
                    }
                    result.append("]\n}");
                    elementAppended = true;
                }
            }
            result.append("]\n");
            result.append("}");
            if (it.hasNext()) {
                result.append(",\n");
            }
        }
        result.append("]");
        return result.toString();
    }
    
    protected boolean isBeanEligible(final String beanName, final BeanDefinition bd, final ConfigurableBeanFactory bf) {
        return bd.getRole() != 2 && (!bd.isLazyInit() || bf.containsSingleton(beanName));
    }
    
    static {
        applicationContexts = new LinkedHashSet<ConfigurableApplicationContext>();
    }
}
