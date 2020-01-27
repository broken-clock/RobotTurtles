// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import java.util.Properties;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import java.util.Map;
import org.springframework.beans.factory.config.MapFactoryBean;
import java.util.Set;
import org.springframework.beans.factory.config.SetFactoryBean;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.config.PropertyPathFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.w3c.dom.Element;

public class UtilNamespaceHandler extends NamespaceHandlerSupport
{
    private static final String SCOPE_ATTRIBUTE = "scope";
    
    @Override
    public void init() {
        this.registerBeanDefinitionParser("constant", new ConstantBeanDefinitionParser());
        this.registerBeanDefinitionParser("property-path", new PropertyPathBeanDefinitionParser());
        this.registerBeanDefinitionParser("list", new ListBeanDefinitionParser());
        this.registerBeanDefinitionParser("set", new SetBeanDefinitionParser());
        this.registerBeanDefinitionParser("map", new MapBeanDefinitionParser());
        this.registerBeanDefinitionParser("properties", new PropertiesBeanDefinitionParser());
    }
    
    private static class ConstantBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser
    {
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return FieldRetrievingFactoryBean.class;
        }
        
        @Override
        protected String resolveId(final Element element, final AbstractBeanDefinition definition, final ParserContext parserContext) {
            String id = super.resolveId(element, definition, parserContext);
            if (!StringUtils.hasText(id)) {
                id = element.getAttribute("static-field");
            }
            return id;
        }
    }
    
    private static class PropertyPathBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
    {
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return PropertyPathFactoryBean.class;
        }
        
        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            final String path = element.getAttribute("path");
            if (!StringUtils.hasText(path)) {
                parserContext.getReaderContext().error("Attribute 'path' must not be empty", element);
                return;
            }
            final int dotIndex = path.indexOf(".");
            if (dotIndex == -1) {
                parserContext.getReaderContext().error("Attribute 'path' must follow pattern 'beanName.propertyName'", element);
                return;
            }
            final String beanName = path.substring(0, dotIndex);
            final String propertyPath = path.substring(dotIndex + 1);
            builder.addPropertyValue("targetBeanName", beanName);
            builder.addPropertyValue("propertyPath", propertyPath);
        }
        
        @Override
        protected String resolveId(final Element element, final AbstractBeanDefinition definition, final ParserContext parserContext) {
            String id = super.resolveId(element, definition, parserContext);
            if (!StringUtils.hasText(id)) {
                id = element.getAttribute("path");
            }
            return id;
        }
    }
    
    private static class ListBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
    {
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return ListFactoryBean.class;
        }
        
        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            final String listClass = element.getAttribute("list-class");
            final List<Object> parsedList = parserContext.getDelegate().parseListElement(element, builder.getRawBeanDefinition());
            builder.addPropertyValue("sourceList", parsedList);
            if (StringUtils.hasText(listClass)) {
                builder.addPropertyValue("targetListClass", listClass);
            }
            final String scope = element.getAttribute("scope");
            if (StringUtils.hasLength(scope)) {
                builder.setScope(scope);
            }
        }
    }
    
    private static class SetBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
    {
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return SetFactoryBean.class;
        }
        
        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            final String setClass = element.getAttribute("set-class");
            final Set<Object> parsedSet = parserContext.getDelegate().parseSetElement(element, builder.getRawBeanDefinition());
            builder.addPropertyValue("sourceSet", parsedSet);
            if (StringUtils.hasText(setClass)) {
                builder.addPropertyValue("targetSetClass", setClass);
            }
            final String scope = element.getAttribute("scope");
            if (StringUtils.hasLength(scope)) {
                builder.setScope(scope);
            }
        }
    }
    
    private static class MapBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
    {
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return MapFactoryBean.class;
        }
        
        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            final String mapClass = element.getAttribute("map-class");
            final Map<Object, Object> parsedMap = parserContext.getDelegate().parseMapElement(element, builder.getRawBeanDefinition());
            builder.addPropertyValue("sourceMap", parsedMap);
            if (StringUtils.hasText(mapClass)) {
                builder.addPropertyValue("targetMapClass", mapClass);
            }
            final String scope = element.getAttribute("scope");
            if (StringUtils.hasLength(scope)) {
                builder.setScope(scope);
            }
        }
    }
    
    private static class PropertiesBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser
    {
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return PropertiesFactoryBean.class;
        }
        
        @Override
        protected boolean isEligibleAttribute(final String attributeName) {
            return super.isEligibleAttribute(attributeName) && !"scope".equals(attributeName);
        }
        
        @Override
        protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
            super.doParse(element, parserContext, builder);
            final Properties parsedProps = parserContext.getDelegate().parsePropsElement(element);
            builder.addPropertyValue("properties", parsedProps);
            final String scope = element.getAttribute("scope");
            if (StringUtils.hasLength(scope)) {
                builder.setScope(scope);
            }
        }
    }
}
