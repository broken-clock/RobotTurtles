// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.Aware;

public interface EnvironmentAware extends Aware
{
    void setEnvironment(final Environment p0);
}
