// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class SourceFilteringListener implements SmartApplicationListener
{
    private final Object source;
    private SmartApplicationListener delegate;
    
    public SourceFilteringListener(final Object source, final ApplicationListener<?> delegate) {
        this.source = source;
        this.delegate = ((delegate instanceof SmartApplicationListener) ? ((SmartApplicationListener)delegate) : new GenericApplicationListenerAdapter(delegate));
    }
    
    protected SourceFilteringListener(final Object source) {
        this.source = source;
    }
    
    @Override
    public void onApplicationEvent(final ApplicationEvent event) {
        if (event.getSource() == this.source) {
            this.onApplicationEventInternal(event);
        }
    }
    
    @Override
    public boolean supportsEventType(final Class<? extends ApplicationEvent> eventType) {
        return this.delegate == null || this.delegate.supportsEventType(eventType);
    }
    
    @Override
    public boolean supportsSourceType(final Class<?> sourceType) {
        return sourceType != null && sourceType.isInstance(this.source);
    }
    
    @Override
    public int getOrder() {
        return (this.delegate != null) ? this.delegate.getOrder() : Integer.MAX_VALUE;
    }
    
    protected void onApplicationEventInternal(final ApplicationEvent event) {
        if (this.delegate == null) {
            throw new IllegalStateException("Must specify a delegate object or override the onApplicationEventInternal method");
        }
        this.delegate.onApplicationEvent(event);
    }
}
