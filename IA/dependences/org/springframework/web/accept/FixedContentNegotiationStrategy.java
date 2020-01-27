// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.http.MediaType;
import org.apache.commons.logging.Log;

public class FixedContentNegotiationStrategy implements ContentNegotiationStrategy
{
    private static final Log logger;
    private final MediaType defaultContentType;
    
    public FixedContentNegotiationStrategy(final MediaType defaultContentType) {
        this.defaultContentType = defaultContentType;
    }
    
    @Override
    public List<MediaType> resolveMediaTypes(final NativeWebRequest webRequest) {
        if (FixedContentNegotiationStrategy.logger.isDebugEnabled()) {
            FixedContentNegotiationStrategy.logger.debug("Requested media types is " + this.defaultContentType + " (based on default MediaType)");
        }
        return Collections.singletonList(this.defaultContentType);
    }
    
    static {
        logger = LogFactory.getLog(FixedContentNegotiationStrategy.class);
    }
}
