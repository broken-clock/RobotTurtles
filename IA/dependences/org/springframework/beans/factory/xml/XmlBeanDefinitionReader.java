// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.BeanUtils;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.springframework.core.io.DescriptiveResource;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.util.HashSet;
import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.beans.factory.parsing.NullSourceExtractor;
import org.springframework.beans.factory.parsing.EmptyReaderEventListener;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.support.EncodedResource;
import java.util.Set;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.Constants;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader
{
    public static final int VALIDATION_NONE = 0;
    public static final int VALIDATION_AUTO = 1;
    public static final int VALIDATION_DTD = 2;
    public static final int VALIDATION_XSD = 3;
    private static final Constants constants;
    private int validationMode;
    private boolean namespaceAware;
    private Class<?> documentReaderClass;
    private ProblemReporter problemReporter;
    private ReaderEventListener eventListener;
    private SourceExtractor sourceExtractor;
    private NamespaceHandlerResolver namespaceHandlerResolver;
    private DocumentLoader documentLoader;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private final XmlValidationModeDetector validationModeDetector;
    private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded;
    
    public XmlBeanDefinitionReader(final BeanDefinitionRegistry registry) {
        super(registry);
        this.validationMode = 1;
        this.namespaceAware = false;
        this.documentReaderClass = DefaultBeanDefinitionDocumentReader.class;
        this.problemReporter = new FailFastProblemReporter();
        this.eventListener = new EmptyReaderEventListener();
        this.sourceExtractor = new NullSourceExtractor();
        this.documentLoader = new DefaultDocumentLoader();
        this.errorHandler = new SimpleSaxErrorHandler(this.logger);
        this.validationModeDetector = new XmlValidationModeDetector();
        this.resourcesCurrentlyBeingLoaded = new NamedThreadLocal<Set<EncodedResource>>("XML bean definition resources currently being loaded");
    }
    
    public void setValidating(final boolean validating) {
        this.validationMode = (validating ? 1 : 0);
        this.namespaceAware = !validating;
    }
    
    public void setValidationModeName(final String validationModeName) {
        this.setValidationMode(XmlBeanDefinitionReader.constants.asNumber(validationModeName).intValue());
    }
    
    public void setValidationMode(final int validationMode) {
        this.validationMode = validationMode;
    }
    
    public int getValidationMode() {
        return this.validationMode;
    }
    
    public void setNamespaceAware(final boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }
    
    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }
    
    public void setProblemReporter(final ProblemReporter problemReporter) {
        this.problemReporter = ((problemReporter != null) ? problemReporter : new FailFastProblemReporter());
    }
    
    public void setEventListener(final ReaderEventListener eventListener) {
        this.eventListener = ((eventListener != null) ? eventListener : new EmptyReaderEventListener());
    }
    
    public void setSourceExtractor(final SourceExtractor sourceExtractor) {
        this.sourceExtractor = ((sourceExtractor != null) ? sourceExtractor : new NullSourceExtractor());
    }
    
    public void setNamespaceHandlerResolver(final NamespaceHandlerResolver namespaceHandlerResolver) {
        this.namespaceHandlerResolver = namespaceHandlerResolver;
    }
    
    public void setDocumentLoader(final DocumentLoader documentLoader) {
        this.documentLoader = ((documentLoader != null) ? documentLoader : new DefaultDocumentLoader());
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    protected EntityResolver getEntityResolver() {
        if (this.entityResolver == null) {
            final ResourceLoader resourceLoader = this.getResourceLoader();
            if (resourceLoader != null) {
                this.entityResolver = new ResourceEntityResolver(resourceLoader);
            }
            else {
                this.entityResolver = new DelegatingEntityResolver(this.getBeanClassLoader());
            }
        }
        return this.entityResolver;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public void setDocumentReaderClass(final Class<?> documentReaderClass) {
        if (documentReaderClass == null || !BeanDefinitionDocumentReader.class.isAssignableFrom(documentReaderClass)) {
            throw new IllegalArgumentException("documentReaderClass must be an implementation of the BeanDefinitionDocumentReader interface");
        }
        this.documentReaderClass = documentReaderClass;
    }
    
    @Override
    public int loadBeanDefinitions(final Resource resource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource));
    }
    
    public int loadBeanDefinitions(final EncodedResource encodedResource) throws BeanDefinitionStoreException {
        Assert.notNull(encodedResource, "EncodedResource must not be null");
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Loading XML bean definitions from " + encodedResource.getResource());
        }
        Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
        if (currentResources == null) {
            currentResources = new HashSet<EncodedResource>(4);
            this.resourcesCurrentlyBeingLoaded.set(currentResources);
        }
        if (!currentResources.add(encodedResource)) {
            throw new BeanDefinitionStoreException("Detected cyclic loading of " + encodedResource + " - check your import definitions!");
        }
        try {
            final InputStream inputStream = encodedResource.getResource().getInputStream();
            try {
                final InputSource inputSource = new InputSource(inputStream);
                if (encodedResource.getEncoding() != null) {
                    inputSource.setEncoding(encodedResource.getEncoding());
                }
                return this.doLoadBeanDefinitions(inputSource, encodedResource.getResource());
            }
            finally {
                inputStream.close();
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("IOException parsing XML document from " + encodedResource.getResource(), ex);
        }
        finally {
            currentResources.remove(encodedResource);
            if (currentResources.isEmpty()) {
                this.resourcesCurrentlyBeingLoaded.remove();
            }
        }
    }
    
    public int loadBeanDefinitions(final InputSource inputSource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(inputSource, "resource loaded through SAX InputSource");
    }
    
    public int loadBeanDefinitions(final InputSource inputSource, final String resourceDescription) throws BeanDefinitionStoreException {
        return this.doLoadBeanDefinitions(inputSource, new DescriptiveResource(resourceDescription));
    }
    
    protected int doLoadBeanDefinitions(final InputSource inputSource, final Resource resource) throws BeanDefinitionStoreException {
        try {
            final Document doc = this.doLoadDocument(inputSource, resource);
            return this.registerBeanDefinitions(doc, resource);
        }
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        catch (SAXParseException ex2) {
            throw new XmlBeanDefinitionStoreException(resource.getDescription(), "Line " + ex2.getLineNumber() + " in XML document from " + resource + " is invalid", ex2);
        }
        catch (SAXException ex3) {
            throw new XmlBeanDefinitionStoreException(resource.getDescription(), "XML document from " + resource + " is invalid", ex3);
        }
        catch (ParserConfigurationException ex4) {
            throw new BeanDefinitionStoreException(resource.getDescription(), "Parser configuration exception parsing XML from " + resource, ex4);
        }
        catch (IOException ex5) {
            throw new BeanDefinitionStoreException(resource.getDescription(), "IOException parsing XML document from " + resource, ex5);
        }
        catch (Throwable ex6) {
            throw new BeanDefinitionStoreException(resource.getDescription(), "Unexpected exception parsing XML document from " + resource, ex6);
        }
    }
    
    protected Document doLoadDocument(final InputSource inputSource, final Resource resource) throws Exception {
        return this.documentLoader.loadDocument(inputSource, this.getEntityResolver(), this.errorHandler, this.getValidationModeForResource(resource), this.isNamespaceAware());
    }
    
    protected int getValidationModeForResource(final Resource resource) {
        final int validationModeToUse = this.getValidationMode();
        if (validationModeToUse != 1) {
            return validationModeToUse;
        }
        final int detectedMode = this.detectValidationMode(resource);
        if (detectedMode != 1) {
            return detectedMode;
        }
        return 3;
    }
    
    protected int detectValidationMode(final Resource resource) {
        if (resource.isOpen()) {
            throw new BeanDefinitionStoreException("Passed-in Resource [" + resource + "] contains an open stream: " + "cannot determine validation mode automatically. Either pass in a Resource " + "that is able to create fresh streams, or explicitly specify the validationMode " + "on your XmlBeanDefinitionReader instance.");
        }
        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("Unable to determine validation mode for [" + resource + "]: cannot open InputStream. " + "Did you attempt to load directly from a SAX InputSource without specifying the " + "validationMode on your XmlBeanDefinitionReader instance?", ex);
        }
        try {
            return this.validationModeDetector.detectValidationMode(inputStream);
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("Unable to determine validation mode for [" + resource + "]: an error occurred whilst reading from the InputStream.", ex);
        }
    }
    
    public int registerBeanDefinitions(final Document doc, final Resource resource) throws BeanDefinitionStoreException {
        final BeanDefinitionDocumentReader documentReader = this.createBeanDefinitionDocumentReader();
        documentReader.setEnvironment(this.getEnvironment());
        final int countBefore = this.getRegistry().getBeanDefinitionCount();
        documentReader.registerBeanDefinitions(doc, this.createReaderContext(resource));
        return this.getRegistry().getBeanDefinitionCount() - countBefore;
    }
    
    protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
        return BeanDefinitionDocumentReader.class.cast(BeanUtils.instantiateClass(this.documentReaderClass));
    }
    
    public XmlReaderContext createReaderContext(final Resource resource) {
        return new XmlReaderContext(resource, this.problemReporter, this.eventListener, this.sourceExtractor, this, this.getNamespaceHandlerResolver());
    }
    
    public NamespaceHandlerResolver getNamespaceHandlerResolver() {
        if (this.namespaceHandlerResolver == null) {
            this.namespaceHandlerResolver = this.createDefaultNamespaceHandlerResolver();
        }
        return this.namespaceHandlerResolver;
    }
    
    protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
        return new DefaultNamespaceHandlerResolver(this.getResourceLoader().getClassLoader());
    }
    
    static {
        constants = new Constants(XmlBeanDefinitionReader.class);
    }
}
