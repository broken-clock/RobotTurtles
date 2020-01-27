// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.weaving;

import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.beans.factory.Aware;

public interface LoadTimeWeaverAware extends Aware
{
    void setLoadTimeWeaver(final LoadTimeWeaver p0);
}
