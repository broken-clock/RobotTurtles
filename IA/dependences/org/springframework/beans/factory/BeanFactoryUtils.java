// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.springframework.util.StringUtils;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.springframework.util.Assert;

public abstract class BeanFactoryUtils
{
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
    
    public static boolean isFactoryDereference(final String name) {
        return name != null && name.startsWith("&");
    }
    
    public static String transformedBeanName(final String name) {
        Assert.notNull(name, "'name' must not be null");
        String beanName;
        for (beanName = name; beanName.startsWith("&"); beanName = beanName.substring("&".length())) {}
        return beanName;
    }
    
    public static boolean isGeneratedBeanName(final String name) {
        return name != null && name.contains("#");
    }
    
    public static String originalBeanName(final String name) {
        Assert.notNull(name, "'name' must not be null");
        final int separatorIndex = name.indexOf("#");
        return (separatorIndex != -1) ? name.substring(0, separatorIndex) : name;
    }
    
    public static int countBeansIncludingAncestors(final ListableBeanFactory lbf) {
        return beanNamesIncludingAncestors(lbf).length;
    }
    
    public static String[] beanNamesIncludingAncestors(final ListableBeanFactory lbf) {
        return beanNamesForTypeIncludingAncestors(lbf, Object.class);
    }
    
    public static String[] beanNamesForTypeIncludingAncestors(final ListableBeanFactory lbf, final Class<?> type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory) {
            final HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                final String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type);
                final List<String> resultList = new ArrayList<String>();
                resultList.addAll(Arrays.asList(result));
                for (final String beanName : parentResult) {
                    if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                        resultList.add(beanName);
                    }
                }
                result = StringUtils.toStringArray(resultList);
            }
        }
        return result;
    }
    
    public static String[] beanNamesForTypeIncludingAncestors(final ListableBeanFactory lbf, final Class<?> type, final boolean includeNonSingletons, final boolean allowEagerInit) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        if (lbf instanceof HierarchicalBeanFactory) {
            final HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                final String[] parentResult = beanNamesForTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                final List<String> resultList = new ArrayList<String>();
                resultList.addAll(Arrays.asList(result));
                for (final String beanName : parentResult) {
                    if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                        resultList.add(beanName);
                    }
                }
                result = StringUtils.toStringArray(resultList);
            }
        }
        return result;
    }
    
    public static <T> Map<String, T> beansOfTypeIncludingAncestors(final ListableBeanFactory lbf, final Class<T> type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        final Map<String, T> result = new LinkedHashMap<String, T>(4);
        result.putAll(lbf.getBeansOfType((Class<? extends T>)type));
        if (lbf instanceof HierarchicalBeanFactory) {
            final HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                final Map<String, T> parentResult = (Map<String, T>)beansOfTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), (Class<Object>)type);
                for (final Map.Entry<String, T> entry : parentResult.entrySet()) {
                    final String beanName = entry.getKey();
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, entry.getValue());
                    }
                }
            }
        }
        return result;
    }
    
    public static <T> Map<String, T> beansOfTypeIncludingAncestors(final ListableBeanFactory lbf, final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        final Map<String, T> result = new LinkedHashMap<String, T>(4);
        result.putAll(lbf.getBeansOfType((Class<? extends T>)type, includeNonSingletons, allowEagerInit));
        if (lbf instanceof HierarchicalBeanFactory) {
            final HierarchicalBeanFactory hbf = (HierarchicalBeanFactory)lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                final Map<String, T> parentResult = (Map<String, T>)beansOfTypeIncludingAncestors((ListableBeanFactory)hbf.getParentBeanFactory(), (Class<Object>)type, includeNonSingletons, allowEagerInit);
                for (final Map.Entry<String, T> entry : parentResult.entrySet()) {
                    final String beanName = entry.getKey();
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, entry.getValue());
                    }
                }
            }
        }
        return result;
    }
    
    public static <T> T beanOfTypeIncludingAncestors(final ListableBeanFactory lbf, final Class<T> type) throws BeansException {
        final Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type);
        return uniqueBean(type, beansOfType);
    }
    
    public static <T> T beanOfTypeIncludingAncestors(final ListableBeanFactory lbf, final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit) throws BeansException {
        final Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
        return uniqueBean(type, beansOfType);
    }
    
    public static <T> T beanOfType(final ListableBeanFactory lbf, final Class<T> type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        final Map<String, T> beansOfType = lbf.getBeansOfType(type);
        return uniqueBean(type, beansOfType);
    }
    
    public static <T> T beanOfType(final ListableBeanFactory lbf, final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        final Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
        return uniqueBean(type, beansOfType);
    }
    
    private static <T> T uniqueBean(final Class<T> type, final Map<String, T> matchingBeans) {
        final int nrFound = matchingBeans.size();
        if (nrFound == 1) {
            return matchingBeans.values().iterator().next();
        }
        if (nrFound > 1) {
            throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
        }
        throw new NoSuchBeanDefinitionException(type);
    }
}
