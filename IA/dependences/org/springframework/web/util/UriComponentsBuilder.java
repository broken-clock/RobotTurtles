// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import org.springframework.util.ObjectUtils;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;
import java.net.URI;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.regex.Pattern;

public class UriComponentsBuilder
{
    private static final Pattern QUERY_PARAM_PATTERN;
    private static final String SCHEME_PATTERN = "([^:/?#]+):";
    private static final String HTTP_PATTERN = "(?i)(http|https):";
    private static final String USERINFO_PATTERN = "([^@/]*)";
    private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";
    private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]";
    private static final String HOST_PATTERN = "(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)";
    private static final String PORT_PATTERN = "(\\d*)";
    private static final String PATH_PATTERN = "([^?#]*)";
    private static final String QUERY_PATTERN = "([^#]*)";
    private static final String LAST_PATTERN = "(.*)";
    private static final Pattern URI_PATTERN;
    private static final Pattern HTTP_URL_PATTERN;
    private String scheme;
    private String ssp;
    private String userInfo;
    private String host;
    private int port;
    private CompositePathComponentBuilder pathBuilder;
    private final MultiValueMap<String, String> queryParams;
    private String fragment;
    
    protected UriComponentsBuilder() {
        this.port = -1;
        this.pathBuilder = new CompositePathComponentBuilder();
        this.queryParams = new LinkedMultiValueMap<String, String>();
    }
    
    public static UriComponentsBuilder newInstance() {
        return new UriComponentsBuilder();
    }
    
    public static UriComponentsBuilder fromPath(final String path) {
        final UriComponentsBuilder builder = new UriComponentsBuilder();
        builder.path(path);
        return builder;
    }
    
    public static UriComponentsBuilder fromUri(final URI uri) {
        final UriComponentsBuilder builder = new UriComponentsBuilder();
        builder.uri(uri);
        return builder;
    }
    
    public static UriComponentsBuilder fromUriString(final String uri) {
        Assert.hasLength(uri, "'uri' must not be empty");
        final Matcher m = UriComponentsBuilder.URI_PATTERN.matcher(uri);
        if (m.matches()) {
            final UriComponentsBuilder builder = new UriComponentsBuilder();
            final String scheme = m.group(2);
            final String userInfo = m.group(5);
            final String host = m.group(6);
            final String port = m.group(8);
            final String path = m.group(9);
            final String query = m.group(11);
            final String fragment = m.group(13);
            boolean opaque = false;
            if (StringUtils.hasLength(scheme)) {
                final String s = uri.substring(scheme.length());
                if (!s.startsWith(":/")) {
                    opaque = true;
                }
            }
            builder.scheme(scheme);
            if (opaque) {
                String ssp = uri.substring(scheme.length()).substring(1);
                if (StringUtils.hasLength(fragment)) {
                    ssp = ssp.substring(0, ssp.length() - (fragment.length() + 1));
                }
                builder.schemeSpecificPart(ssp);
            }
            else {
                builder.userInfo(userInfo);
                builder.host(host);
                if (StringUtils.hasLength(port)) {
                    builder.port(Integer.parseInt(port));
                }
                builder.path(path);
                builder.query(query);
            }
            if (StringUtils.hasText(fragment)) {
                builder.fragment(fragment);
            }
            return builder;
        }
        throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
    }
    
    public static UriComponentsBuilder fromHttpUrl(final String httpUrl) {
        Assert.notNull(httpUrl, "'httpUrl' must not be null");
        final Matcher m = UriComponentsBuilder.HTTP_URL_PATTERN.matcher(httpUrl);
        if (!m.matches()) {
            throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
        }
        final UriComponentsBuilder builder = new UriComponentsBuilder();
        final String scheme = m.group(1);
        builder.scheme((scheme != null) ? scheme.toLowerCase() : scheme);
        builder.userInfo(m.group(4));
        final String host = m.group(5);
        if (StringUtils.hasLength(scheme) && !StringUtils.hasLength(host)) {
            throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
        }
        builder.host(host);
        final String port = m.group(7);
        if (StringUtils.hasLength(port)) {
            builder.port(Integer.parseInt(port));
        }
        builder.path(m.group(8));
        builder.query(m.group(10));
        return builder;
    }
    
    public UriComponents build() {
        return this.build(false);
    }
    
    public UriComponents build(final boolean encoded) {
        if (this.ssp != null) {
            return new OpaqueUriComponents(this.scheme, this.ssp, this.fragment);
        }
        return new HierarchicalUriComponents(this.scheme, this.userInfo, this.host, this.port, this.pathBuilder.build(), this.queryParams, this.fragment, encoded, true);
    }
    
    public UriComponents buildAndExpand(final Map<String, ?> uriVariables) {
        return this.build(false).expand(uriVariables);
    }
    
    public UriComponents buildAndExpand(final Object... uriVariableValues) {
        return this.build(false).expand(uriVariableValues);
    }
    
    public UriComponentsBuilder uri(final URI uri) {
        Assert.notNull(uri, "'uri' must not be null");
        this.scheme = uri.getScheme();
        if (uri.isOpaque()) {
            this.ssp = uri.getRawSchemeSpecificPart();
            this.resetHierarchicalComponents();
        }
        else {
            if (uri.getRawUserInfo() != null) {
                this.userInfo = uri.getRawUserInfo();
            }
            if (uri.getHost() != null) {
                this.host = uri.getHost();
            }
            if (uri.getPort() != -1) {
                this.port = uri.getPort();
            }
            if (StringUtils.hasLength(uri.getRawPath())) {
                this.pathBuilder = new CompositePathComponentBuilder(uri.getRawPath());
            }
            if (StringUtils.hasLength(uri.getRawQuery())) {
                this.queryParams.clear();
                this.query(uri.getRawQuery());
            }
            this.resetSchemeSpecificPart();
        }
        if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
        }
        return this;
    }
    
    private void resetHierarchicalComponents() {
        this.userInfo = null;
        this.host = null;
        this.port = -1;
        this.pathBuilder = new CompositePathComponentBuilder();
        this.queryParams.clear();
    }
    
    private void resetSchemeSpecificPart() {
        this.ssp = null;
    }
    
    public UriComponentsBuilder scheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }
    
    public UriComponentsBuilder uriComponents(final UriComponents uriComponents) {
        Assert.notNull(uriComponents, "'uriComponents' must not be null");
        this.scheme = uriComponents.getScheme();
        if (uriComponents instanceof OpaqueUriComponents) {
            this.ssp = uriComponents.getSchemeSpecificPart();
            this.resetHierarchicalComponents();
        }
        else {
            if (uriComponents.getUserInfo() != null) {
                this.userInfo = uriComponents.getUserInfo();
            }
            if (uriComponents.getHost() != null) {
                this.host = uriComponents.getHost();
            }
            if (uriComponents.getPort() != -1) {
                this.port = uriComponents.getPort();
            }
            if (StringUtils.hasLength(uriComponents.getPath())) {
                final List<String> segments = uriComponents.getPathSegments();
                if (segments.isEmpty()) {
                    this.pathBuilder.addPath(uriComponents.getPath());
                }
                else {
                    this.pathBuilder.addPathSegments((String[])segments.toArray(new String[segments.size()]));
                }
            }
            if (!uriComponents.getQueryParams().isEmpty()) {
                this.queryParams.clear();
                this.queryParams.putAll((Map<?, ?>)uriComponents.getQueryParams());
            }
            this.resetSchemeSpecificPart();
        }
        if (uriComponents.getFragment() != null) {
            this.fragment = uriComponents.getFragment();
        }
        return this;
    }
    
    public UriComponentsBuilder schemeSpecificPart(final String ssp) {
        this.ssp = ssp;
        this.resetHierarchicalComponents();
        return this;
    }
    
    public UriComponentsBuilder userInfo(final String userInfo) {
        this.userInfo = userInfo;
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder host(final String host) {
        this.host = host;
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder port(final int port) {
        Assert.isTrue(port >= -1, "'port' must not be < -1");
        this.port = port;
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder path(final String path) {
        this.pathBuilder.addPath(path);
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder replacePath(final String path) {
        this.pathBuilder = new CompositePathComponentBuilder(path);
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder pathSegment(final String... pathSegments) throws IllegalArgumentException {
        Assert.notNull(pathSegments, "'segments' must not be null");
        this.pathBuilder.addPathSegments(pathSegments);
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder query(final String query) {
        if (query != null) {
            final Matcher m = UriComponentsBuilder.QUERY_PARAM_PATTERN.matcher(query);
            while (m.find()) {
                final String name = m.group(1);
                final String eq = m.group(2);
                final String value = m.group(3);
                this.queryParam(name, (value != null) ? value : (StringUtils.hasLength(eq) ? "" : null));
            }
        }
        else {
            this.queryParams.clear();
        }
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder replaceQuery(final String query) {
        this.queryParams.clear();
        this.query(query);
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder queryParam(final String name, final Object... values) {
        Assert.notNull(name, "'name' must not be null");
        if (!ObjectUtils.isEmpty(values)) {
            for (final Object value : values) {
                final String valueAsString = (value != null) ? value.toString() : null;
                this.queryParams.add(name, valueAsString);
            }
        }
        else {
            this.queryParams.add(name, null);
        }
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder queryParams(final MultiValueMap<String, String> params) {
        Assert.notNull(params, "'params' must not be null");
        this.queryParams.putAll((Map<?, ?>)params);
        return this;
    }
    
    public UriComponentsBuilder replaceQueryParam(final String name, final Object... values) {
        Assert.notNull(name, "'name' must not be null");
        this.queryParams.remove(name);
        if (!ObjectUtils.isEmpty(values)) {
            this.queryParam(name, values);
        }
        this.resetSchemeSpecificPart();
        return this;
    }
    
    public UriComponentsBuilder fragment(final String fragment) {
        if (fragment != null) {
            Assert.hasLength(fragment, "'fragment' must not be empty");
            this.fragment = fragment;
        }
        else {
            this.fragment = null;
        }
        return this;
    }
    
    static {
        QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
        URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//(([^@/]*)@)?(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)(:(\\d*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
        HTTP_URL_PATTERN = Pattern.compile("^(?i)(http|https):(//(([^@/]*)@)?(\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]|[^\\[/?#:]*)(:(\\d*))?)?([^?#]*)(\\?(.*))?");
    }
    
    private static class CompositePathComponentBuilder implements PathComponentBuilder
    {
        private final LinkedList<PathComponentBuilder> componentBuilders;
        
        public CompositePathComponentBuilder() {
            this.componentBuilders = new LinkedList<PathComponentBuilder>();
        }
        
        public CompositePathComponentBuilder(final String path) {
            this.componentBuilders = new LinkedList<PathComponentBuilder>();
            this.addPath(path);
        }
        
        public void addPathSegments(final String... pathSegments) {
            if (!ObjectUtils.isEmpty(pathSegments)) {
                PathSegmentComponentBuilder psBuilder = this.getLastBuilder(PathSegmentComponentBuilder.class);
                final FullPathComponentBuilder fpBuilder = this.getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder == null) {
                    psBuilder = new PathSegmentComponentBuilder();
                    this.componentBuilders.add(psBuilder);
                    if (fpBuilder != null) {
                        fpBuilder.removeTrailingSlash();
                    }
                }
                psBuilder.append(pathSegments);
            }
        }
        
        public void addPath(String path) {
            if (StringUtils.hasText(path)) {
                final PathSegmentComponentBuilder psBuilder = this.getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = this.getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder != null) {
                    path = (path.startsWith("/") ? path : ("/" + path));
                }
                if (fpBuilder == null) {
                    fpBuilder = new FullPathComponentBuilder();
                    this.componentBuilders.add(fpBuilder);
                }
                fpBuilder.append(path);
            }
        }
        
        private <T> T getLastBuilder(final Class<T> builderClass) {
            if (!this.componentBuilders.isEmpty()) {
                final PathComponentBuilder last = this.componentBuilders.getLast();
                if (builderClass.isInstance(last)) {
                    return (T)last;
                }
            }
            return null;
        }
        
        @Override
        public HierarchicalUriComponents.PathComponent build() {
            final int size = this.componentBuilders.size();
            final List<HierarchicalUriComponents.PathComponent> components = new ArrayList<HierarchicalUriComponents.PathComponent>(size);
            for (final PathComponentBuilder componentBuilder : this.componentBuilders) {
                final HierarchicalUriComponents.PathComponent pathComponent = componentBuilder.build();
                if (pathComponent != null) {
                    components.add(pathComponent);
                }
            }
            if (components.isEmpty()) {
                return HierarchicalUriComponents.NULL_PATH_COMPONENT;
            }
            if (components.size() == 1) {
                return components.get(0);
            }
            return new HierarchicalUriComponents.PathComponentComposite(components);
        }
    }
    
    private static class FullPathComponentBuilder implements PathComponentBuilder
    {
        private final StringBuilder path;
        
        private FullPathComponentBuilder() {
            this.path = new StringBuilder();
        }
        
        public void append(final String path) {
            this.path.append(path);
        }
        
        @Override
        public HierarchicalUriComponents.PathComponent build() {
            if (this.path.length() == 0) {
                return null;
            }
            final String path = this.path.toString().replace("//", "/");
            return new HierarchicalUriComponents.FullPathComponent(path);
        }
        
        public void removeTrailingSlash() {
            final int index = this.path.length() - 1;
            if (this.path.charAt(index) == '/') {
                this.path.deleteCharAt(index);
            }
        }
    }
    
    private static class PathSegmentComponentBuilder implements PathComponentBuilder
    {
        private final List<String> pathSegments;
        
        private PathSegmentComponentBuilder() {
            this.pathSegments = new LinkedList<String>();
        }
        
        public void append(final String... pathSegments) {
            for (final String pathSegment : pathSegments) {
                if (StringUtils.hasText(pathSegment)) {
                    this.pathSegments.add(pathSegment);
                }
            }
        }
        
        @Override
        public HierarchicalUriComponents.PathComponent build() {
            return this.pathSegments.isEmpty() ? null : new HierarchicalUriComponents.PathSegmentComponent(this.pathSegments);
        }
    }
    
    private interface PathComponentBuilder
    {
        HierarchicalUriComponents.PathComponent build();
    }
}
