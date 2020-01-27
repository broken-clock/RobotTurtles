// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import org.apache.commons.logging.Log;
import java.io.Writer;

public class CommonsLogWriter extends Writer
{
    private final Log logger;
    private final StringBuilder buffer;
    
    public CommonsLogWriter(final Log logger) {
        this.buffer = new StringBuilder();
        Assert.notNull(logger, "Logger must not be null");
        this.logger = logger;
    }
    
    public void write(final char ch) {
        if (ch == '\n' && this.buffer.length() > 0) {
            this.logger.debug(this.buffer.toString());
            this.buffer.setLength(0);
        }
        else {
            this.buffer.append(ch);
        }
    }
    
    @Override
    public void write(final char[] buffer, final int offset, final int length) {
        for (int i = 0; i < length; ++i) {
            final char ch = buffer[offset + i];
            if (ch == '\n' && this.buffer.length() > 0) {
                this.logger.debug(this.buffer.toString());
                this.buffer.setLength(0);
            }
            else {
                this.buffer.append(ch);
            }
        }
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void close() {
    }
}
