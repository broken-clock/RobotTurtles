// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.util.ObjectUtils;
import org.w3c.dom.NamedNodeMap;
import org.springframework.beans.factory.support.ManagedProperties;
import java.util.Properties;
import org.springframework.beans.factory.support.ManagedMap;
import java.util.Map;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.util.ClassUtils;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.parsing.QualifierEntry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.parsing.PropertyEntry;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.parsing.ConstructorArgumentEntry;
import java.util.Iterator;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.beans.factory.support.MethodOverride;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MethodOverrides;
import org.w3c.dom.NodeList;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.util.PatternMatchUtils;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.util.xml.DomUtils;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import java.util.List;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import java.util.HashSet;
import org.apache.commons.logging.LogFactory;
import java.util.Set;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.parsing.ParseState;
import org.apache.commons.logging.Log;

public class BeanDefinitionParserDelegate
{
    public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
    public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";
    @Deprecated
    public static final String BEAN_NAME_DELIMITERS = ",; ";
    public static final String TRUE_VALUE = "true";
    public static final String FALSE_VALUE = "false";
    public static final String DEFAULT_VALUE = "default";
    public static final String DESCRIPTION_ELEMENT = "description";
    public static final String AUTOWIRE_NO_VALUE = "no";
    public static final String AUTOWIRE_BY_NAME_VALUE = "byName";
    public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
    public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";
    public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";
    public static final String DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE = "all";
    public static final String DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE = "simple";
    public static final String DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE = "objects";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String BEAN_ELEMENT = "bean";
    public static final String META_ELEMENT = "meta";
    public static final String ID_ATTRIBUTE = "id";
    public static final String PARENT_ATTRIBUTE = "parent";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String ABSTRACT_ATTRIBUTE = "abstract";
    public static final String SCOPE_ATTRIBUTE = "scope";
    public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";
    public static final String AUTOWIRE_ATTRIBUTE = "autowire";
    public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";
    public static final String PRIMARY_ATTRIBUTE = "primary";
    public static final String DEPENDENCY_CHECK_ATTRIBUTE = "dependency-check";
    public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";
    public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";
    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
    public static final String INDEX_ATTRIBUTE = "index";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String VALUE_TYPE_ATTRIBUTE = "value-type";
    public static final String KEY_TYPE_ATTRIBUTE = "key-type";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";
    public static final String REPLACED_METHOD_ELEMENT = "replaced-method";
    public static final String REPLACER_ATTRIBUTE = "replacer";
    public static final String ARG_TYPE_ELEMENT = "arg-type";
    public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";
    public static final String REF_ELEMENT = "ref";
    public static final String IDREF_ELEMENT = "idref";
    public static final String BEAN_REF_ATTRIBUTE = "bean";
    public static final String LOCAL_REF_ATTRIBUTE = "local";
    public static final String PARENT_REF_ATTRIBUTE = "parent";
    public static final String VALUE_ELEMENT = "value";
    public static final String NULL_ELEMENT = "null";
    public static final String ARRAY_ELEMENT = "array";
    public static final String LIST_ELEMENT = "list";
    public static final String SET_ELEMENT = "set";
    public static final String MAP_ELEMENT = "map";
    public static final String ENTRY_ELEMENT = "entry";
    public static final String KEY_ELEMENT = "key";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String KEY_REF_ATTRIBUTE = "key-ref";
    public static final String VALUE_REF_ATTRIBUTE = "value-ref";
    public static final String PROPS_ELEMENT = "props";
    public static final String PROP_ELEMENT = "prop";
    public static final String MERGE_ATTRIBUTE = "merge";
    public static final String QUALIFIER_ELEMENT = "qualifier";
    public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";
    public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";
    public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";
    public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";
    public static final String DEFAULT_DEPENDENCY_CHECK_ATTRIBUTE = "default-dependency-check";
    public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";
    public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";
    public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";
    protected final Log logger;
    private final XmlReaderContext readerContext;
    private final DocumentDefaultsDefinition defaults;
    private final ParseState parseState;
    private Environment environment;
    private final Set<String> usedNames;
    
    public BeanDefinitionParserDelegate(final XmlReaderContext readerContext, final Environment environment) {
        this.logger = LogFactory.getLog(this.getClass());
        this.defaults = new DocumentDefaultsDefinition();
        this.parseState = new ParseState();
        this.usedNames = new HashSet<String>();
        Assert.notNull(readerContext, "XmlReaderContext must not be null");
        Assert.notNull(readerContext, "Environment must not be null");
        this.readerContext = readerContext;
        this.environment = environment;
    }
    
    @Deprecated
    public BeanDefinitionParserDelegate(final XmlReaderContext readerContext) {
        this(readerContext, new StandardEnvironment());
    }
    
    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }
    
    public final Environment getEnvironment() {
        return this.environment;
    }
    
    protected Object extractSource(final Element ele) {
        return this.readerContext.extractSource(ele);
    }
    
    protected void error(final String message, final Node source) {
        this.readerContext.error(message, source, this.parseState.snapshot());
    }
    
    protected void error(final String message, final Element source) {
        this.readerContext.error(message, source, this.parseState.snapshot());
    }
    
    protected void error(final String message, final Element source, final Throwable cause) {
        this.readerContext.error(message, source, this.parseState.snapshot(), cause);
    }
    
    public void initDefaults(final Element root) {
        this.initDefaults(root, null);
    }
    
    public void initDefaults(final Element root, final BeanDefinitionParserDelegate parent) {
        this.populateDefaults(this.defaults, (parent != null) ? parent.defaults : null, root);
        this.readerContext.fireDefaultsRegistered(this.defaults);
    }
    
    protected void populateDefaults(final DocumentDefaultsDefinition defaults, final DocumentDefaultsDefinition parentDefaults, final Element root) {
        String lazyInit = root.getAttribute("default-lazy-init");
        if ("default".equals(lazyInit)) {
            lazyInit = ((parentDefaults != null) ? parentDefaults.getLazyInit() : "false");
        }
        defaults.setLazyInit(lazyInit);
        String merge = root.getAttribute("default-merge");
        if ("default".equals(merge)) {
            merge = ((parentDefaults != null) ? parentDefaults.getMerge() : "false");
        }
        defaults.setMerge(merge);
        String autowire = root.getAttribute("default-autowire");
        if ("default".equals(autowire)) {
            autowire = ((parentDefaults != null) ? parentDefaults.getAutowire() : "no");
        }
        defaults.setAutowire(autowire);
        defaults.setDependencyCheck(root.getAttribute("default-dependency-check"));
        if (root.hasAttribute("default-autowire-candidates")) {
            defaults.setAutowireCandidates(root.getAttribute("default-autowire-candidates"));
        }
        else if (parentDefaults != null) {
            defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
        }
        if (root.hasAttribute("default-init-method")) {
            defaults.setInitMethod(root.getAttribute("default-init-method"));
        }
        else if (parentDefaults != null) {
            defaults.setInitMethod(parentDefaults.getInitMethod());
        }
        if (root.hasAttribute("default-destroy-method")) {
            defaults.setDestroyMethod(root.getAttribute("default-destroy-method"));
        }
        else if (parentDefaults != null) {
            defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
        }
        defaults.setSource(this.readerContext.extractSource(root));
    }
    
    public DocumentDefaultsDefinition getDefaults() {
        return this.defaults;
    }
    
    public BeanDefinitionDefaults getBeanDefinitionDefaults() {
        final BeanDefinitionDefaults bdd = new BeanDefinitionDefaults();
        bdd.setLazyInit("TRUE".equalsIgnoreCase(this.defaults.getLazyInit()));
        bdd.setDependencyCheck(this.getDependencyCheck("default"));
        bdd.setAutowireMode(this.getAutowireMode("default"));
        bdd.setInitMethodName(this.defaults.getInitMethod());
        bdd.setDestroyMethodName(this.defaults.getDestroyMethod());
        return bdd;
    }
    
    public String[] getAutowireCandidatePatterns() {
        final String candidatePattern = this.defaults.getAutowireCandidates();
        return (String[])((candidatePattern != null) ? StringUtils.commaDelimitedListToStringArray(candidatePattern) : null);
    }
    
    public BeanDefinitionHolder parseBeanDefinitionElement(final Element ele) {
        return this.parseBeanDefinitionElement(ele, null);
    }
    
    public BeanDefinitionHolder parseBeanDefinitionElement(final Element ele, final BeanDefinition containingBean) {
        final String id = ele.getAttribute("id");
        final String nameAttr = ele.getAttribute("name");
        final List<String> aliases = new ArrayList<String>();
        if (StringUtils.hasLength(nameAttr)) {
            final String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, ",; ");
            aliases.addAll(Arrays.asList(nameArr));
        }
        String beanName = id;
        if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
            beanName = aliases.remove(0);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No XML 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases");
            }
        }
        if (containingBean == null) {
            this.checkNameUniqueness(beanName, aliases, ele);
        }
        final AbstractBeanDefinition beanDefinition = this.parseBeanDefinitionElement(ele, beanName, containingBean);
        if (beanDefinition != null) {
            if (!StringUtils.hasText(beanName)) {
                try {
                    if (containingBean != null) {
                        beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, this.readerContext.getRegistry(), true);
                    }
                    else {
                        beanName = this.readerContext.generateBeanName(beanDefinition);
                        final String beanClassName = beanDefinition.getBeanClassName();
                        if (beanClassName != null && beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() && !this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
                            aliases.add(beanClassName);
                        }
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Neither XML 'id' nor 'name' specified - using generated bean name [" + beanName + "]");
                    }
                }
                catch (Exception ex) {
                    this.error(ex.getMessage(), ele);
                    return null;
                }
            }
            final String[] aliasesArray = StringUtils.toStringArray(aliases);
            return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
        }
        return null;
    }
    
    protected void checkNameUniqueness(final String beanName, final List<String> aliases, final Element beanElement) {
        String foundName = null;
        if (StringUtils.hasText(beanName) && this.usedNames.contains(beanName)) {
            foundName = beanName;
        }
        if (foundName == null) {
            foundName = CollectionUtils.findFirstMatch(this.usedNames, aliases);
        }
        if (foundName != null) {
            this.error("Bean name '" + foundName + "' is already used in this <beans> element", beanElement);
        }
        this.usedNames.add(beanName);
        this.usedNames.addAll(aliases);
    }
    
    public AbstractBeanDefinition parseBeanDefinitionElement(final Element ele, final String beanName, final BeanDefinition containingBean) {
        this.parseState.push(new BeanEntry(beanName));
        String className = null;
        while (true) {
            if (ele.hasAttribute("class")) {
                className = ele.getAttribute("class").trim();
                try {
                    String parent = null;
                    if (ele.hasAttribute("parent")) {
                        parent = ele.getAttribute("parent");
                    }
                    final AbstractBeanDefinition bd = this.createBeanDefinition(className, parent);
                    this.parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
                    bd.setDescription(DomUtils.getChildElementValueByTagName(ele, "description"));
                    this.parseMetaElements(ele, bd);
                    this.parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
                    this.parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
                    this.parseConstructorArgElements(ele, bd);
                    this.parsePropertyElements(ele, bd);
                    this.parseQualifierElements(ele, bd);
                    bd.setResource(this.readerContext.getResource());
                    bd.setSource(this.extractSource(ele));
                    return bd;
                }
                catch (ClassNotFoundException ex) {
                    this.error("Bean class [" + className + "] not found", ele, ex);
                }
                catch (NoClassDefFoundError err) {
                    this.error("Class that bean class [" + className + "] depends on not found", ele, err);
                }
                catch (Throwable ex2) {
                    this.error("Unexpected failure during bean definition parsing", ele, ex2);
                }
                finally {
                    this.parseState.pop();
                }
                return null;
            }
            continue;
        }
    }
    
    public AbstractBeanDefinition parseBeanDefinitionAttributes(final Element ele, final String beanName, final BeanDefinition containingBean, final AbstractBeanDefinition bd) {
        if (ele.hasAttribute("scope")) {
            bd.setScope(ele.getAttribute("scope"));
        }
        else if (containingBean != null) {
            bd.setScope(containingBean.getScope());
        }
        if (ele.hasAttribute("abstract")) {
            bd.setAbstract("true".equals(ele.getAttribute("abstract")));
        }
        String lazyInit = ele.getAttribute("lazy-init");
        if ("default".equals(lazyInit)) {
            lazyInit = this.defaults.getLazyInit();
        }
        bd.setLazyInit("true".equals(lazyInit));
        final String autowire = ele.getAttribute("autowire");
        bd.setAutowireMode(this.getAutowireMode(autowire));
        final String dependencyCheck = ele.getAttribute("dependency-check");
        bd.setDependencyCheck(this.getDependencyCheck(dependencyCheck));
        if (ele.hasAttribute("depends-on")) {
            final String dependsOn = ele.getAttribute("depends-on");
            bd.setDependsOn(StringUtils.tokenizeToStringArray(dependsOn, ",; "));
        }
        final String autowireCandidate = ele.getAttribute("autowire-candidate");
        if ("".equals(autowireCandidate) || "default".equals(autowireCandidate)) {
            final String candidatePattern = this.defaults.getAutowireCandidates();
            if (candidatePattern != null) {
                final String[] patterns = StringUtils.commaDelimitedListToStringArray(candidatePattern);
                bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
            }
        }
        else {
            bd.setAutowireCandidate("true".equals(autowireCandidate));
        }
        if (ele.hasAttribute("primary")) {
            bd.setPrimary("true".equals(ele.getAttribute("primary")));
        }
        if (ele.hasAttribute("init-method")) {
            final String initMethodName = ele.getAttribute("init-method");
            if (!"".equals(initMethodName)) {
                bd.setInitMethodName(initMethodName);
            }
        }
        else if (this.defaults.getInitMethod() != null) {
            bd.setInitMethodName(this.defaults.getInitMethod());
            bd.setEnforceInitMethod(false);
        }
        if (ele.hasAttribute("destroy-method")) {
            final String destroyMethodName = ele.getAttribute("destroy-method");
            if (!"".equals(destroyMethodName)) {
                bd.setDestroyMethodName(destroyMethodName);
            }
        }
        else if (this.defaults.getDestroyMethod() != null) {
            bd.setDestroyMethodName(this.defaults.getDestroyMethod());
            bd.setEnforceDestroyMethod(false);
        }
        if (ele.hasAttribute("factory-method")) {
            bd.setFactoryMethodName(ele.getAttribute("factory-method"));
        }
        if (ele.hasAttribute("factory-bean")) {
            bd.setFactoryBeanName(ele.getAttribute("factory-bean"));
        }
        return bd;
    }
    
    protected AbstractBeanDefinition createBeanDefinition(final String className, final String parentName) throws ClassNotFoundException {
        return BeanDefinitionReaderUtils.createBeanDefinition(parentName, className, this.readerContext.getBeanClassLoader());
    }
    
    public void parseMetaElements(final Element ele, final BeanMetadataAttributeAccessor attributeAccessor) {
        final NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "meta")) {
                final Element metaElement = (Element)node;
                final String key = metaElement.getAttribute("key");
                final String value = metaElement.getAttribute("value");
                final BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
                attribute.setSource(this.extractSource(metaElement));
                attributeAccessor.addMetadataAttribute(attribute);
            }
        }
    }
    
    public int getAutowireMode(final String attValue) {
        String att = attValue;
        if ("default".equals(att)) {
            att = this.defaults.getAutowire();
        }
        int autowire = 0;
        if ("byName".equals(att)) {
            autowire = 1;
        }
        else if ("byType".equals(att)) {
            autowire = 2;
        }
        else if ("constructor".equals(att)) {
            autowire = 3;
        }
        else if ("autodetect".equals(att)) {
            autowire = 4;
        }
        return autowire;
    }
    
    public int getDependencyCheck(final String attValue) {
        String att = attValue;
        if ("default".equals(att)) {
            att = this.defaults.getDependencyCheck();
        }
        if ("all".equals(att)) {
            return 3;
        }
        if ("objects".equals(att)) {
            return 1;
        }
        if ("simple".equals(att)) {
            return 2;
        }
        return 0;
    }
    
    public void parseConstructorArgElements(final Element beanEle, final BeanDefinition bd) {
        final NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "constructor-arg")) {
                this.parseConstructorArgElement((Element)node, bd);
            }
        }
    }
    
    public void parsePropertyElements(final Element beanEle, final BeanDefinition bd) {
        final NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "property")) {
                this.parsePropertyElement((Element)node, bd);
            }
        }
    }
    
    public void parseQualifierElements(final Element beanEle, final AbstractBeanDefinition bd) {
        final NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "qualifier")) {
                this.parseQualifierElement((Element)node, bd);
            }
        }
    }
    
    public void parseLookupOverrideSubElements(final Element beanEle, final MethodOverrides overrides) {
        final NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "lookup-method")) {
                final Element ele = (Element)node;
                final String methodName = ele.getAttribute("name");
                final String beanRef = ele.getAttribute("bean");
                final LookupOverride override = new LookupOverride(methodName, beanRef);
                override.setSource(this.extractSource(ele));
                overrides.addOverride(override);
            }
        }
    }
    
    public void parseReplacedMethodSubElements(final Element beanEle, final MethodOverrides overrides) {
        final NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "replaced-method")) {
                final Element replacedMethodEle = (Element)node;
                final String name = replacedMethodEle.getAttribute("name");
                final String callback = replacedMethodEle.getAttribute("replacer");
                final ReplaceOverride replaceOverride = new ReplaceOverride(name, callback);
                final List<Element> argTypeEles = DomUtils.getChildElementsByTagName(replacedMethodEle, "arg-type");
                for (final Element argTypeEle : argTypeEles) {
                    String match = argTypeEle.getAttribute("match");
                    match = (StringUtils.hasText(match) ? match : DomUtils.getTextValue(argTypeEle));
                    if (StringUtils.hasText(match)) {
                        replaceOverride.addTypeIdentifier(match);
                    }
                }
                replaceOverride.setSource(this.extractSource(replacedMethodEle));
                overrides.addOverride(replaceOverride);
            }
        }
    }
    
    public void parseConstructorArgElement(final Element ele, final BeanDefinition bd) {
        final String indexAttr = ele.getAttribute("index");
        final String typeAttr = ele.getAttribute("type");
        final String nameAttr = ele.getAttribute("name");
        if (StringUtils.hasLength(indexAttr)) {
            try {
                final int index = Integer.parseInt(indexAttr);
                if (index < 0) {
                    this.error("'index' cannot be lower than 0", ele);
                }
                else {
                    try {
                        this.parseState.push(new ConstructorArgumentEntry(index));
                        final Object value = this.parsePropertyValue(ele, bd, null);
                        final ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                        if (StringUtils.hasLength(typeAttr)) {
                            valueHolder.setType(typeAttr);
                        }
                        if (StringUtils.hasLength(nameAttr)) {
                            valueHolder.setName(nameAttr);
                        }
                        valueHolder.setSource(this.extractSource(ele));
                        if (bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
                            this.error("Ambiguous constructor-arg entries for index " + index, ele);
                        }
                        else {
                            bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
                        }
                    }
                    finally {
                        this.parseState.pop();
                    }
                }
            }
            catch (NumberFormatException ex) {
                this.error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
            }
        }
        else {
            try {
                this.parseState.push(new ConstructorArgumentEntry());
                final Object value2 = this.parsePropertyValue(ele, bd, null);
                final ConstructorArgumentValues.ValueHolder valueHolder2 = new ConstructorArgumentValues.ValueHolder(value2);
                if (StringUtils.hasLength(typeAttr)) {
                    valueHolder2.setType(typeAttr);
                }
                if (StringUtils.hasLength(nameAttr)) {
                    valueHolder2.setName(nameAttr);
                }
                valueHolder2.setSource(this.extractSource(ele));
                bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder2);
            }
            finally {
                this.parseState.pop();
            }
        }
    }
    
    public void parsePropertyElement(final Element ele, final BeanDefinition bd) {
        final String propertyName = ele.getAttribute("name");
        if (!StringUtils.hasLength(propertyName)) {
            this.error("Tag 'property' must have a 'name' attribute", ele);
            return;
        }
        this.parseState.push(new PropertyEntry(propertyName));
        try {
            if (bd.getPropertyValues().contains(propertyName)) {
                this.error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
                return;
            }
            final Object val = this.parsePropertyValue(ele, bd, propertyName);
            final PropertyValue pv = new PropertyValue(propertyName, val);
            this.parseMetaElements(ele, pv);
            pv.setSource(this.extractSource(ele));
            bd.getPropertyValues().addPropertyValue(pv);
        }
        finally {
            this.parseState.pop();
        }
    }
    
    public void parseQualifierElement(final Element ele, final AbstractBeanDefinition bd) {
        final String typeName = ele.getAttribute("type");
        if (!StringUtils.hasLength(typeName)) {
            this.error("Tag 'qualifier' must have a 'type' attribute", ele);
            return;
        }
        this.parseState.push(new QualifierEntry(typeName));
        try {
            final AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(typeName);
            qualifier.setSource(this.extractSource(ele));
            final String value = ele.getAttribute("value");
            if (StringUtils.hasLength(value)) {
                qualifier.setAttribute(AutowireCandidateQualifier.VALUE_KEY, value);
            }
            final NodeList nl = ele.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node node = nl.item(i);
                if (this.isCandidateElement(node) && this.nodeNameEquals(node, "attribute")) {
                    final Element attributeEle = (Element)node;
                    final String attributeName = attributeEle.getAttribute("key");
                    final String attributeValue = attributeEle.getAttribute("value");
                    if (!StringUtils.hasLength(attributeName) || !StringUtils.hasLength(attributeValue)) {
                        this.error("Qualifier 'attribute' tag must have a 'name' and 'value'", attributeEle);
                        return;
                    }
                    final BeanMetadataAttribute attribute = new BeanMetadataAttribute(attributeName, attributeValue);
                    attribute.setSource(this.extractSource(attributeEle));
                    qualifier.addMetadataAttribute(attribute);
                }
            }
            bd.addQualifier(qualifier);
        }
        finally {
            this.parseState.pop();
        }
    }
    
    public Object parsePropertyValue(final Element ele, final BeanDefinition bd, final String propertyName) {
        final String elementName = (propertyName != null) ? ("<property> element for property '" + propertyName + "'") : "<constructor-arg> element";
        final NodeList nl = ele.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node instanceof Element && !this.nodeNameEquals(node, "description") && !this.nodeNameEquals(node, "meta")) {
                if (subElement != null) {
                    this.error(elementName + " must not contain more than one sub-element", ele);
                }
                else {
                    subElement = (Element)node;
                }
            }
        }
        final boolean hasRefAttribute = ele.hasAttribute("ref");
        final boolean hasValueAttribute = ele.hasAttribute("value");
        if ((hasRefAttribute && hasValueAttribute) || ((hasRefAttribute || hasValueAttribute) && subElement != null)) {
            this.error(elementName + " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element", ele);
        }
        if (hasRefAttribute) {
            final String refName = ele.getAttribute("ref");
            if (!StringUtils.hasText(refName)) {
                this.error(elementName + " contains empty 'ref' attribute", ele);
            }
            final RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            ref.setSource(this.extractSource(ele));
            return ref;
        }
        if (hasValueAttribute) {
            final TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute("value"));
            valueHolder.setSource(this.extractSource(ele));
            return valueHolder;
        }
        if (subElement != null) {
            return this.parsePropertySubElement(subElement, bd);
        }
        this.error(elementName + " must specify a ref or value", ele);
        return null;
    }
    
    public Object parsePropertySubElement(final Element ele, final BeanDefinition bd) {
        return this.parsePropertySubElement(ele, bd, null);
    }
    
    public Object parsePropertySubElement(final Element ele, final BeanDefinition bd, final String defaultValueType) {
        if (!this.isDefaultNamespace(ele)) {
            return this.parseNestedCustomElement(ele, bd);
        }
        if (this.nodeNameEquals(ele, "bean")) {
            BeanDefinitionHolder nestedBd = this.parseBeanDefinitionElement(ele, bd);
            if (nestedBd != null) {
                nestedBd = this.decorateBeanDefinitionIfRequired(ele, nestedBd, bd);
            }
            return nestedBd;
        }
        if (this.nodeNameEquals(ele, "ref")) {
            String refName = ele.getAttribute("bean");
            boolean toParent = false;
            if (!StringUtils.hasLength(refName)) {
                refName = ele.getAttribute("local");
                if (!StringUtils.hasLength(refName)) {
                    refName = ele.getAttribute("parent");
                    toParent = true;
                    if (!StringUtils.hasLength(refName)) {
                        this.error("'bean', 'local' or 'parent' is required for <ref> element", ele);
                        return null;
                    }
                }
            }
            if (!StringUtils.hasText(refName)) {
                this.error("<ref> element contains empty target attribute", ele);
                return null;
            }
            final RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
            ref.setSource(this.extractSource(ele));
            return ref;
        }
        else {
            if (this.nodeNameEquals(ele, "idref")) {
                return this.parseIdRefElement(ele);
            }
            if (this.nodeNameEquals(ele, "value")) {
                return this.parseValueElement(ele, defaultValueType);
            }
            if (this.nodeNameEquals(ele, "null")) {
                final TypedStringValue nullHolder = new TypedStringValue(null);
                nullHolder.setSource(this.extractSource(ele));
                return nullHolder;
            }
            if (this.nodeNameEquals(ele, "array")) {
                return this.parseArrayElement(ele, bd);
            }
            if (this.nodeNameEquals(ele, "list")) {
                return this.parseListElement(ele, bd);
            }
            if (this.nodeNameEquals(ele, "set")) {
                return this.parseSetElement(ele, bd);
            }
            if (this.nodeNameEquals(ele, "map")) {
                return this.parseMapElement(ele, bd);
            }
            if (this.nodeNameEquals(ele, "props")) {
                return this.parsePropsElement(ele);
            }
            this.error("Unknown property sub-element: [" + ele.getNodeName() + "]", ele);
            return null;
        }
    }
    
    public Object parseIdRefElement(final Element ele) {
        String refName = ele.getAttribute("bean");
        if (!StringUtils.hasLength(refName)) {
            refName = ele.getAttribute("local");
            if (!StringUtils.hasLength(refName)) {
                this.error("Either 'bean' or 'local' is required for <idref> element", ele);
                return null;
            }
        }
        if (!StringUtils.hasText(refName)) {
            this.error("<idref> element contains empty target attribute", ele);
            return null;
        }
        final RuntimeBeanNameReference ref = new RuntimeBeanNameReference(refName);
        ref.setSource(this.extractSource(ele));
        return ref;
    }
    
    public Object parseValueElement(final Element ele, final String defaultTypeName) {
        final String value = DomUtils.getTextValue(ele);
        String typeName;
        final String specifiedTypeName = typeName = ele.getAttribute("type");
        if (!StringUtils.hasText(typeName)) {
            typeName = defaultTypeName;
        }
        try {
            final TypedStringValue typedValue = this.buildTypedStringValue(value, typeName);
            typedValue.setSource(this.extractSource(ele));
            typedValue.setSpecifiedTypeName(specifiedTypeName);
            return typedValue;
        }
        catch (ClassNotFoundException ex) {
            this.error("Type class [" + typeName + "] not found for <value> element", ele, ex);
            return value;
        }
    }
    
    protected TypedStringValue buildTypedStringValue(final String value, final String targetTypeName) throws ClassNotFoundException {
        final ClassLoader classLoader = this.readerContext.getBeanClassLoader();
        TypedStringValue typedValue;
        if (!StringUtils.hasText(targetTypeName)) {
            typedValue = new TypedStringValue(value);
        }
        else if (classLoader != null) {
            final Class<?> targetType = ClassUtils.forName(targetTypeName, classLoader);
            typedValue = new TypedStringValue(value, targetType);
        }
        else {
            typedValue = new TypedStringValue(value, targetTypeName);
        }
        return typedValue;
    }
    
    public Object parseArrayElement(final Element arrayEle, final BeanDefinition bd) {
        final String elementType = arrayEle.getAttribute("value-type");
        final NodeList nl = arrayEle.getChildNodes();
        final ManagedArray target = new ManagedArray(elementType, nl.getLength());
        target.setSource(this.extractSource(arrayEle));
        target.setElementTypeName(elementType);
        target.setMergeEnabled(this.parseMergeAttribute(arrayEle));
        this.parseCollectionElements(nl, target, bd, elementType);
        return target;
    }
    
    public List<Object> parseListElement(final Element collectionEle, final BeanDefinition bd) {
        final String defaultElementType = collectionEle.getAttribute("value-type");
        final NodeList nl = collectionEle.getChildNodes();
        final ManagedList<Object> target = new ManagedList<Object>(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, target, bd, defaultElementType);
        return target;
    }
    
    public Set<Object> parseSetElement(final Element collectionEle, final BeanDefinition bd) {
        final String defaultElementType = collectionEle.getAttribute("value-type");
        final NodeList nl = collectionEle.getChildNodes();
        final ManagedSet<Object> target = new ManagedSet<Object>(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, target, bd, defaultElementType);
        return target;
    }
    
    protected void parseCollectionElements(final NodeList elementNodes, final Collection<Object> target, final BeanDefinition bd, final String defaultElementType) {
        for (int i = 0; i < elementNodes.getLength(); ++i) {
            final Node node = elementNodes.item(i);
            if (node instanceof Element && !this.nodeNameEquals(node, "description")) {
                target.add(this.parsePropertySubElement((Element)node, bd, defaultElementType));
            }
        }
    }
    
    public Map<Object, Object> parseMapElement(final Element mapEle, final BeanDefinition bd) {
        final String defaultKeyType = mapEle.getAttribute("key-type");
        final String defaultValueType = mapEle.getAttribute("value-type");
        final List<Element> entryEles = DomUtils.getChildElementsByTagName(mapEle, "entry");
        final ManagedMap<Object, Object> map = new ManagedMap<Object, Object>(entryEles.size());
        map.setSource(this.extractSource(mapEle));
        map.setKeyTypeName(defaultKeyType);
        map.setValueTypeName(defaultValueType);
        map.setMergeEnabled(this.parseMergeAttribute(mapEle));
        for (final Element entryEle : entryEles) {
            final NodeList entrySubNodes = entryEle.getChildNodes();
            Element keyEle = null;
            Element valueEle = null;
            for (int j = 0; j < entrySubNodes.getLength(); ++j) {
                final Node node = entrySubNodes.item(j);
                if (node instanceof Element) {
                    final Element candidateEle = (Element)node;
                    if (this.nodeNameEquals(candidateEle, "key")) {
                        if (keyEle != null) {
                            this.error("<entry> element is only allowed to contain one <key> sub-element", entryEle);
                        }
                        else {
                            keyEle = candidateEle;
                        }
                    }
                    else if (!this.nodeNameEquals(candidateEle, "description")) {
                        if (valueEle != null) {
                            this.error("<entry> element must not contain more than one value sub-element", entryEle);
                        }
                        else {
                            valueEle = candidateEle;
                        }
                    }
                }
            }
            Object key = null;
            final boolean hasKeyAttribute = entryEle.hasAttribute("key");
            final boolean hasKeyRefAttribute = entryEle.hasAttribute("key-ref");
            if ((hasKeyAttribute && hasKeyRefAttribute) || ((hasKeyAttribute || hasKeyRefAttribute) && keyEle != null)) {
                this.error("<entry> element is only allowed to contain either a 'key' attribute OR a 'key-ref' attribute OR a <key> sub-element", entryEle);
            }
            if (hasKeyAttribute) {
                key = this.buildTypedStringValueForMap(entryEle.getAttribute("key"), defaultKeyType, entryEle);
            }
            else if (hasKeyRefAttribute) {
                final String refName = entryEle.getAttribute("key-ref");
                if (!StringUtils.hasText(refName)) {
                    this.error("<entry> element contains empty 'key-ref' attribute", entryEle);
                }
                final RuntimeBeanReference ref = new RuntimeBeanReference(refName);
                ref.setSource(this.extractSource(entryEle));
                key = ref;
            }
            else if (keyEle != null) {
                key = this.parseKeyElement(keyEle, bd, defaultKeyType);
            }
            else {
                this.error("<entry> element must specify a key", entryEle);
            }
            Object value = null;
            final boolean hasValueAttribute = entryEle.hasAttribute("value");
            final boolean hasValueRefAttribute = entryEle.hasAttribute("value-ref");
            final boolean hasValueTypeAttribute = entryEle.hasAttribute("value-type");
            if ((hasValueAttribute && hasValueRefAttribute) || ((hasValueAttribute || hasValueRefAttribute) && valueEle != null)) {
                this.error("<entry> element is only allowed to contain either 'value' attribute OR 'value-ref' attribute OR <value> sub-element", entryEle);
            }
            if ((hasValueTypeAttribute && hasValueRefAttribute) || (hasValueTypeAttribute && !hasValueAttribute) || (hasValueTypeAttribute && valueEle != null)) {
                this.error("<entry> element is only allowed to contain a 'value-type' attribute when it has a 'value' attribute", entryEle);
            }
            if (hasValueAttribute) {
                String valueType = entryEle.getAttribute("value-type");
                if (!StringUtils.hasText(valueType)) {
                    valueType = defaultValueType;
                }
                value = this.buildTypedStringValueForMap(entryEle.getAttribute("value"), valueType, entryEle);
            }
            else if (hasValueRefAttribute) {
                final String refName2 = entryEle.getAttribute("value-ref");
                if (!StringUtils.hasText(refName2)) {
                    this.error("<entry> element contains empty 'value-ref' attribute", entryEle);
                }
                final RuntimeBeanReference ref2 = new RuntimeBeanReference(refName2);
                ref2.setSource(this.extractSource(entryEle));
                value = ref2;
            }
            else if (valueEle != null) {
                value = this.parsePropertySubElement(valueEle, bd, defaultValueType);
            }
            else {
                this.error("<entry> element must specify a value", entryEle);
            }
            map.put(key, value);
        }
        return map;
    }
    
    protected final Object buildTypedStringValueForMap(final String value, final String defaultTypeName, final Element entryEle) {
        try {
            final TypedStringValue typedValue = this.buildTypedStringValue(value, defaultTypeName);
            typedValue.setSource(this.extractSource(entryEle));
            return typedValue;
        }
        catch (ClassNotFoundException ex) {
            this.error("Type class [" + defaultTypeName + "] not found for Map key/value type", entryEle, ex);
            return value;
        }
    }
    
    protected Object parseKeyElement(final Element keyEle, final BeanDefinition bd, final String defaultKeyTypeName) {
        final NodeList nl = keyEle.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node instanceof Element) {
                if (subElement != null) {
                    this.error("<key> element must not contain more than one value sub-element", keyEle);
                }
                else {
                    subElement = (Element)node;
                }
            }
        }
        return this.parsePropertySubElement(subElement, bd, defaultKeyTypeName);
    }
    
    public Properties parsePropsElement(final Element propsEle) {
        final ManagedProperties props = new ManagedProperties();
        props.setSource(this.extractSource(propsEle));
        props.setMergeEnabled(this.parseMergeAttribute(propsEle));
        final List<Element> propEles = DomUtils.getChildElementsByTagName(propsEle, "prop");
        for (final Element propEle : propEles) {
            final String key = propEle.getAttribute("key");
            final String value = DomUtils.getTextValue(propEle).trim();
            final TypedStringValue keyHolder = new TypedStringValue(key);
            keyHolder.setSource(this.extractSource(propEle));
            final TypedStringValue valueHolder = new TypedStringValue(value);
            valueHolder.setSource(this.extractSource(propEle));
            props.put(keyHolder, valueHolder);
        }
        return props;
    }
    
    public boolean parseMergeAttribute(final Element collectionElement) {
        String value = collectionElement.getAttribute("merge");
        if ("default".equals(value)) {
            value = this.defaults.getMerge();
        }
        return "true".equals(value);
    }
    
    public BeanDefinition parseCustomElement(final Element ele) {
        return this.parseCustomElement(ele, null);
    }
    
    public BeanDefinition parseCustomElement(final Element ele, final BeanDefinition containingBd) {
        final String namespaceUri = this.getNamespaceURI(ele);
        final NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
        if (handler == null) {
            this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
            return null;
        }
        return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
    }
    
    public BeanDefinitionHolder decorateBeanDefinitionIfRequired(final Element ele, final BeanDefinitionHolder definitionHolder) {
        return this.decorateBeanDefinitionIfRequired(ele, definitionHolder, null);
    }
    
    public BeanDefinitionHolder decorateBeanDefinitionIfRequired(final Element ele, final BeanDefinitionHolder definitionHolder, final BeanDefinition containingBd) {
        BeanDefinitionHolder finalDefinition = definitionHolder;
        final NamedNodeMap attributes = ele.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node node = attributes.item(i);
            finalDefinition = this.decorateIfRequired(node, finalDefinition, containingBd);
        }
        final NodeList children = ele.getChildNodes();
        for (int j = 0; j < children.getLength(); ++j) {
            final Node node2 = children.item(j);
            if (node2.getNodeType() == 1) {
                finalDefinition = this.decorateIfRequired(node2, finalDefinition, containingBd);
            }
        }
        return finalDefinition;
    }
    
    public BeanDefinitionHolder decorateIfRequired(final Node node, final BeanDefinitionHolder originalDef, final BeanDefinition containingBd) {
        final String namespaceUri = this.getNamespaceURI(node);
        if (!this.isDefaultNamespace(namespaceUri)) {
            final NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
            if (handler != null) {
                return handler.decorate(node, originalDef, new ParserContext(this.readerContext, this, containingBd));
            }
            if (namespaceUri != null && namespaceUri.startsWith("http://www.springframework.org/")) {
                this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", node);
            }
            else if (this.logger.isDebugEnabled()) {
                this.logger.debug("No Spring NamespaceHandler found for XML schema namespace [" + namespaceUri + "]");
            }
        }
        return originalDef;
    }
    
    private BeanDefinitionHolder parseNestedCustomElement(final Element ele, final BeanDefinition containingBd) {
        final BeanDefinition innerDefinition = this.parseCustomElement(ele, containingBd);
        if (innerDefinition == null) {
            this.error("Incorrect usage of element '" + ele.getNodeName() + "' in a nested manner. " + "This tag cannot be used nested inside <property>.", ele);
            return null;
        }
        final String id = ele.getNodeName() + "#" + ObjectUtils.getIdentityHexString(innerDefinition);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Using generated bean name [" + id + "] for nested custom element '" + ele.getNodeName() + "'");
        }
        return new BeanDefinitionHolder(innerDefinition, id);
    }
    
    public String getNamespaceURI(final Node node) {
        return node.getNamespaceURI();
    }
    
    public String getLocalName(final Node node) {
        return node.getLocalName();
    }
    
    public boolean nodeNameEquals(final Node node, final String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(this.getLocalName(node));
    }
    
    public boolean isDefaultNamespace(final String namespaceUri) {
        return !StringUtils.hasLength(namespaceUri) || "http://www.springframework.org/schema/beans".equals(namespaceUri);
    }
    
    public boolean isDefaultNamespace(final Node node) {
        return this.isDefaultNamespace(this.getNamespaceURI(node));
    }
    
    private boolean isCandidateElement(final Node node) {
        return node instanceof Element && (this.isDefaultNamespace(node) || !this.isDefaultNamespace(node.getParentNode()));
    }
}
