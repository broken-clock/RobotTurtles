// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

public abstract class HtmlUtils
{
    private static final HtmlCharacterEntityReferences characterEntityReferences;
    
    public static String htmlEscape(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); ++i) {
            final char character = input.charAt(i);
            final String reference = HtmlUtils.characterEntityReferences.convertToReference(character);
            if (reference != null) {
                escaped.append(reference);
            }
            else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }
    
    public static String htmlEscapeDecimal(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); ++i) {
            final char character = input.charAt(i);
            if (HtmlUtils.characterEntityReferences.isMappedToReference(character)) {
                escaped.append("&#");
                escaped.append((int)character);
                escaped.append(';');
            }
            else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }
    
    public static String htmlEscapeHex(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); ++i) {
            final char character = input.charAt(i);
            if (HtmlUtils.characterEntityReferences.isMappedToReference(character)) {
                escaped.append("&#x");
                escaped.append(Integer.toString(character, 16));
                escaped.append(';');
            }
            else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }
    
    public static String htmlUnescape(final String input) {
        if (input == null) {
            return null;
        }
        return new HtmlCharacterEntityDecoder(HtmlUtils.characterEntityReferences, input).decode();
    }
    
    static {
        characterEntityReferences = new HtmlCharacterEntityReferences();
    }
}
