// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import org.springframework.util.StringUtils;
import javax.activation.MimetypesFileTypeMap;
import org.springframework.core.io.ClassPathResource;
import javax.activation.FileTypeMap;
import org.springframework.util.ClassUtils;
import java.io.InputStream;
import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.StreamUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;

public class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Resource>
{
    private static final boolean jafPresent;
    
    public ResourceHttpMessageConverter() {
        super(MediaType.ALL);
    }
    
    @Override
    protected boolean supports(final Class<?> clazz) {
        return Resource.class.isAssignableFrom(clazz);
    }
    
    @Override
    protected Resource readInternal(final Class<? extends Resource> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
        return new ByteArrayResource(body);
    }
    
    @Override
    protected MediaType getDefaultContentType(final Resource resource) {
        if (ResourceHttpMessageConverter.jafPresent) {
            return ActivationMediaTypeFactory.getMediaType(resource);
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
    
    @Override
    protected Long getContentLength(final Resource resource, final MediaType contentType) throws IOException {
        return resource.contentLength();
    }
    
    @Override
    protected void writeInternal(final Resource resource, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final InputStream in = resource.getInputStream();
        try {
            StreamUtils.copy(in, outputMessage.getBody());
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
        }
        outputMessage.getBody().flush();
    }
    
    static {
        jafPresent = ClassUtils.isPresent("javax.activation.FileTypeMap", ResourceHttpMessageConverter.class.getClassLoader());
    }
    
    private static class ActivationMediaTypeFactory
    {
        private static final FileTypeMap fileTypeMap;
        
        private static FileTypeMap loadFileTypeMapFromContextSupportModule() {
            final Resource mappingLocation = new ClassPathResource("org/springframework/mail/javamail/mime.types");
            if (mappingLocation.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = mappingLocation.getInputStream();
                    return (FileTypeMap)new MimetypesFileTypeMap(inputStream);
                }
                catch (IOException ex) {}
                finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException ex2) {}
                    }
                }
            }
            return FileTypeMap.getDefaultFileTypeMap();
        }
        
        public static MediaType getMediaType(final Resource resource) {
            if (resource.getFilename() == null) {
                return null;
            }
            final String mediaType = ActivationMediaTypeFactory.fileTypeMap.getContentType(resource.getFilename());
            return StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null;
        }
        
        static {
            fileTypeMap = loadFileTypeMapFromContextSupportModule();
        }
    }
}
