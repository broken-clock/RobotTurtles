// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.xml;

import org.xml.sax.ContentHandler;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Comment;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.util.Assert;
import java.util.List;
import org.w3c.dom.Element;

public abstract class DomUtils
{
    public static List<Element> getChildElementsByTagName(final Element ele, final String... childEleNames) {
        Assert.notNull(ele, "Element must not be null");
        Assert.notNull(childEleNames, "Element names collection must not be null");
        final List<String> childEleNameList = Arrays.asList(childEleNames);
        final NodeList nl = ele.getChildNodes();
        final List<Element> childEles = new ArrayList<Element>();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node instanceof Element && nodeNameMatch(node, childEleNameList)) {
                childEles.add((Element)node);
            }
        }
        return childEles;
    }
    
    public static List<Element> getChildElementsByTagName(final Element ele, final String childEleName) {
        return getChildElementsByTagName(ele, new String[] { childEleName });
    }
    
    public static Element getChildElementByTagName(final Element ele, final String childEleName) {
        Assert.notNull(ele, "Element must not be null");
        Assert.notNull(childEleName, "Element name must not be null");
        final NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node instanceof Element && nodeNameMatch(node, childEleName)) {
                return (Element)node;
            }
        }
        return null;
    }
    
    public static String getChildElementValueByTagName(final Element ele, final String childEleName) {
        final Element child = getChildElementByTagName(ele, childEleName);
        return (child != null) ? getTextValue(child) : null;
    }
    
    public static List<Element> getChildElements(final Element ele) {
        Assert.notNull(ele, "Element must not be null");
        final NodeList nl = ele.getChildNodes();
        final List<Element> childEles = new ArrayList<Element>();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node node = nl.item(i);
            if (node instanceof Element) {
                childEles.add((Element)node);
            }
        }
        return childEles;
    }
    
    public static String getTextValue(final Element valueEle) {
        Assert.notNull(valueEle, "Element must not be null");
        final StringBuilder sb = new StringBuilder();
        final NodeList nl = valueEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                sb.append(item.getNodeValue());
            }
        }
        return sb.toString();
    }
    
    public static boolean nodeNameEquals(final Node node, final String desiredName) {
        Assert.notNull(node, "Node must not be null");
        Assert.notNull(desiredName, "Desired name must not be null");
        return nodeNameMatch(node, desiredName);
    }
    
    public static ContentHandler createContentHandler(final Node node) {
        return new DomContentHandler(node);
    }
    
    private static boolean nodeNameMatch(final Node node, final String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName());
    }
    
    private static boolean nodeNameMatch(final Node node, final Collection<?> desiredNames) {
        return desiredNames.contains(node.getNodeName()) || desiredNames.contains(node.getLocalName());
    }
}
