// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Locale;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.BitSet;
import java.io.Serializable;

public class MimeType implements Comparable<MimeType>, Serializable
{
    private static final long serialVersionUID = 4085923477777865903L;
    protected static final String WILDCARD_TYPE = "*";
    private static final BitSet TOKEN;
    private static final String PARAM_CHARSET = "charset";
    private final String type;
    private final String subtype;
    private final Map<String, String> parameters;
    
    public MimeType(final String type) {
        this(type, "*");
    }
    
    public MimeType(final String type, final String subtype) {
        this(type, subtype, Collections.emptyMap());
    }
    
    public MimeType(final String type, final String subtype, final Charset charSet) {
        this(type, subtype, Collections.singletonMap("charset", charSet.name()));
    }
    
    public MimeType(final MimeType other, final Map<String, String> parameters) {
        this(other.getType(), other.getSubtype(), parameters);
    }
    
    public MimeType(final String type, final String subtype, final Map<String, String> parameters) {
        Assert.hasLength(type, "type must not be empty");
        Assert.hasLength(subtype, "subtype must not be empty");
        this.checkToken(type);
        this.checkToken(subtype);
        this.type = type.toLowerCase(Locale.ENGLISH);
        this.subtype = subtype.toLowerCase(Locale.ENGLISH);
        if (!CollectionUtils.isEmpty(parameters)) {
            final Map<String, String> m = (Map<String, String>)new LinkedCaseInsensitiveMap(parameters.size(), Locale.ENGLISH);
            for (final Map.Entry<String, String> entry : parameters.entrySet()) {
                final String attribute = entry.getKey();
                final String value = entry.getValue();
                this.checkParameters(attribute, value);
                m.put(attribute, value);
            }
            this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)m);
        }
        else {
            this.parameters = Collections.emptyMap();
        }
    }
    
    private void checkToken(final String token) {
        for (int i = 0; i < token.length(); ++i) {
            final char ch = token.charAt(i);
            if (!MimeType.TOKEN.get(ch)) {
                throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
            }
        }
    }
    
    protected void checkParameters(final String attribute, String value) {
        Assert.hasLength(attribute, "parameter attribute must not be empty");
        Assert.hasLength(value, "parameter value must not be empty");
        this.checkToken(attribute);
        if ("charset".equals(attribute)) {
            value = this.unquote(value);
            Charset.forName(value);
        }
        else if (!this.isQuotedString(value)) {
            this.checkToken(value);
        }
    }
    
    private boolean isQuotedString(final String s) {
        return s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
    }
    
    protected String unquote(final String s) {
        if (s == null) {
            return null;
        }
        return this.isQuotedString(s) ? s.substring(1, s.length() - 1) : s;
    }
    
    public boolean isWildcardType() {
        return "*".equals(this.getType());
    }
    
    public boolean isWildcardSubtype() {
        return "*".equals(this.getSubtype()) || this.getSubtype().startsWith("*+");
    }
    
    public boolean isConcrete() {
        return !this.isWildcardType() && !this.isWildcardSubtype();
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getSubtype() {
        return this.subtype;
    }
    
    public Charset getCharSet() {
        final String charSet = this.getParameter("charset");
        return (charSet != null) ? Charset.forName(this.unquote(charSet)) : null;
    }
    
    public String getParameter(final String name) {
        return this.parameters.get(name);
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public boolean includes(final MimeType other) {
        if (other == null) {
            return false;
        }
        if (this.isWildcardType()) {
            return true;
        }
        if (this.getType().equals(other.getType())) {
            if (this.getSubtype().equals(other.getSubtype())) {
                return true;
            }
            if (this.isWildcardSubtype()) {
                final int thisPlusIdx = this.getSubtype().indexOf(43);
                if (thisPlusIdx == -1) {
                    return true;
                }
                final int otherPlusIdx = other.getSubtype().indexOf(43);
                if (otherPlusIdx != -1) {
                    final String thisSubtypeNoSuffix = this.getSubtype().substring(0, thisPlusIdx);
                    final String thisSubtypeSuffix = this.getSubtype().substring(thisPlusIdx + 1);
                    final String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
                    if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && "*".equals(thisSubtypeNoSuffix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isCompatibleWith(final MimeType other) {
        if (other == null) {
            return false;
        }
        if (this.isWildcardType() || other.isWildcardType()) {
            return true;
        }
        if (this.getType().equals(other.getType())) {
            if (this.getSubtype().equals(other.getSubtype())) {
                return true;
            }
            if (this.isWildcardSubtype() || other.isWildcardSubtype()) {
                final int thisPlusIdx = this.getSubtype().indexOf(43);
                final int otherPlusIdx = other.getSubtype().indexOf(43);
                if (thisPlusIdx == -1 && otherPlusIdx == -1) {
                    return true;
                }
                if (thisPlusIdx != -1 && otherPlusIdx != -1) {
                    final String thisSubtypeNoSuffix = this.getSubtype().substring(0, thisPlusIdx);
                    final String otherSubtypeNoSuffix = other.getSubtype().substring(0, otherPlusIdx);
                    final String thisSubtypeSuffix = this.getSubtype().substring(thisPlusIdx + 1);
                    final String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
                    if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && ("*".equals(thisSubtypeNoSuffix) || "*".equals(otherSubtypeNoSuffix))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public int compareTo(final MimeType other) {
        int comp = this.getType().compareToIgnoreCase(other.getType());
        if (comp != 0) {
            return comp;
        }
        comp = this.getSubtype().compareToIgnoreCase(other.getSubtype());
        if (comp != 0) {
            return comp;
        }
        comp = this.getParameters().size() - other.getParameters().size();
        if (comp != 0) {
            return comp;
        }
        final TreeSet<String> thisAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        thisAttributes.addAll(this.getParameters().keySet());
        final TreeSet<String> otherAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        otherAttributes.addAll(other.getParameters().keySet());
        final Iterator<String> thisAttributesIterator = thisAttributes.iterator();
        final Iterator<String> otherAttributesIterator = otherAttributes.iterator();
        while (thisAttributesIterator.hasNext()) {
            final String thisAttribute = thisAttributesIterator.next();
            final String otherAttribute = otherAttributesIterator.next();
            comp = thisAttribute.compareToIgnoreCase(otherAttribute);
            if (comp != 0) {
                return comp;
            }
            final String thisValue = this.getParameters().get(thisAttribute);
            String otherValue = other.getParameters().get(otherAttribute);
            if (otherValue == null) {
                otherValue = "";
            }
            comp = thisValue.compareTo(otherValue);
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MimeType)) {
            return false;
        }
        final MimeType otherType = (MimeType)other;
        return this.type.equalsIgnoreCase(otherType.type) && this.subtype.equalsIgnoreCase(otherType.subtype) && this.parameters.equals(otherType.parameters);
    }
    
    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.subtype.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        this.appendTo(builder);
        return builder.toString();
    }
    
    protected void appendTo(final StringBuilder builder) {
        builder.append(this.type);
        builder.append('/');
        builder.append(this.subtype);
        this.appendTo(this.parameters, builder);
    }
    
    private void appendTo(final Map<String, String> map, final StringBuilder builder) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(';');
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
        }
    }
    
    public static MimeType valueOf(final String value) {
        return MimeTypeUtils.parseMimeType(value);
    }
    
    static {
        final BitSet ctl = new BitSet(128);
        for (int i = 0; i <= 31; ++i) {
            ctl.set(i);
        }
        ctl.set(127);
        final BitSet separators = new BitSet(128);
        separators.set(40);
        separators.set(41);
        separators.set(60);
        separators.set(62);
        separators.set(64);
        separators.set(44);
        separators.set(59);
        separators.set(58);
        separators.set(92);
        separators.set(34);
        separators.set(47);
        separators.set(91);
        separators.set(93);
        separators.set(63);
        separators.set(61);
        separators.set(123);
        separators.set(125);
        separators.set(32);
        separators.set(9);
        (TOKEN = new BitSet(128)).set(0, 128);
        MimeType.TOKEN.andNot(ctl);
        MimeType.TOKEN.andNot(separators);
    }
    
    public static class SpecificityComparator<T extends MimeType> implements Comparator<T>
    {
        @Override
        public int compare(final T mimeType1, final T mimeType2) {
            if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) {
                return 1;
            }
            if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) {
                return -1;
            }
            if (!mimeType1.getType().equals(mimeType2.getType())) {
                return 0;
            }
            if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) {
                return 1;
            }
            if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) {
                return -1;
            }
            if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) {
                return 0;
            }
            return this.compareParameters(mimeType1, mimeType2);
        }
        
        protected int compareParameters(final T mimeType1, final T mimeType2) {
            final int paramsSize1 = mimeType1.getParameters().size();
            final int paramsSize2 = mimeType2.getParameters().size();
            return (paramsSize2 < paramsSize1) ? -1 : ((paramsSize2 == paramsSize1) ? 0 : 1);
        }
    }
}
