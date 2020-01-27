// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ObjectNameManager
{
    public static ObjectName getInstance(final Object objectName) throws MalformedObjectNameException {
        if (objectName instanceof ObjectName) {
            return (ObjectName)objectName;
        }
        if (!(objectName instanceof String)) {
            throw new MalformedObjectNameException("Invalid ObjectName value type [" + objectName.getClass().getName() + "]: only ObjectName and String supported.");
        }
        return getInstance((String)objectName);
    }
    
    public static ObjectName getInstance(final String objectName) throws MalformedObjectNameException {
        return ObjectName.getInstance(objectName);
    }
    
    public static ObjectName getInstance(final String domainName, final String key, final String value) throws MalformedObjectNameException {
        return ObjectName.getInstance(domainName, key, value);
    }
    
    public static ObjectName getInstance(final String domainName, final Hashtable<String, String> properties) throws MalformedObjectNameException {
        return ObjectName.getInstance(domainName, properties);
    }
}
