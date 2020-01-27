// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import javax.servlet.ServletConfig;
import org.springframework.beans.factory.Aware;

public interface ServletConfigAware extends Aware
{
    void setServletConfig(final ServletConfig p0);
}
