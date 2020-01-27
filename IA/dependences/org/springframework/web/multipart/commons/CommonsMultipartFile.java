// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.commons;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.FileUploadException;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;

public class CommonsMultipartFile implements MultipartFile, Serializable
{
    protected static final Log logger;
    private final FileItem fileItem;
    private final long size;
    
    public CommonsMultipartFile(final FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }
    
    public final FileItem getFileItem() {
        return this.fileItem;
    }
    
    @Override
    public String getName() {
        return this.fileItem.getFieldName();
    }
    
    @Override
    public String getOriginalFilename() {
        final String filename = this.fileItem.getName();
        if (filename == null) {
            return "";
        }
        int pos = filename.lastIndexOf("/");
        if (pos == -1) {
            pos = filename.lastIndexOf("\\");
        }
        if (pos != -1) {
            return filename.substring(pos + 1);
        }
        return filename;
    }
    
    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }
    
    @Override
    public long getSize() {
        return this.size;
    }
    
    @Override
    public byte[] getBytes() {
        if (!this.isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        final byte[] bytes = this.fileItem.get();
        return (bytes != null) ? bytes : new byte[0];
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        final InputStream inputStream = this.fileItem.getInputStream();
        return (inputStream != null) ? inputStream : new ByteArrayInputStream(new byte[0]);
    }
    
    @Override
    public void transferTo(final File dest) throws IOException, IllegalStateException {
        if (!this.isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }
        if (dest.exists() && !dest.delete()) {
            throw new IOException("Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
        }
        try {
            this.fileItem.write(dest);
            if (CommonsMultipartFile.logger.isDebugEnabled()) {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = (this.isAvailable() ? "copied" : "moved");
                }
                CommonsMultipartFile.logger.debug("Multipart file '" + this.getName() + "' with original filename [" + this.getOriginalFilename() + "], stored " + this.getStorageDescription() + ": " + action + " to [" + dest.getAbsolutePath() + "]");
            }
        }
        catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
        catch (IOException ex2) {
            throw ex2;
        }
        catch (Exception ex3) {
            CommonsMultipartFile.logger.error("Could not transfer to file", ex3);
            throw new IOException("Could not transfer to file: " + ex3.getMessage());
        }
    }
    
    protected boolean isAvailable() {
        if (this.fileItem.isInMemory()) {
            return true;
        }
        if (this.fileItem instanceof DiskFileItem) {
            return ((DiskFileItem)this.fileItem).getStoreLocation().exists();
        }
        return this.fileItem.getSize() == this.size;
    }
    
    public String getStorageDescription() {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        }
        if (this.fileItem instanceof DiskFileItem) {
            return "at [" + ((DiskFileItem)this.fileItem).getStoreLocation().getAbsolutePath() + "]";
        }
        return "on disk";
    }
    
    static {
        logger = LogFactory.getLog(CommonsMultipartFile.class);
    }
}
