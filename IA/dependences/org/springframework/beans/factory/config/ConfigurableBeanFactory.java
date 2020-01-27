// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import java.security.AccessControlContext;
import org.springframework.util.StringValueResolver;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.PropertyEditorRegistry;
import java.beans.PropertyEditor;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry
{
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";
    
    void setParentBeanFactory(final BeanFactory p0) throws IllegalStateException;
    
    void setBeanClassLoader(final ClassLoader p0);
    
    ClassLoader getBeanClassLoader();
    
    void setTempClassLoader(final ClassLoader p0);
    
    ClassLoader getTempClassLoader();
    
    void setCacheBeanMetadata(final boolean p0);
    
    boolean isCacheBeanMetadata();
    
    void setBeanExpressionResolver(final BeanExpressionResolver p0);
    
    BeanExpressionResolver getBeanExpressionResolver();
    
    void setConversionService(final ConversionService p0);
    
    ConversionService getConversionService();
    
    void addPropertyEditorRegistrar(final PropertyEditorRegistrar p0);
    
    void registerCustomEditor(final Class<?> p0, final Class<? extends PropertyEditor> p1);
    
    void copyRegisteredEditorsTo(final PropertyEditorRegistry p0);
    
    void setTypeConverter(final TypeConverter p0);
    
    TypeConverter getTypeConverter();
    
    void addEmbeddedValueResolver(final StringValueResolver p0);
    
    String resolveEmbeddedValue(final String p0);
    
    void addBeanPostProcessor(final BeanPostProcessor p0);
    
    int getBeanPostProcessorCount();
    
    void registerScope(final String p0, final Scope p1);
    
    String[] getRegisteredScopeNames();
    
    Scope getRegisteredScope(final String p0);
    
    AccessControlContext getAccessControlContext();
    
    void copyConfigurationFrom(final ConfigurableBeanFactory p0);
    
    void registerAlias(final String p0, final String p1) throws BeanDefinitionStoreException;
    
    void resolveAliases(final StringValueResolver p0);
    
    BeanDefinition getMergedBeanDefinition(final String p0) throws NoSuchBeanDefinitionException;
    
    boolean isFactoryBean(final String p0) throws NoSuchBeanDefinitionException;
    
    void setCurrentlyInCreation(final String p0, final boolean p1);
    
    boolean isCurrentlyInCreation(final String p0);
    
    void registerDependentBean(final String p0, final String p1);
    
    String[] getDependentBeans(final String p0);
    
    String[] getDependenciesForBean(final String p0);
    
    void destroyBean(final String p0, final Object p1);
    
    void destroyScopedBean(final String p0);
    
    void destroySingletons();
}
