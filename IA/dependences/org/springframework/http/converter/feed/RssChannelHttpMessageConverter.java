// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.feed;

import org.springframework.http.MediaType;
import com.sun.syndication.feed.rss.Channel;

public class RssChannelHttpMessageConverter extends AbstractWireFeedHttpMessageConverter<Channel>
{
    public RssChannelHttpMessageConverter() {
        super(new MediaType("application", "rss+xml"));
    }
    
    @Override
    protected boolean supports(final Class<?> clazz) {
        return Channel.class.isAssignableFrom(clazz);
    }
}
