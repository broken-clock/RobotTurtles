// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.parsing.ReaderEventListener;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.parsing.ReaderContext;

public class XmlReaderContext extends ReaderContext
{
    private final XmlBeanDefinitionReader reader;
    private final NamespaceHandlerResolver namespaceHandlerResolver;
    
    public XmlReaderContext(final Resource resource, final ProblemReporter problemReporter, final ReaderEventListener eventListener, final SourceExtractor sourceExtractor, final XmlBeanDefinitionReader reader, final NamespaceHandlerResolver namespaceHandlerResolver) {
        super(resource, problemReporter, eventListener, sourceExtractor);
        this.reader = reader;
        this.namespaceHandlerResolver = namespaceHandlerResolver;
    }
    
    public final XmlBeanDefinitionReader getReader() {
        return this.reader;
    }
    
    public final BeanDefinitionRegistry getRegistry() {
        return this.reader.getRegistry();
    }
    
    public final ResourceLoader getResourceLoader() {
        return this.reader.getResourceLoader();
    }
    
    public final ClassLoader getBeanClassLoader() {
        return this.reader.getBeanClassLoader();
    }
    
    public final NamespaceHandlerResolver getNamespaceHandlerResolver() {
        return this.namespaceHandlerResolver;
    }
    
    public String generateBeanName(final BeanDefinition beanDefinition) {
        return this.reader.getBeanNameGenerator().generateBeanName(beanDefinition, this.getRegistry());
    }
    
    public String registerWithGeneratedName(final BeanDefinition beanDefinition) {
        final String generatedName = this.generateBeanName(beanDefinition);
        this.getRegistry().registerBeanDefinition(generatedName, beanDefinition);
        return generatedName;
    }
    
    public Document readDocumentFromString(final String documentContent) {
        final InputSource is = new InputSource(new StringReader(documentContent));
        try {
            return this.reader.doLoadDocument(is, this.getResource());
        }
        catch (Exception ex) {
            throw new BeanDefinitionStoreException("Failed to read XML document", ex);
        }
    }
}
