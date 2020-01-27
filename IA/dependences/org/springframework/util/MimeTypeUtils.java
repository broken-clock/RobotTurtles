// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Comparator;

public abstract class MimeTypeUtils
{
    public static final MimeType ALL;
    public static final String ALL_VALUE = "*/*";
    public static final MimeType APPLICATION_ATOM_XML;
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";
    public static final MimeType APPLICATION_FORM_URLENCODED;
    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";
    public static final MimeType APPLICATION_JSON;
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final MimeType APPLICATION_OCTET_STREAM;
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final MimeType APPLICATION_XHTML_XML;
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";
    public static final MimeType APPLICATION_XML;
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final MimeType IMAGE_GIF;
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final MimeType IMAGE_JPEG;
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final MimeType IMAGE_PNG;
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final MimeType MULTIPART_FORM_DATA;
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final MimeType TEXT_HTML;
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final MimeType TEXT_PLAIN;
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final MimeType TEXT_XML;
    public static final String TEXT_XML_VALUE = "text/xml";
    public static final Comparator<MimeType> SPECIFICITY_COMPARATOR;
    
    public static MimeType parseMimeType(final String mimeType) {
        if (!StringUtils.hasLength(mimeType)) {
            throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
        }
        final String[] parts = StringUtils.tokenizeToStringArray(mimeType, ";");
        String fullType = parts[0].trim();
        if ("*".equals(fullType)) {
            fullType = "*/*";
        }
        final int subIndex = fullType.indexOf(47);
        if (subIndex == -1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
        }
        if (subIndex == fullType.length() - 1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
        }
        final String type = fullType.substring(0, subIndex);
        final String subtype = fullType.substring(subIndex + 1, fullType.length());
        if ("*".equals(type) && !"*".equals(subtype)) {
            throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
        }
        Map<String, String> parameters = null;
        if (parts.length > 1) {
            parameters = new LinkedHashMap<String, String>(parts.length - 1);
            for (int i = 1; i < parts.length; ++i) {
                final String parameter = parts[i];
                final int eqIndex = parameter.indexOf(61);
                if (eqIndex != -1) {
                    final String attribute = parameter.substring(0, eqIndex);
                    final String value = parameter.substring(eqIndex + 1, parameter.length());
                    parameters.put(attribute, value);
                }
            }
        }
        try {
            return new MimeType(type, subtype, parameters);
        }
        catch (UnsupportedCharsetException ex) {
            throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
        }
        catch (IllegalArgumentException ex2) {
            throw new InvalidMimeTypeException(mimeType, ex2.getMessage());
        }
    }
    
    public static List<MimeType> parseMimeTypes(final String mimeTypes) {
        if (!StringUtils.hasLength(mimeTypes)) {
            return Collections.emptyList();
        }
        final String[] tokens = mimeTypes.split(",\\s*");
        final List<MimeType> result = new ArrayList<MimeType>(tokens.length);
        for (final String token : tokens) {
            result.add(parseMimeType(token));
        }
        return result;
    }
    
    public static String toString(final Collection<? extends MimeType> mimeTypes) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<? extends MimeType> iterator = mimeTypes.iterator();
        while (iterator.hasNext()) {
            final MimeType mimeType = (MimeType)iterator.next();
            mimeType.appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
    
    public static void sortBySpecificity(final List<MimeType> mimeTypes) {
        Assert.notNull(mimeTypes, "'mimeTypes' must not be null");
        if (mimeTypes.size() > 1) {
            Collections.sort(mimeTypes, MimeTypeUtils.SPECIFICITY_COMPARATOR);
        }
    }
    
    static {
        ALL = MimeType.valueOf("*/*");
        APPLICATION_ATOM_XML = MimeType.valueOf("application/atom+xml");
        APPLICATION_FORM_URLENCODED = MimeType.valueOf("application/x-www-form-urlencoded");
        APPLICATION_JSON = MimeType.valueOf("application/json");
        APPLICATION_OCTET_STREAM = MimeType.valueOf("application/octet-stream");
        APPLICATION_XHTML_XML = MimeType.valueOf("application/xhtml+xml");
        APPLICATION_XML = MimeType.valueOf("application/xml");
        IMAGE_GIF = MimeType.valueOf("image/gif");
        IMAGE_JPEG = MimeType.valueOf("image/jpeg");
        IMAGE_PNG = MimeType.valueOf("image/png");
        MULTIPART_FORM_DATA = MimeType.valueOf("multipart/form-data");
        TEXT_HTML = MimeType.valueOf("text/html");
        TEXT_PLAIN = MimeType.valueOf("text/plain");
        TEXT_XML = MimeType.valueOf("text/xml");
        SPECIFICITY_COMPARATOR = new MimeType.SpecificityComparator<MimeType>();
    }
}
