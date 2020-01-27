// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.conn.ssl;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.security.cert.CertificateParsingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import java.util.NoSuchElementException;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import org.apache.http.conn.util.DomainType;
import java.util.Locale;
import javax.security.auth.x500.X500Principal;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;
import java.security.cert.Certificate;
import javax.net.ssl.SSLException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Immutable;
import javax.net.ssl.HostnameVerifier;

@Immutable
public final class DefaultHostnameVerifier implements HostnameVerifier
{
    static final int DNS_NAME_TYPE = 2;
    static final int IP_ADDRESS_TYPE = 7;
    private final Log log;
    private final PublicSuffixMatcher publicSuffixMatcher;
    
    public DefaultHostnameVerifier(final PublicSuffixMatcher publicSuffixMatcher) {
        this.log = LogFactory.getLog(this.getClass());
        this.publicSuffixMatcher = publicSuffixMatcher;
    }
    
    public DefaultHostnameVerifier() {
        this(null);
    }
    
    @Override
    public boolean verify(final String host, final SSLSession session) {
        try {
            final Certificate[] certs = session.getPeerCertificates();
            final X509Certificate x509 = (X509Certificate)certs[0];
            this.verify(host, x509);
            return true;
        }
        catch (SSLException ex) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(ex.getMessage(), ex);
            }
            return false;
        }
    }
    
    public void verify(final String host, final X509Certificate cert) throws SSLException {
        final boolean ipv4 = InetAddressUtils.isIPv4Address(host);
        final boolean ipv5 = InetAddressUtils.isIPv6Address(host);
        final int subjectType = (ipv4 || ipv5) ? 7 : 2;
        final List<String> subjectAlts = extractSubjectAlts(cert, subjectType);
        if (subjectAlts != null && !subjectAlts.isEmpty()) {
            if (ipv4) {
                matchIPAddress(host, subjectAlts);
            }
            else if (ipv5) {
                matchIPv6Address(host, subjectAlts);
            }
            else {
                matchDNSName(host, subjectAlts, this.publicSuffixMatcher);
            }
        }
        else {
            final X500Principal subjectPrincipal = cert.getSubjectX500Principal();
            final String cn = extractCN(subjectPrincipal.getName("RFC2253"));
            if (cn == null) {
                throw new SSLException("Certificate subject for <" + host + "> doesn't contain " + "a common name and does not have alternative names");
            }
            matchCN(host, cn, this.publicSuffixMatcher);
        }
    }
    
    static void matchIPAddress(final String host, final List<String> subjectAlts) throws SSLException {
        for (int i = 0; i < subjectAlts.size(); ++i) {
            final String subjectAlt = subjectAlts.get(i);
            if (host.equals(subjectAlt)) {
                return;
            }
        }
        throw new SSLException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }
    
    static void matchIPv6Address(final String host, final List<String> subjectAlts) throws SSLException {
        final String normalisedHost = normaliseAddress(host);
        for (int i = 0; i < subjectAlts.size(); ++i) {
            final String subjectAlt = subjectAlts.get(i);
            final String normalizedSubjectAlt = normaliseAddress(subjectAlt);
            if (normalisedHost.equals(normalizedSubjectAlt)) {
                return;
            }
        }
        throw new SSLException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }
    
    static void matchDNSName(final String host, final List<String> subjectAlts, final PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
        final String normalizedHost = host.toLowerCase(Locale.ROOT);
        for (int i = 0; i < subjectAlts.size(); ++i) {
            final String subjectAlt = subjectAlts.get(i);
            final String normalizedSubjectAlt = subjectAlt.toLowerCase(Locale.ROOT);
            if (matchIdentityStrict(normalizedHost, normalizedSubjectAlt, publicSuffixMatcher)) {
                return;
            }
        }
        throw new SSLException("Certificate for <" + host + "> doesn't match any " + "of the subject alternative names: " + subjectAlts);
    }
    
    static void matchCN(final String host, final String cn, final PublicSuffixMatcher publicSuffixMatcher) throws SSLException {
        if (!matchIdentityStrict(host, cn, publicSuffixMatcher)) {
            throw new SSLException("Certificate for <" + host + "> doesn't match " + "common name of the certificate subject: " + cn);
        }
    }
    
    static boolean matchDomainRoot(final String host, final String domainRoot) {
        return domainRoot != null && host.endsWith(domainRoot) && (host.length() == domainRoot.length() || host.charAt(host.length() - domainRoot.length() - 1) == '.');
    }
    
    private static boolean matchIdentity(final String host, final String identity, final PublicSuffixMatcher publicSuffixMatcher, final boolean strict) {
        if (publicSuffixMatcher != null && host.contains(".") && !matchDomainRoot(host, publicSuffixMatcher.getDomainRoot(identity, DomainType.ICANN))) {
            return false;
        }
        final int asteriskIdx = identity.indexOf(42);
        if (asteriskIdx == -1) {
            return host.equalsIgnoreCase(identity);
        }
        final String prefix = identity.substring(0, asteriskIdx);
        final String suffix = identity.substring(asteriskIdx + 1);
        if (!prefix.isEmpty() && !host.startsWith(prefix)) {
            return false;
        }
        if (!suffix.isEmpty() && !host.endsWith(suffix)) {
            return false;
        }
        if (strict) {
            final String remainder = host.substring(prefix.length(), host.length() - suffix.length());
            if (remainder.contains(".")) {
                return false;
            }
        }
        return true;
    }
    
    static boolean matchIdentity(final String host, final String identity, final PublicSuffixMatcher publicSuffixMatcher) {
        return matchIdentity(host, identity, publicSuffixMatcher, false);
    }
    
    static boolean matchIdentity(final String host, final String identity) {
        return matchIdentity(host, identity, null, false);
    }
    
    static boolean matchIdentityStrict(final String host, final String identity, final PublicSuffixMatcher publicSuffixMatcher) {
        return matchIdentity(host, identity, publicSuffixMatcher, true);
    }
    
    static boolean matchIdentityStrict(final String host, final String identity) {
        return matchIdentity(host, identity, null, true);
    }
    
    static String extractCN(final String subjectPrincipal) throws SSLException {
        if (subjectPrincipal == null) {
            return null;
        }
        try {
            final LdapName subjectDN = new LdapName(subjectPrincipal);
            final List<Rdn> rdns = subjectDN.getRdns();
            for (int i = rdns.size() - 1; i >= 0; --i) {
                final Rdn rds = rdns.get(i);
                final Attributes attributes = rds.toAttributes();
                final Attribute cn = attributes.get("cn");
                if (cn != null) {
                    try {
                        final Object value = cn.get();
                        if (value != null) {
                            return value.toString();
                        }
                    }
                    catch (NoSuchElementException ignore) {}
                    catch (NamingException ex) {}
                }
            }
            return null;
        }
        catch (InvalidNameException e) {
            throw new SSLException(subjectPrincipal + " is not a valid X500 distinguished name");
        }
    }
    
    static List<String> extractSubjectAlts(final X509Certificate cert, final int subjectType) {
        Collection<List<?>> c = null;
        try {
            c = cert.getSubjectAlternativeNames();
        }
        catch (CertificateParsingException ex) {}
        List<String> subjectAltList = null;
        if (c != null) {
            for (final List<?> list : c) {
                final List<?> aC = list;
                final int type = (int)list.get(0);
                if (type == subjectType) {
                    final String s = (String)list.get(1);
                    if (subjectAltList == null) {
                        subjectAltList = new ArrayList<String>();
                    }
                    subjectAltList.add(s);
                }
            }
        }
        return subjectAltList;
    }
    
    static String normaliseAddress(final String hostname) {
        if (hostname == null) {
            return hostname;
        }
        try {
            final InetAddress inetAddress = InetAddress.getByName(hostname);
            return inetAddress.getHostAddress();
        }
        catch (UnknownHostException unexpected) {
            return hostname;
        }
    }
}
