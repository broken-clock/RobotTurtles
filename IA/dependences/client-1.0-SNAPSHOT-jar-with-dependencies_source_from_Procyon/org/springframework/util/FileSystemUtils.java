// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.io.IOException;
import java.io.File;

public abstract class FileSystemUtils
{
    public static boolean deleteRecursively(final File root) {
        if (root != null && root.exists()) {
            if (root.isDirectory()) {
                final File[] children = root.listFiles();
                if (children != null) {
                    for (final File child : children) {
                        deleteRecursively(child);
                    }
                }
            }
            return root.delete();
        }
        return false;
    }
    
    public static void copyRecursively(final File src, final File dest) throws IOException {
        Assert.isTrue(src != null && (src.isDirectory() || src.isFile()), "Source File must denote a directory or file");
        Assert.notNull(dest, "Destination File must not be null");
        doCopyRecursively(src, dest);
    }
    
    private static void doCopyRecursively(final File src, final File dest) throws IOException {
        if (src.isDirectory()) {
            dest.mkdir();
            final File[] entries = src.listFiles();
            if (entries == null) {
                throw new IOException("Could not list files in directory: " + src);
            }
            for (final File entry : entries) {
                doCopyRecursively(entry, new File(dest, entry.getName()));
            }
        }
        else if (src.isFile()) {
            try {
                dest.createNewFile();
            }
            catch (IOException ex) {
                final IOException ioex = new IOException("Failed to create file: " + dest);
                ioex.initCause(ex);
                throw ioex;
            }
            FileCopyUtils.copy(src, dest);
        }
    }
}
