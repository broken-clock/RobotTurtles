// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.support;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import org.springframework.util.FileCopyUtils;
import java.util.List;
import java.util.ArrayList;
import org.springframework.http.HttpHeaders;
import java.util.Iterator;
import org.springframework.util.MultiValueMap;
import java.util.Collection;
import org.springframework.web.multipart.MultipartException;
import javax.servlet.http.Part;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import javax.servlet.http.HttpServletRequest;

public class StandardMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest
{
    private static final String CONTENT_DISPOSITION = "content-disposition";
    private static final String FILENAME_KEY = "filename=";
    
    public StandardMultipartHttpServletRequest(final HttpServletRequest request) throws MultipartException {
        super(request);
        try {
            final Collection<Part> parts = (Collection<Part>)request.getParts();
            final MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>(parts.size());
            for (final Part part : parts) {
                final String filename = this.extractFilename(part.getHeader("content-disposition"));
                if (filename != null) {
                    files.add(part.getName(), new StandardMultipartFile(part, filename));
                }
            }
            this.setMultipartFiles(files);
        }
        catch (Exception ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }
    
    private String extractFilename(final String contentDisposition) {
        if (contentDisposition == null) {
            return null;
        }
        final int startIndex = contentDisposition.indexOf("filename=");
        if (startIndex == -1) {
            return null;
        }
        final String filename = contentDisposition.substring(startIndex + "filename=".length());
        if (filename.startsWith("\"")) {
            final int endIndex = filename.indexOf("\"", 1);
            if (endIndex != -1) {
                return filename.substring(1, endIndex);
            }
        }
        else {
            final int endIndex = filename.indexOf(";");
            if (endIndex != -1) {
                return filename.substring(0, endIndex);
            }
        }
        return filename;
    }
    
    public String getMultipartContentType(final String paramOrFileName) {
        try {
            final Part part = this.getPart(paramOrFileName);
            return (part != null) ? part.getContentType() : null;
        }
        catch (Exception ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }
    
    public HttpHeaders getMultipartHeaders(final String paramOrFileName) {
        try {
            final Part part = this.getPart(paramOrFileName);
            if (part != null) {
                final HttpHeaders headers = new HttpHeaders();
                for (final String headerName : part.getHeaderNames()) {
                    headers.put(headerName, (List<String>)new ArrayList<String>(part.getHeaders(headerName)));
                }
                return headers;
            }
            return null;
        }
        catch (Exception ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }
    
    private static class StandardMultipartFile implements MultipartFile
    {
        private final Part part;
        private final String filename;
        
        public StandardMultipartFile(final Part part, final String filename) {
            this.part = part;
            this.filename = filename;
        }
        
        @Override
        public String getName() {
            return this.part.getName();
        }
        
        @Override
        public String getOriginalFilename() {
            return this.filename;
        }
        
        @Override
        public String getContentType() {
            return this.part.getContentType();
        }
        
        @Override
        public boolean isEmpty() {
            return this.part.getSize() == 0L;
        }
        
        @Override
        public long getSize() {
            return this.part.getSize();
        }
        
        @Override
        public byte[] getBytes() throws IOException {
            return FileCopyUtils.copyToByteArray(this.part.getInputStream());
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return this.part.getInputStream();
        }
        
        @Override
        public void transferTo(final File dest) throws IOException, IllegalStateException {
            this.part.write(dest.getPath());
        }
    }
}
