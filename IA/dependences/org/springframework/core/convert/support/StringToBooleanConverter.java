// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert.support;

import java.util.HashSet;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;

final class StringToBooleanConverter implements Converter<String, Boolean>
{
    private static final Set<String> trueValues;
    private static final Set<String> falseValues;
    
    @Override
    public Boolean convert(final String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        value = value.toLowerCase();
        if (StringToBooleanConverter.trueValues.contains(value)) {
            return Boolean.TRUE;
        }
        if (StringToBooleanConverter.falseValues.contains(value)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
    }
    
    static {
        trueValues = new HashSet<String>(4);
        falseValues = new HashSet<String>(4);
        StringToBooleanConverter.trueValues.add("true");
        StringToBooleanConverter.trueValues.add("on");
        StringToBooleanConverter.trueValues.add("yes");
        StringToBooleanConverter.trueValues.add("1");
        StringToBooleanConverter.falseValues.add("false");
        StringToBooleanConverter.falseValues.add("off");
        StringToBooleanConverter.falseValues.add("no");
        StringToBooleanConverter.falseValues.add("0");
    }
}
