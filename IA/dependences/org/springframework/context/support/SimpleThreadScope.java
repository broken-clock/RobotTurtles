// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import java.util.HashMap;
import org.springframework.core.NamedThreadLocal;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.config.Scope;

public class SimpleThreadScope implements Scope
{
    private static final Log logger;
    private final ThreadLocal<Map<String, Object>> threadScope;
    
    public SimpleThreadScope() {
        this.threadScope = new NamedThreadLocal<Map<String, Object>>("SimpleThreadScope") {
            @Override
            protected Map<String, Object> initialValue() {
                return new HashMap<String, Object>();
            }
        };
    }
    
    @Override
    public Object get(final String name, final ObjectFactory<?> objectFactory) {
        final Map<String, Object> scope = this.threadScope.get();
        Object object = scope.get(name);
        if (object == null) {
            object = objectFactory.getObject();
            scope.put(name, object);
        }
        return object;
    }
    
    @Override
    public Object remove(final String name) {
        final Map<String, Object> scope = this.threadScope.get();
        return scope.remove(name);
    }
    
    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
        SimpleThreadScope.logger.warn("SimpleThreadScope does not support destruction callbacks. Consider using RequestScope in a web environment.");
    }
    
    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }
    
    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
    
    static {
        logger = LogFactory.getLog(SimpleThreadScope.class);
    }
}
