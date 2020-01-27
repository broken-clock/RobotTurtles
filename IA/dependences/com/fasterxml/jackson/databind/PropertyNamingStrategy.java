// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public class PropertyNamingStrategy implements Serializable
{
    public static final PropertyNamingStrategy SNAKE_CASE;
    public static final PropertyNamingStrategy UPPER_CAMEL_CASE;
    public static final PropertyNamingStrategy LOWER_CAMEL_CASE;
    public static final PropertyNamingStrategy LOWER_CASE;
    public static final PropertyNamingStrategy KEBAB_CASE;
    @Deprecated
    public static final PropertyNamingStrategy CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES;
    @Deprecated
    public static final PropertyNamingStrategy PASCAL_CASE_TO_CAMEL_CASE;
    
    public String nameForField(final MapperConfig<?> config, final AnnotatedField field, final String defaultName) {
        return defaultName;
    }
    
    public String nameForGetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
        return defaultName;
    }
    
    public String nameForSetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
        return defaultName;
    }
    
    public String nameForConstructorParameter(final MapperConfig<?> config, final AnnotatedParameter ctorParam, final String defaultName) {
        return defaultName;
    }
    
    static {
        SNAKE_CASE = new SnakeCaseStrategy();
        UPPER_CAMEL_CASE = new UpperCamelCaseStrategy();
        LOWER_CAMEL_CASE = new PropertyNamingStrategy();
        LOWER_CASE = new LowerCaseStrategy();
        KEBAB_CASE = new KebabCaseStrategy();
        CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES = PropertyNamingStrategy.SNAKE_CASE;
        PASCAL_CASE_TO_CAMEL_CASE = PropertyNamingStrategy.UPPER_CAMEL_CASE;
    }
    
    public abstract static class PropertyNamingStrategyBase extends PropertyNamingStrategy
    {
        @Override
        public String nameForField(final MapperConfig<?> config, final AnnotatedField field, final String defaultName) {
            return this.translate(defaultName);
        }
        
        @Override
        public String nameForGetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
            return this.translate(defaultName);
        }
        
        @Override
        public String nameForSetterMethod(final MapperConfig<?> config, final AnnotatedMethod method, final String defaultName) {
            return this.translate(defaultName);
        }
        
        @Override
        public String nameForConstructorParameter(final MapperConfig<?> config, final AnnotatedParameter ctorParam, final String defaultName) {
            return this.translate(defaultName);
        }
        
        public abstract String translate(final String p0);
    }
    
    public static class SnakeCaseStrategy extends PropertyNamingStrategyBase
    {
        @Override
        public String translate(final String input) {
            if (input == null) {
                return input;
            }
            final int length = input.length();
            final StringBuilder result = new StringBuilder(length * 2);
            int resultLength = 0;
            boolean wasPrevTranslated = false;
            for (int i = 0; i < length; ++i) {
                char c = input.charAt(i);
                if (i > 0 || c != '_') {
                    if (Character.isUpperCase(c)) {
                        if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                            result.append('_');
                            ++resultLength;
                        }
                        c = Character.toLowerCase(c);
                        wasPrevTranslated = true;
                    }
                    else {
                        wasPrevTranslated = false;
                    }
                    result.append(c);
                    ++resultLength;
                }
            }
            return (resultLength > 0) ? result.toString() : input;
        }
    }
    
    public static class UpperCamelCaseStrategy extends PropertyNamingStrategyBase
    {
        @Override
        public String translate(final String input) {
            if (input == null || input.length() == 0) {
                return input;
            }
            final char c = input.charAt(0);
            final char uc = Character.toUpperCase(c);
            if (c == uc) {
                return input;
            }
            final StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(0, uc);
            return sb.toString();
        }
    }
    
    public static class LowerCaseStrategy extends PropertyNamingStrategyBase
    {
        @Override
        public String translate(final String input) {
            return input.toLowerCase();
        }
    }
    
    public static class KebabCaseStrategy extends PropertyNamingStrategyBase
    {
        @Override
        public String translate(final String input) {
            if (input == null) {
                return input;
            }
            final int length = input.length();
            if (length == 0) {
                return input;
            }
            final StringBuilder result = new StringBuilder(length + (length >> 1));
            int upperCount = 0;
            for (int i = 0; i < length; ++i) {
                final char ch = input.charAt(i);
                final char lc = Character.toLowerCase(ch);
                if (lc == ch) {
                    if (upperCount > 1) {
                        result.insert(result.length() - 1, '-');
                    }
                    upperCount = 0;
                }
                else {
                    if (upperCount == 0 && i > 0) {
                        result.append('-');
                    }
                    ++upperCount;
                }
                result.append(lc);
            }
            return result.toString();
        }
    }
    
    @Deprecated
    public static class LowerCaseWithUnderscoresStrategy extends SnakeCaseStrategy
    {
    }
    
    @Deprecated
    public static class PascalCaseStrategy extends UpperCamelCaseStrategy
    {
    }
}
