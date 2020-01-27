// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.method;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;
import org.springframework.context.ApplicationContext;
import java.util.Iterator;
import org.springframework.util.StringUtils;
import org.springframework.core.annotation.Order;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.apache.commons.logging.Log;
import org.springframework.core.Ordered;

public class ControllerAdviceBean implements Ordered
{
    private static final Log logger;
    private final Object bean;
    private final int order;
    private final BeanFactory beanFactory;
    private final List<Package> basePackages;
    private final List<Class<? extends Annotation>> annotations;
    private final List<Class<?>> assignableTypes;
    
    public ControllerAdviceBean(final String beanName, final BeanFactory beanFactory) {
        this.basePackages = new ArrayList<Package>();
        this.annotations = new ArrayList<Class<? extends Annotation>>();
        this.assignableTypes = new ArrayList<Class<?>>();
        Assert.hasText(beanName, "'beanName' must not be null");
        Assert.notNull(beanFactory, "'beanFactory' must not be null");
        Assert.isTrue(beanFactory.containsBean(beanName), "Bean factory [" + beanFactory + "] does not contain bean " + "with name [" + beanName + "]");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        final Class<?> beanType = this.beanFactory.getType(beanName);
        this.order = initOrderFromBeanType(beanType);
        final ControllerAdvice annotation = AnnotationUtils.findAnnotation(beanType, ControllerAdvice.class);
        Assert.notNull(annotation, "BeanType [" + beanType.getName() + "] is not annotated @ControllerAdvice");
        this.basePackages.addAll(initBasePackagesFromBeanType(beanType, annotation));
        this.annotations.addAll(Arrays.asList(annotation.annotations()));
        this.assignableTypes.addAll(Arrays.asList(annotation.assignableTypes()));
    }
    
    private static int initOrderFromBeanType(final Class<?> beanType) {
        final Order annot = AnnotationUtils.findAnnotation(beanType, Order.class);
        return (annot != null) ? annot.value() : Integer.MAX_VALUE;
    }
    
    private static List<Package> initBasePackagesFromBeanType(final Class<?> beanType, final ControllerAdvice annotation) {
        final List<Package> basePackages = new ArrayList<Package>();
        final List<String> basePackageNames = new ArrayList<String>();
        basePackageNames.addAll(Arrays.asList(annotation.value()));
        basePackageNames.addAll(Arrays.asList(annotation.basePackages()));
        for (final String pkgName : basePackageNames) {
            if (StringUtils.hasText(pkgName)) {
                final Package pkg = Package.getPackage(pkgName);
                if (pkg != null) {
                    basePackages.add(pkg);
                }
                else {
                    ControllerAdviceBean.logger.warn("Package [" + pkgName + "] was not found, see [" + beanType.getName() + "]");
                }
            }
        }
        for (final Class<?> markerClass : annotation.basePackageClasses()) {
            final Package pack = markerClass.getPackage();
            if (pack != null) {
                basePackages.add(pack);
            }
            else {
                ControllerAdviceBean.logger.warn("Package was not found for class [" + markerClass.getName() + "], see [" + beanType.getName() + "]");
            }
        }
        return basePackages;
    }
    
    public ControllerAdviceBean(final Object bean) {
        this.basePackages = new ArrayList<Package>();
        this.annotations = new ArrayList<Class<? extends Annotation>>();
        this.assignableTypes = new ArrayList<Class<?>>();
        Assert.notNull(bean, "'bean' must not be null");
        this.bean = bean;
        this.order = initOrderFromBean(bean);
        final Class<?> beanType = bean.getClass();
        final ControllerAdvice annotation = AnnotationUtils.findAnnotation(beanType, ControllerAdvice.class);
        Assert.notNull(annotation, "BeanType [" + beanType.getName() + "] is not annotated @ControllerAdvice");
        this.basePackages.addAll(initBasePackagesFromBeanType(beanType, annotation));
        this.annotations.addAll(Arrays.asList(annotation.annotations()));
        this.assignableTypes.addAll(Arrays.asList(annotation.assignableTypes()));
        this.beanFactory = null;
    }
    
    private static int initOrderFromBean(final Object bean) {
        return (bean instanceof Ordered) ? ((Ordered)bean).getOrder() : initOrderFromBeanType(bean.getClass());
    }
    
    public static List<ControllerAdviceBean> findAnnotatedBeans(final ApplicationContext applicationContext) {
        final List<ControllerAdviceBean> beans = new ArrayList<ControllerAdviceBean>();
        for (final String name : applicationContext.getBeanDefinitionNames()) {
            if (applicationContext.findAnnotationOnBean(name, ControllerAdvice.class) != null) {
                beans.add(new ControllerAdviceBean(name, applicationContext));
            }
        }
        return beans;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    public Class<?> getBeanType() {
        final Class<?> clazz = (this.bean instanceof String) ? this.beanFactory.getType((String)this.bean) : this.bean.getClass();
        return ClassUtils.getUserClass(clazz);
    }
    
    public Object resolveBean() {
        return (this.bean instanceof String) ? this.beanFactory.getBean((String)this.bean) : this.bean;
    }
    
    public boolean isApplicableToBeanType(final Class<?> beanType) {
        if (!this.hasSelectors()) {
            return true;
        }
        if (beanType != null) {
            for (final Class<?> clazz : this.assignableTypes) {
                if (ClassUtils.isAssignable(clazz, beanType)) {
                    return true;
                }
            }
            for (final Class<? extends Annotation> annotationClass : this.annotations) {
                if (AnnotationUtils.findAnnotation(beanType, annotationClass) != null) {
                    return true;
                }
            }
            final String packageName = beanType.getPackage().getName();
            for (final Package basePackage : this.basePackages) {
                if (packageName.startsWith(basePackage.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasSelectors() {
        return !this.basePackages.isEmpty() || !this.annotations.isEmpty() || !this.assignableTypes.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o instanceof ControllerAdviceBean) {
            final ControllerAdviceBean other = (ControllerAdviceBean)o;
            return this.bean.equals(other.bean);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * this.bean.hashCode();
    }
    
    @Override
    public String toString() {
        return this.bean.toString();
    }
    
    static {
        logger = LogFactory.getLog(ControllerAdviceBean.class);
    }
}
