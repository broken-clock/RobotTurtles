// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;

public class JdkRegexpMethodPointcut extends AbstractRegexpMethodPointcut
{
    private Pattern[] compiledPatterns;
    private Pattern[] compiledExclusionPatterns;
    
    public JdkRegexpMethodPointcut() {
        this.compiledPatterns = new Pattern[0];
        this.compiledExclusionPatterns = new Pattern[0];
    }
    
    @Override
    protected void initPatternRepresentation(final String[] patterns) throws PatternSyntaxException {
        this.compiledPatterns = this.compilePatterns(patterns);
    }
    
    @Override
    protected void initExcludedPatternRepresentation(final String[] excludedPatterns) throws PatternSyntaxException {
        this.compiledExclusionPatterns = this.compilePatterns(excludedPatterns);
    }
    
    @Override
    protected boolean matches(final String pattern, final int patternIndex) {
        final Matcher matcher = this.compiledPatterns[patternIndex].matcher(pattern);
        return matcher.matches();
    }
    
    @Override
    protected boolean matchesExclusion(final String candidate, final int patternIndex) {
        final Matcher matcher = this.compiledExclusionPatterns[patternIndex].matcher(candidate);
        return matcher.matches();
    }
    
    private Pattern[] compilePatterns(final String[] source) throws PatternSyntaxException {
        final Pattern[] destination = new Pattern[source.length];
        for (int i = 0; i < source.length; ++i) {
            destination[i] = Pattern.compile(source[i]);
        }
        return destination;
    }
}
