// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.regex.Matcher;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AntPathMatcher implements PathMatcher
{
    public static final String DEFAULT_PATH_SEPARATOR = "/";
    private static final int CACHE_TURNOFF_THRESHOLD = 65536;
    private static final Pattern VARIABLE_PATTERN;
    private String pathSeparator;
    private boolean trimTokens;
    private volatile Boolean cachePatterns;
    private final Map<String, String[]> tokenizedPatternCache;
    final Map<String, AntPathStringMatcher> stringMatcherCache;
    
    public AntPathMatcher() {
        this.pathSeparator = "/";
        this.trimTokens = true;
        this.tokenizedPatternCache = new ConcurrentHashMap<String, String[]>(256);
        this.stringMatcherCache = new ConcurrentHashMap<String, AntPathStringMatcher>(256);
    }
    
    public void setPathSeparator(final String pathSeparator) {
        this.pathSeparator = ((pathSeparator != null) ? pathSeparator : "/");
    }
    
    public void setTrimTokens(final boolean trimTokens) {
        this.trimTokens = trimTokens;
    }
    
    public void setCachePatterns(final boolean cachePatterns) {
        this.cachePatterns = cachePatterns;
    }
    
    private void deactivatePatternCache() {
        this.cachePatterns = false;
        this.tokenizedPatternCache.clear();
        this.stringMatcherCache.clear();
    }
    
    @Override
    public boolean isPattern(final String path) {
        return path.indexOf(42) != -1 || path.indexOf(63) != -1;
    }
    
    @Override
    public boolean match(final String pattern, final String path) {
        return this.doMatch(pattern, path, true, null);
    }
    
    @Override
    public boolean matchStart(final String pattern, final String path) {
        return this.doMatch(pattern, path, false, null);
    }
    
    protected boolean doMatch(final String pattern, final String path, final boolean fullMatch, final Map<String, String> uriTemplateVariables) {
        if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
            return false;
        }
        final String[] pattDirs = this.tokenizePattern(pattern);
        final String[] pathDirs = this.tokenizePath(path);
        int pattIdxStart;
        int pattIdxEnd;
        int pathIdxStart;
        int pathIdxEnd;
        for (pattIdxStart = 0, pattIdxEnd = pattDirs.length - 1, pathIdxStart = 0, pathIdxEnd = pathDirs.length - 1; pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd; ++pattIdxStart, ++pathIdxStart) {
            final String pattDir = pattDirs[pattIdxStart];
            if ("**".equals(pattDir)) {
                break;
            }
            if (!this.matchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
                return false;
            }
        }
        if (pathIdxStart > pathIdxEnd) {
            if (pattIdxStart > pattIdxEnd) {
                return pattern.endsWith(this.pathSeparator) ? path.endsWith(this.pathSeparator) : (!path.endsWith(this.pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(this.pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; ++i) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (pattIdxStart > pattIdxEnd) {
                return false;
            }
            if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
                return true;
            }
            while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
                final String pattDir = pattDirs[pattIdxEnd];
                if (pattDir.equals("**")) {
                    break;
                }
                if (!this.matchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                    return false;
                }
                --pattIdxEnd;
                --pathIdxEnd;
            }
            if (pathIdxStart > pathIdxEnd) {
                for (int i = pattIdxStart; i <= pattIdxEnd; ++i) {
                    if (!pattDirs[i].equals("**")) {
                        return false;
                    }
                }
                return true;
            }
            while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
                int patIdxTmp = -1;
                for (int j = pattIdxStart + 1; j <= pattIdxEnd; ++j) {
                    if (pattDirs[j].equals("**")) {
                        patIdxTmp = j;
                        break;
                    }
                }
                if (patIdxTmp == pattIdxStart + 1) {
                    ++pattIdxStart;
                }
                else {
                    final int patLength = patIdxTmp - pattIdxStart - 1;
                    final int strLength = pathIdxEnd - pathIdxStart + 1;
                    int foundIdx = -1;
                    int k = 0;
                Label_0467:
                    while (k <= strLength - patLength) {
                        for (int l = 0; l < patLength; ++l) {
                            final String subPat = pattDirs[pattIdxStart + l + 1];
                            final String subStr = pathDirs[pathIdxStart + k + l];
                            if (!this.matchStrings(subPat, subStr, uriTemplateVariables)) {
                                ++k;
                                continue Label_0467;
                            }
                        }
                        foundIdx = pathIdxStart + k;
                        break;
                    }
                    if (foundIdx == -1) {
                        return false;
                    }
                    pattIdxStart = patIdxTmp;
                    pathIdxStart = foundIdx + patLength;
                }
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; ++i) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }
    }
    
    protected String[] tokenizePattern(final String pattern) {
        String[] tokenized = null;
        final Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns) {
            tokenized = this.tokenizedPatternCache.get(pattern);
        }
        if (tokenized == null) {
            tokenized = this.tokenizePath(pattern);
            if (cachePatterns == null && this.tokenizedPatternCache.size() >= 65536) {
                this.deactivatePatternCache();
                return tokenized;
            }
            if (cachePatterns == null || cachePatterns) {
                this.tokenizedPatternCache.put(pattern, tokenized);
            }
        }
        return tokenized;
    }
    
    protected String[] tokenizePath(final String path) {
        return StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
    }
    
    private boolean matchStrings(final String pattern, final String str, final Map<String, String> uriTemplateVariables) {
        return this.getStringMatcher(pattern).matchStrings(str, uriTemplateVariables);
    }
    
    protected AntPathStringMatcher getStringMatcher(final String pattern) {
        AntPathStringMatcher matcher = null;
        final Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns) {
            matcher = this.stringMatcherCache.get(pattern);
        }
        if (matcher == null) {
            matcher = new AntPathStringMatcher(pattern);
            if (cachePatterns == null && this.stringMatcherCache.size() >= 65536) {
                this.deactivatePatternCache();
                return matcher;
            }
            if (cachePatterns == null || cachePatterns) {
                this.stringMatcherCache.put(pattern, matcher);
            }
        }
        return matcher;
    }
    
    @Override
    public String extractPathWithinPattern(final String pattern, final String path) {
        final String[] patternParts = StringUtils.tokenizeToStringArray(pattern, this.pathSeparator, this.trimTokens, true);
        final String[] pathParts = StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
        final StringBuilder builder = new StringBuilder();
        int puts = 0;
        for (int i = 0; i < patternParts.length; ++i) {
            final String patternPart = patternParts[i];
            if ((patternPart.indexOf(42) > -1 || patternPart.indexOf(63) > -1) && pathParts.length >= i + 1) {
                if (puts > 0 || (i == 0 && !pattern.startsWith(this.pathSeparator))) {
                    builder.append(this.pathSeparator);
                }
                builder.append(pathParts[i]);
                ++puts;
            }
        }
        for (int i = patternParts.length; i < pathParts.length; ++i) {
            if (puts > 0 || i > 0) {
                builder.append(this.pathSeparator);
            }
            builder.append(pathParts[i]);
        }
        return builder.toString();
    }
    
    @Override
    public Map<String, String> extractUriTemplateVariables(final String pattern, final String path) {
        final Map<String, String> variables = new LinkedHashMap<String, String>();
        final boolean result = this.doMatch(pattern, path, true, variables);
        Assert.state(result, "Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
        return variables;
    }
    
    @Override
    public String combine(final String pattern1, final String pattern2) {
        if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {
            return "";
        }
        if (!StringUtils.hasText(pattern1)) {
            return pattern2;
        }
        if (!StringUtils.hasText(pattern2)) {
            return pattern1;
        }
        final boolean pattern1ContainsUriVar = pattern1.indexOf(123) != -1;
        if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar && this.match(pattern1, pattern2)) {
            return pattern2;
        }
        if (pattern1.endsWith("/*")) {
            return this.slashConcat(pattern1.substring(0, pattern1.length() - 2), pattern2);
        }
        if (pattern1.endsWith("/**")) {
            return this.slashConcat(pattern1, pattern2);
        }
        final int starDotPos1 = pattern1.indexOf("*.");
        if (pattern1ContainsUriVar || starDotPos1 == -1) {
            return this.slashConcat(pattern1, pattern2);
        }
        final String extension1 = pattern1.substring(starDotPos1 + 1);
        final int dotPos2 = pattern2.indexOf(46);
        final String fileName2 = (dotPos2 == -1) ? pattern2 : pattern2.substring(0, dotPos2);
        final String extension2 = (dotPos2 == -1) ? "" : pattern2.substring(dotPos2);
        final String extension3 = extension1.startsWith("*") ? extension2 : extension1;
        return fileName2 + extension3;
    }
    
    private String slashConcat(final String path1, final String path2) {
        if (path1.endsWith("/") || path2.startsWith("/")) {
            return path1 + path2;
        }
        return path1 + "/" + path2;
    }
    
    @Override
    public Comparator<String> getPatternComparator(final String path) {
        return new AntPatternComparator(path);
    }
    
    static {
        VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");
    }
    
    protected static class AntPathStringMatcher
    {
        private static final Pattern GLOB_PATTERN;
        private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";
        private final Pattern pattern;
        private final List<String> variableNames;
        
        public AntPathStringMatcher(final String pattern) {
            this.variableNames = new LinkedList<String>();
            final StringBuilder patternBuilder = new StringBuilder();
            final Matcher m = AntPathStringMatcher.GLOB_PATTERN.matcher(pattern);
            int end = 0;
            while (m.find()) {
                patternBuilder.append(this.quote(pattern, end, m.start()));
                final String match = m.group();
                if ("?".equals(match)) {
                    patternBuilder.append('.');
                }
                else if ("*".equals(match)) {
                    patternBuilder.append(".*");
                }
                else if (match.startsWith("{") && match.endsWith("}")) {
                    final int colonIdx = match.indexOf(58);
                    if (colonIdx == -1) {
                        patternBuilder.append("(.*)");
                        this.variableNames.add(m.group(1));
                    }
                    else {
                        final String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append('(');
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(')');
                        final String variableName = match.substring(1, colonIdx);
                        this.variableNames.add(variableName);
                    }
                }
                end = m.end();
            }
            patternBuilder.append(this.quote(pattern, end, pattern.length()));
            this.pattern = Pattern.compile(patternBuilder.toString());
        }
        
        private String quote(final String s, final int start, final int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(s.substring(start, end));
        }
        
        public boolean matchStrings(final String str, final Map<String, String> uriTemplateVariables) {
            final Matcher matcher = this.pattern.matcher(str);
            if (matcher.matches()) {
                if (uriTemplateVariables != null) {
                    Assert.isTrue(this.variableNames.size() == matcher.groupCount(), "The number of capturing groups in the pattern segment " + this.pattern + " does not match the number of URI template variables it defines, which can occur if " + " capturing groups are used in a URI template regex. Use non-capturing groups instead.");
                    for (int i = 1; i <= matcher.groupCount(); ++i) {
                        final String name = this.variableNames.get(i - 1);
                        final String value = matcher.group(i);
                        uriTemplateVariables.put(name, value);
                    }
                }
                return true;
            }
            return false;
        }
        
        static {
            GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");
        }
    }
    
    protected static class AntPatternComparator implements Comparator<String>
    {
        private final String path;
        
        public AntPatternComparator(final String path) {
            this.path = path;
        }
        
        @Override
        public int compare(final String pattern1, final String pattern2) {
            if (this.isNullOrCaptureAllPattern(pattern1) && this.isNullOrCaptureAllPattern(pattern2)) {
                return 0;
            }
            if (this.isNullOrCaptureAllPattern(pattern1)) {
                return 1;
            }
            if (this.isNullOrCaptureAllPattern(pattern2)) {
                return -1;
            }
            final boolean pattern1EqualsPath = pattern1.equals(this.path);
            final boolean pattern2EqualsPath = pattern2.equals(this.path);
            if (pattern1EqualsPath && pattern2EqualsPath) {
                return 0;
            }
            if (pattern1EqualsPath) {
                return -1;
            }
            if (pattern2EqualsPath) {
                return 1;
            }
            final int wildCardCount1 = this.getWildCardCount(pattern1);
            final int wildCardCount2 = this.getWildCardCount(pattern2);
            final int bracketCount1 = StringUtils.countOccurrencesOf(pattern1, "{");
            final int bracketCount2 = StringUtils.countOccurrencesOf(pattern2, "{");
            final int totalCount1 = wildCardCount1 + bracketCount1;
            final int totalCount2 = wildCardCount2 + bracketCount2;
            if (totalCount1 != totalCount2) {
                return totalCount1 - totalCount2;
            }
            final int pattern1Length = this.getPatternLength(pattern1);
            final int pattern2Length = this.getPatternLength(pattern2);
            if (pattern1Length != pattern2Length) {
                return pattern2Length - pattern1Length;
            }
            if (wildCardCount1 < wildCardCount2) {
                return -1;
            }
            if (wildCardCount2 < wildCardCount1) {
                return 1;
            }
            if (bracketCount1 < bracketCount2) {
                return -1;
            }
            if (bracketCount2 < bracketCount1) {
                return 1;
            }
            return 0;
        }
        
        private boolean isNullOrCaptureAllPattern(final String pattern) {
            return pattern == null || "/**".equals(pattern);
        }
        
        private int getWildCardCount(String pattern) {
            if (pattern.endsWith(".*")) {
                pattern = pattern.substring(0, pattern.length() - 2);
            }
            return StringUtils.countOccurrencesOf(pattern, "*");
        }
        
        private int getPatternLength(final String pattern) {
            return AntPathMatcher.VARIABLE_PATTERN.matcher(pattern).replaceAll("#").length();
        }
    }
}
