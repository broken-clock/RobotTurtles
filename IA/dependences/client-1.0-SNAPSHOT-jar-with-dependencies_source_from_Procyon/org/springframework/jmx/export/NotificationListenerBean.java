// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

import org.springframework.util.Assert;
import javax.management.NotificationListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.support.NotificationListenerHolder;

public class NotificationListenerBean extends NotificationListenerHolder implements InitializingBean
{
    public NotificationListenerBean() {
    }
    
    public NotificationListenerBean(final NotificationListener notificationListener) {
        Assert.notNull(notificationListener, "NotificationListener must not be null");
        this.setNotificationListener(notificationListener);
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.getNotificationListener() == null) {
            throw new IllegalArgumentException("Property 'notificationListener' is required");
        }
    }
    
    void replaceObjectName(final Object originalName, final Object newName) {
        if (this.mappedObjectNames != null && this.mappedObjectNames.contains(originalName)) {
            this.mappedObjectNames.remove(originalName);
            this.mappedObjectNames.add(newName);
        }
    }
}
