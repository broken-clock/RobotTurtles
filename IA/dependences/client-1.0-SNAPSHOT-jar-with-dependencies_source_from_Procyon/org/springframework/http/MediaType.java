// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import org.springframework.util.comparator.CompoundComparator;
import java.util.Collection;
import java.util.ArrayList;
import org.springframework.util.StringUtils;
import java.util.List;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeTypeUtils;
import java.util.LinkedHashMap;
import org.springframework.util.Assert;
import java.util.Map;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.io.Serializable;
import org.springframework.util.MimeType;

public class MediaType extends MimeType implements Serializable
{
    private static final long serialVersionUID = 2069937152339670231L;
    public static final MediaType ALL;
    public static final String ALL_VALUE = "*/*";
    public static final MediaType APPLICATION_ATOM_XML;
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";
    public static final MediaType APPLICATION_FORM_URLENCODED;
    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";
    public static final MediaType APPLICATION_JSON;
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final MediaType APPLICATION_OCTET_STREAM;
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final MediaType APPLICATION_XHTML_XML;
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";
    public static final MediaType APPLICATION_XML;
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final MediaType IMAGE_GIF;
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final MediaType IMAGE_JPEG;
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final MediaType IMAGE_PNG;
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final MediaType MULTIPART_FORM_DATA;
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final MediaType TEXT_HTML;
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final MediaType TEXT_PLAIN;
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final MediaType TEXT_XML;
    public static final String TEXT_XML_VALUE = "text/xml";
    private static final String PARAM_QUALITY_FACTOR = "q";
    public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR;
    public static final Comparator<MediaType> SPECIFICITY_COMPARATOR;
    
    public MediaType(final String type) {
        super(type);
    }
    
    public MediaType(final String type, final String subtype) {
        super(type, subtype, Collections.emptyMap());
    }
    
    public MediaType(final String type, final String subtype, final Charset charset) {
        super(type, subtype, charset);
    }
    
    public MediaType(final String type, final String subtype, final double qualityValue) {
        this(type, subtype, Collections.singletonMap("q", Double.toString(qualityValue)));
    }
    
    public MediaType(final MediaType other, final Map<String, String> parameters) {
        super(other.getType(), other.getSubtype(), parameters);
    }
    
    public MediaType(final String type, final String subtype, final Map<String, String> parameters) {
        super(type, subtype, parameters);
    }
    
    @Override
    protected void checkParameters(final String attribute, String value) {
        super.checkParameters(attribute, value);
        if ("q".equals(attribute)) {
            value = this.unquote(value);
            final double d = Double.parseDouble(value);
            Assert.isTrue(d >= 0.0 && d <= 1.0, "Invalid quality value \"" + value + "\": should be between 0.0 and 1.0");
        }
    }
    
    public double getQualityValue() {
        final String qualityFactory = this.getParameter("q");
        return (qualityFactory != null) ? Double.parseDouble(this.unquote(qualityFactory)) : 1.0;
    }
    
    public boolean includes(final MediaType other) {
        return super.includes(other);
    }
    
    public boolean isCompatibleWith(final MediaType other) {
        return super.isCompatibleWith(other);
    }
    
    public MediaType copyQualityValue(final MediaType mediaType) {
        if (!mediaType.getParameters().containsKey("q")) {
            return this;
        }
        final Map<String, String> params = new LinkedHashMap<String, String>(this.getParameters());
        params.put("q", mediaType.getParameters().get("q"));
        return new MediaType(this, params);
    }
    
    public MediaType removeQualityValue() {
        if (!this.getParameters().containsKey("q")) {
            return this;
        }
        final Map<String, String> params = new LinkedHashMap<String, String>(this.getParameters());
        params.remove("q");
        return new MediaType(this, params);
    }
    
    public static MediaType valueOf(final String value) {
        return parseMediaType(value);
    }
    
    public static MediaType parseMediaType(final String mediaType) {
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType(mediaType);
        }
        catch (InvalidMimeTypeException ex) {
            throw new InvalidMediaTypeException(ex);
        }
        try {
            return new MediaType(type.getType(), type.getSubtype(), type.getParameters());
        }
        catch (IllegalArgumentException ex2) {
            throw new InvalidMediaTypeException(mediaType, ex2.getMessage());
        }
    }
    
    public static List<MediaType> parseMediaTypes(final String mediaTypes) {
        if (!StringUtils.hasLength(mediaTypes)) {
            return Collections.emptyList();
        }
        final String[] tokens = mediaTypes.split(",\\s*");
        final List<MediaType> result = new ArrayList<MediaType>(tokens.length);
        for (final String token : tokens) {
            result.add(parseMediaType(token));
        }
        return result;
    }
    
    public static String toString(final Collection<MediaType> mediaTypes) {
        return MimeTypeUtils.toString(mediaTypes);
    }
    
    public static void sortBySpecificity(final List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            Collections.sort(mediaTypes, MediaType.SPECIFICITY_COMPARATOR);
        }
    }
    
    public static void sortByQualityValue(final List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            Collections.sort(mediaTypes, MediaType.QUALITY_VALUE_COMPARATOR);
        }
    }
    
    public static void sortBySpecificityAndQuality(final List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            Collections.sort(mediaTypes, new CompoundComparator<Object>(new Comparator[] { MediaType.SPECIFICITY_COMPARATOR, MediaType.QUALITY_VALUE_COMPARATOR }));
        }
    }
    
    static {
        ALL = valueOf("*/*");
        APPLICATION_ATOM_XML = valueOf("application/atom+xml");
        APPLICATION_FORM_URLENCODED = valueOf("application/x-www-form-urlencoded");
        APPLICATION_JSON = valueOf("application/json");
        APPLICATION_OCTET_STREAM = valueOf("application/octet-stream");
        APPLICATION_XHTML_XML = valueOf("application/xhtml+xml");
        APPLICATION_XML = valueOf("application/xml");
        IMAGE_GIF = valueOf("image/gif");
        IMAGE_JPEG = valueOf("image/jpeg");
        IMAGE_PNG = valueOf("image/png");
        MULTIPART_FORM_DATA = valueOf("multipart/form-data");
        TEXT_HTML = valueOf("text/html");
        TEXT_PLAIN = valueOf("text/plain");
        TEXT_XML = valueOf("text/xml");
        QUALITY_VALUE_COMPARATOR = new Comparator<MediaType>() {
            @Override
            public int compare(final MediaType mediaType1, final MediaType mediaType2) {
                final double quality1 = mediaType1.getQualityValue();
                final double quality2 = mediaType2.getQualityValue();
                final int qualityComparison = Double.compare(quality2, quality1);
                if (qualityComparison != 0) {
                    return qualityComparison;
                }
                if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) {
                    return 1;
                }
                if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) {
                    return -1;
                }
                if (!mediaType1.getType().equals(mediaType2.getType())) {
                    return 0;
                }
                if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) {
                    return 1;
                }
                if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) {
                    return -1;
                }
                if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) {
                    return 0;
                }
                final int paramsSize1 = mediaType1.getParameters().size();
                final int paramsSize2 = mediaType2.getParameters().size();
                return (paramsSize2 < paramsSize1) ? -1 : ((paramsSize2 == paramsSize1) ? 0 : 1);
            }
        };
        SPECIFICITY_COMPARATOR = new SpecificityComparator<MediaType>() {
            @Override
            protected int compareParameters(final MediaType mediaType1, final MediaType mediaType2) {
                final double quality1 = mediaType1.getQualityValue();
                final double quality2 = mediaType2.getQualityValue();
                final int qualityComparison = Double.compare(quality2, quality1);
                if (qualityComparison != 0) {
                    return qualityComparison;
                }
                return super.compareParameters(mediaType1, mediaType2);
            }
        };
    }
}
