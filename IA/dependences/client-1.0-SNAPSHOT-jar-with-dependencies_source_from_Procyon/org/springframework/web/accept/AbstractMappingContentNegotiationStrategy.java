// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import java.util.Collections;
import org.springframework.util.StringUtils;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.http.MediaType;
import java.util.Map;

public abstract class AbstractMappingContentNegotiationStrategy extends MappingMediaTypeFileExtensionResolver implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver
{
    public AbstractMappingContentNegotiationStrategy(final Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
    }
    
    @Override
    public List<MediaType> resolveMediaTypes(final NativeWebRequest webRequest) {
        final String key = this.getMediaTypeKey(webRequest);
        if (StringUtils.hasText(key)) {
            MediaType mediaType = this.lookupMediaType(key);
            if (mediaType != null) {
                this.handleMatch(key, mediaType);
                return Collections.singletonList(mediaType);
            }
            mediaType = this.handleNoMatch(webRequest, key);
            if (mediaType != null) {
                this.addMapping(key, mediaType);
                return Collections.singletonList(mediaType);
            }
        }
        return Collections.emptyList();
    }
    
    protected abstract String getMediaTypeKey(final NativeWebRequest p0);
    
    protected void handleMatch(final String mappingKey, final MediaType mediaType) {
    }
    
    protected MediaType handleNoMatch(final NativeWebRequest request, final String mappingKey) {
        return null;
    }
}
