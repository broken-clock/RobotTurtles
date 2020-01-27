// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;

public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync>
{
    public String[] selectImports(final AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY: {
                return new String[] { ProxyAsyncConfiguration.class.getName() };
            }
            case ASPECTJ: {
                return new String[] { "org.springframework.scheduling.aspectj.AspectJAsyncConfiguration" };
            }
            default: {
                return null;
            }
        }
    }
}
