// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.annotation;

import java.lang.reflect.Method;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import java.util.Iterator;
import java.util.Map;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.BeanFactory;

public class BeanFactoryAnnotationUtils
{
    public static <T> T qualifiedBeanOfType(final BeanFactory beanFactory, final Class<T> beanType, final String qualifier) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            return qualifiedBeanOfType((ConfigurableListableBeanFactory)beanFactory, beanType, qualifier);
        }
        if (beanFactory.containsBean(qualifier)) {
            return beanFactory.getBean(qualifier, beanType);
        }
        throw new NoSuchBeanDefinitionException(qualifier, "No matching " + beanType.getSimpleName() + " bean found for bean name '" + qualifier + "'! (Note: Qualifier matching not supported because given " + "BeanFactory does not implement ConfigurableListableBeanFactory.)");
    }
    
    private static <T> T qualifiedBeanOfType(final ConfigurableListableBeanFactory bf, final Class<T> beanType, final String qualifier) {
        final Map<String, T> candidateBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(bf, beanType);
        T matchingBean = null;
        for (final String beanName : candidateBeans.keySet()) {
            if (isQualifierMatch(qualifier, beanName, bf)) {
                if (matchingBean != null) {
                    throw new NoSuchBeanDefinitionException(qualifier, "No unique " + beanType.getSimpleName() + " bean found for qualifier '" + qualifier + "'");
                }
                matchingBean = candidateBeans.get(beanName);
            }
        }
        if (matchingBean != null) {
            return matchingBean;
        }
        throw new NoSuchBeanDefinitionException(qualifier, "No matching " + beanType.getSimpleName() + " bean found for qualifier '" + qualifier + "' - neither qualifier " + "match nor bean name match!");
    }
    
    private static boolean isQualifierMatch(final String qualifier, final String beanName, final ConfigurableListableBeanFactory bf) {
        if (bf.containsBean(beanName)) {
            try {
                final BeanDefinition bd = bf.getMergedBeanDefinition(beanName);
                if (bd instanceof AbstractBeanDefinition) {
                    final AbstractBeanDefinition abd = (AbstractBeanDefinition)bd;
                    final AutowireCandidateQualifier candidate = abd.getQualifier(Qualifier.class.getName());
                    if ((candidate != null && qualifier.equals(candidate.getAttribute(AutowireCandidateQualifier.VALUE_KEY))) || qualifier.equals(beanName) || ObjectUtils.containsElement(bf.getAliases(beanName), qualifier)) {
                        return true;
                    }
                }
                if (bd instanceof RootBeanDefinition) {
                    final Method factoryMethod = ((RootBeanDefinition)bd).getResolvedFactoryMethod();
                    if (factoryMethod != null) {
                        final Qualifier targetAnnotation = factoryMethod.getAnnotation(Qualifier.class);
                        if (targetAnnotation != null && qualifier.equals(targetAnnotation.value())) {
                            return true;
                        }
                    }
                }
            }
            catch (NoSuchBeanDefinitionException ex) {}
        }
        return false;
    }
}
