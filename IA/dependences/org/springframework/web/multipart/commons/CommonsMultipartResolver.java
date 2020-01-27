// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.commons;

import org.apache.commons.fileupload.FileItem;
import java.util.List;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileUploadBase;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.util.WebUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileItemFactory;
import javax.servlet.ServletContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartResolver;

public class CommonsMultipartResolver extends CommonsFileUploadSupport implements MultipartResolver, ServletContextAware
{
    private boolean resolveLazily;
    
    public CommonsMultipartResolver() {
        this.resolveLazily = false;
    }
    
    public CommonsMultipartResolver(final ServletContext servletContext) {
        this();
        this.setServletContext(servletContext);
    }
    
    public void setResolveLazily(final boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }
    
    @Override
    protected FileUpload newFileUpload(final FileItemFactory fileItemFactory) {
        return (FileUpload)new ServletFileUpload(fileItemFactory);
    }
    
    @Override
    public void setServletContext(final ServletContext servletContext) {
        if (!this.isUploadTempDirSpecified()) {
            this.getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
        }
    }
    
    @Override
    public boolean isMultipart(final HttpServletRequest request) {
        return request != null && ServletFileUpload.isMultipartContent(request);
    }
    
    @Override
    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        Assert.notNull(request, "Request must not be null");
        if (this.resolveLazily) {
            return new DefaultMultipartHttpServletRequest(request) {
                @Override
                protected void initializeMultipart() {
                    final MultipartParsingResult parsingResult = CommonsMultipartResolver.this.parseRequest(request);
                    this.setMultipartFiles(parsingResult.getMultipartFiles());
                    this.setMultipartParameters(parsingResult.getMultipartParameters());
                    this.setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
                }
            };
        }
        final MultipartParsingResult parsingResult = this.parseRequest(request);
        return new DefaultMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
    }
    
    protected MultipartParsingResult parseRequest(final HttpServletRequest request) throws MultipartException {
        final String encoding = this.determineEncoding(request);
        final FileUpload fileUpload = this.prepareFileUpload(encoding);
        try {
            final List<FileItem> fileItems = (List<FileItem>)((ServletFileUpload)fileUpload).parseRequest(request);
            return this.parseFileItems(fileItems, encoding);
        }
        catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), (Throwable)ex);
        }
        catch (FileUploadException ex2) {
            throw new MultipartException("Could not parse multipart servlet request", (Throwable)ex2);
        }
    }
    
    protected String determineEncoding(final HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = this.getDefaultEncoding();
        }
        return encoding;
    }
    
    @Override
    public void cleanupMultipart(final MultipartHttpServletRequest request) {
        if (request != null) {
            try {
                this.cleanupFileItems(request.getMultiFileMap());
            }
            catch (Throwable ex) {
                this.logger.warn("Failed to perform multipart cleanup for servlet request", ex);
            }
        }
    }
}
