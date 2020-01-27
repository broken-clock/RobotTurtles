// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import java.util.Enumeration;
import java.io.InputStream;
import org.springframework.util.Assert;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

class HtmlCharacterEntityReferences
{
    private static final String PROPERTIES_FILE = "HtmlCharacterEntityReferences.properties";
    static final char REFERENCE_START = '&';
    static final String DECIMAL_REFERENCE_START = "&#";
    static final String HEX_REFERENCE_START = "&#x";
    static final char REFERENCE_END = ';';
    static final char CHAR_NULL = '\uffff';
    private final String[] characterToEntityReferenceMap;
    private final Map<String, Character> entityReferenceToCharacterMap;
    
    public HtmlCharacterEntityReferences() {
        this.characterToEntityReferenceMap = new String[3000];
        this.entityReferenceToCharacterMap = new HashMap<String, Character>(252);
        final Properties entityReferences = new Properties();
        final InputStream is = HtmlCharacterEntityReferences.class.getResourceAsStream("HtmlCharacterEntityReferences.properties");
        if (is == null) {
            throw new IllegalStateException("Cannot find reference definition file [HtmlCharacterEntityReferences.properties] as class path resource");
        }
        try {
            try {
                entityReferences.load(is);
            }
            finally {
                is.close();
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to parse reference definition file [HtmlCharacterEntityReferences.properties]: " + ex.getMessage());
        }
        final Enumeration<?> keys = entityReferences.propertyNames();
        while (keys.hasMoreElements()) {
            final String key = (String)keys.nextElement();
            final int referredChar = Integer.parseInt(key);
            Assert.isTrue(referredChar < 1000 || (referredChar >= 8000 && referredChar < 10000), "Invalid reference to special HTML entity: " + referredChar);
            final int index = (referredChar < 1000) ? referredChar : (referredChar - 7000);
            final String reference = entityReferences.getProperty(key);
            this.characterToEntityReferenceMap[index] = '&' + reference + ';';
            this.entityReferenceToCharacterMap.put(reference, new Character((char)referredChar));
        }
    }
    
    public int getSupportedReferenceCount() {
        return this.entityReferenceToCharacterMap.size();
    }
    
    public boolean isMappedToReference(final char character) {
        return this.convertToReference(character) != null;
    }
    
    public String convertToReference(final char character) {
        if (character < '\u03e8' || (character >= '\u1f40' && character < '\u2710')) {
            final int index = (character < '\u03e8') ? character : (character - '\u1b58');
            final String entityReference = this.characterToEntityReferenceMap[index];
            if (entityReference != null) {
                return entityReference;
            }
        }
        return null;
    }
    
    public char convertToCharacter(final String entityReference) {
        final Character referredCharacter = this.entityReferenceToCharacterMap.get(entityReference);
        if (referredCharacter != null) {
            return referredCharacter;
        }
        return '\uffff';
    }
}
