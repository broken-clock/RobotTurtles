// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import org.springframework.util.Assert;
import java.util.regex.Pattern;

public abstract class UriUtils
{
    private static final String SCHEME_PATTERN = "([^:/?#]+):";
    private static final String HTTP_PATTERN = "(http|https):";
    private static final String USERINFO_PATTERN = "([^@/]*)";
    private static final String HOST_PATTERN = "([^/?#:]*)";
    private static final String PORT_PATTERN = "(\\d*)";
    private static final String PATH_PATTERN = "([^?#]*)";
    private static final String QUERY_PATTERN = "([^#]*)";
    private static final String LAST_PATTERN = "(.*)";
    private static final Pattern URI_PATTERN;
    private static final Pattern HTTP_URL_PATTERN;
    
    @Deprecated
    public static String encodeUri(final String uri, final String encoding) throws UnsupportedEncodingException {
        Assert.notNull(uri, "'uri' must not be null");
        Assert.hasLength(encoding, "'encoding' must not be empty");
        final Matcher m = UriUtils.URI_PATTERN.matcher(uri);
        if (m.matches()) {
            final String scheme = m.group(2);
            final String authority = m.group(3);
            final String userinfo = m.group(5);
            final String host = m.group(6);
            final String port = m.group(8);
            final String path = m.group(9);
            final String query = m.group(11);
            final String fragment = m.group(13);
            return encodeUriComponents(scheme, authority, userinfo, host, port, path, query, fragment, encoding);
        }
        throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
    }
    
    @Deprecated
    public static String encodeHttpUrl(final String httpUrl, final String encoding) throws UnsupportedEncodingException {
        Assert.notNull(httpUrl, "'httpUrl' must not be null");
        Assert.hasLength(encoding, "'encoding' must not be empty");
        final Matcher m = UriUtils.HTTP_URL_PATTERN.matcher(httpUrl);
        if (m.matches()) {
            final String scheme = m.group(1);
            final String authority = m.group(2);
            final String userinfo = m.group(4);
            final String host = m.group(5);
            final String portString = m.group(7);
            final String path = m.group(8);
            final String query = m.group(10);
            return encodeUriComponents(scheme, authority, userinfo, host, portString, path, query, null, encoding);
        }
        throw new IllegalArgumentException("[" + httpUrl + "] is not a valid HTTP URL");
    }
    
    @Deprecated
    public static String encodeUriComponents(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final String encoding) throws UnsupportedEncodingException {
        Assert.hasLength(encoding, "'encoding' must not be empty");
        final StringBuilder sb = new StringBuilder();
        if (scheme != null) {
            sb.append(encodeScheme(scheme, encoding));
            sb.append(':');
        }
        if (authority != null) {
            sb.append("//");
            if (userInfo != null) {
                sb.append(encodeUserInfo(userInfo, encoding));
                sb.append('@');
            }
            if (host != null) {
                sb.append(encodeHost(host, encoding));
            }
            if (port != null) {
                sb.append(':');
                sb.append(encodePort(port, encoding));
            }
        }
        sb.append(encodePath(path, encoding));
        if (query != null) {
            sb.append('?');
            sb.append(encodeQuery(query, encoding));
        }
        if (fragment != null) {
            sb.append('#');
            sb.append(encodeFragment(fragment, encoding));
        }
        return sb.toString();
    }
    
    public static String encodeScheme(final String scheme, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(scheme, encoding, HierarchicalUriComponents.Type.SCHEME);
    }
    
    public static String encodeAuthority(final String authority, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(authority, encoding, HierarchicalUriComponents.Type.AUTHORITY);
    }
    
    public static String encodeUserInfo(final String userInfo, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(userInfo, encoding, HierarchicalUriComponents.Type.USER_INFO);
    }
    
    public static String encodeHost(final String host, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(host, encoding, HierarchicalUriComponents.Type.HOST_IPV4);
    }
    
    public static String encodePort(final String port, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(port, encoding, HierarchicalUriComponents.Type.PORT);
    }
    
    public static String encodePath(final String path, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(path, encoding, HierarchicalUriComponents.Type.PATH);
    }
    
    public static String encodePathSegment(final String segment, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(segment, encoding, HierarchicalUriComponents.Type.PATH_SEGMENT);
    }
    
    public static String encodeQuery(final String query, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(query, encoding, HierarchicalUriComponents.Type.QUERY);
    }
    
    public static String encodeQueryParam(final String queryParam, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(queryParam, encoding, HierarchicalUriComponents.Type.QUERY_PARAM);
    }
    
    public static String encodeFragment(final String fragment, final String encoding) throws UnsupportedEncodingException {
        return HierarchicalUriComponents.encodeUriComponent(fragment, encoding, HierarchicalUriComponents.Type.FRAGMENT);
    }
    
    public static String decode(final String source, final String encoding) throws UnsupportedEncodingException {
        Assert.notNull(source, "'source' must not be null");
        Assert.hasLength(encoding, "'encoding' must not be empty");
        final int length = source.length();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        boolean changed = false;
        for (int i = 0; i < length; ++i) {
            final int ch = source.charAt(i);
            if (ch == 37) {
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
                bos.write((char)((u << 4) + l));
                i += 2;
                changed = true;
            }
            else {
                bos.write(ch);
            }
        }
        return changed ? new String(bos.toByteArray(), encoding) : source;
    }
    
    static {
        URI_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//(([^@/]*)@)?([^/?#:]*)(:(\\d*))?)?([^?#]*)(\\?([^#]*))?(#(.*))?");
        HTTP_URL_PATTERN = Pattern.compile("^(http|https):(//(([^@/]*)@)?([^/?#:]*)(:(\\d*))?)?([^?#]*)(\\?(.*))?");
    }
}
