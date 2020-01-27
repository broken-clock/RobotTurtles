// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.util.StringUtils;
import org.aspectj.weaver.tools.PointcutParser;
import org.springframework.util.Assert;
import org.aspectj.weaver.tools.TypePatternMatcher;
import org.springframework.aop.ClassFilter;

public class TypePatternClassFilter implements ClassFilter
{
    private String typePattern;
    private TypePatternMatcher aspectJTypePatternMatcher;
    
    public TypePatternClassFilter() {
    }
    
    public TypePatternClassFilter(final String typePattern) {
        this.setTypePattern(typePattern);
    }
    
    public void setTypePattern(final String typePattern) {
        Assert.notNull(typePattern);
        this.typePattern = typePattern;
        this.aspectJTypePatternMatcher = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().parseTypePattern(this.replaceBooleanOperators(typePattern));
    }
    
    public String getTypePattern() {
        return this.typePattern;
    }
    
    @Override
    public boolean matches(final Class<?> clazz) {
        if (this.aspectJTypePatternMatcher == null) {
            throw new IllegalStateException("No 'typePattern' has been set via ctor/setter.");
        }
        return this.aspectJTypePatternMatcher.matches((Class)clazz);
    }
    
    private String replaceBooleanOperators(String pcExpr) {
        pcExpr = StringUtils.replace(pcExpr, " and ", " && ");
        pcExpr = StringUtils.replace(pcExpr, " or ", " || ");
        pcExpr = StringUtils.replace(pcExpr, " not ", " ! ");
        return pcExpr;
    }
}
