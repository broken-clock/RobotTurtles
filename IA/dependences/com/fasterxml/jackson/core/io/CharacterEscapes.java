// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.io;

import java.util.Arrays;
import com.fasterxml.jackson.core.SerializableString;
import java.io.Serializable;

public abstract class CharacterEscapes implements Serializable
{
    public static final int ESCAPE_NONE = 0;
    public static final int ESCAPE_STANDARD = -1;
    public static final int ESCAPE_CUSTOM = -2;
    
    public abstract int[] getEscapeCodesForAscii();
    
    public abstract SerializableString getEscapeSequence(final int p0);
    
    public static int[] standardAsciiEscapesForJSON() {
        final int[] esc = CharTypes.get7BitOutputEscapes();
        return Arrays.copyOf(esc, esc.length);
    }
}
