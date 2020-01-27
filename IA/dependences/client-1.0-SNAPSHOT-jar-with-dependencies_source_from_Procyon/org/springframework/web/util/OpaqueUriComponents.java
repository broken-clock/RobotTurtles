// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ObjectUtils;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.springframework.util.MultiValueMap;

final class OpaqueUriComponents extends UriComponents
{
    private static final MultiValueMap<String, String> QUERY_PARAMS_NONE;
    private final String ssp;
    
    OpaqueUriComponents(final String scheme, final String schemeSpecificPart, final String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }
    
    @Override
    public String getSchemeSpecificPart() {
        return this.ssp;
    }
    
    @Override
    public String getUserInfo() {
        return null;
    }
    
    @Override
    public String getHost() {
        return null;
    }
    
    @Override
    public int getPort() {
        return -1;
    }
    
    @Override
    public String getPath() {
        return null;
    }
    
    @Override
    public List<String> getPathSegments() {
        return Collections.emptyList();
    }
    
    @Override
    public String getQuery() {
        return null;
    }
    
    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return OpaqueUriComponents.QUERY_PARAMS_NONE;
    }
    
    @Override
    public UriComponents encode(final String encoding) throws UnsupportedEncodingException {
        return this;
    }
    
    protected UriComponents expandInternal(final UriTemplateVariables uriVariables) {
        final String expandedScheme = UriComponents.expandUriComponent(this.getScheme(), uriVariables);
        final String expandedSSp = UriComponents.expandUriComponent(this.ssp, uriVariables);
        final String expandedFragment = UriComponents.expandUriComponent(this.getFragment(), uriVariables);
        return new OpaqueUriComponents(expandedScheme, expandedSSp, expandedFragment);
    }
    
    @Override
    public UriComponents normalize() {
        return this;
    }
    
    @Override
    public String toUriString() {
        final StringBuilder uriBuilder = new StringBuilder();
        if (this.getScheme() != null) {
            uriBuilder.append(this.getScheme());
            uriBuilder.append(':');
        }
        if (this.ssp != null) {
            uriBuilder.append(this.ssp);
        }
        if (this.getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(this.getFragment());
        }
        return uriBuilder.toString();
    }
    
    @Override
    public URI toUri() {
        try {
            return new URI(this.getScheme(), this.ssp, this.getFragment());
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpaqueUriComponents)) {
            return false;
        }
        final OpaqueUriComponents other = (OpaqueUriComponents)obj;
        return ObjectUtils.nullSafeEquals(this.getScheme(), other.getScheme()) && ObjectUtils.nullSafeEquals(this.ssp, other.ssp) && ObjectUtils.nullSafeEquals(this.getFragment(), other.getFragment());
    }
    
    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.getScheme());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.ssp);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.getFragment());
        return result;
    }
    
    static {
        QUERY_PARAMS_NONE = new LinkedMultiValueMap<String, String>(0);
    }
}
