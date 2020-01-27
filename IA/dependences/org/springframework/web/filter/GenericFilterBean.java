// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.filter;

import java.util.Enumeration;
import org.springframework.util.StringUtils;
import org.springframework.beans.PropertyValue;
import java.util.Collection;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.BeansException;
import org.springframework.web.util.NestedServletException;
import java.beans.PropertyEditor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.Assert;
import javax.servlet.ServletException;
import org.springframework.web.context.support.StandardServletEnvironment;
import java.util.HashSet;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletContext;
import org.springframework.core.env.Environment;
import javax.servlet.FilterConfig;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.beans.factory.BeanNameAware;
import javax.servlet.Filter;

public abstract class GenericFilterBean implements Filter, BeanNameAware, EnvironmentAware, ServletContextAware, InitializingBean, DisposableBean
{
    protected final Log logger;
    private final Set<String> requiredProperties;
    private FilterConfig filterConfig;
    private String beanName;
    private Environment environment;
    private ServletContext servletContext;
    
    public GenericFilterBean() {
        this.logger = LogFactory.getLog(this.getClass());
        this.requiredProperties = new HashSet<String>();
        this.environment = new StandardServletEnvironment();
    }
    
    public final void setBeanName(final String beanName) {
        this.beanName = beanName;
    }
    
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
    
    public final void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    public void afterPropertiesSet() throws ServletException {
        this.initFilterBean();
    }
    
    protected final void addRequiredProperty(final String property) {
        this.requiredProperties.add(property);
    }
    
    public final void init(final FilterConfig filterConfig) throws ServletException {
        Assert.notNull(filterConfig, "FilterConfig must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Initializing filter '" + filterConfig.getFilterName() + "'");
        }
        this.filterConfig = filterConfig;
        try {
            final PropertyValues pvs = new FilterConfigPropertyValues(filterConfig, this.requiredProperties);
            final BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            final ResourceLoader resourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
            bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, this.environment));
            this.initBeanWrapper(bw);
            bw.setPropertyValues(pvs, true);
        }
        catch (BeansException ex) {
            final String msg = "Failed to set bean properties on filter '" + filterConfig.getFilterName() + "': " + ex.getMessage();
            this.logger.error(msg, ex);
            throw new NestedServletException(msg, ex);
        }
        this.initFilterBean();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Filter '" + filterConfig.getFilterName() + "' configured successfully");
        }
    }
    
    protected void initBeanWrapper(final BeanWrapper bw) throws BeansException {
    }
    
    public final FilterConfig getFilterConfig() {
        return this.filterConfig;
    }
    
    protected final String getFilterName() {
        return (this.filterConfig != null) ? this.filterConfig.getFilterName() : this.beanName;
    }
    
    protected final ServletContext getServletContext() {
        return (this.filterConfig != null) ? this.filterConfig.getServletContext() : this.servletContext;
    }
    
    protected void initFilterBean() throws ServletException {
    }
    
    public void destroy() {
    }
    
    private static class FilterConfigPropertyValues extends MutablePropertyValues
    {
        public FilterConfigPropertyValues(final FilterConfig config, final Set<String> requiredProperties) throws ServletException {
            final Set<String> missingProps = (requiredProperties != null && !requiredProperties.isEmpty()) ? new HashSet<String>(requiredProperties) : null;
            final Enumeration<?> en = (Enumeration<?>)config.getInitParameterNames();
            while (en.hasMoreElements()) {
                final String property = (String)en.nextElement();
                final Object value = config.getInitParameter(property);
                this.addPropertyValue(new PropertyValue(property, value));
                if (missingProps != null) {
                    missingProps.remove(property);
                }
            }
            if (missingProps != null && missingProps.size() > 0) {
                throw new ServletException("Initialization from FilterConfig for filter '" + config.getFilterName() + "' failed; the following required properties were missing: " + StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }
}
