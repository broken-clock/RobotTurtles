// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

public class SimpleNamespaceContext implements NamespaceContext
{
    private Map<String, String> prefixToNamespaceUri;
    private Map<String, List<String>> namespaceUriToPrefixes;
    private String defaultNamespaceUri;
    
    public SimpleNamespaceContext() {
        this.prefixToNamespaceUri = new HashMap<String, String>();
        this.namespaceUriToPrefixes = new HashMap<String, List<String>>();
        this.defaultNamespaceUri = "";
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        Assert.notNull(prefix, "prefix is null");
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if ("xmlns".equals(prefix)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        if ("".equals(prefix)) {
            return this.defaultNamespaceUri;
        }
        if (this.prefixToNamespaceUri.containsKey(prefix)) {
            return this.prefixToNamespaceUri.get(prefix);
        }
        return "";
    }
    
    @Override
    public String getPrefix(final String namespaceUri) {
        final List<?> prefixes = this.getPrefixesInternal(namespaceUri);
        return prefixes.isEmpty() ? null : ((String)prefixes.get(0));
    }
    
    @Override
    public Iterator<String> getPrefixes(final String namespaceUri) {
        return this.getPrefixesInternal(namespaceUri).iterator();
    }
    
    public void setBindings(final Map<String, String> bindings) {
        for (final Map.Entry<String, String> entry : bindings.entrySet()) {
            this.bindNamespaceUri(entry.getKey(), entry.getValue());
        }
    }
    
    public void bindDefaultNamespaceUri(final String namespaceUri) {
        this.bindNamespaceUri("", namespaceUri);
    }
    
    public void bindNamespaceUri(final String prefix, final String namespaceUri) {
        Assert.notNull(prefix, "No prefix given");
        Assert.notNull(namespaceUri, "No namespaceUri given");
        if ("".equals(prefix)) {
            this.defaultNamespaceUri = namespaceUri;
        }
        else {
            this.prefixToNamespaceUri.put(prefix, namespaceUri);
            this.getPrefixesInternal(namespaceUri).add(prefix);
        }
    }
    
    public void clear() {
        this.prefixToNamespaceUri.clear();
    }
    
    public Iterator<String> getBoundPrefixes() {
        return this.prefixToNamespaceUri.keySet().iterator();
    }
    
    private List<String> getPrefixesInternal(final String namespaceUri) {
        if (this.defaultNamespaceUri.equals(namespaceUri)) {
            return Collections.singletonList("");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceUri)) {
            return Collections.singletonList("xml");
        }
        if ("http://www.w3.org/2000/xmlns/".equals(namespaceUri)) {
            return Collections.singletonList("xmlns");
        }
        List<String> list = this.namespaceUriToPrefixes.get(namespaceUri);
        if (list == null) {
            list = new ArrayList<String>();
            this.namespaceUriToPrefixes.put(namespaceUri, list);
        }
        return list;
    }
    
    public void removeBinding(final String prefix) {
        if ("".equals(prefix)) {
            this.defaultNamespaceUri = "";
        }
        else {
            final String namespaceUri = this.prefixToNamespaceUri.remove(prefix);
            final List<String> prefixes = this.getPrefixesInternal(namespaceUri);
            prefixes.remove(prefix);
        }
    }
}
