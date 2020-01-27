// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.util.ObjectUtils;
import java.util.Arrays;
import java.lang.reflect.Method;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;
import java.io.Serializable;

public abstract class AbstractRegexpMethodPointcut extends StaticMethodMatcherPointcut implements Serializable
{
    private String[] patterns;
    private String[] excludedPatterns;
    
    public AbstractRegexpMethodPointcut() {
        this.patterns = new String[0];
        this.excludedPatterns = new String[0];
    }
    
    public void setPattern(final String pattern) {
        this.setPatterns(pattern);
    }
    
    public void setPatterns(final String... patterns) {
        Assert.notEmpty(patterns, "'patterns' must not be empty");
        this.patterns = new String[patterns.length];
        for (int i = 0; i < patterns.length; ++i) {
            this.patterns[i] = StringUtils.trimWhitespace(patterns[i]);
        }
        this.initPatternRepresentation(this.patterns);
    }
    
    public String[] getPatterns() {
        return this.patterns;
    }
    
    public void setExcludedPattern(final String excludedPattern) {
        this.setExcludedPatterns(excludedPattern);
    }
    
    public void setExcludedPatterns(final String... excludedPatterns) {
        Assert.notEmpty(excludedPatterns, "'excludedPatterns' must not be empty");
        this.excludedPatterns = new String[excludedPatterns.length];
        for (int i = 0; i < excludedPatterns.length; ++i) {
            this.excludedPatterns[i] = StringUtils.trimWhitespace(excludedPatterns[i]);
        }
        this.initExcludedPatternRepresentation(this.excludedPatterns);
    }
    
    public String[] getExcludedPatterns() {
        return this.excludedPatterns;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        return (targetClass != null && this.matchesPattern(targetClass.getName() + "." + method.getName())) || this.matchesPattern(method.getDeclaringClass().getName() + "." + method.getName());
    }
    
    protected boolean matchesPattern(final String signatureString) {
        for (int i = 0; i < this.patterns.length; ++i) {
            final boolean matched = this.matches(signatureString, i);
            if (matched) {
                for (int j = 0; j < this.excludedPatterns.length; ++j) {
                    final boolean excluded = this.matchesExclusion(signatureString, j);
                    if (excluded) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    protected abstract void initPatternRepresentation(final String[] p0) throws IllegalArgumentException;
    
    protected abstract void initExcludedPatternRepresentation(final String[] p0) throws IllegalArgumentException;
    
    protected abstract boolean matches(final String p0, final int p1);
    
    protected abstract boolean matchesExclusion(final String p0, final int p1);
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractRegexpMethodPointcut)) {
            return false;
        }
        final AbstractRegexpMethodPointcut otherPointcut = (AbstractRegexpMethodPointcut)other;
        return Arrays.equals(this.patterns, otherPointcut.patterns) && Arrays.equals(this.excludedPatterns, otherPointcut.excludedPatterns);
    }
    
    @Override
    public int hashCode() {
        int result = 27;
        for (final String pattern : this.patterns) {
            result = 13 * result + pattern.hashCode();
        }
        for (final String excludedPattern : this.excludedPatterns) {
            result = 13 * result + excludedPattern.hashCode();
        }
        return result;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": patterns " + ObjectUtils.nullSafeToString(this.patterns) + ", excluded patterns " + ObjectUtils.nullSafeToString(this.excludedPatterns);
    }
}
