// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.imageio.stream.FileCacheImageOutputStream;
import java.io.OutputStream;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.RenderedImage;
import javax.imageio.IIOImage;
import org.springframework.http.HttpOutputMessage;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.FileCacheImageInputStream;
import java.io.InputStream;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import java.util.Collections;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import java.util.Iterator;
import org.springframework.util.Assert;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.io.File;
import org.springframework.http.MediaType;
import java.util.List;
import java.awt.image.BufferedImage;

public class BufferedImageHttpMessageConverter implements HttpMessageConverter<BufferedImage>
{
    private final List<MediaType> readableMediaTypes;
    private MediaType defaultContentType;
    private File cacheDir;
    
    public BufferedImageHttpMessageConverter() {
        this.readableMediaTypes = new ArrayList<MediaType>();
        final String[] readerMIMETypes;
        final String[] readerMediaTypes = readerMIMETypes = ImageIO.getReaderMIMETypes();
        for (final String mediaType : readerMIMETypes) {
            this.readableMediaTypes.add(MediaType.parseMediaType(mediaType));
        }
        final String[] writerMediaTypes = ImageIO.getWriterMIMETypes();
        if (writerMediaTypes.length > 0) {
            this.defaultContentType = MediaType.parseMediaType(writerMediaTypes[0]);
        }
    }
    
    public void setDefaultContentType(final MediaType defaultContentType) {
        Assert.notNull(defaultContentType, "'contentType' must not be null");
        final Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(defaultContentType.toString());
        if (!imageWriters.hasNext()) {
            throw new IllegalArgumentException("ContentType [" + defaultContentType + "] is not supported by the Java Image I/O API");
        }
        this.defaultContentType = defaultContentType;
    }
    
    public MediaType getDefaultContentType() {
        return this.defaultContentType;
    }
    
    public void setCacheDir(final File cacheDir) {
        Assert.notNull(cacheDir, "'cacheDir' must not be null");
        Assert.isTrue(cacheDir.isDirectory(), "'cacheDir' is not a directory");
        this.cacheDir = cacheDir;
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return BufferedImage.class.equals(clazz) && this.isReadable(mediaType);
    }
    
    private boolean isReadable(final MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        final Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(mediaType.toString());
        return imageReaders.hasNext();
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return BufferedImage.class.equals(clazz) && this.isWritable(mediaType);
    }
    
    private boolean isWritable(final MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        final Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(mediaType.toString());
        return imageWriters.hasNext();
    }
    
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList((List<? extends MediaType>)this.readableMediaTypes);
    }
    
    @Override
    public BufferedImage read(final Class<? extends BufferedImage> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ImageInputStream imageInputStream = null;
        ImageReader imageReader = null;
        try {
            imageInputStream = this.createImageInputStream(inputMessage.getBody());
            final MediaType contentType = inputMessage.getHeaders().getContentType();
            final Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(contentType.toString());
            if (imageReaders.hasNext()) {
                imageReader = imageReaders.next();
                final ImageReadParam irp = imageReader.getDefaultReadParam();
                this.process(irp);
                imageReader.setInput(imageInputStream, true);
                return imageReader.read(0, irp);
            }
            throw new HttpMessageNotReadableException("Could not find javax.imageio.ImageReader for Content-Type [" + contentType + "]");
        }
        finally {
            if (imageReader != null) {
                imageReader.dispose();
            }
            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private ImageInputStream createImageInputStream(final InputStream is) throws IOException {
        if (this.cacheDir != null) {
            return new FileCacheImageInputStream(is, this.cacheDir);
        }
        return new MemoryCacheImageInputStream(is);
    }
    
    @Override
    public void write(final BufferedImage image, MediaType contentType, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
            contentType = this.getDefaultContentType();
        }
        Assert.notNull(contentType, "Count not determine Content-Type, set one using the 'defaultContentType' property");
        outputMessage.getHeaders().setContentType(contentType);
        ImageOutputStream imageOutputStream = null;
        ImageWriter imageWriter = null;
        try {
            imageOutputStream = this.createImageOutputStream(outputMessage.getBody());
            final Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(contentType.toString());
            if (!imageWriters.hasNext()) {
                throw new HttpMessageNotWritableException("Could not find javax.imageio.ImageWriter for Content-Type [" + contentType + "]");
            }
            imageWriter = imageWriters.next();
            final ImageWriteParam iwp = imageWriter.getDefaultWriteParam();
            this.process(iwp);
            imageWriter.setOutput(imageOutputStream);
            imageWriter.write(null, new IIOImage(image, null, null), iwp);
        }
        finally {
            if (imageWriter != null) {
                imageWriter.dispose();
            }
            if (imageOutputStream != null) {
                try {
                    imageOutputStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private ImageOutputStream createImageOutputStream(final OutputStream os) throws IOException {
        if (this.cacheDir != null) {
            return new FileCacheImageOutputStream(os, this.cacheDir);
        }
        return new MemoryCacheImageOutputStream(os);
    }
    
    protected void process(final ImageReadParam irp) {
    }
    
    protected void process(final ImageWriteParam iwp) {
    }
}
