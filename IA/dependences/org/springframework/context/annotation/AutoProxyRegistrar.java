// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.util.LinkedHashMap;
import org.springframework.core.annotation.AnnotationAttributes;
import java.util.Iterator;
import java.util.Set;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar
{
    private final Log logger;
    
    public AutoProxyRegistrar() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata, final BeanDefinitionRegistry registry) {
        boolean candidateFound = false;
        final Set<String> annoTypes = importingClassMetadata.getAnnotationTypes();
        for (final String annoType : annoTypes) {
            final AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annoType);
            final Object mode = ((LinkedHashMap<K, Object>)candidate).get("mode");
            final Object proxyTargetClass = ((LinkedHashMap<K, Object>)candidate).get("proxyTargetClass");
            if (mode != null && proxyTargetClass != null && mode.getClass().equals(AdviceMode.class) && proxyTargetClass.getClass().equals(Boolean.class)) {
                candidateFound = true;
                if (mode != AdviceMode.PROXY) {
                    continue;
                }
                AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                if (proxyTargetClass) {
                    AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                    return;
                }
                continue;
            }
        }
        if (!candidateFound) {
            final String name = this.getClass().getSimpleName();
            this.logger.warn(String.format("%s was imported but no annotations were found having both 'mode' and 'proxyTargetClass' attributes of type AdviceMode and boolean respectively. This means that auto proxy creator registration and configuration may not have occured as intended, and components may not be proxied as expected. Check to ensure that %s has been @Import'ed on the same class where these annotations are declared; otherwise remove the import of %s altogether.", name, name, name));
        }
    }
}
