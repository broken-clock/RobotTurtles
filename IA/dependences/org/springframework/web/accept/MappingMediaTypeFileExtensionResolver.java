// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.LinkedList;
import org.springframework.util.LinkedMultiValueMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import org.springframework.util.MultiValueMap;
import org.springframework.http.MediaType;
import java.util.concurrent.ConcurrentMap;

public class MappingMediaTypeFileExtensionResolver implements MediaTypeFileExtensionResolver
{
    private final ConcurrentMap<String, MediaType> mediaTypes;
    private final MultiValueMap<MediaType, String> fileExtensions;
    private final List<String> allFileExtensions;
    
    public MappingMediaTypeFileExtensionResolver(final Map<String, MediaType> mediaTypes) {
        this.mediaTypes = new ConcurrentHashMap<String, MediaType>(64);
        this.fileExtensions = new LinkedMultiValueMap<MediaType, String>();
        this.allFileExtensions = new LinkedList<String>();
        if (mediaTypes != null) {
            for (final Map.Entry<String, MediaType> entries : mediaTypes.entrySet()) {
                final String extension = entries.getKey().toLowerCase(Locale.ENGLISH);
                final MediaType mediaType = entries.getValue();
                this.addMapping(extension, mediaType);
            }
        }
    }
    
    @Override
    public List<String> resolveFileExtensions(final MediaType mediaType) {
        final List<String> fileExtensions = this.fileExtensions.get(mediaType);
        return (fileExtensions != null) ? fileExtensions : Collections.emptyList();
    }
    
    @Override
    public List<String> getAllFileExtensions() {
        return Collections.unmodifiableList((List<? extends String>)this.allFileExtensions);
    }
    
    protected MediaType lookupMediaType(final String extension) {
        return this.mediaTypes.get(extension);
    }
    
    protected void addMapping(final String extension, final MediaType mediaType) {
        final MediaType previous = this.mediaTypes.putIfAbsent(extension, mediaType);
        if (previous == null) {
            this.fileExtensions.add(mediaType, extension);
            this.allFileExtensions.add(extension);
        }
    }
}
