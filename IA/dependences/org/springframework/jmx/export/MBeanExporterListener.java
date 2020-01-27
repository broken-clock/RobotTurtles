// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

import javax.management.ObjectName;

public interface MBeanExporterListener
{
    void mbeanRegistered(final ObjectName p0);
    
    void mbeanUnregistered(final ObjectName p0);
}
