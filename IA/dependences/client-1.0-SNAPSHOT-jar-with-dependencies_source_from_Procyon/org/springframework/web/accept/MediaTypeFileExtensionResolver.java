// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import java.util.List;
import org.springframework.http.MediaType;

public interface MediaTypeFileExtensionResolver
{
    List<String> resolveFileExtensions(final MediaType p0);
    
    List<String> getAllFileExtensions();
}
