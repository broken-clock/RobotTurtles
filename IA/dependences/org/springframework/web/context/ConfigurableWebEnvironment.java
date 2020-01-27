// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.springframework.core.env.ConfigurableEnvironment;

public interface ConfigurableWebEnvironment extends ConfigurableEnvironment
{
    void initPropertySources(final ServletContext p0, final ServletConfig p1);
}
