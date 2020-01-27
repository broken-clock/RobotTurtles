// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Bean;
import java.lang.annotation.Annotation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyAsyncConfiguration extends AbstractAsyncConfiguration
{
    @Bean(name = { "org.springframework.context.annotation.internalAsyncAnnotationProcessor" })
    @Role(2)
    public AsyncAnnotationBeanPostProcessor asyncAdvisor() {
        Assert.notNull(this.enableAsync, "@EnableAsync annotation metadata was not injected");
        final AsyncAnnotationBeanPostProcessor bpp = new AsyncAnnotationBeanPostProcessor();
        final Class<? extends Annotation> customAsyncAnnotation = (Class<? extends Annotation>)this.enableAsync.getClass("annotation");
        if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableAsync.class, "annotation")) {
            bpp.setAsyncAnnotationType(customAsyncAnnotation);
        }
        if (this.executor != null) {
            bpp.setExecutor(this.executor);
        }
        bpp.setProxyTargetClass(this.enableAsync.getBoolean("proxyTargetClass"));
        bpp.setOrder(this.enableAsync.getNumber("order"));
        return bpp;
    }
}
