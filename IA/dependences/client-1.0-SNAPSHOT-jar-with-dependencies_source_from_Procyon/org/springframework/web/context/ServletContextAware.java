// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.Aware;

public interface ServletContextAware extends Aware
{
    void setServletContext(final ServletContext p0);
}
