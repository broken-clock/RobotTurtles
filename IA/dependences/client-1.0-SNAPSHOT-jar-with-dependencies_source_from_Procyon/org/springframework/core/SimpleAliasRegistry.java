// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.HashMap;
import org.springframework.util.StringValueResolver;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import org.springframework.util.Assert;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SimpleAliasRegistry implements AliasRegistry
{
    private final Map<String, String> aliasMap;
    
    public SimpleAliasRegistry() {
        this.aliasMap = new ConcurrentHashMap<String, String>(16);
    }
    
    @Override
    public void registerAlias(final String name, final String alias) {
        Assert.hasText(name, "'name' must not be empty");
        Assert.hasText(alias, "'alias' must not be empty");
        if (alias.equals(name)) {
            this.aliasMap.remove(alias);
        }
        else {
            if (!this.allowAliasOverriding()) {
                final String registeredName = this.aliasMap.get(alias);
                if (registeredName != null && !registeredName.equals(name)) {
                    throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': It is already registered for name '" + registeredName + "'.");
                }
            }
            this.checkForAliasCircle(name, alias);
            this.aliasMap.put(alias, name);
        }
    }
    
    protected boolean allowAliasOverriding() {
        return true;
    }
    
    @Override
    public void removeAlias(final String alias) {
        final String name = this.aliasMap.remove(alias);
        if (name == null) {
            throw new IllegalStateException("No alias '" + alias + "' registered");
        }
    }
    
    @Override
    public boolean isAlias(final String name) {
        return this.aliasMap.containsKey(name);
    }
    
    @Override
    public String[] getAliases(final String name) {
        final List<String> result = new ArrayList<String>();
        synchronized (this.aliasMap) {
            this.retrieveAliases(name, result);
        }
        return StringUtils.toStringArray(result);
    }
    
    private void retrieveAliases(final String name, final List<String> result) {
        for (final Map.Entry<String, String> entry : this.aliasMap.entrySet()) {
            final String registeredName = entry.getValue();
            if (registeredName.equals(name)) {
                final String alias = entry.getKey();
                result.add(alias);
                this.retrieveAliases(alias, result);
            }
        }
    }
    
    public void resolveAliases(final StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        synchronized (this.aliasMap) {
            final Map<String, String> aliasCopy = new HashMap<String, String>(this.aliasMap);
            for (final String alias : aliasCopy.keySet()) {
                final String registeredName = aliasCopy.get(alias);
                final String resolvedAlias = valueResolver.resolveStringValue(alias);
                final String resolvedName = valueResolver.resolveStringValue(registeredName);
                if (resolvedAlias.equals(resolvedName)) {
                    this.aliasMap.remove(alias);
                }
                else if (!resolvedAlias.equals(alias)) {
                    final String existingName = this.aliasMap.get(resolvedAlias);
                    if (existingName != null && !existingName.equals(resolvedName)) {
                        throw new IllegalStateException("Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias + "') for name '" + resolvedName + "': It is already registered for name '" + registeredName + "'.");
                    }
                    this.checkForAliasCircle(resolvedName, resolvedAlias);
                    this.aliasMap.remove(alias);
                    this.aliasMap.put(resolvedAlias, resolvedName);
                }
                else {
                    if (registeredName.equals(resolvedName)) {
                        continue;
                    }
                    this.aliasMap.put(alias, resolvedName);
                }
            }
        }
    }
    
    public String canonicalName(final String name) {
        String canonicalName = name;
        String resolvedName;
        do {
            resolvedName = this.aliasMap.get(canonicalName);
            if (resolvedName != null) {
                canonicalName = resolvedName;
            }
        } while (resolvedName != null);
        return canonicalName;
    }
    
    protected void checkForAliasCircle(final String name, final String alias) {
        if (alias.equals(this.canonicalName(name))) {
            throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Circular reference - '" + name + "' is a direct or indirect alias for '" + alias + "' already");
        }
    }
}
