// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

import javax.management.ObjectName;

public interface MBeanExportOperations
{
    ObjectName registerManagedResource(final Object p0) throws MBeanExportException;
    
    void registerManagedResource(final Object p0, final ObjectName p1) throws MBeanExportException;
    
    void unregisterManagedResource(final ObjectName p0);
}
