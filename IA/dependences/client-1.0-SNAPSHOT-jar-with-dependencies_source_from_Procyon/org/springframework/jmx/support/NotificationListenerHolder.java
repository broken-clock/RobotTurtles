// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import org.springframework.util.ObjectUtils;
import javax.management.MalformedObjectNameException;
import java.util.Iterator;
import javax.management.ObjectName;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.Set;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

public class NotificationListenerHolder
{
    private NotificationListener notificationListener;
    private NotificationFilter notificationFilter;
    private Object handback;
    protected Set<Object> mappedObjectNames;
    
    public void setNotificationListener(final NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }
    
    public NotificationListener getNotificationListener() {
        return this.notificationListener;
    }
    
    public void setNotificationFilter(final NotificationFilter notificationFilter) {
        this.notificationFilter = notificationFilter;
    }
    
    public NotificationFilter getNotificationFilter() {
        return this.notificationFilter;
    }
    
    public void setHandback(final Object handback) {
        this.handback = handback;
    }
    
    public Object getHandback() {
        return this.handback;
    }
    
    public void setMappedObjectName(final Object mappedObjectName) {
        this.setMappedObjectNames((Object[])((mappedObjectName != null) ? new Object[] { mappedObjectName } : null));
    }
    
    public void setMappedObjectNames(final Object[] mappedObjectNames) {
        this.mappedObjectNames = ((mappedObjectNames != null) ? new LinkedHashSet<Object>(Arrays.asList(mappedObjectNames)) : null);
    }
    
    public ObjectName[] getResolvedObjectNames() throws MalformedObjectNameException {
        if (this.mappedObjectNames == null) {
            return null;
        }
        final ObjectName[] resolved = new ObjectName[this.mappedObjectNames.size()];
        int i = 0;
        for (final Object objectName : this.mappedObjectNames) {
            resolved[i] = ObjectNameManager.getInstance(objectName);
            ++i;
        }
        return resolved;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NotificationListenerHolder)) {
            return false;
        }
        final NotificationListenerHolder otherNlh = (NotificationListenerHolder)other;
        return ObjectUtils.nullSafeEquals(this.notificationListener, otherNlh.notificationListener) && ObjectUtils.nullSafeEquals(this.notificationFilter, otherNlh.notificationFilter) && ObjectUtils.nullSafeEquals(this.handback, otherNlh.handback) && ObjectUtils.nullSafeEquals(this.mappedObjectNames, otherNlh.mappedObjectNames);
    }
    
    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.notificationListener);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.notificationFilter);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.handback);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.mappedObjectNames);
        return hashCode;
    }
}
