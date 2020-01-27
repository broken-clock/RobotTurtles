// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;

public class CachingConfigurationSelector extends AdviceModeImportSelector<EnableCaching>
{
    public String[] selectImports(final AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY: {
                return new String[] { AutoProxyRegistrar.class.getName(), ProxyCachingConfiguration.class.getName() };
            }
            case ASPECTJ: {
                return new String[] { "org.springframework.cache.aspectj.AspectJCachingConfiguration" };
            }
            default: {
                return null;
            }
        }
    }
}
