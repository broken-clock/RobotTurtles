// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.config;

import org.springframework.cache.interceptor.NameMatchCacheOperationSource;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.util.StringUtils;
import org.springframework.cache.interceptor.CacheEvictOperation;
import java.util.ArrayList;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.cache.interceptor.CacheOperation;
import java.util.Collection;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedMap;
import java.util.Iterator;
import org.springframework.beans.factory.support.ManagedList;
import java.util.List;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.util.xml.DomUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;

class CacheAdviceParser extends AbstractSingleBeanDefinitionParser
{
    private static final String CACHEABLE_ELEMENT = "cacheable";
    private static final String CACHE_EVICT_ELEMENT = "cache-evict";
    private static final String CACHE_PUT_ELEMENT = "cache-put";
    private static final String METHOD_ATTRIBUTE = "method";
    private static final String DEFS_ELEMENT = "caching";
    
    @Override
    protected Class<?> getBeanClass(final Element element) {
        return CacheInterceptor.class;
    }
    
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        builder.addPropertyReference("cacheManager", CacheNamespaceHandler.extractCacheManager(element));
        CacheNamespaceHandler.parseKeyGenerator(element, builder.getBeanDefinition());
        final List<Element> cacheDefs = DomUtils.getChildElementsByTagName(element, "caching");
        if (cacheDefs.size() >= 1) {
            final List<RootBeanDefinition> attributeSourceDefinitions = this.parseDefinitionsSources(cacheDefs, parserContext);
            builder.addPropertyValue("cacheOperationSources", attributeSourceDefinitions);
        }
        else {
            builder.addPropertyValue("cacheOperationSources", new RootBeanDefinition(AnnotationCacheOperationSource.class));
        }
    }
    
    private List<RootBeanDefinition> parseDefinitionsSources(final List<Element> definitions, final ParserContext parserContext) {
        final ManagedList<RootBeanDefinition> defs = new ManagedList<RootBeanDefinition>(definitions.size());
        for (final Element element : definitions) {
            defs.add(this.parseDefinitionSource(element, parserContext));
        }
        return defs;
    }
    
    private RootBeanDefinition parseDefinitionSource(final Element definition, final ParserContext parserContext) {
        final Props prop = new Props(definition);
        final ManagedMap<TypedStringValue, Collection<CacheOperation>> cacheOpMap = new ManagedMap<TypedStringValue, Collection<CacheOperation>>();
        cacheOpMap.setSource(parserContext.extractSource(definition));
        final List<Element> cacheableCacheMethods = DomUtils.getChildElementsByTagName(definition, "cacheable");
        for (final Element opElement : cacheableCacheMethods) {
            final String name = prop.merge(opElement, parserContext.getReaderContext());
            final TypedStringValue nameHolder = new TypedStringValue(name);
            nameHolder.setSource(parserContext.extractSource(opElement));
            final CacheableOperation op = prop.merge(opElement, parserContext.getReaderContext(), new CacheableOperation());
            op.setUnless(getAttributeValue(opElement, "unless", ""));
            Collection<CacheOperation> col = cacheOpMap.get(nameHolder);
            if (col == null) {
                col = new ArrayList<CacheOperation>(2);
                cacheOpMap.put(nameHolder, col);
            }
            col.add(op);
        }
        final List<Element> evictCacheMethods = DomUtils.getChildElementsByTagName(definition, "cache-evict");
        for (final Element opElement2 : evictCacheMethods) {
            final String name2 = prop.merge(opElement2, parserContext.getReaderContext());
            final TypedStringValue nameHolder2 = new TypedStringValue(name2);
            nameHolder2.setSource(parserContext.extractSource(opElement2));
            final CacheEvictOperation op2 = prop.merge(opElement2, parserContext.getReaderContext(), new CacheEvictOperation());
            final String wide = opElement2.getAttribute("all-entries");
            if (StringUtils.hasText(wide)) {
                op2.setCacheWide(Boolean.valueOf(wide.trim()));
            }
            final String after = opElement2.getAttribute("before-invocation");
            if (StringUtils.hasText(after)) {
                op2.setBeforeInvocation(Boolean.valueOf(after.trim()));
            }
            Collection<CacheOperation> col2 = cacheOpMap.get(nameHolder2);
            if (col2 == null) {
                col2 = new ArrayList<CacheOperation>(2);
                cacheOpMap.put(nameHolder2, col2);
            }
            col2.add(op2);
        }
        final List<Element> putCacheMethods = DomUtils.getChildElementsByTagName(definition, "cache-put");
        for (final Element opElement3 : putCacheMethods) {
            final String name3 = prop.merge(opElement3, parserContext.getReaderContext());
            final TypedStringValue nameHolder3 = new TypedStringValue(name3);
            nameHolder3.setSource(parserContext.extractSource(opElement3));
            final CachePutOperation op3 = prop.merge(opElement3, parserContext.getReaderContext(), new CachePutOperation());
            op3.setUnless(getAttributeValue(opElement3, "unless", ""));
            Collection<CacheOperation> col3 = cacheOpMap.get(nameHolder3);
            if (col3 == null) {
                col3 = new ArrayList<CacheOperation>(2);
                cacheOpMap.put(nameHolder3, col3);
            }
            col3.add(op3);
        }
        final RootBeanDefinition attributeSourceDefinition = new RootBeanDefinition(NameMatchCacheOperationSource.class);
        attributeSourceDefinition.setSource(parserContext.extractSource(definition));
        attributeSourceDefinition.getPropertyValues().add("nameMap", cacheOpMap);
        return attributeSourceDefinition;
    }
    
    private static String getAttributeValue(final Element element, final String attributeName, final String defaultValue) {
        final String attribute = element.getAttribute(attributeName);
        if (StringUtils.hasText(attribute)) {
            return attribute.trim();
        }
        return defaultValue;
    }
    
    private static class Props
    {
        private String key;
        private String condition;
        private String method;
        private String[] caches;
        
        Props(final Element root) {
            this.caches = null;
            final String defaultCache = root.getAttribute("cache");
            this.key = root.getAttribute("key");
            this.condition = root.getAttribute("condition");
            this.method = root.getAttribute("method");
            if (StringUtils.hasText(defaultCache)) {
                this.caches = StringUtils.commaDelimitedListToStringArray(defaultCache.trim());
            }
        }
        
         <T extends CacheOperation> T merge(final Element element, final ReaderContext readerCtx, final T op) {
            final String cache = element.getAttribute("cache");
            String[] localCaches = this.caches;
            if (StringUtils.hasText(cache)) {
                localCaches = StringUtils.commaDelimitedListToStringArray(cache.trim());
            }
            else if (this.caches == null) {
                readerCtx.error("No cache specified specified for " + element.getNodeName(), element);
            }
            op.setCacheNames(localCaches);
            op.setKey(getAttributeValue(element, "key", this.key));
            op.setCondition(getAttributeValue(element, "condition", this.condition));
            return op;
        }
        
        String merge(final Element element, final ReaderContext readerCtx) {
            final String m = element.getAttribute("method");
            if (StringUtils.hasText(m)) {
                return m.trim();
            }
            if (StringUtils.hasText(this.method)) {
                return this.method;
            }
            readerCtx.error("No method specified for " + element.getNodeName(), element);
            return null;
        }
    }
}
