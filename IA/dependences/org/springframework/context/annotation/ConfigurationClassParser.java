// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Location;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import java.util.ArrayList;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.parsing.Problem;
import java.util.Stack;
import java.util.Collections;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import java.io.FileNotFoundException;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.core.type.MethodMetadata;
import java.util.Collection;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.beans.factory.config.BeanDefinition;
import java.util.Iterator;
import java.io.IOException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import java.util.LinkedList;
import org.springframework.util.LinkedMultiValueMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.support.BeanNameGenerator;
import java.util.List;
import org.springframework.core.env.PropertySource;
import org.springframework.util.MultiValueMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import java.util.Comparator;

class ConfigurationClassParser
{
    private static final Comparator<DeferredImportSelectorHolder> DEFERRED_IMPORT_COMPARATOR;
    private final MetadataReaderFactory metadataReaderFactory;
    private final ProblemReporter problemReporter;
    private final Environment environment;
    private final ResourceLoader resourceLoader;
    private final BeanDefinitionRegistry registry;
    private final ComponentScanAnnotationParser componentScanParser;
    private final Set<ConfigurationClass> configurationClasses;
    private final Map<String, ConfigurationClass> knownSuperclasses;
    private final MultiValueMap<String, PropertySource<?>> propertySources;
    private final ImportStack importStack;
    private final List<DeferredImportSelectorHolder> deferredImportSelectors;
    private final ConditionEvaluator conditionEvaluator;
    
    public ConfigurationClassParser(final MetadataReaderFactory metadataReaderFactory, final ProblemReporter problemReporter, final Environment environment, final ResourceLoader resourceLoader, final BeanNameGenerator componentScanBeanNameGenerator, final BeanDefinitionRegistry registry) {
        this.configurationClasses = new LinkedHashSet<ConfigurationClass>();
        this.knownSuperclasses = new HashMap<String, ConfigurationClass>();
        this.propertySources = new LinkedMultiValueMap<String, PropertySource<?>>();
        this.importStack = new ImportStack();
        this.deferredImportSelectors = new LinkedList<DeferredImportSelectorHolder>();
        this.metadataReaderFactory = metadataReaderFactory;
        this.problemReporter = problemReporter;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
        this.registry = registry;
        this.componentScanParser = new ComponentScanAnnotationParser(resourceLoader, environment, componentScanBeanNameGenerator, registry);
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, resourceLoader);
    }
    
    public void parse(final Set<BeanDefinitionHolder> configCandidates) {
        for (final BeanDefinitionHolder holder : configCandidates) {
            final BeanDefinition bd = holder.getBeanDefinition();
            try {
                if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)bd).hasBeanClass()) {
                    this.parse(((AbstractBeanDefinition)bd).getBeanClass(), holder.getBeanName());
                }
                else {
                    this.parse(bd.getBeanClassName(), holder.getBeanName());
                }
            }
            catch (IOException ex) {
                throw new BeanDefinitionStoreException("Failed to load bean class: " + bd.getBeanClassName(), ex);
            }
        }
        this.processDeferredImportSelectors();
    }
    
    protected final void parse(final String className, final String beanName) throws IOException {
        final MetadataReader reader = this.metadataReaderFactory.getMetadataReader(className);
        this.processConfigurationClass(new ConfigurationClass(reader, beanName));
    }
    
    protected final void parse(final Class<?> clazz, final String beanName) throws IOException {
        this.processConfigurationClass(new ConfigurationClass(clazz, beanName));
    }
    
    protected void processConfigurationClass(final ConfigurationClass configClass) throws IOException {
        if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION)) {
            return;
        }
        if (this.configurationClasses.contains(configClass) && configClass.getBeanName() != null) {
            this.configurationClasses.remove(configClass);
            final Iterator<ConfigurationClass> it = this.knownSuperclasses.values().iterator();
            while (it.hasNext()) {
                if (configClass.equals(it.next())) {
                    it.remove();
                }
            }
        }
        SourceClass sourceClass = this.asSourceClass(configClass);
        do {
            sourceClass = this.doProcessConfigurationClass(configClass, sourceClass);
        } while (sourceClass != null);
        this.configurationClasses.add(configClass);
    }
    
    protected final SourceClass doProcessConfigurationClass(final ConfigurationClass configClass, final SourceClass sourceClass) throws IOException {
        this.processMemberClasses(configClass, sourceClass);
        for (final AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), PropertySources.class, org.springframework.context.annotation.PropertySource.class)) {
            this.processPropertySource(propertySource);
        }
        final AnnotationAttributes componentScan = AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ComponentScan.class);
        if (componentScan != null && !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN)) {
            final Set<BeanDefinitionHolder> scannedBeanDefinitions = this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
            for (final BeanDefinitionHolder holder : scannedBeanDefinitions) {
                if (ConfigurationClassUtils.checkConfigurationClassCandidate(holder.getBeanDefinition(), this.metadataReaderFactory)) {
                    this.parse(holder.getBeanDefinition().getBeanClassName(), holder.getBeanName());
                }
            }
        }
        this.processImports(configClass, sourceClass, this.getImports(sourceClass), true);
        if (sourceClass.getMetadata().isAnnotated(ImportResource.class.getName())) {
            final AnnotationAttributes importResource = AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
            final String[] resources = importResource.getStringArray("value");
            final Class<? extends BeanDefinitionReader> readerClass = (Class<? extends BeanDefinitionReader>)importResource.getClass("reader");
            for (final String resource : resources) {
                final String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
                configClass.addImportedResource(resolvedResource, readerClass);
            }
        }
        final Set<MethodMetadata> beanMethods = sourceClass.getMetadata().getAnnotatedMethods(Bean.class.getName());
        for (final MethodMetadata methodMetadata : beanMethods) {
            configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
        }
        if (sourceClass.getMetadata().hasSuperClass()) {
            final String superclass = sourceClass.getMetadata().getSuperClassName();
            if (!this.knownSuperclasses.containsKey(superclass)) {
                this.knownSuperclasses.put(superclass, configClass);
                try {
                    return sourceClass.getSuperClass();
                }
                catch (ClassNotFoundException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
        return null;
    }
    
    private void processMemberClasses(final ConfigurationClass configClass, final SourceClass sourceClass) throws IOException {
        for (final SourceClass memberClass : sourceClass.getMemberClasses()) {
            if (ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata())) {
                this.processConfigurationClass(memberClass.asConfigClass(configClass));
            }
        }
    }
    
    private void processPropertySource(final AnnotationAttributes propertySource) throws IOException {
        final String name = propertySource.getString("name");
        final String[] locations = propertySource.getStringArray("value");
        final boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");
        final int locationCount = locations.length;
        if (locationCount == 0) {
            throw new IllegalArgumentException("At least one @PropertySource(value) location is required");
        }
        for (final String location : locations) {
            final Resource resource = this.resourceLoader.getResource(this.environment.resolveRequiredPlaceholders(location));
            try {
                if (!StringUtils.hasText(name) || this.propertySources.containsKey(name)) {
                    final ResourcePropertySource ps = new ResourcePropertySource(resource);
                    this.propertySources.add(StringUtils.hasText(name) ? name : ps.getName(), ps);
                }
                else {
                    this.propertySources.add(name, new ResourcePropertySource(name, resource));
                }
            }
            catch (FileNotFoundException ex) {
                if (!ignoreResourceNotFound) {
                    throw ex;
                }
            }
        }
    }
    
    private Set<SourceClass> getImports(final SourceClass sourceClass) throws IOException {
        final Set<SourceClass> imports = new LinkedHashSet<SourceClass>();
        final Set<SourceClass> visited = new LinkedHashSet<SourceClass>();
        this.collectImports(sourceClass, imports, visited);
        return imports;
    }
    
    private void collectImports(final SourceClass sourceClass, final Set<SourceClass> imports, final Set<SourceClass> visited) throws IOException {
        try {
            if (visited.add(sourceClass)) {
                for (final SourceClass annotation : sourceClass.getAnnotations()) {
                    final String annName = annotation.getMetadata().getClassName();
                    if (!annName.startsWith("java") && !annName.equals(Import.class.getName())) {
                        this.collectImports(annotation, imports, visited);
                    }
                }
                imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
            }
        }
        catch (ClassNotFoundException ex) {
            throw new NestedIOException("Unable to collect imports", ex);
        }
    }
    
    private void processDeferredImportSelectors() {
        Collections.sort(this.deferredImportSelectors, ConfigurationClassParser.DEFERRED_IMPORT_COMPARATOR);
        for (final DeferredImportSelectorHolder deferredImport : this.deferredImportSelectors) {
            try {
                final ConfigurationClass configClass = deferredImport.getConfigurationClass();
                final String[] imports = deferredImport.getImportSelector().selectImports(configClass.getMetadata());
                this.processImports(configClass, this.asSourceClass(configClass), this.asSourceClasses(imports), false);
            }
            catch (Exception ex) {
                throw new BeanDefinitionStoreException("Failed to load bean class: ", ex);
            }
        }
        this.deferredImportSelectors.clear();
    }
    
    private void processImports(final ConfigurationClass configClass, final SourceClass currentSourceClass, final Collection<SourceClass> importCandidates, final boolean checkForCircularImports) throws IOException {
        if (importCandidates.isEmpty()) {
            return;
        }
        if (checkForCircularImports && this.importStack.contains(configClass)) {
            this.problemReporter.error(new CircularImportProblem(configClass, this.importStack, configClass.getMetadata()));
        }
        else {
            this.importStack.push(configClass);
            try {
                for (final SourceClass candidate : importCandidates) {
                    if (candidate.isAssignable(ImportSelector.class)) {
                        final Class<?> candidateClass = candidate.loadClass();
                        final ImportSelector selector = BeanUtils.instantiateClass(candidateClass, ImportSelector.class);
                        this.invokeAwareMethods(selector);
                        if (selector instanceof DeferredImportSelector) {
                            this.deferredImportSelectors.add(new DeferredImportSelectorHolder(configClass, (DeferredImportSelector)selector));
                        }
                        else {
                            final String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                            final Collection<SourceClass> importSourceClasses = this.asSourceClasses(importClassNames);
                            this.processImports(configClass, currentSourceClass, importSourceClasses, false);
                        }
                    }
                    else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                        final Class<?> candidateClass = candidate.loadClass();
                        final ImportBeanDefinitionRegistrar registrar = BeanUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class);
                        this.invokeAwareMethods(registrar);
                        configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                    }
                    else {
                        this.importStack.registerImport(currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                        this.processConfigurationClass(candidate.asConfigClass(configClass));
                    }
                }
            }
            catch (ClassNotFoundException ex) {
                throw new NestedIOException("Failed to load import candidate class", ex);
            }
            finally {
                this.importStack.pop();
            }
        }
    }
    
    private void invokeAwareMethods(final Object importStrategyBean) {
        if (importStrategyBean instanceof Aware) {
            if (importStrategyBean instanceof EnvironmentAware) {
                ((EnvironmentAware)importStrategyBean).setEnvironment(this.environment);
            }
            if (importStrategyBean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)importStrategyBean).setResourceLoader(this.resourceLoader);
            }
            if (importStrategyBean instanceof BeanClassLoaderAware) {
                final ClassLoader classLoader = (this.registry instanceof ConfigurableBeanFactory) ? ((ConfigurableBeanFactory)this.registry).getBeanClassLoader() : this.resourceLoader.getClassLoader();
                ((BeanClassLoaderAware)importStrategyBean).setBeanClassLoader(classLoader);
            }
            if (importStrategyBean instanceof BeanFactoryAware && this.registry instanceof BeanFactory) {
                ((BeanFactoryAware)importStrategyBean).setBeanFactory((BeanFactory)this.registry);
            }
        }
    }
    
    public void validate() {
        for (final ConfigurationClass configClass : this.configurationClasses) {
            configClass.validate(this.problemReporter);
        }
    }
    
    public Set<ConfigurationClass> getConfigurationClasses() {
        return this.configurationClasses;
    }
    
    public List<PropertySource<?>> getPropertySources() {
        final List<PropertySource<?>> propertySources = new LinkedList<PropertySource<?>>();
        for (final Map.Entry<String, List<PropertySource<?>>> entry : this.propertySources.entrySet()) {
            propertySources.add(0, this.collatePropertySources(entry.getKey(), entry.getValue()));
        }
        return propertySources;
    }
    
    private PropertySource<?> collatePropertySources(final String name, final List<PropertySource<?>> propertySources) {
        if (propertySources.size() == 1) {
            return propertySources.get(0);
        }
        final CompositePropertySource result = new CompositePropertySource(name);
        for (int i = propertySources.size() - 1; i >= 0; --i) {
            result.addPropertySource(propertySources.get(i));
        }
        return result;
    }
    
    ImportRegistry getImportRegistry() {
        return this.importStack;
    }
    
    public SourceClass asSourceClass(final ConfigurationClass configurationClass) throws IOException {
        try {
            final AnnotationMetadata metadata = configurationClass.getMetadata();
            if (metadata instanceof StandardAnnotationMetadata) {
                return this.asSourceClass(((StandardAnnotationMetadata)metadata).getIntrospectedClass());
            }
            return this.asSourceClass(configurationClass.getMetadata().getClassName());
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    public SourceClass asSourceClass(final Class<?> classType) throws IOException, ClassNotFoundException {
        try {
            classType.getAnnotations();
            return new SourceClass(classType);
        }
        catch (Throwable ex) {
            return this.asSourceClass(classType.getName());
        }
    }
    
    public Collection<SourceClass> asSourceClasses(final String[] classNames) throws IOException, ClassNotFoundException {
        final List<SourceClass> annotatedClasses = new ArrayList<SourceClass>();
        for (final String className : classNames) {
            annotatedClasses.add(this.asSourceClass(className));
        }
        return annotatedClasses;
    }
    
    public SourceClass asSourceClass(final String className) throws IOException, ClassNotFoundException {
        if (className.startsWith("java")) {
            return new SourceClass(this.resourceLoader.getClassLoader().loadClass(className));
        }
        return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
    }
    
    static {
        DEFERRED_IMPORT_COMPARATOR = new Comparator<DeferredImportSelectorHolder>() {
            @Override
            public int compare(final DeferredImportSelectorHolder o1, final DeferredImportSelectorHolder o2) {
                return AnnotationAwareOrderComparator.INSTANCE.compare(o1.getImportSelector(), o2.getImportSelector());
            }
        };
    }
    
    private static class ImportStack extends Stack<ConfigurationClass> implements ImportRegistry
    {
        private final Map<String, AnnotationMetadata> imports;
        
        private ImportStack() {
            this.imports = new HashMap<String, AnnotationMetadata>();
        }
        
        public void registerImport(final AnnotationMetadata importingClass, final String importedClass) {
            this.imports.put(importedClass, importingClass);
        }
        
        @Override
        public AnnotationMetadata getImportingClassFor(final String importedClass) {
            return this.imports.get(importedClass);
        }
        
        @Override
        public boolean contains(final Object elem) {
            final ConfigurationClass configClass = (ConfigurationClass)elem;
            final Comparator<ConfigurationClass> comparator = new Comparator<ConfigurationClass>() {
                @Override
                public int compare(final ConfigurationClass first, final ConfigurationClass second) {
                    return first.getMetadata().getClassName().equals(second.getMetadata().getClassName()) ? 0 : 1;
                }
            };
            return Collections.binarySearch(this, configClass, comparator) != -1;
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("ImportStack: [");
            final Iterator<ConfigurationClass> iterator = this.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next().getSimpleName());
                if (iterator.hasNext()) {
                    builder.append("->");
                }
            }
            return builder.append(']').toString();
        }
    }
    
    private static class DeferredImportSelectorHolder
    {
        private final ConfigurationClass configurationClass;
        private final DeferredImportSelector importSelector;
        
        public DeferredImportSelectorHolder(final ConfigurationClass configurationClass, final DeferredImportSelector importSelector) {
            this.configurationClass = configurationClass;
            this.importSelector = importSelector;
        }
        
        public ConfigurationClass getConfigurationClass() {
            return this.configurationClass;
        }
        
        public DeferredImportSelector getImportSelector() {
            return this.importSelector;
        }
    }
    
    private class SourceClass
    {
        private final Object source;
        private final AnnotationMetadata metadata;
        
        public SourceClass(final Object source) {
            this.source = source;
            if (source instanceof Class) {
                this.metadata = new StandardAnnotationMetadata((Class<?>)source, true);
            }
            else {
                this.metadata = ((MetadataReader)source).getAnnotationMetadata();
            }
        }
        
        public final AnnotationMetadata getMetadata() {
            return this.metadata;
        }
        
        public Class<?> loadClass() throws ClassNotFoundException {
            if (this.source instanceof Class) {
                return (Class<?>)this.source;
            }
            final String className = ((MetadataReader)this.source).getClassMetadata().getClassName();
            return ConfigurationClassParser.this.resourceLoader.getClassLoader().loadClass(className);
        }
        
        public boolean isAssignable(final Class<?> clazz) throws IOException {
            if (this.source instanceof Class) {
                return clazz.isAssignableFrom((Class<?>)this.source);
            }
            return new AssignableTypeFilter(clazz).match((MetadataReader)this.source, ConfigurationClassParser.this.metadataReaderFactory);
        }
        
        public ConfigurationClass asConfigClass(final ConfigurationClass importedBy) throws IOException {
            if (this.source instanceof Class) {
                return new ConfigurationClass((Class<?>)this.source, importedBy);
            }
            return new ConfigurationClass((MetadataReader)this.source, importedBy);
        }
        
        public Collection<SourceClass> getMemberClasses() throws IOException {
            final List<SourceClass> members = new ArrayList<SourceClass>();
            if (this.source instanceof Class) {
                final Class<?> sourceClass = (Class<?>)this.source;
                for (final Class<?> declaredClass : sourceClass.getDeclaredClasses()) {
                    try {
                        members.add(ConfigurationClassParser.this.asSourceClass(declaredClass));
                    }
                    catch (ClassNotFoundException ex) {}
                }
            }
            else {
                final MetadataReader sourceReader = (MetadataReader)this.source;
                for (final String memberClassName : sourceReader.getClassMetadata().getMemberClassNames()) {
                    try {
                        members.add(ConfigurationClassParser.this.asSourceClass(memberClassName));
                    }
                    catch (ClassNotFoundException ex2) {}
                }
            }
            return members;
        }
        
        public SourceClass getSuperClass() throws IOException, ClassNotFoundException {
            if (this.source instanceof Class) {
                return ConfigurationClassParser.this.asSourceClass(((Class)this.source).getSuperclass());
            }
            return ConfigurationClassParser.this.asSourceClass(((MetadataReader)this.source).getClassMetadata().getSuperClassName());
        }
        
        public Set<SourceClass> getAnnotations() throws IOException {
            final Set<SourceClass> result = new LinkedHashSet<SourceClass>();
            for (final String className : this.metadata.getAnnotationTypes()) {
                try {
                    result.add(this.getRelated(className));
                }
                catch (Throwable t) {}
            }
            return result;
        }
        
        public Collection<SourceClass> getAnnotationAttributes(final String annotationType, final String attribute) throws IOException, ClassNotFoundException {
            final Map<String, Object> annotationAttributes = this.metadata.getAnnotationAttributes(annotationType, true);
            if (annotationAttributes == null || !annotationAttributes.containsKey(attribute)) {
                return (Collection<SourceClass>)Collections.emptySet();
            }
            final String[] classNames = annotationAttributes.get(attribute);
            final Set<SourceClass> result = new LinkedHashSet<SourceClass>();
            for (final String className : classNames) {
                result.add(this.getRelated(className));
            }
            return result;
        }
        
        private SourceClass getRelated(final String className) throws IOException, ClassNotFoundException {
            if (this.source instanceof Class) {
                try {
                    final Class<?> clazz = ConfigurationClassParser.this.resourceLoader.getClassLoader().loadClass(className);
                    return ConfigurationClassParser.this.asSourceClass(clazz);
                }
                catch (ClassNotFoundException ex) {
                    if (className.startsWith("java")) {
                        throw ex;
                    }
                    return new SourceClass(ConfigurationClassParser.this.metadataReaderFactory.getMetadataReader(className));
                }
            }
            return ConfigurationClassParser.this.asSourceClass(className);
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof SourceClass && this.metadata.getClassName().equals(((SourceClass)other).metadata.getClassName()));
        }
        
        @Override
        public int hashCode() {
            return this.metadata.getClassName().hashCode();
        }
        
        @Override
        public String toString() {
            return this.metadata.getClassName();
        }
    }
    
    private static class CircularImportProblem extends Problem
    {
        public CircularImportProblem(final ConfigurationClass attemptedImport, final Stack<ConfigurationClass> importStack, final AnnotationMetadata metadata) {
            super(String.format("A circular @Import has been detected: Illegal attempt by @Configuration class '%s' to import class '%s' as '%s' is already present in the current import stack [%s]", importStack.peek().getSimpleName(), attemptedImport.getSimpleName(), attemptedImport.getSimpleName(), importStack), new Location(importStack.peek().getResource(), metadata));
        }
    }
    
    interface ImportRegistry
    {
        AnnotationMetadata getImportingClassFor(final String p0);
    }
}
