// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx;

public class MBeanServerNotFoundException extends JmxException
{
    public MBeanServerNotFoundException(final String msg) {
        super(msg);
    }
    
    public MBeanServerNotFoundException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
