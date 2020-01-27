// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.util.Assert;
import org.springframework.http.MediaType;
import java.util.Map;
import javax.servlet.ServletContext;

public class ServletPathExtensionContentNegotiationStrategy extends PathExtensionContentNegotiationStrategy
{
    private final ServletContext servletContext;
    
    public ServletPathExtensionContentNegotiationStrategy(final ServletContext servletContext, final Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        Assert.notNull(servletContext, "ServletContext is required!");
        this.servletContext = servletContext;
    }
    
    public ServletPathExtensionContentNegotiationStrategy(final ServletContext servletContext) {
        this(servletContext, null);
    }
    
    @Override
    protected MediaType handleNoMatch(final NativeWebRequest webRequest, final String extension) {
        MediaType mediaType = null;
        if (this.servletContext != null) {
            final String mimeType = this.servletContext.getMimeType("file." + extension);
            if (StringUtils.hasText(mimeType)) {
                mediaType = MediaType.parseMediaType(mimeType);
            }
        }
        if (mediaType == null || MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
            final MediaType superMediaType = super.handleNoMatch(webRequest, extension);
            if (superMediaType != null) {
                mediaType = superMediaType;
            }
        }
        return mediaType;
    }
}
