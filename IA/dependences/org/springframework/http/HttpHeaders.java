// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.URI;
import java.util.EnumSet;
import java.util.Set;
import java.util.ArrayList;
import java.nio.charset.Charset;
import org.springframework.util.StringUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import org.springframework.util.LinkedCaseInsensitiveMap;
import java.util.Locale;
import org.springframework.util.Assert;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.io.Serializable;
import org.springframework.util.MultiValueMap;

public class HttpHeaders implements MultiValueMap<String, String>, Serializable
{
    private static final long serialVersionUID = -8578554704772377436L;
    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_CHARSET = "Accept-Charset";
    private static final String ALLOW = "Allow";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CONNECTION = "Connection";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DATE = "Date";
    private static final String ETAG = "ETag";
    private static final String EXPIRES = "Expires";
    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    private static final String IF_NONE_MATCH = "If-None-Match";
    private static final String LAST_MODIFIED = "Last-Modified";
    private static final String LOCATION = "Location";
    private static final String ORIGIN = "Origin";
    private static final String PRAGMA = "Pragma";
    private static final String UPGRADE = "Upgrade";
    private static final String[] DATE_FORMATS;
    private static TimeZone GMT;
    private final Map<String, List<String>> headers;
    
    private HttpHeaders(final Map<String, List<String>> headers, final boolean readOnly) {
        Assert.notNull(headers, "'headers' must not be null");
        if (readOnly) {
            final Map<String, List<String>> map = (Map<String, List<String>>)new LinkedCaseInsensitiveMap(headers.size(), Locale.ENGLISH);
            for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
                final List<String> values = Collections.unmodifiableList((List<? extends String>)entry.getValue());
                map.put(entry.getKey(), values);
            }
            this.headers = Collections.unmodifiableMap((Map<? extends String, ? extends List<String>>)map);
        }
        else {
            this.headers = headers;
        }
    }
    
    public HttpHeaders() {
        this((Map<String, List<String>>)new LinkedCaseInsensitiveMap(8, Locale.ENGLISH), false);
    }
    
    public static HttpHeaders readOnlyHttpHeaders(final HttpHeaders headers) {
        return new HttpHeaders(headers, true);
    }
    
    public void setAccept(final List<MediaType> acceptableMediaTypes) {
        this.set("Accept", MediaType.toString(acceptableMediaTypes));
    }
    
    public List<MediaType> getAccept() {
        String value = this.getFirst("Accept");
        List<MediaType> result = (value != null) ? MediaType.parseMediaTypes(value) : Collections.emptyList();
        if (result.size() == 1 && this.headers.get("Accept").size() > 1) {
            value = StringUtils.collectionToCommaDelimitedString(this.headers.get("Accept"));
            result = MediaType.parseMediaTypes(value);
        }
        return result;
    }
    
    public void setAcceptCharset(final List<Charset> acceptableCharsets) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<Charset> iterator = acceptableCharsets.iterator();
        while (iterator.hasNext()) {
            final Charset charset = iterator.next();
            builder.append(charset.name().toLowerCase(Locale.ENGLISH));
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        this.set("Accept-Charset", builder.toString());
    }
    
    public List<Charset> getAcceptCharset() {
        final List<Charset> result = new ArrayList<Charset>();
        final String value = this.getFirst("Accept-Charset");
        if (value != null) {
            final String[] split;
            final String[] tokens = split = value.split(",\\s*");
            for (final String token : split) {
                final int paramIdx = token.indexOf(59);
                String charsetName;
                if (paramIdx == -1) {
                    charsetName = token;
                }
                else {
                    charsetName = token.substring(0, paramIdx);
                }
                if (!charsetName.equals("*")) {
                    result.add(Charset.forName(charsetName));
                }
            }
        }
        return result;
    }
    
    public void setAllow(final Set<HttpMethod> allowedMethods) {
        this.set("Allow", StringUtils.collectionToCommaDelimitedString(allowedMethods));
    }
    
    public Set<HttpMethod> getAllow() {
        final String value = this.getFirst("Allow");
        if (value != null) {
            final List<HttpMethod> allowedMethod = new ArrayList<HttpMethod>(5);
            final String[] split;
            final String[] tokens = split = value.split(",\\s*");
            for (final String token : split) {
                allowedMethod.add(HttpMethod.valueOf(token));
            }
            return EnumSet.copyOf(allowedMethod);
        }
        return EnumSet.noneOf(HttpMethod.class);
    }
    
    public void setCacheControl(final String cacheControl) {
        this.set("Cache-Control", cacheControl);
    }
    
    public String getCacheControl() {
        return this.getFirst("Cache-Control");
    }
    
    public void setConnection(final String connection) {
        this.set("Connection", connection);
    }
    
    public void setConnection(final List<String> connection) {
        this.set("Connection", this.toCommaDelimitedString(connection));
    }
    
    public List<String> getConnection() {
        return this.getFirstValueAsList("Connection");
    }
    
    public void setContentDispositionFormData(final String name, final String filename) {
        Assert.notNull(name, "'name' must not be null");
        final StringBuilder builder = new StringBuilder("form-data; name=\"");
        builder.append(name).append('\"');
        if (filename != null) {
            builder.append("; filename=\"");
            builder.append(filename).append('\"');
        }
        this.set("Content-Disposition", builder.toString());
    }
    
    public void setContentLength(final long contentLength) {
        this.set("Content-Length", Long.toString(contentLength));
    }
    
    public long getContentLength() {
        final String value = this.getFirst("Content-Length");
        return (value != null) ? Long.parseLong(value) : -1L;
    }
    
    public void setContentType(final MediaType mediaType) {
        Assert.isTrue(!mediaType.isWildcardType(), "'Content-Type' cannot contain wildcard type '*'");
        Assert.isTrue(!mediaType.isWildcardSubtype(), "'Content-Type' cannot contain wildcard subtype '*'");
        this.set("Content-Type", mediaType.toString());
    }
    
    public MediaType getContentType() {
        final String value = this.getFirst("Content-Type");
        return (value != null) ? MediaType.parseMediaType(value) : null;
    }
    
    public void setDate(final long date) {
        this.setDate("Date", date);
    }
    
    public long getDate() {
        return this.getFirstDate("Date");
    }
    
    public void setETag(final String eTag) {
        if (eTag != null) {
            Assert.isTrue(eTag.startsWith("\"") || eTag.startsWith("W/"), "Invalid eTag, does not start with W/ or \"");
            Assert.isTrue(eTag.endsWith("\""), "Invalid eTag, does not end with \"");
        }
        this.set("ETag", eTag);
    }
    
    public String getETag() {
        return this.getFirst("ETag");
    }
    
    public void setExpires(final long expires) {
        this.setDate("Expires", expires);
    }
    
    public long getExpires() {
        try {
            return this.getFirstDate("Expires");
        }
        catch (IllegalArgumentException ex) {
            return -1L;
        }
    }
    
    public void setIfModifiedSince(final long ifModifiedSince) {
        this.setDate("If-Modified-Since", ifModifiedSince);
    }
    
    @Deprecated
    public long getIfNotModifiedSince() {
        return this.getIfModifiedSince();
    }
    
    public long getIfModifiedSince() {
        return this.getFirstDate("If-Modified-Since");
    }
    
    public void setIfNoneMatch(final String ifNoneMatch) {
        this.set("If-None-Match", ifNoneMatch);
    }
    
    public void setIfNoneMatch(final List<String> ifNoneMatchList) {
        this.set("If-None-Match", this.toCommaDelimitedString(ifNoneMatchList));
    }
    
    protected String toCommaDelimitedString(final List<String> list) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            final String ifNoneMatch = iterator.next();
            builder.append(ifNoneMatch);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
    
    public List<String> getIfNoneMatch() {
        return this.getFirstValueAsList("If-None-Match");
    }
    
    protected List<String> getFirstValueAsList(final String header) {
        final List<String> result = new ArrayList<String>();
        final String value = this.getFirst(header);
        if (value != null) {
            final String[] split;
            final String[] tokens = split = value.split(",\\s*");
            for (final String token : split) {
                result.add(token);
            }
        }
        return result;
    }
    
    public void setLastModified(final long lastModified) {
        this.setDate("Last-Modified", lastModified);
    }
    
    public long getLastModified() {
        return this.getFirstDate("Last-Modified");
    }
    
    public void setLocation(final URI location) {
        this.set("Location", location.toASCIIString());
    }
    
    public URI getLocation() {
        final String value = this.getFirst("Location");
        return (value != null) ? URI.create(value) : null;
    }
    
    public void setOrigin(final String origin) {
        this.set("Origin", origin);
    }
    
    public String getOrigin() {
        return this.getFirst("Origin");
    }
    
    public void setPragma(final String pragma) {
        this.set("Pragma", pragma);
    }
    
    public String getPragma() {
        return this.getFirst("Pragma");
    }
    
    public void setUpgrade(final String upgrade) {
        this.set("Upgrade", upgrade);
    }
    
    public String getUpgrade() {
        return this.getFirst("Upgrade");
    }
    
    public long getFirstDate(final String headerName) {
        final String headerValue = this.getFirst(headerName);
        if (headerValue == null) {
            return -1L;
        }
        final String[] date_FORMATS = HttpHeaders.DATE_FORMATS;
        final int length = date_FORMATS.length;
        int i = 0;
        while (i < length) {
            final String dateFormat = date_FORMATS[i];
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            simpleDateFormat.setTimeZone(HttpHeaders.GMT);
            try {
                return simpleDateFormat.parse(headerValue).getTime();
            }
            catch (ParseException e) {
                ++i;
                continue;
            }
            break;
        }
        throw new IllegalArgumentException("Cannot parse date value \"" + headerValue + "\" for \"" + headerName + "\" header");
    }
    
    public void setDate(final String headerName, final long date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(HttpHeaders.DATE_FORMATS[0], Locale.US);
        dateFormat.setTimeZone(HttpHeaders.GMT);
        this.set(headerName, dateFormat.format(new Date(date)));
    }
    
    @Override
    public String getFirst(final String headerName) {
        final List<String> headerValues = this.headers.get(headerName);
        return (headerValues != null) ? headerValues.get(0) : null;
    }
    
    @Override
    public void add(final String headerName, final String headerValue) {
        List<String> headerValues = this.headers.get(headerName);
        if (headerValues == null) {
            headerValues = new LinkedList<String>();
            this.headers.put(headerName, headerValues);
        }
        headerValues.add(headerValue);
    }
    
    @Override
    public void set(final String headerName, final String headerValue) {
        final List<String> headerValues = new LinkedList<String>();
        headerValues.add(headerValue);
        this.headers.put(headerName, headerValues);
    }
    
    @Override
    public void setAll(final Map<String, String> values) {
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            this.set((String)entry.getKey(), (String)entry.getValue());
        }
    }
    
    @Override
    public Map<String, String> toSingleValueMap() {
        final LinkedHashMap<String, String> singleValueMap = new LinkedHashMap<String, String>(this.headers.size());
        for (final Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }
    
    @Override
    public int size() {
        return this.headers.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.headers.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.headers.containsValue(value);
    }
    
    @Override
    public List<String> get(final Object key) {
        return this.headers.get(key);
    }
    
    @Override
    public List<String> put(final String key, final List<String> value) {
        return this.headers.put(key, value);
    }
    
    @Override
    public List<String> remove(final Object key) {
        return this.headers.remove(key);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends List<String>> m) {
        this.headers.putAll(m);
    }
    
    @Override
    public void clear() {
        this.headers.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.headers.keySet();
    }
    
    @Override
    public Collection<List<String>> values() {
        return this.headers.values();
    }
    
    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpHeaders)) {
            return false;
        }
        final HttpHeaders otherHeaders = (HttpHeaders)other;
        return this.headers.equals(otherHeaders.headers);
    }
    
    @Override
    public int hashCode() {
        return this.headers.hashCode();
    }
    
    @Override
    public String toString() {
        return this.headers.toString();
    }
    
    static {
        DATE_FORMATS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy" };
        HttpHeaders.GMT = TimeZone.getTimeZone("GMT");
    }
}
