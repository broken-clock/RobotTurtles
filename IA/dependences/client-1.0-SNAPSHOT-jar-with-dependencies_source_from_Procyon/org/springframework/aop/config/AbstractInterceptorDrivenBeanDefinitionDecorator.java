// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.util.StringUtils;
import org.springframework.util.ClassUtils;
import java.util.List;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;

public abstract class AbstractInterceptorDrivenBeanDefinitionDecorator implements BeanDefinitionDecorator
{
    @Override
    public final BeanDefinitionHolder decorate(final Node node, final BeanDefinitionHolder definitionHolder, final ParserContext parserContext) {
        final BeanDefinitionRegistry registry = parserContext.getRegistry();
        final String existingBeanName = definitionHolder.getBeanName();
        final BeanDefinition targetDefinition = definitionHolder.getBeanDefinition();
        final BeanDefinitionHolder targetHolder = new BeanDefinitionHolder(targetDefinition, existingBeanName + ".TARGET");
        final BeanDefinition interceptorDefinition = this.createInterceptorDefinition(node);
        final String interceptorName = existingBeanName + "." + this.getInterceptorNameSuffix(interceptorDefinition);
        BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(interceptorDefinition, interceptorName), registry);
        BeanDefinitionHolder result = definitionHolder;
        if (!this.isProxyFactoryBeanDefinition(targetDefinition)) {
            final RootBeanDefinition proxyDefinition = new RootBeanDefinition();
            proxyDefinition.setBeanClass(ProxyFactoryBean.class);
            proxyDefinition.setScope(targetDefinition.getScope());
            proxyDefinition.setLazyInit(targetDefinition.isLazyInit());
            proxyDefinition.setDecoratedDefinition(targetHolder);
            proxyDefinition.getPropertyValues().add("target", targetHolder);
            proxyDefinition.getPropertyValues().add("interceptorNames", new ManagedList());
            proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
            proxyDefinition.setPrimary(targetDefinition.isPrimary());
            if (targetDefinition instanceof AbstractBeanDefinition) {
                proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition)targetDefinition);
            }
            result = new BeanDefinitionHolder(proxyDefinition, existingBeanName);
        }
        this.addInterceptorNameToList(interceptorName, result.getBeanDefinition());
        return result;
    }
    
    private void addInterceptorNameToList(final String interceptorName, final BeanDefinition beanDefinition) {
        final List<String> list = (List<String>)beanDefinition.getPropertyValues().getPropertyValue("interceptorNames").getValue();
        list.add(interceptorName);
    }
    
    private boolean isProxyFactoryBeanDefinition(final BeanDefinition existingDefinition) {
        return ProxyFactoryBean.class.getName().equals(existingDefinition.getBeanClassName());
    }
    
    protected String getInterceptorNameSuffix(final BeanDefinition interceptorDefinition) {
        return StringUtils.uncapitalize(ClassUtils.getShortName(interceptorDefinition.getBeanClassName()));
    }
    
    protected abstract BeanDefinition createInterceptorDefinition(final Node p0);
}
