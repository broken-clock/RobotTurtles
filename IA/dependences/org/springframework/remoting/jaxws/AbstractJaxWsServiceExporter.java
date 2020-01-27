// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.jaxws;

import org.springframework.util.ClassUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import java.util.Iterator;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import javax.xml.ws.WebServiceProvider;
import javax.jws.WebService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.beans.factory.BeanFactory;
import java.util.LinkedHashSet;
import javax.xml.ws.Endpoint;
import java.util.Set;
import org.springframework.beans.factory.ListableBeanFactory;
import javax.xml.ws.WebServiceFeature;
import java.util.concurrent.Executor;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanFactoryAware;

public abstract class AbstractJaxWsServiceExporter implements BeanFactoryAware, InitializingBean, DisposableBean
{
    private Map<String, Object> endpointProperties;
    private Executor executor;
    private String bindingType;
    private WebServiceFeature[] endpointFeatures;
    private Object[] webServiceFeatures;
    private ListableBeanFactory beanFactory;
    private final Set<Endpoint> publishedEndpoints;
    
    public AbstractJaxWsServiceExporter() {
        this.publishedEndpoints = new LinkedHashSet<Endpoint>();
    }
    
    public void setEndpointProperties(final Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }
    
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }
    
    public void setBindingType(final String bindingType) {
        this.bindingType = bindingType;
    }
    
    public void setEndpointFeatures(final WebServiceFeature... endpointFeatures) {
        this.endpointFeatures = endpointFeatures;
    }
    
    @Deprecated
    public void setWebServiceFeatures(final Object[] webServiceFeatures) {
        this.webServiceFeatures = webServiceFeatures;
    }
    
    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " requires a ListableBeanFactory");
        }
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.publishEndpoints();
    }
    
    public void publishEndpoints() {
        final Set<String> beanNames = new LinkedHashSet<String>(this.beanFactory.getBeanDefinitionCount());
        beanNames.addAll(Arrays.asList(this.beanFactory.getBeanDefinitionNames()));
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            beanNames.addAll(Arrays.asList(((ConfigurableBeanFactory)this.beanFactory).getSingletonNames()));
        }
        for (final String beanName : beanNames) {
            try {
                final Class<?> type = this.beanFactory.getType(beanName);
                if (type == null || type.isInterface()) {
                    continue;
                }
                final WebService wsAnnotation = type.getAnnotation(WebService.class);
                final WebServiceProvider wsProviderAnnotation = type.getAnnotation(WebServiceProvider.class);
                if (wsAnnotation == null && wsProviderAnnotation == null) {
                    continue;
                }
                final Endpoint endpoint = this.createEndpoint(this.beanFactory.getBean(beanName));
                if (this.endpointProperties != null) {
                    endpoint.setProperties((Map)this.endpointProperties);
                }
                if (this.executor != null) {
                    endpoint.setExecutor(this.executor);
                }
                if (wsAnnotation != null) {
                    this.publishEndpoint(endpoint, wsAnnotation);
                }
                else {
                    this.publishEndpoint(endpoint, wsProviderAnnotation);
                }
                this.publishedEndpoints.add(endpoint);
            }
            catch (CannotLoadBeanClassException ex) {}
        }
    }
    
    protected Endpoint createEndpoint(final Object bean) {
        if (this.endpointFeatures != null || this.webServiceFeatures != null) {
            WebServiceFeature[] endpointFeaturesToUse = this.endpointFeatures;
            if (endpointFeaturesToUse == null) {
                endpointFeaturesToUse = new WebServiceFeature[this.webServiceFeatures.length];
                for (int i = 0; i < this.webServiceFeatures.length; ++i) {
                    endpointFeaturesToUse[i] = this.convertWebServiceFeature(this.webServiceFeatures[i]);
                }
            }
            return Endpoint.create(this.bindingType, bean, endpointFeaturesToUse);
        }
        return Endpoint.create(this.bindingType, bean);
    }
    
    private WebServiceFeature convertWebServiceFeature(final Object feature) {
        Assert.notNull(feature, "WebServiceFeature specification object must not be null");
        if (feature instanceof WebServiceFeature) {
            return (WebServiceFeature)feature;
        }
        if (feature instanceof Class) {
            return BeanUtils.instantiate((Class<WebServiceFeature>)feature);
        }
        if (feature instanceof String) {
            try {
                final Class<?> featureClass = this.getBeanClassLoader().loadClass((String)feature);
                return BeanUtils.instantiate(featureClass);
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException("Could not load WebServiceFeature class [" + feature + "]");
            }
        }
        throw new IllegalArgumentException("Unknown WebServiceFeature specification type: " + feature.getClass());
    }
    
    private ClassLoader getBeanClassLoader() {
        return (this.beanFactory instanceof ConfigurableBeanFactory) ? ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader() : ClassUtils.getDefaultClassLoader();
    }
    
    protected abstract void publishEndpoint(final Endpoint p0, final WebService p1);
    
    protected abstract void publishEndpoint(final Endpoint p0, final WebServiceProvider p1);
    
    @Override
    public void destroy() {
        for (final Endpoint endpoint : this.publishedEndpoints) {
            endpoint.stop();
        }
    }
}
