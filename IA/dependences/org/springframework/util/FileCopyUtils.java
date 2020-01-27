// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.io.StringWriter;
import java.io.Writer;
import java.io.Reader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;

public abstract class FileCopyUtils
{
    public static final int BUFFER_SIZE = 4096;
    
    public static int copy(final File in, final File out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");
        return copy(new BufferedInputStream(new FileInputStream(in)), new BufferedOutputStream(new FileOutputStream(out)));
    }
    
    public static void copy(final byte[] in, final File out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No output File specified");
        final ByteArrayInputStream inStream = new ByteArrayInputStream(in);
        final OutputStream outStream = new BufferedOutputStream(new FileOutputStream(out));
        copy(inStream, outStream);
    }
    
    public static byte[] copyToByteArray(final File in) throws IOException {
        Assert.notNull(in, "No input File specified");
        return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
    }
    
    public static int copy(final InputStream in, final OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            return StreamUtils.copy(in, out);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
            try {
                out.close();
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void copy(final byte[] in, final OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            out.write(in);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static byte[] copyToByteArray(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toByteArray();
    }
    
    public static int copy(final Reader in, final Writer out) throws IOException {
        Assert.notNull(in, "No Reader specified");
        Assert.notNull(out, "No Writer specified");
        try {
            int byteCount = 0;
            final char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
            try {
                out.close();
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void copy(final String in, final Writer out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(out, "No Writer specified");
        try {
            out.write(in);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static String copyToString(final Reader in) throws IOException {
        final StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }
}
