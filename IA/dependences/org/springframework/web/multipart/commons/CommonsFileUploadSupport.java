// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart.commons;

import java.nio.charset.Charset;
import org.springframework.http.MediaType;
import java.util.Iterator;
import java.util.Map;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.apache.commons.fileupload.FileItem;
import java.util.List;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;

public abstract class CommonsFileUploadSupport
{
    protected final Log logger;
    private final DiskFileItemFactory fileItemFactory;
    private final FileUpload fileUpload;
    private boolean uploadTempDirSpecified;
    
    public CommonsFileUploadSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.uploadTempDirSpecified = false;
        this.fileItemFactory = this.newFileItemFactory();
        this.fileUpload = this.newFileUpload((FileItemFactory)this.getFileItemFactory());
    }
    
    public DiskFileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }
    
    public FileUpload getFileUpload() {
        return this.fileUpload;
    }
    
    public void setMaxUploadSize(final long maxUploadSize) {
        this.fileUpload.setSizeMax(maxUploadSize);
    }
    
    public void setMaxInMemorySize(final int maxInMemorySize) {
        this.fileItemFactory.setSizeThreshold(maxInMemorySize);
    }
    
    public void setDefaultEncoding(final String defaultEncoding) {
        this.fileUpload.setHeaderEncoding(defaultEncoding);
    }
    
    protected String getDefaultEncoding() {
        String encoding = this.getFileUpload().getHeaderEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        return encoding;
    }
    
    public void setUploadTempDir(final Resource uploadTempDir) throws IOException {
        if (!uploadTempDir.exists() && !uploadTempDir.getFile().mkdirs()) {
            throw new IllegalArgumentException("Given uploadTempDir [" + uploadTempDir + "] could not be created");
        }
        this.fileItemFactory.setRepository(uploadTempDir.getFile());
        this.uploadTempDirSpecified = true;
    }
    
    protected boolean isUploadTempDirSpecified() {
        return this.uploadTempDirSpecified;
    }
    
    protected DiskFileItemFactory newFileItemFactory() {
        return new DiskFileItemFactory();
    }
    
    protected abstract FileUpload newFileUpload(final FileItemFactory p0);
    
    protected FileUpload prepareFileUpload(final String encoding) {
        FileUpload actualFileUpload;
        final FileUpload fileUpload = actualFileUpload = this.getFileUpload();
        if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
            actualFileUpload = this.newFileUpload((FileItemFactory)this.getFileItemFactory());
            actualFileUpload.setSizeMax(fileUpload.getSizeMax());
            actualFileUpload.setHeaderEncoding(encoding);
        }
        return actualFileUpload;
    }
    
    protected MultipartParsingResult parseFileItems(final List<FileItem> fileItems, final String encoding) {
        final MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<String, MultipartFile>();
        final Map<String, String[]> multipartParameters = new HashMap<String, String[]>();
        final Map<String, String> multipartParameterContentTypes = new HashMap<String, String>();
        for (final FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {
                final String partEncoding = this.determineEncoding(fileItem.getContentType(), encoding);
                String value;
                if (partEncoding != null) {
                    try {
                        value = fileItem.getString(partEncoding);
                    }
                    catch (UnsupportedEncodingException ex) {
                        if (this.logger.isWarnEnabled()) {
                            this.logger.warn("Could not decode multipart item '" + fileItem.getFieldName() + "' with encoding '" + partEncoding + "': using platform default");
                        }
                        value = fileItem.getString();
                    }
                }
                else {
                    value = fileItem.getString();
                }
                final String[] curParam = multipartParameters.get(fileItem.getFieldName());
                if (curParam == null) {
                    multipartParameters.put(fileItem.getFieldName(), new String[] { value });
                }
                else {
                    final String[] newParam = StringUtils.addStringToArray(curParam, value);
                    multipartParameters.put(fileItem.getFieldName(), newParam);
                }
                multipartParameterContentTypes.put(fileItem.getFieldName(), fileItem.getContentType());
            }
            else {
                final CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
                multipartFiles.add(file.getName(), file);
                if (!this.logger.isDebugEnabled()) {
                    continue;
                }
                this.logger.debug("Found multipart file [" + file.getName() + "] of size " + file.getSize() + " bytes with original filename [" + file.getOriginalFilename() + "], stored " + file.getStorageDescription());
            }
        }
        return new MultipartParsingResult(multipartFiles, multipartParameters, multipartParameterContentTypes);
    }
    
    protected void cleanupFileItems(final MultiValueMap<String, MultipartFile> multipartFiles) {
        for (final List<MultipartFile> files : multipartFiles.values()) {
            for (final MultipartFile file : files) {
                if (file instanceof CommonsMultipartFile) {
                    final CommonsMultipartFile cmf = (CommonsMultipartFile)file;
                    cmf.getFileItem().delete();
                    if (!this.logger.isDebugEnabled()) {
                        continue;
                    }
                    this.logger.debug("Cleaning up multipart file [" + cmf.getName() + "] with original filename [" + cmf.getOriginalFilename() + "], stored " + cmf.getStorageDescription());
                }
            }
        }
    }
    
    private String determineEncoding(final String contentTypeHeader, final String defaultEncoding) {
        if (!StringUtils.hasText(contentTypeHeader)) {
            return defaultEncoding;
        }
        final MediaType contentType = MediaType.parseMediaType(contentTypeHeader);
        final Charset charset = contentType.getCharSet();
        return (charset != null) ? charset.name() : defaultEncoding;
    }
    
    protected static class MultipartParsingResult
    {
        private final MultiValueMap<String, MultipartFile> multipartFiles;
        private final Map<String, String[]> multipartParameters;
        private final Map<String, String> multipartParameterContentTypes;
        
        public MultipartParsingResult(final MultiValueMap<String, MultipartFile> mpFiles, final Map<String, String[]> mpParams, final Map<String, String> mpParamContentTypes) {
            this.multipartFiles = mpFiles;
            this.multipartParameters = mpParams;
            this.multipartParameterContentTypes = mpParamContentTypes;
        }
        
        public MultiValueMap<String, MultipartFile> getMultipartFiles() {
            return this.multipartFiles;
        }
        
        public Map<String, String[]> getMultipartParameters() {
            return this.multipartParameters;
        }
        
        public Map<String, String> getMultipartParameterContentTypes() {
            return this.multipartParameterContentTypes;
        }
    }
}
