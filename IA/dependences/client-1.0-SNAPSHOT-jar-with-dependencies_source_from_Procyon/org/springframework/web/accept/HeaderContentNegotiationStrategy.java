// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import java.util.Collections;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.util.StringUtils;
import org.springframework.http.MediaType;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;

public class HeaderContentNegotiationStrategy implements ContentNegotiationStrategy
{
    private static final String ACCEPT_HEADER = "Accept";
    
    @Override
    public List<MediaType> resolveMediaTypes(final NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        final String acceptHeader = webRequest.getHeader("Accept");
        try {
            if (StringUtils.hasText(acceptHeader)) {
                final List<MediaType> mediaTypes = MediaType.parseMediaTypes(acceptHeader);
                MediaType.sortBySpecificityAndQuality(mediaTypes);
                return mediaTypes;
            }
        }
        catch (InvalidMediaTypeException ex) {
            throw new HttpMediaTypeNotAcceptableException("Could not parse accept header [" + acceptHeader + "]: " + ex.getMessage());
        }
        return Collections.emptyList();
    }
}
