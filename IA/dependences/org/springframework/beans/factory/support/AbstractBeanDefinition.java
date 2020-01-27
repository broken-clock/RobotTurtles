// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.util.Arrays;
import org.springframework.util.ObjectUtils;
import java.util.Iterator;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.util.Assert;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.lang.reflect.Constructor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.core.AttributeAccessor;
import org.springframework.beans.PropertyValues;
import java.util.LinkedHashMap;
import org.springframework.core.io.Resource;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.BeanMetadataAttributeAccessor;

public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition, Cloneable
{
    public static final String SCOPE_DEFAULT = "";
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final int DEPENDENCY_CHECK_NONE = 0;
    public static final int DEPENDENCY_CHECK_OBJECTS = 1;
    public static final int DEPENDENCY_CHECK_SIMPLE = 2;
    public static final int DEPENDENCY_CHECK_ALL = 3;
    public static final String INFER_METHOD = "(inferred)";
    private volatile Object beanClass;
    private String scope;
    private boolean abstractFlag;
    private boolean lazyInit;
    private int autowireMode;
    private int dependencyCheck;
    private String[] dependsOn;
    private boolean autowireCandidate;
    private boolean primary;
    private final Map<String, AutowireCandidateQualifier> qualifiers;
    private boolean nonPublicAccessAllowed;
    private boolean lenientConstructorResolution;
    private ConstructorArgumentValues constructorArgumentValues;
    private MutablePropertyValues propertyValues;
    private MethodOverrides methodOverrides;
    private String factoryBeanName;
    private String factoryMethodName;
    private String initMethodName;
    private String destroyMethodName;
    private boolean enforceInitMethod;
    private boolean enforceDestroyMethod;
    private boolean synthetic;
    private int role;
    private String description;
    private Resource resource;
    
    protected AbstractBeanDefinition() {
        this(null, null);
    }
    
    protected AbstractBeanDefinition(final ConstructorArgumentValues cargs, final MutablePropertyValues pvs) {
        this.scope = "";
        this.abstractFlag = false;
        this.lazyInit = false;
        this.autowireMode = 0;
        this.dependencyCheck = 0;
        this.autowireCandidate = true;
        this.primary = false;
        this.qualifiers = new LinkedHashMap<String, AutowireCandidateQualifier>(0);
        this.nonPublicAccessAllowed = true;
        this.lenientConstructorResolution = true;
        this.methodOverrides = new MethodOverrides();
        this.enforceInitMethod = true;
        this.enforceDestroyMethod = true;
        this.synthetic = false;
        this.role = 0;
        this.setConstructorArgumentValues(cargs);
        this.setPropertyValues(pvs);
    }
    
    protected AbstractBeanDefinition(final BeanDefinition original) {
        this.scope = "";
        this.abstractFlag = false;
        this.lazyInit = false;
        this.autowireMode = 0;
        this.dependencyCheck = 0;
        this.autowireCandidate = true;
        this.primary = false;
        this.qualifiers = new LinkedHashMap<String, AutowireCandidateQualifier>(0);
        this.nonPublicAccessAllowed = true;
        this.lenientConstructorResolution = true;
        this.methodOverrides = new MethodOverrides();
        this.enforceInitMethod = true;
        this.enforceDestroyMethod = true;
        this.synthetic = false;
        this.role = 0;
        this.setParentName(original.getParentName());
        this.setBeanClassName(original.getBeanClassName());
        this.setFactoryBeanName(original.getFactoryBeanName());
        this.setFactoryMethodName(original.getFactoryMethodName());
        this.setScope(original.getScope());
        this.setAbstract(original.isAbstract());
        this.setLazyInit(original.isLazyInit());
        this.setRole(original.getRole());
        this.setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
        this.setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
        this.setSource(original.getSource());
        this.copyAttributesFrom(original);
        if (original instanceof AbstractBeanDefinition) {
            final AbstractBeanDefinition originalAbd = (AbstractBeanDefinition)original;
            if (originalAbd.hasBeanClass()) {
                this.setBeanClass(originalAbd.getBeanClass());
            }
            this.setAutowireMode(originalAbd.getAutowireMode());
            this.setDependencyCheck(originalAbd.getDependencyCheck());
            this.setDependsOn(originalAbd.getDependsOn());
            this.setAutowireCandidate(originalAbd.isAutowireCandidate());
            this.copyQualifiersFrom(originalAbd);
            this.setPrimary(originalAbd.isPrimary());
            this.setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
            this.setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
            this.setInitMethodName(originalAbd.getInitMethodName());
            this.setEnforceInitMethod(originalAbd.isEnforceInitMethod());
            this.setDestroyMethodName(originalAbd.getDestroyMethodName());
            this.setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
            this.setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
            this.setSynthetic(originalAbd.isSynthetic());
            this.setResource(originalAbd.getResource());
        }
        else {
            this.setResourceDescription(original.getResourceDescription());
        }
    }
    
    public void overrideFrom(final BeanDefinition other) {
        if (StringUtils.hasLength(other.getBeanClassName())) {
            this.setBeanClassName(other.getBeanClassName());
        }
        if (StringUtils.hasLength(other.getFactoryBeanName())) {
            this.setFactoryBeanName(other.getFactoryBeanName());
        }
        if (StringUtils.hasLength(other.getFactoryMethodName())) {
            this.setFactoryMethodName(other.getFactoryMethodName());
        }
        if (StringUtils.hasLength(other.getScope())) {
            this.setScope(other.getScope());
        }
        this.setAbstract(other.isAbstract());
        this.setLazyInit(other.isLazyInit());
        this.setRole(other.getRole());
        this.getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
        this.getPropertyValues().addPropertyValues(other.getPropertyValues());
        this.setSource(other.getSource());
        this.copyAttributesFrom(other);
        if (other instanceof AbstractBeanDefinition) {
            final AbstractBeanDefinition otherAbd = (AbstractBeanDefinition)other;
            if (otherAbd.hasBeanClass()) {
                this.setBeanClass(otherAbd.getBeanClass());
            }
            this.setAutowireCandidate(otherAbd.isAutowireCandidate());
            this.setAutowireMode(otherAbd.getAutowireMode());
            this.copyQualifiersFrom(otherAbd);
            this.setPrimary(otherAbd.isPrimary());
            this.setDependencyCheck(otherAbd.getDependencyCheck());
            this.setDependsOn(otherAbd.getDependsOn());
            this.setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
            this.setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
            if (StringUtils.hasLength(otherAbd.getInitMethodName())) {
                this.setInitMethodName(otherAbd.getInitMethodName());
                this.setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }
            if (StringUtils.hasLength(otherAbd.getDestroyMethodName())) {
                this.setDestroyMethodName(otherAbd.getDestroyMethodName());
                this.setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }
            this.getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            this.setSynthetic(otherAbd.isSynthetic());
            this.setResource(otherAbd.getResource());
        }
        else {
            this.setResourceDescription(other.getResourceDescription());
        }
    }
    
    public void applyDefaults(final BeanDefinitionDefaults defaults) {
        this.setLazyInit(defaults.isLazyInit());
        this.setAutowireMode(defaults.getAutowireMode());
        this.setDependencyCheck(defaults.getDependencyCheck());
        this.setInitMethodName(defaults.getInitMethodName());
        this.setEnforceInitMethod(false);
        this.setDestroyMethodName(defaults.getDestroyMethodName());
        this.setEnforceDestroyMethod(false);
    }
    
    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }
    
    public void setBeanClass(final Class<?> beanClass) {
        this.beanClass = beanClass;
    }
    
    public Class<?> getBeanClass() throws IllegalStateException {
        final Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return (Class<?>)beanClassObject;
    }
    
    @Override
    public void setBeanClassName(final String beanClassName) {
        this.beanClass = beanClassName;
    }
    
    @Override
    public String getBeanClassName() {
        final Object beanClassObject = this.beanClass;
        if (beanClassObject instanceof Class) {
            return ((Class)beanClassObject).getName();
        }
        return (String)beanClassObject;
    }
    
    public Class<?> resolveBeanClass(final ClassLoader classLoader) throws ClassNotFoundException {
        final String className = this.getBeanClassName();
        if (className == null) {
            return null;
        }
        final Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
        return (Class<?>)(this.beanClass = resolvedClass);
    }
    
    @Override
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    @Override
    public String getScope() {
        return this.scope;
    }
    
    @Override
    public boolean isSingleton() {
        return "singleton".equals(this.scope) || "".equals(this.scope);
    }
    
    @Override
    public boolean isPrototype() {
        return "prototype".equals(this.scope);
    }
    
    public void setAbstract(final boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }
    
    @Override
    public boolean isAbstract() {
        return this.abstractFlag;
    }
    
    @Override
    public void setLazyInit(final boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    @Override
    public boolean isLazyInit() {
        return this.lazyInit;
    }
    
    public void setAutowireMode(final int autowireMode) {
        this.autowireMode = autowireMode;
    }
    
    public int getAutowireMode() {
        return this.autowireMode;
    }
    
    public int getResolvedAutowireMode() {
        if (this.autowireMode == 4) {
            final Constructor<?>[] constructors2;
            final Constructor<?>[] constructors = constructors2 = this.getBeanClass().getConstructors();
            for (final Constructor<?> constructor : constructors2) {
                if (constructor.getParameterTypes().length == 0) {
                    return 2;
                }
            }
            return 3;
        }
        return this.autowireMode;
    }
    
    public void setDependencyCheck(final int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }
    
    public int getDependencyCheck() {
        return this.dependencyCheck;
    }
    
    @Override
    public void setDependsOn(final String[] dependsOn) {
        this.dependsOn = dependsOn;
    }
    
    @Override
    public String[] getDependsOn() {
        return this.dependsOn;
    }
    
    @Override
    public void setAutowireCandidate(final boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }
    
    @Override
    public boolean isAutowireCandidate() {
        return this.autowireCandidate;
    }
    
    @Override
    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }
    
    @Override
    public boolean isPrimary() {
        return this.primary;
    }
    
    public void addQualifier(final AutowireCandidateQualifier qualifier) {
        this.qualifiers.put(qualifier.getTypeName(), qualifier);
    }
    
    public boolean hasQualifier(final String typeName) {
        return this.qualifiers.keySet().contains(typeName);
    }
    
    public AutowireCandidateQualifier getQualifier(final String typeName) {
        return this.qualifiers.get(typeName);
    }
    
    public Set<AutowireCandidateQualifier> getQualifiers() {
        return new LinkedHashSet<AutowireCandidateQualifier>(this.qualifiers.values());
    }
    
    public void copyQualifiersFrom(final AbstractBeanDefinition source) {
        Assert.notNull(source, "Source must not be null");
        this.qualifiers.putAll(source.qualifiers);
    }
    
    public void setNonPublicAccessAllowed(final boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }
    
    public boolean isNonPublicAccessAllowed() {
        return this.nonPublicAccessAllowed;
    }
    
    public void setLenientConstructorResolution(final boolean lenientConstructorResolution) {
        this.lenientConstructorResolution = lenientConstructorResolution;
    }
    
    public boolean isLenientConstructorResolution() {
        return this.lenientConstructorResolution;
    }
    
    public void setConstructorArgumentValues(final ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = ((constructorArgumentValues != null) ? constructorArgumentValues : new ConstructorArgumentValues());
    }
    
    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }
    
    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgumentValues.isEmpty();
    }
    
    public void setPropertyValues(final MutablePropertyValues propertyValues) {
        this.propertyValues = ((propertyValues != null) ? propertyValues : new MutablePropertyValues());
    }
    
    @Override
    public MutablePropertyValues getPropertyValues() {
        return this.propertyValues;
    }
    
    public void setMethodOverrides(final MethodOverrides methodOverrides) {
        this.methodOverrides = ((methodOverrides != null) ? methodOverrides : new MethodOverrides());
    }
    
    public MethodOverrides getMethodOverrides() {
        return this.methodOverrides;
    }
    
    @Override
    public void setFactoryBeanName(final String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
    
    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }
    
    @Override
    public void setFactoryMethodName(final String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }
    
    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }
    
    public void setInitMethodName(final String initMethodName) {
        this.initMethodName = initMethodName;
    }
    
    public String getInitMethodName() {
        return this.initMethodName;
    }
    
    public void setEnforceInitMethod(final boolean enforceInitMethod) {
        this.enforceInitMethod = enforceInitMethod;
    }
    
    public boolean isEnforceInitMethod() {
        return this.enforceInitMethod;
    }
    
    public void setDestroyMethodName(final String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }
    
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }
    
    public void setEnforceDestroyMethod(final boolean enforceDestroyMethod) {
        this.enforceDestroyMethod = enforceDestroyMethod;
    }
    
    public boolean isEnforceDestroyMethod() {
        return this.enforceDestroyMethod;
    }
    
    public void setSynthetic(final boolean synthetic) {
        this.synthetic = synthetic;
    }
    
    public boolean isSynthetic() {
        return this.synthetic;
    }
    
    public void setRole(final int role) {
        this.role = role;
    }
    
    @Override
    public int getRole() {
        return this.role;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    public void setResource(final Resource resource) {
        this.resource = resource;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public void setResourceDescription(final String resourceDescription) {
        this.resource = new DescriptiveResource(resourceDescription);
    }
    
    @Override
    public String getResourceDescription() {
        return (this.resource != null) ? this.resource.getDescription() : null;
    }
    
    public void setOriginatingBeanDefinition(final BeanDefinition originatingBd) {
        this.resource = new BeanDefinitionResource(originatingBd);
    }
    
    @Override
    public BeanDefinition getOriginatingBeanDefinition() {
        return (this.resource instanceof BeanDefinitionResource) ? ((BeanDefinitionResource)this.resource).getBeanDefinition() : null;
    }
    
    public void validate() throws BeanDefinitionValidationException {
        if (!this.getMethodOverrides().isEmpty() && this.getFactoryMethodName() != null) {
            throw new BeanDefinitionValidationException("Cannot combine static factory method with method overrides: the static factory method must create the instance");
        }
        if (this.hasBeanClass()) {
            this.prepareMethodOverrides();
        }
    }
    
    public void prepareMethodOverrides() throws BeanDefinitionValidationException {
        final MethodOverrides methodOverrides = this.getMethodOverrides();
        if (!methodOverrides.isEmpty()) {
            for (final MethodOverride mo : methodOverrides.getOverrides()) {
                this.prepareMethodOverride(mo);
            }
        }
    }
    
    protected void prepareMethodOverride(final MethodOverride mo) throws BeanDefinitionValidationException {
        final int count = ClassUtils.getMethodCountForName(this.getBeanClass(), mo.getMethodName());
        if (count == 0) {
            throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo.getMethodName() + "' on class [" + this.getBeanClassName() + "]");
        }
        if (count == 1) {
            mo.setOverloaded(false);
        }
    }
    
    public Object clone() {
        return this.cloneBeanDefinition();
    }
    
    public abstract AbstractBeanDefinition cloneBeanDefinition();
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractBeanDefinition)) {
            return false;
        }
        final AbstractBeanDefinition that = (AbstractBeanDefinition)other;
        return ObjectUtils.nullSafeEquals(this.getBeanClassName(), that.getBeanClassName()) && ObjectUtils.nullSafeEquals(this.scope, that.scope) && this.abstractFlag == that.abstractFlag && this.lazyInit == that.lazyInit && this.autowireMode == that.autowireMode && this.dependencyCheck == that.dependencyCheck && Arrays.equals(this.dependsOn, that.dependsOn) && this.autowireCandidate == that.autowireCandidate && ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers) && this.primary == that.primary && this.nonPublicAccessAllowed == that.nonPublicAccessAllowed && this.lenientConstructorResolution == that.lenientConstructorResolution && ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues) && ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues) && ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides) && ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName) && ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName) && ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName) && this.enforceInitMethod == that.enforceInitMethod && ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName) && this.enforceDestroyMethod == that.enforceDestroyMethod && this.synthetic == that.synthetic && this.role == that.role && super.equals(other);
    }
    
    @Override
    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.getBeanClassName());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
        hashCode = 29 * hashCode + super.hashCode();
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("class [");
        sb.append(this.getBeanClassName()).append("]");
        sb.append("; scope=").append(this.scope);
        sb.append("; abstract=").append(this.abstractFlag);
        sb.append("; lazyInit=").append(this.lazyInit);
        sb.append("; autowireMode=").append(this.autowireMode);
        sb.append("; dependencyCheck=").append(this.dependencyCheck);
        sb.append("; autowireCandidate=").append(this.autowireCandidate);
        sb.append("; primary=").append(this.primary);
        sb.append("; factoryBeanName=").append(this.factoryBeanName);
        sb.append("; factoryMethodName=").append(this.factoryMethodName);
        sb.append("; initMethodName=").append(this.initMethodName);
        sb.append("; destroyMethodName=").append(this.destroyMethodName);
        if (this.resource != null) {
            sb.append("; defined in ").append(this.resource.getDescription());
        }
        return sb.toString();
    }
}
