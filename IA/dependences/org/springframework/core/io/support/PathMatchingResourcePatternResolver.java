// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.io.support;

import org.springframework.core.io.VfsResource;
import java.lang.reflect.InvocationHandler;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import java.util.Iterator;
import org.springframework.core.io.FileSystemResource;
import java.io.File;
import java.util.Collections;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URLConnection;
import java.net.JarURLConnection;
import org.springframework.util.ResourceUtils;
import org.springframework.util.ReflectionUtils;
import java.util.Collection;
import org.springframework.core.io.UrlResource;
import java.util.Set;
import java.util.Enumeration;
import java.net.URL;
import java.util.LinkedHashSet;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.core.io.ResourceLoader;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;

public class PathMatchingResourcePatternResolver implements ResourcePatternResolver
{
    private static final Log logger;
    private static Method equinoxResolveMethod;
    private final ResourceLoader resourceLoader;
    private PathMatcher pathMatcher;
    
    public PathMatchingResourcePatternResolver() {
        this.pathMatcher = new AntPathMatcher();
        this.resourceLoader = new DefaultResourceLoader();
    }
    
    public PathMatchingResourcePatternResolver(final ClassLoader classLoader) {
        this.pathMatcher = new AntPathMatcher();
        this.resourceLoader = new DefaultResourceLoader(classLoader);
    }
    
    public PathMatchingResourcePatternResolver(final ResourceLoader resourceLoader) {
        this.pathMatcher = new AntPathMatcher();
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }
    
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return this.getResourceLoader().getClassLoader();
    }
    
    public void setPathMatcher(final PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }
    
    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }
    
    @Override
    public Resource getResource(final String location) {
        return this.getResourceLoader().getResource(location);
    }
    
    @Override
    public Resource[] getResources(final String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        if (locationPattern.startsWith("classpath*:")) {
            if (this.getPathMatcher().isPattern(locationPattern.substring("classpath*:".length()))) {
                return this.findPathMatchingResources(locationPattern);
            }
            return this.findAllClassPathResources(locationPattern.substring("classpath*:".length()));
        }
        else {
            final int prefixEnd = locationPattern.indexOf(":") + 1;
            if (this.getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
                return this.findPathMatchingResources(locationPattern);
            }
            return new Resource[] { this.getResourceLoader().getResource(locationPattern) };
        }
    }
    
    protected Resource[] findAllClassPathResources(final String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        final Enumeration<URL> resourceUrls = this.getClassLoader().getResources(path);
        final Set<Resource> result = new LinkedHashSet<Resource>(16);
        while (resourceUrls.hasMoreElements()) {
            final URL url = resourceUrls.nextElement();
            result.add(this.convertClassLoaderURL(url));
        }
        return result.toArray(new Resource[result.size()]);
    }
    
    protected Resource convertClassLoaderURL(final URL url) {
        return new UrlResource(url);
    }
    
    protected Resource[] findPathMatchingResources(final String locationPattern) throws IOException {
        final String rootDirPath = this.determineRootDir(locationPattern);
        final String subPattern = locationPattern.substring(rootDirPath.length());
        final Resource[] rootDirResources = this.getResources(rootDirPath);
        final Set<Resource> result = new LinkedHashSet<Resource>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = this.resolveRootDirResource(rootDirResource);
            if (this.isJarResource(rootDirResource)) {
                result.addAll(this.doFindPathMatchingJarResources(rootDirResource, subPattern));
            }
            else if (rootDirResource.getURL().getProtocol().startsWith("vfs")) {
                result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirResource, subPattern, this.getPathMatcher()));
            }
            else {
                result.addAll(this.doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (PathMatchingResourcePatternResolver.logger.isDebugEnabled()) {
            PathMatchingResourcePatternResolver.logger.debug("Resolved location pattern [" + locationPattern + "] to resources " + result);
        }
        return result.toArray(new Resource[result.size()]);
    }
    
    protected String determineRootDir(final String location) {
        int prefixEnd;
        int rootDirEnd;
        for (prefixEnd = location.indexOf(":") + 1, rootDirEnd = location.length(); rootDirEnd > prefixEnd && this.getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd)); rootDirEnd = location.lastIndexOf(47, rootDirEnd - 2) + 1) {}
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }
    
    protected Resource resolveRootDirResource(final Resource original) throws IOException {
        if (PathMatchingResourcePatternResolver.equinoxResolveMethod != null) {
            final URL url = original.getURL();
            if (url.getProtocol().startsWith("bundle")) {
                return new UrlResource((URL)ReflectionUtils.invokeMethod(PathMatchingResourcePatternResolver.equinoxResolveMethod, null, url));
            }
        }
        return original;
    }
    
    protected boolean isJarResource(final Resource resource) throws IOException {
        return ResourceUtils.isJarURL(resource.getURL());
    }
    
    protected Set<Resource> doFindPathMatchingJarResources(final Resource rootDirResource, final String subPattern) throws IOException {
        final URLConnection con = rootDirResource.getURL().openConnection();
        boolean newJarFile = false;
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        if (con instanceof JarURLConnection) {
            final JarURLConnection jarCon = (JarURLConnection)con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            final JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = ((jarEntry != null) ? jarEntry.getName() : "");
        }
        else {
            final String urlFile = rootDirResource.getURL().getFile();
            final int separatorIndex = urlFile.indexOf("!/");
            if (separatorIndex != -1) {
                jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex + "!/".length());
                jarFile = this.getJarFile(jarFileUrl);
            }
            else {
                jarFile = new JarFile(urlFile);
                jarFileUrl = urlFile;
                rootEntryPath = "";
            }
            newJarFile = true;
        }
        try {
            if (PathMatchingResourcePatternResolver.logger.isDebugEnabled()) {
                PathMatchingResourcePatternResolver.logger.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                rootEntryPath += "/";
            }
            final Set<Resource> result = new LinkedHashSet<Resource>(8);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    final String relativePath = entryPath.substring(rootEntryPath.length());
                    if (!this.getPathMatcher().match(subPattern, relativePath)) {
                        continue;
                    }
                    result.add(rootDirResource.createRelative(relativePath));
                }
            }
            return result;
        }
        finally {
            if (newJarFile) {
                jarFile.close();
            }
        }
    }
    
    protected JarFile getJarFile(final String jarFileUrl) throws IOException {
        if (jarFileUrl.startsWith("file:")) {
            try {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            }
            catch (URISyntaxException ex) {
                return new JarFile(jarFileUrl.substring("file:".length()));
            }
        }
        return new JarFile(jarFileUrl);
    }
    
    protected Set<Resource> doFindPathMatchingFileResources(final Resource rootDirResource, final String subPattern) throws IOException {
        File rootDir;
        try {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        }
        catch (IOException ex) {
            if (PathMatchingResourcePatternResolver.logger.isWarnEnabled()) {
                PathMatchingResourcePatternResolver.logger.warn("Cannot search for matching files underneath " + rootDirResource + " because it does not correspond to a directory in the file system", ex);
            }
            return Collections.emptySet();
        }
        return this.doFindMatchingFileSystemResources(rootDir, subPattern);
    }
    
    protected Set<Resource> doFindMatchingFileSystemResources(final File rootDir, final String subPattern) throws IOException {
        if (PathMatchingResourcePatternResolver.logger.isDebugEnabled()) {
            PathMatchingResourcePatternResolver.logger.debug("Looking for matching resources in directory tree [" + rootDir.getPath() + "]");
        }
        final Set<File> matchingFiles = this.retrieveMatchingFiles(rootDir, subPattern);
        final Set<Resource> result = new LinkedHashSet<Resource>(matchingFiles.size());
        for (final File file : matchingFiles) {
            result.add(new FileSystemResource(file));
        }
        return result;
    }
    
    protected Set<File> retrieveMatchingFiles(final File rootDir, final String pattern) throws IOException {
        if (!rootDir.exists()) {
            if (PathMatchingResourcePatternResolver.logger.isDebugEnabled()) {
                PathMatchingResourcePatternResolver.logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
            }
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            if (PathMatchingResourcePatternResolver.logger.isWarnEnabled()) {
                PathMatchingResourcePatternResolver.logger.warn("Skipping [" + rootDir.getAbsolutePath() + "] because it does not denote a directory");
            }
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            if (PathMatchingResourcePatternResolver.logger.isWarnEnabled()) {
                PathMatchingResourcePatternResolver.logger.warn("Cannot search for matching files underneath directory [" + rootDir.getAbsolutePath() + "] because the application is not allowed to read the directory");
            }
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern += StringUtils.replace(pattern, File.separator, "/");
        final Set<File> result = new LinkedHashSet<File>(8);
        this.doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }
    
    protected void doRetrieveMatchingFiles(final String fullPattern, final File dir, final Set<File> result) throws IOException {
        if (PathMatchingResourcePatternResolver.logger.isDebugEnabled()) {
            PathMatchingResourcePatternResolver.logger.debug("Searching directory [" + dir.getAbsolutePath() + "] for files matching pattern [" + fullPattern + "]");
        }
        final File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            if (PathMatchingResourcePatternResolver.logger.isWarnEnabled()) {
                PathMatchingResourcePatternResolver.logger.warn("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
            }
            return;
        }
        for (final File content : dirContents) {
            final String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && this.getPathMatcher().matchStart(fullPattern, currPath + "/")) {
                if (!content.canRead()) {
                    if (PathMatchingResourcePatternResolver.logger.isDebugEnabled()) {
                        PathMatchingResourcePatternResolver.logger.debug("Skipping subdirectory [" + dir.getAbsolutePath() + "] because the application is not allowed to read the directory");
                    }
                }
                else {
                    this.doRetrieveMatchingFiles(fullPattern, content, result);
                }
            }
            if (this.getPathMatcher().match(fullPattern, currPath)) {
                result.add(content);
            }
        }
    }
    
    static {
        logger = LogFactory.getLog(PathMatchingResourcePatternResolver.class);
        try {
            final Class<?> fileLocatorClass = PathMatchingResourcePatternResolver.class.getClassLoader().loadClass("org.eclipse.core.runtime.FileLocator");
            PathMatchingResourcePatternResolver.equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
            PathMatchingResourcePatternResolver.logger.debug("Found Equinox FileLocator for OSGi bundle URL resolution");
        }
        catch (Throwable ex) {
            PathMatchingResourcePatternResolver.equinoxResolveMethod = null;
        }
    }
    
    private static class VfsResourceMatchingDelegate
    {
        public static Set<Resource> findMatchingResources(final Resource rootResource, final String locationPattern, final PathMatcher pathMatcher) throws IOException {
            final Object root = VfsPatternUtils.findRoot(rootResource.getURL());
            final PatternVirtualFileVisitor visitor = new PatternVirtualFileVisitor(VfsPatternUtils.getPath(root), locationPattern, pathMatcher);
            VfsPatternUtils.visit(root, visitor);
            return visitor.getResources();
        }
    }
    
    private static class PatternVirtualFileVisitor implements InvocationHandler
    {
        private final String subPattern;
        private final PathMatcher pathMatcher;
        private final String rootPath;
        private final Set<Resource> resources;
        
        public PatternVirtualFileVisitor(final String rootPath, final String subPattern, final PathMatcher pathMatcher) {
            this.resources = new LinkedHashSet<Resource>();
            this.subPattern = subPattern;
            this.pathMatcher = pathMatcher;
            this.rootPath = ((rootPath.length() == 0 || rootPath.endsWith("/")) ? rootPath : (rootPath + "/"));
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String methodName = method.getName();
            if (Object.class.equals(method.getDeclaringClass())) {
                if (methodName.equals("equals")) {
                    return proxy == args[0];
                }
                if (methodName.equals("hashCode")) {
                    return System.identityHashCode(proxy);
                }
            }
            else {
                if ("getAttributes".equals(methodName)) {
                    return this.getAttributes();
                }
                if ("visit".equals(methodName)) {
                    this.visit(args[0]);
                    return null;
                }
                if ("toString".equals(methodName)) {
                    return this.toString();
                }
            }
            throw new IllegalStateException("Unexpected method invocation: " + method);
        }
        
        public void visit(final Object vfsResource) {
            if (this.pathMatcher.match(this.subPattern, VfsPatternUtils.getPath(vfsResource).substring(this.rootPath.length()))) {
                this.resources.add(new VfsResource(vfsResource));
            }
        }
        
        public Object getAttributes() {
            return VfsPatternUtils.getVisitorAttribute();
        }
        
        public Set<Resource> getResources() {
            return this.resources;
        }
        
        public int size() {
            return this.resources.size();
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("sub-pattern: ").append(this.subPattern);
            sb.append(", resources: ").append(this.resources);
            return sb.toString();
        }
    }
}
