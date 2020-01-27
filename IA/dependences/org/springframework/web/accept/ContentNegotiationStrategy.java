// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.http.MediaType;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;

public interface ContentNegotiationStrategy
{
    List<MediaType> resolveMediaTypes(final NativeWebRequest p0) throws HttpMediaTypeNotAcceptableException;
}
