// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.context.weaving.AspectJWeavingEnabler;
import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.util.Assert;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.beans.factory.BeanClassLoaderAware;

@Configuration
public class LoadTimeWeavingConfiguration implements ImportAware, BeanClassLoaderAware
{
    private AnnotationAttributes enableLTW;
    @Autowired(required = false)
    private LoadTimeWeavingConfigurer ltwConfigurer;
    private ClassLoader beanClassLoader;
    
    @Override
    public void setImportMetadata(final AnnotationMetadata importMetadata) {
        Assert.notNull(this.enableLTW = AnnotationConfigUtils.attributesFor(importMetadata, EnableLoadTimeWeaving.class), "@EnableLoadTimeWeaving is not present on importing class " + importMetadata.getClassName());
    }
    
    @Override
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    @Bean(name = { "loadTimeWeaver" })
    @Role(2)
    public LoadTimeWeaver loadTimeWeaver() {
        LoadTimeWeaver loadTimeWeaver = null;
        if (this.ltwConfigurer != null) {
            loadTimeWeaver = this.ltwConfigurer.getLoadTimeWeaver();
        }
        if (loadTimeWeaver == null) {
            loadTimeWeaver = new DefaultContextLoadTimeWeaver(this.beanClassLoader);
        }
        final EnableLoadTimeWeaving.AspectJWeaving aspectJWeaving = this.enableLTW.getEnum("aspectjWeaving");
        switch (aspectJWeaving) {
            case AUTODETECT: {
                if (this.beanClassLoader.getResource("META-INF/aop.xml") == null) {
                    break;
                }
                AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                break;
            }
            case ENABLED: {
                AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                break;
            }
        }
        return loadTimeWeaver;
    }
}
