// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.util.StringUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.io.Serializable;

public class DefaultMessageCodesResolver implements MessageCodesResolver, Serializable
{
    public static final String CODE_SEPARATOR = ".";
    private static final MessageCodeFormatter DEFAULT_FORMATTER;
    private String prefix;
    private MessageCodeFormatter formatter;
    
    public DefaultMessageCodesResolver() {
        this.prefix = "";
        this.formatter = DefaultMessageCodesResolver.DEFAULT_FORMATTER;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = ((prefix != null) ? prefix : "");
    }
    
    public void setMessageCodeFormatter(final MessageCodeFormatter formatter) {
        this.formatter = ((formatter == null) ? DefaultMessageCodesResolver.DEFAULT_FORMATTER : formatter);
    }
    
    protected String getPrefix() {
        return this.prefix;
    }
    
    @Override
    public String[] resolveMessageCodes(final String errorCode, final String objectName) {
        return this.resolveMessageCodes(errorCode, objectName, "", null);
    }
    
    @Override
    public String[] resolveMessageCodes(final String errorCode, final String objectName, final String field, final Class<?> fieldType) {
        final Set<String> codeList = new LinkedHashSet<String>();
        final List<String> fieldList = new ArrayList<String>();
        this.buildFieldList(field, fieldList);
        this.addCodes(codeList, errorCode, objectName, fieldList);
        final int dotIndex = field.lastIndexOf(46);
        if (dotIndex != -1) {
            this.buildFieldList(field.substring(dotIndex + 1), fieldList);
        }
        this.addCodes(codeList, errorCode, null, fieldList);
        if (fieldType != null) {
            this.addCode(codeList, errorCode, null, fieldType.getName());
        }
        this.addCode(codeList, errorCode, null, null);
        return StringUtils.toStringArray(codeList);
    }
    
    private void addCodes(final Collection<String> codeList, final String errorCode, final String objectName, final Iterable<String> fields) {
        for (final String field : fields) {
            this.addCode(codeList, errorCode, objectName, field);
        }
    }
    
    private void addCode(final Collection<String> codeList, final String errorCode, final String objectName, final String field) {
        codeList.add(this.postProcessMessageCode(this.formatter.format(errorCode, objectName, field)));
    }
    
    protected void buildFieldList(final String field, final List<String> fieldList) {
        fieldList.add(field);
        String plainField = field;
        int keyIndex = plainField.lastIndexOf(91);
        while (keyIndex != -1) {
            final int endKeyIndex = plainField.indexOf(93, keyIndex);
            if (endKeyIndex != -1) {
                plainField = plainField.substring(0, keyIndex) + plainField.substring(endKeyIndex + 1);
                fieldList.add(plainField);
                keyIndex = plainField.lastIndexOf(91);
            }
            else {
                keyIndex = -1;
            }
        }
    }
    
    protected String postProcessMessageCode(final String code) {
        return this.getPrefix() + code;
    }
    
    static {
        DEFAULT_FORMATTER = Format.PREFIX_ERROR_CODE;
    }
    
    public enum Format implements MessageCodeFormatter
    {
        PREFIX_ERROR_CODE {
            @Override
            public String format(final String errorCode, final String objectName, final String field) {
                return Format.toDelimitedString(errorCode, objectName, field);
            }
        }, 
        POSTFIX_ERROR_CODE {
            @Override
            public String format(final String errorCode, final String objectName, final String field) {
                return Format.toDelimitedString(objectName, field, errorCode);
            }
        };
        
        public static String toDelimitedString(final String... elements) {
            final StringBuilder rtn = new StringBuilder();
            for (final String element : elements) {
                if (StringUtils.hasLength(element)) {
                    rtn.append((rtn.length() == 0) ? "" : ".");
                    rtn.append(element);
                }
            }
            return rtn.toString();
        }
    }
}
