// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.io.FilterOutputStream;
import java.io.FilterInputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public abstract class StreamUtils
{
    public static final int BUFFER_SIZE = 4096;
    
    public static byte[] copyToByteArray(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toByteArray();
    }
    
    public static String copyToString(final InputStream in, final Charset charset) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        final StringBuilder out = new StringBuilder();
        final InputStreamReader reader = new InputStreamReader(in, charset);
        final char[] buffer = new char[4096];
        int bytesRead = -1;
        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }
        return out.toString();
    }
    
    public static void copy(final byte[] in, final OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        out.write(in);
    }
    
    public static void copy(final String in, final Charset charset, final OutputStream out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(charset, "No charset specified");
        Assert.notNull(out, "No OutputStream specified");
        final Writer writer = new OutputStreamWriter(out, charset);
        writer.write(in);
        writer.flush();
    }
    
    public static int copy(final InputStream in, final OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        int byteCount = 0;
        final byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }
    
    public static InputStream nonClosing(final InputStream in) {
        Assert.notNull(in, "No InputStream specified");
        return new NonClosingInputStream(in);
    }
    
    public static OutputStream nonClosing(final OutputStream out) {
        Assert.notNull(out, "No OutputStream specified");
        return new NonClosingOutputStream(out);
    }
    
    private static class NonClosingInputStream extends FilterInputStream
    {
        public NonClosingInputStream(final InputStream in) {
            super(in);
        }
        
        @Override
        public void close() throws IOException {
        }
    }
    
    private static class NonClosingOutputStream extends FilterOutputStream
    {
        public NonClosingOutputStream(final OutputStream out) {
            super(out);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int let) throws IOException {
            this.out.write(b, off, let);
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
