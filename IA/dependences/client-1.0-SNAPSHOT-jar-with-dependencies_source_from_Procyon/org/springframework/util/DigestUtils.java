// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public abstract class DigestUtils
{
    private static final String MD5_ALGORITHM_NAME = "MD5";
    private static final char[] HEX_CHARS;
    
    public static byte[] md5Digest(final byte[] bytes) {
        return digest("MD5", bytes);
    }
    
    public static String md5DigestAsHex(final byte[] bytes) {
        return digestAsHexString("MD5", bytes);
    }
    
    public static StringBuilder appendMd5DigestAsHex(final byte[] bytes, final StringBuilder builder) {
        return appendDigestAsHex("MD5", bytes, builder);
    }
    
    private static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
        }
    }
    
    private static byte[] digest(final String algorithm, final byte[] bytes) {
        return getDigest(algorithm).digest(bytes);
    }
    
    private static String digestAsHexString(final String algorithm, final byte[] bytes) {
        final char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }
    
    private static StringBuilder appendDigestAsHex(final String algorithm, final byte[] bytes, final StringBuilder builder) {
        final char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return builder.append(hexDigest);
    }
    
    private static char[] digestAsHexChars(final String algorithm, final byte[] bytes) {
        final byte[] digest = digest(algorithm, bytes);
        return encodeHex(digest);
    }
    
    private static char[] encodeHex(final byte[] bytes) {
        final char[] chars = new char[32];
        for (int i = 0; i < chars.length; i += 2) {
            final byte b = bytes[i / 2];
            chars[i] = DigestUtils.HEX_CHARS[b >>> 4 & 0xF];
            chars[i + 1] = DigestUtils.HEX_CHARS[b & 0xF];
        }
        return chars;
    }
    
    static {
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
