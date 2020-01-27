// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import java.util.Set;
import java.io.IOException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import java.net.URISyntaxException;
import org.springframework.util.ResourceUtils;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.io.Resource;
import java.util.LinkedHashSet;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.apache.commons.logging.Log;

public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader
{
    public static final String BEAN_ELEMENT = "bean";
    public static final String NESTED_BEANS_ELEMENT = "beans";
    public static final String ALIAS_ELEMENT = "alias";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String ALIAS_ATTRIBUTE = "alias";
    public static final String IMPORT_ELEMENT = "import";
    public static final String RESOURCE_ATTRIBUTE = "resource";
    public static final String PROFILE_ATTRIBUTE = "profile";
    protected final Log logger;
    private Environment environment;
    private XmlReaderContext readerContext;
    private BeanDefinitionParserDelegate delegate;
    
    public DefaultBeanDefinitionDocumentReader() {
        this.logger = LogFactory.getLog(this.getClass());
    }
    
    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public void registerBeanDefinitions(final Document doc, final XmlReaderContext readerContext) {
        this.readerContext = readerContext;
        this.logger.debug("Loading bean definitions");
        final Element root = doc.getDocumentElement();
        this.doRegisterBeanDefinitions(root);
    }
    
    protected void doRegisterBeanDefinitions(final Element root) {
        final String profileSpec = root.getAttribute("profile");
        if (StringUtils.hasText(profileSpec)) {
            Assert.state(this.environment != null, "Environment must be set for evaluating profiles");
            final String[] specifiedProfiles = StringUtils.tokenizeToStringArray(profileSpec, ",; ");
            if (!this.environment.acceptsProfiles(specifiedProfiles)) {
                return;
            }
        }
        final BeanDefinitionParserDelegate parent = this.delegate;
        this.delegate = this.createDelegate(this.readerContext, root, parent);
        this.preProcessXml(root);
        this.parseBeanDefinitions(root, this.delegate);
        this.postProcessXml(root);
        this.delegate = parent;
    }
    
    protected BeanDefinitionParserDelegate createDelegate(final XmlReaderContext readerContext, final Element root, final BeanDefinitionParserDelegate parentDelegate) {
        final BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext, this.environment);
        delegate.initDefaults(root, parentDelegate);
        return delegate;
    }
    
    protected final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }
    
    protected Object extractSource(final Element ele) {
        return this.readerContext.extractSource(ele);
    }
    
    protected void parseBeanDefinitions(final Element root, final BeanDefinitionParserDelegate delegate) {
        if (delegate.isDefaultNamespace(root)) {
            final NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node node = nl.item(i);
                if (node instanceof Element) {
                    final Element ele = (Element)node;
                    if (delegate.isDefaultNamespace(ele)) {
                        this.parseDefaultElement(ele, delegate);
                    }
                    else {
                        delegate.parseCustomElement(ele);
                    }
                }
            }
        }
        else {
            delegate.parseCustomElement(root);
        }
    }
    
    private void parseDefaultElement(final Element ele, final BeanDefinitionParserDelegate delegate) {
        if (delegate.nodeNameEquals(ele, "import")) {
            this.importBeanDefinitionResource(ele);
        }
        else if (delegate.nodeNameEquals(ele, "alias")) {
            this.processAliasRegistration(ele);
        }
        else if (delegate.nodeNameEquals(ele, "bean")) {
            this.processBeanDefinition(ele, delegate);
        }
        else if (delegate.nodeNameEquals(ele, "beans")) {
            this.doRegisterBeanDefinitions(ele);
        }
    }
    
    protected void importBeanDefinitionResource(final Element ele) {
        String location = ele.getAttribute("resource");
        if (!StringUtils.hasText(location)) {
            this.getReaderContext().error("Resource location must not be empty", ele);
            return;
        }
        location = this.environment.resolveRequiredPlaceholders(location);
        final Set<Resource> actualResources = new LinkedHashSet<Resource>(4);
        boolean absoluteLocation = false;
        try {
            absoluteLocation = (ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute());
        }
        catch (URISyntaxException ex3) {}
        if (absoluteLocation) {
            try {
                final int importCount = this.getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Imported " + importCount + " bean definitions from URL location [" + location + "]");
                }
            }
            catch (BeanDefinitionStoreException ex) {
                this.getReaderContext().error("Failed to import bean definitions from URL location [" + location + "]", ele, ex);
            }
        }
        else {
            try {
                final Resource relativeResource = this.getReaderContext().getResource().createRelative(location);
                int importCount;
                if (relativeResource.exists()) {
                    importCount = this.getReaderContext().getReader().loadBeanDefinitions(relativeResource);
                    actualResources.add(relativeResource);
                }
                else {
                    final String baseLocation = this.getReaderContext().getResource().getURL().toString();
                    importCount = this.getReaderContext().getReader().loadBeanDefinitions(StringUtils.applyRelativePath(baseLocation, location), actualResources);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Imported " + importCount + " bean definitions from relative location [" + location + "]");
                }
            }
            catch (IOException ex2) {
                this.getReaderContext().error("Failed to resolve current resource location", ele, ex2);
            }
            catch (BeanDefinitionStoreException ex) {
                this.getReaderContext().error("Failed to import bean definitions from relative location [" + location + "]", ele, ex);
            }
        }
        final Resource[] actResArray = actualResources.toArray(new Resource[actualResources.size()]);
        this.getReaderContext().fireImportProcessed(location, actResArray, this.extractSource(ele));
    }
    
    protected void processAliasRegistration(final Element ele) {
        final String name = ele.getAttribute("name");
        final String alias = ele.getAttribute("alias");
        boolean valid = true;
        if (!StringUtils.hasText(name)) {
            this.getReaderContext().error("Name must not be empty", ele);
            valid = false;
        }
        if (!StringUtils.hasText(alias)) {
            this.getReaderContext().error("Alias must not be empty", ele);
            valid = false;
        }
        if (valid) {
            try {
                this.getReaderContext().getRegistry().registerAlias(name, alias);
            }
            catch (Exception ex) {
                this.getReaderContext().error("Failed to register alias '" + alias + "' for bean with name '" + name + "'", ele, ex);
            }
            this.getReaderContext().fireAliasRegistered(name, alias, this.extractSource(ele));
        }
    }
    
    protected void processBeanDefinition(final Element ele, final BeanDefinitionParserDelegate delegate) {
        BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
        if (bdHolder != null) {
            bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
            try {
                BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, this.getReaderContext().getRegistry());
            }
            catch (BeanDefinitionStoreException ex) {
                this.getReaderContext().error("Failed to register bean definition with name '" + bdHolder.getBeanName() + "'", ele, ex);
            }
            this.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
        }
    }
    
    protected void preProcessXml(final Element root) {
    }
    
    protected void postProcessXml(final Element root) {
    }
}
