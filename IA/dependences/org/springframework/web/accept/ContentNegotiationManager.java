// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.accept;

import org.springframework.web.HttpMediaTypeNotAcceptableException;
import java.util.Collections;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.util.Assert;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.Set;
import org.springframework.http.MediaType;
import java.util.List;

public class ContentNegotiationManager implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver
{
    private static final List<MediaType> MEDIA_TYPE_ALL;
    private final List<ContentNegotiationStrategy> contentNegotiationStrategies;
    private final Set<MediaTypeFileExtensionResolver> fileExtensionResolvers;
    
    public ContentNegotiationManager(final ContentNegotiationStrategy... strategies) {
        this.contentNegotiationStrategies = new ArrayList<ContentNegotiationStrategy>();
        this.fileExtensionResolvers = new LinkedHashSet<MediaTypeFileExtensionResolver>();
        Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
        this.contentNegotiationStrategies.addAll(Arrays.asList(strategies));
        for (final ContentNegotiationStrategy strategy : this.contentNegotiationStrategies) {
            if (strategy instanceof MediaTypeFileExtensionResolver) {
                this.fileExtensionResolvers.add((MediaTypeFileExtensionResolver)strategy);
            }
        }
    }
    
    public ContentNegotiationManager(final Collection<ContentNegotiationStrategy> strategies) {
        this.contentNegotiationStrategies = new ArrayList<ContentNegotiationStrategy>();
        this.fileExtensionResolvers = new LinkedHashSet<MediaTypeFileExtensionResolver>();
        Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
        this.contentNegotiationStrategies.addAll(strategies);
        for (final ContentNegotiationStrategy strategy : this.contentNegotiationStrategies) {
            if (strategy instanceof MediaTypeFileExtensionResolver) {
                this.fileExtensionResolvers.add((MediaTypeFileExtensionResolver)strategy);
            }
        }
    }
    
    public ContentNegotiationManager() {
        this(new ContentNegotiationStrategy[] { new HeaderContentNegotiationStrategy() });
    }
    
    public void addFileExtensionResolvers(final MediaTypeFileExtensionResolver... resolvers) {
        this.fileExtensionResolvers.addAll(Arrays.asList(resolvers));
    }
    
    @Override
    public List<MediaType> resolveMediaTypes(final NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        for (final ContentNegotiationStrategy strategy : this.contentNegotiationStrategies) {
            final List<MediaType> mediaTypes = strategy.resolveMediaTypes(webRequest);
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.equals(ContentNegotiationManager.MEDIA_TYPE_ALL)) {
                    continue;
                }
                return mediaTypes;
            }
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<String> resolveFileExtensions(final MediaType mediaType) {
        final Set<String> result = new LinkedHashSet<String>();
        for (final MediaTypeFileExtensionResolver resolver : this.fileExtensionResolvers) {
            result.addAll(resolver.resolveFileExtensions(mediaType));
        }
        return new ArrayList<String>(result);
    }
    
    @Override
    public List<String> getAllFileExtensions() {
        final Set<String> result = new LinkedHashSet<String>();
        for (final MediaTypeFileExtensionResolver resolver : this.fileExtensionResolvers) {
            result.addAll(resolver.getAllFileExtensions());
        }
        return new ArrayList<String>(result);
    }
    
    static {
        MEDIA_TYPE_ALL = Arrays.asList(MediaType.ALL);
    }
}
