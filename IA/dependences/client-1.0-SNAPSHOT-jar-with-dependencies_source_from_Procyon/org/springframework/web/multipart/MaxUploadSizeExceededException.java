// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart;

public class MaxUploadSizeExceededException extends MultipartException
{
    private final long maxUploadSize;
    
    public MaxUploadSizeExceededException(final long maxUploadSize) {
        this(maxUploadSize, null);
    }
    
    public MaxUploadSizeExceededException(final long maxUploadSize, final Throwable ex) {
        super("Maximum upload size of " + maxUploadSize + " bytes exceeded", ex);
        this.maxUploadSize = maxUploadSize;
    }
    
    public long getMaxUploadSize() {
        return this.maxUploadSize;
    }
}
