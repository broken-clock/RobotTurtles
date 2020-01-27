// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import java.util.Collection;
import org.springframework.util.Assert;
import org.springframework.core.type.AnnotationMetadata;
import java.util.concurrent.Executor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;

@Configuration
public abstract class AbstractAsyncConfiguration implements ImportAware
{
    protected AnnotationAttributes enableAsync;
    protected Executor executor;
    
    @Override
    public void setImportMetadata(final AnnotationMetadata importMetadata) {
        Assert.notNull(this.enableAsync = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableAsync.class.getName(), false)), "@EnableAsync is not present on importing class " + importMetadata.getClassName());
    }
    
    @Autowired(required = false)
    void setConfigurers(final Collection<AsyncConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException("Only one AsyncConfigurer may exist");
        }
        final AsyncConfigurer configurer = configurers.iterator().next();
        this.executor = configurer.getAsyncExecutor();
    }
}
