// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.util.ObjectUtils;
import java.net.URISyntaxException;
import java.net.URI;
import org.springframework.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.springframework.util.Assert;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

final class HierarchicalUriComponents extends UriComponents
{
    private static final char PATH_DELIMITER = '/';
    private final String userInfo;
    private final String host;
    private final int port;
    private final PathComponent path;
    private final MultiValueMap<String, String> queryParams;
    private final boolean encoded;
    static final PathComponent NULL_PATH_COMPONENT;
    
    HierarchicalUriComponents(final String scheme, final String userInfo, final String host, final int port, final PathComponent path, final MultiValueMap<String, String> queryParams, final String fragment, final boolean encoded, final boolean verify) {
        super(scheme, fragment);
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = ((path != null) ? path : HierarchicalUriComponents.NULL_PATH_COMPONENT);
        this.queryParams = CollectionUtils.unmodifiableMultiValueMap((MultiValueMap<? extends String, ? extends String>)((queryParams != null) ? queryParams : new LinkedMultiValueMap<String, String>(0)));
        this.encoded = encoded;
        if (verify) {
            this.verify();
        }
    }
    
    @Override
    public String getSchemeSpecificPart() {
        return null;
    }
    
    @Override
    public String getUserInfo() {
        return this.userInfo;
    }
    
    @Override
    public String getHost() {
        return this.host;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public String getPath() {
        return this.path.getPath();
    }
    
    @Override
    public List<String> getPathSegments() {
        return this.path.getPathSegments();
    }
    
    @Override
    public String getQuery() {
        if (!this.queryParams.isEmpty()) {
            final StringBuilder queryBuilder = new StringBuilder();
            for (final Map.Entry<String, List<String>> entry : this.queryParams.entrySet()) {
                final String name = entry.getKey();
                final List<String> values = entry.getValue();
                if (CollectionUtils.isEmpty(values)) {
                    if (queryBuilder.length() != 0) {
                        queryBuilder.append('&');
                    }
                    queryBuilder.append(name);
                }
                else {
                    for (final Object value : values) {
                        if (queryBuilder.length() != 0) {
                            queryBuilder.append('&');
                        }
                        queryBuilder.append(name);
                        if (value != null) {
                            queryBuilder.append('=');
                            queryBuilder.append(value.toString());
                        }
                    }
                }
            }
            return queryBuilder.toString();
        }
        return null;
    }
    
    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return this.queryParams;
    }
    
    @Override
    public HierarchicalUriComponents encode(final String encoding) throws UnsupportedEncodingException {
        Assert.hasLength(encoding, "'encoding' must not be empty");
        if (this.encoded) {
            return this;
        }
        final String encodedScheme = encodeUriComponent(this.getScheme(), encoding, Type.SCHEME);
        final String encodedUserInfo = encodeUriComponent(this.userInfo, encoding, Type.USER_INFO);
        final String encodedHost = encodeUriComponent(this.host, encoding, this.getHostType());
        final PathComponent encodedPath = this.path.encode(encoding);
        final MultiValueMap<String, String> encodedQueryParams = new LinkedMultiValueMap<String, String>(this.queryParams.size());
        for (final Map.Entry<String, List<String>> entry : this.queryParams.entrySet()) {
            final String encodedName = encodeUriComponent(entry.getKey(), encoding, Type.QUERY_PARAM);
            final List<String> encodedValues = new ArrayList<String>(entry.getValue().size());
            for (final String value : entry.getValue()) {
                final String encodedValue = encodeUriComponent(value, encoding, Type.QUERY_PARAM);
                encodedValues.add(encodedValue);
            }
            encodedQueryParams.put(encodedName, encodedValues);
        }
        final String encodedFragment = encodeUriComponent(this.getFragment(), encoding, Type.FRAGMENT);
        return new HierarchicalUriComponents(encodedScheme, encodedUserInfo, encodedHost, this.port, encodedPath, encodedQueryParams, encodedFragment, true, false);
    }
    
    static String encodeUriComponent(final String source, final String encoding, final Type type) throws UnsupportedEncodingException {
        if (source == null) {
            return null;
        }
        Assert.hasLength(encoding, "'encoding' must not be empty");
        final byte[] bytes = encodeBytes(source.getBytes(encoding), type);
        return new String(bytes, "US-ASCII");
    }
    
    private static byte[] encodeBytes(final byte[] source, final Type type) {
        Assert.notNull(source, "'source' must not be null");
        Assert.notNull(type, "'type' must not be null");
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length);
        for (byte b : source) {
            if (b < 0) {
                b += 256;
            }
            if (type.isAllowed(b)) {
                bos.write(b);
            }
            else {
                bos.write(37);
                final char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
                final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                bos.write(hex1);
                bos.write(hex2);
            }
        }
        return bos.toByteArray();
    }
    
    private Type getHostType() {
        return (this.host != null && this.host.startsWith("[")) ? Type.HOST_IPV6 : Type.HOST_IPV4;
    }
    
    private void verify() {
        if (!this.encoded) {
            return;
        }
        verifyUriComponent(this.getScheme(), Type.SCHEME);
        verifyUriComponent(this.userInfo, Type.USER_INFO);
        verifyUriComponent(this.host, this.getHostType());
        this.path.verify();
        for (final Map.Entry<String, List<String>> entry : this.queryParams.entrySet()) {
            verifyUriComponent(entry.getKey(), Type.QUERY_PARAM);
            for (final String value : entry.getValue()) {
                verifyUriComponent(value, Type.QUERY_PARAM);
            }
        }
        verifyUriComponent(this.getFragment(), Type.FRAGMENT);
    }
    
    private static void verifyUriComponent(final String source, final Type type) {
        if (source == null) {
            return;
        }
        for (int length = source.length(), i = 0; i < length; ++i) {
            final char ch = source.charAt(i);
            if (ch == '%') {
                if (i + 2 >= length) {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
                final char hex1 = source.charAt(i + 1);
                final char hex2 = source.charAt(i + 2);
                final int u = Character.digit(hex1, 16);
                final int l = Character.digit(hex2, 16);
                if (u == -1 || l == -1) {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
                i += 2;
            }
            else if (!type.isAllowed(ch)) {
                throw new IllegalArgumentException("Invalid character '" + ch + "' for " + type.name() + " in \"" + source + "\"");
            }
        }
    }
    
    protected HierarchicalUriComponents expandInternal(final UriTemplateVariables uriVariables) {
        Assert.state(!this.encoded, "Cannot expand an already encoded UriComponents object");
        final String expandedScheme = UriComponents.expandUriComponent(this.getScheme(), uriVariables);
        final String expandedUserInfo = UriComponents.expandUriComponent(this.userInfo, uriVariables);
        final String expandedHost = UriComponents.expandUriComponent(this.host, uriVariables);
        final PathComponent expandedPath = this.path.expand(uriVariables);
        final MultiValueMap<String, String> expandedQueryParams = new LinkedMultiValueMap<String, String>(this.queryParams.size());
        for (final Map.Entry<String, List<String>> entry : this.queryParams.entrySet()) {
            final String expandedName = UriComponents.expandUriComponent(entry.getKey(), uriVariables);
            final List<String> expandedValues = new ArrayList<String>(entry.getValue().size());
            for (final String value : entry.getValue()) {
                final String expandedValue = UriComponents.expandUriComponent(value, uriVariables);
                expandedValues.add(expandedValue);
            }
            expandedQueryParams.put(expandedName, expandedValues);
        }
        final String expandedFragment = UriComponents.expandUriComponent(this.getFragment(), uriVariables);
        return new HierarchicalUriComponents(expandedScheme, expandedUserInfo, expandedHost, this.port, expandedPath, expandedQueryParams, expandedFragment, false, false);
    }
    
    @Override
    public UriComponents normalize() {
        final String normalizedPath = StringUtils.cleanPath(this.getPath());
        return new HierarchicalUriComponents(this.getScheme(), this.userInfo, this.host, this.port, new FullPathComponent(normalizedPath), this.queryParams, this.getFragment(), this.encoded, false);
    }
    
    @Override
    public String toUriString() {
        final StringBuilder uriBuilder = new StringBuilder();
        if (this.getScheme() != null) {
            uriBuilder.append(this.getScheme());
            uriBuilder.append(':');
        }
        if (this.userInfo != null || this.host != null) {
            uriBuilder.append("//");
            if (this.userInfo != null) {
                uriBuilder.append(this.userInfo);
                uriBuilder.append('@');
            }
            if (this.host != null) {
                uriBuilder.append(this.host);
            }
            if (this.port != -1) {
                uriBuilder.append(':');
                uriBuilder.append(this.port);
            }
        }
        final String path = this.getPath();
        if (StringUtils.hasLength(path)) {
            if (uriBuilder.length() != 0 && path.charAt(0) != '/') {
                uriBuilder.append('/');
            }
            uriBuilder.append(path);
        }
        final String query = this.getQuery();
        if (query != null) {
            uriBuilder.append('?');
            uriBuilder.append(query);
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
            if (this.encoded) {
                return new URI(this.toString());
            }
            String path = this.getPath();
            if (StringUtils.hasLength(path) && path.charAt(0) != '/' && (this.getScheme() != null || this.getUserInfo() != null || this.getHost() != null || this.getPort() != -1)) {
                path = '/' + path;
            }
            return new URI(this.getScheme(), this.getUserInfo(), this.getHost(), this.getPort(), path, this.getQuery(), this.getFragment());
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
        if (!(obj instanceof HierarchicalUriComponents)) {
            return false;
        }
        final HierarchicalUriComponents other = (HierarchicalUriComponents)obj;
        return ObjectUtils.nullSafeEquals(this.getScheme(), other.getScheme()) && ObjectUtils.nullSafeEquals(this.getUserInfo(), other.getUserInfo()) && ObjectUtils.nullSafeEquals(this.getHost(), other.getHost()) && this.getPort() == other.getPort() && this.path.equals(other.path) && this.queryParams.equals(other.queryParams) && ObjectUtils.nullSafeEquals(this.getFragment(), other.getFragment());
    }
    
    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.getScheme());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.userInfo);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.host);
        result = 31 * result + this.port;
        result = 31 * result + this.path.hashCode();
        result = 31 * result + this.queryParams.hashCode();
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.getFragment());
        return result;
    }
    
    static {
        NULL_PATH_COMPONENT = new PathComponent() {
            @Override
            public String getPath() {
                return null;
            }
            
            @Override
            public List<String> getPathSegments() {
                return Collections.emptyList();
            }
            
            @Override
            public PathComponent encode(final String encoding) throws UnsupportedEncodingException {
                return this;
            }
            
            @Override
            public void verify() {
            }
            
            @Override
            public PathComponent expand(final UriTemplateVariables uriVariables) {
                return this;
            }
            
            @Override
            public boolean equals(final Object obj) {
                return this == obj;
            }
            
            @Override
            public int hashCode() {
                return 42;
            }
        };
    }
    
    enum Type
    {
        SCHEME {
            @Override
            public boolean isAllowed(final int c) {
                return this.isAlpha(c) || this.isDigit(c) || 43 == c || 45 == c || 46 == c;
            }
        }, 
        AUTHORITY {
            @Override
            public boolean isAllowed(final int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c) || 58 == c || 64 == c;
            }
        }, 
        USER_INFO {
            @Override
            public boolean isAllowed(final int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c) || 58 == c;
            }
        }, 
        HOST_IPV4 {
            @Override
            public boolean isAllowed(final int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c);
            }
        }, 
        HOST_IPV6 {
            @Override
            public boolean isAllowed(final int c) {
                return this.isUnreserved(c) || this.isSubDelimiter(c) || 91 == c || 93 == c || 58 == c;
            }
        }, 
        PORT {
            @Override
            public boolean isAllowed(final int c) {
                return this.isDigit(c);
            }
        }, 
        PATH {
            @Override
            public boolean isAllowed(final int c) {
                return this.isPchar(c) || 47 == c;
            }
        }, 
        PATH_SEGMENT {
            @Override
            public boolean isAllowed(final int c) {
                return this.isPchar(c);
            }
        }, 
        QUERY {
            @Override
            public boolean isAllowed(final int c) {
                return this.isPchar(c) || 47 == c || 63 == c;
            }
        }, 
        QUERY_PARAM {
            @Override
            public boolean isAllowed(final int c) {
                return 61 != c && 43 != c && 38 != c && (this.isPchar(c) || 47 == c || 63 == c);
            }
        }, 
        FRAGMENT {
            @Override
            public boolean isAllowed(final int c) {
                return this.isPchar(c) || 47 == c || 63 == c;
            }
        };
        
        public abstract boolean isAllowed(final int p0);
        
        protected boolean isAlpha(final int c) {
            return (c >= 97 && c <= 122) || (c >= 65 && c <= 90);
        }
        
        protected boolean isDigit(final int c) {
            return c >= 48 && c <= 57;
        }
        
        protected boolean isGenericDelimiter(final int c) {
            return 58 == c || 47 == c || 63 == c || 35 == c || 91 == c || 93 == c || 64 == c;
        }
        
        protected boolean isSubDelimiter(final int c) {
            return 33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c;
        }
        
        protected boolean isReserved(final char c) {
            return this.isGenericDelimiter(c) || this.isReserved(c);
        }
        
        protected boolean isUnreserved(final int c) {
            return this.isAlpha(c) || this.isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c;
        }
        
        protected boolean isPchar(final int c) {
            return this.isUnreserved(c) || this.isSubDelimiter(c) || 58 == c || 64 == c;
        }
    }
    
    static final class FullPathComponent implements PathComponent
    {
        private final String path;
        
        public FullPathComponent(final String path) {
            this.path = path;
        }
        
        @Override
        public String getPath() {
            return this.path;
        }
        
        @Override
        public List<String> getPathSegments() {
            final String delimiter = new String(new char[] { '/' });
            final String[] pathSegments = StringUtils.tokenizeToStringArray(this.path, delimiter);
            return Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])pathSegments));
        }
        
        @Override
        public PathComponent encode(final String encoding) throws UnsupportedEncodingException {
            final String encodedPath = HierarchicalUriComponents.encodeUriComponent(this.getPath(), encoding, Type.PATH);
            return new FullPathComponent(encodedPath);
        }
        
        @Override
        public void verify() {
            verifyUriComponent(this.path, Type.PATH);
        }
        
        @Override
        public PathComponent expand(final UriTemplateVariables uriVariables) {
            final String expandedPath = UriComponents.expandUriComponent(this.getPath(), uriVariables);
            return new FullPathComponent(expandedPath);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof FullPathComponent && this.getPath().equals(((FullPathComponent)obj).getPath()));
        }
        
        @Override
        public int hashCode() {
            return this.getPath().hashCode();
        }
    }
    
    static final class PathSegmentComponent implements PathComponent
    {
        private final List<String> pathSegments;
        
        public PathSegmentComponent(final List<String> pathSegments) {
            this.pathSegments = Collections.unmodifiableList((List<? extends String>)pathSegments);
        }
        
        @Override
        public String getPath() {
            final StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append('/');
            final Iterator<String> iterator = this.pathSegments.iterator();
            while (iterator.hasNext()) {
                final String pathSegment = iterator.next();
                pathBuilder.append(pathSegment);
                if (iterator.hasNext()) {
                    pathBuilder.append('/');
                }
            }
            return pathBuilder.toString();
        }
        
        @Override
        public List<String> getPathSegments() {
            return this.pathSegments;
        }
        
        @Override
        public PathComponent encode(final String encoding) throws UnsupportedEncodingException {
            final List<String> pathSegments = this.getPathSegments();
            final List<String> encodedPathSegments = new ArrayList<String>(pathSegments.size());
            for (final String pathSegment : pathSegments) {
                final String encodedPathSegment = HierarchicalUriComponents.encodeUriComponent(pathSegment, encoding, Type.PATH_SEGMENT);
                encodedPathSegments.add(encodedPathSegment);
            }
            return new PathSegmentComponent(encodedPathSegments);
        }
        
        @Override
        public void verify() {
            for (final String pathSegment : this.getPathSegments()) {
                verifyUriComponent(pathSegment, Type.PATH_SEGMENT);
            }
        }
        
        @Override
        public PathComponent expand(final UriTemplateVariables uriVariables) {
            final List<String> pathSegments = this.getPathSegments();
            final List<String> expandedPathSegments = new ArrayList<String>(pathSegments.size());
            for (final String pathSegment : pathSegments) {
                final String expandedPathSegment = UriComponents.expandUriComponent(pathSegment, uriVariables);
                expandedPathSegments.add(expandedPathSegment);
            }
            return new PathSegmentComponent(expandedPathSegments);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof PathSegmentComponent && this.getPathSegments().equals(((PathSegmentComponent)obj).getPathSegments()));
        }
        
        @Override
        public int hashCode() {
            return this.getPathSegments().hashCode();
        }
    }
    
    static final class PathComponentComposite implements PathComponent
    {
        private final List<PathComponent> pathComponents;
        
        public PathComponentComposite(final List<PathComponent> pathComponents) {
            this.pathComponents = pathComponents;
        }
        
        @Override
        public String getPath() {
            final StringBuilder pathBuilder = new StringBuilder();
            for (final PathComponent pathComponent : this.pathComponents) {
                pathBuilder.append(pathComponent.getPath());
            }
            return pathBuilder.toString();
        }
        
        @Override
        public List<String> getPathSegments() {
            final List<String> result = new ArrayList<String>();
            for (final PathComponent pathComponent : this.pathComponents) {
                result.addAll(pathComponent.getPathSegments());
            }
            return result;
        }
        
        @Override
        public PathComponent encode(final String encoding) throws UnsupportedEncodingException {
            final List<PathComponent> encodedComponents = new ArrayList<PathComponent>(this.pathComponents.size());
            for (final PathComponent pathComponent : this.pathComponents) {
                encodedComponents.add(pathComponent.encode(encoding));
            }
            return new PathComponentComposite(encodedComponents);
        }
        
        @Override
        public void verify() {
            for (final PathComponent pathComponent : this.pathComponents) {
                pathComponent.verify();
            }
        }
        
        @Override
        public PathComponent expand(final UriTemplateVariables uriVariables) {
            final List<PathComponent> expandedComponents = new ArrayList<PathComponent>(this.pathComponents.size());
            for (final PathComponent pathComponent : this.pathComponents) {
                expandedComponents.add(pathComponent.expand(uriVariables));
            }
            return new PathComponentComposite(expandedComponents);
        }
    }
    
    interface PathComponent extends Serializable
    {
        String getPath();
        
        List<String> getPathSegments();
        
        PathComponent encode(final String p0) throws UnsupportedEncodingException;
        
        void verify();
        
        PathComponent expand(final UriTemplateVariables p0);
    }
}
