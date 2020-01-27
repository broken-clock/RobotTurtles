// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter.feed;

import org.springframework.http.MediaType;
import com.sun.syndication.feed.atom.Feed;

public class AtomFeedHttpMessageConverter extends AbstractWireFeedHttpMessageConverter<Feed>
{
    public AtomFeedHttpMessageConverter() {
        super(new MediaType("application", "atom+xml"));
    }
    
    @Override
    protected boolean supports(final Class<?> clazz) {
        return Feed.class.isAssignableFrom(clazz);
    }
}
