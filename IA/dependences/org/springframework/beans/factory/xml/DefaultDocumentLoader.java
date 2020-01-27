// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.apache.commons.logging.LogFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;

public class DefaultDocumentLoader implements DocumentLoader
{
    private static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String XSD_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
    private static final Log logger;
    
    @Override
    public Document loadDocument(final InputSource inputSource, final EntityResolver entityResolver, final ErrorHandler errorHandler, final int validationMode, final boolean namespaceAware) throws Exception {
        final DocumentBuilderFactory factory = this.createDocumentBuilderFactory(validationMode, namespaceAware);
        if (DefaultDocumentLoader.logger.isDebugEnabled()) {
            DefaultDocumentLoader.logger.debug("Using JAXP provider [" + factory.getClass().getName() + "]");
        }
        final DocumentBuilder builder = this.createDocumentBuilder(factory, entityResolver, errorHandler);
        return builder.parse(inputSource);
    }
    
    protected DocumentBuilderFactory createDocumentBuilderFactory(final int validationMode, final boolean namespaceAware) throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        if (validationMode != 0) {
            factory.setValidating(true);
            if (validationMode == 3) {
                factory.setNamespaceAware(true);
                try {
                    factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
                }
                catch (IllegalArgumentException ex) {
                    final ParserConfigurationException pcex = new ParserConfigurationException("Unable to validate using XSD: Your JAXP provider [" + factory + "] does not support XML Schema. Are you running on Java 1.4 with Apache Crimson? " + "Upgrade to Apache Xerces (or Java 1.5) for full XSD support.");
                    pcex.initCause(ex);
                    throw pcex;
                }
            }
        }
        return factory;
    }
    
    protected DocumentBuilder createDocumentBuilder(final DocumentBuilderFactory factory, final EntityResolver entityResolver, final ErrorHandler errorHandler) throws ParserConfigurationException {
        final DocumentBuilder docBuilder = factory.newDocumentBuilder();
        if (entityResolver != null) {
            docBuilder.setEntityResolver(entityResolver);
        }
        if (errorHandler != null) {
            docBuilder.setErrorHandler(errorHandler);
        }
        return docBuilder;
    }
    
    static {
        logger = LogFactory.getLog(DefaultDocumentLoader.class);
    }
}
