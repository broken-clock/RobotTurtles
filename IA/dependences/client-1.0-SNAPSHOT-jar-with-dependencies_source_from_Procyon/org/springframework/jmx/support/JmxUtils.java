// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.support;

import javax.management.MXBean;
import org.apache.commons.logging.LogFactory;
import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import java.util.Hashtable;
import org.springframework.util.ObjectUtils;
import javax.management.ObjectName;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.springframework.util.ClassUtils;
import javax.management.MBeanParameterInfo;
import java.util.List;
import java.lang.management.ManagementFactory;
import org.springframework.util.StringUtils;
import javax.management.MBeanServerFactory;
import org.springframework.jmx.MBeanServerNotFoundException;
import javax.management.MBeanServer;
import org.apache.commons.logging.Log;

public abstract class JmxUtils
{
    public static final String IDENTITY_OBJECT_NAME_KEY = "identity";
    private static final String MBEAN_SUFFIX = "MBean";
    private static final String MXBEAN_SUFFIX = "MXBean";
    private static final String MXBEAN_ANNOTATION_CLASS_NAME = "javax.management.MXBean";
    private static final boolean mxBeanAnnotationAvailable;
    private static final Log logger;
    
    public static MBeanServer locateMBeanServer() throws MBeanServerNotFoundException {
        return locateMBeanServer(null);
    }
    
    public static MBeanServer locateMBeanServer(final String agentId) throws MBeanServerNotFoundException {
        MBeanServer server = null;
        if (!"".equals(agentId)) {
            final List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(agentId);
            if (servers != null && servers.size() > 0) {
                if (servers.size() > 1 && JmxUtils.logger.isWarnEnabled()) {
                    JmxUtils.logger.warn("Found more than one MBeanServer instance" + ((agentId != null) ? (" with agent id [" + agentId + "]") : "") + ". Returning first from list.");
                }
                server = servers.get(0);
            }
        }
        if (server == null && !StringUtils.hasLength(agentId)) {
            try {
                server = ManagementFactory.getPlatformMBeanServer();
            }
            catch (SecurityException ex) {
                throw new MBeanServerNotFoundException("No specific MBeanServer found, and not allowed to obtain the Java platform MBeanServer", ex);
            }
        }
        if (server == null) {
            throw new MBeanServerNotFoundException("Unable to locate an MBeanServer instance" + ((agentId != null) ? (" with agent id [" + agentId + "]") : ""));
        }
        if (JmxUtils.logger.isDebugEnabled()) {
            JmxUtils.logger.debug("Found MBeanServer: " + server);
        }
        return server;
    }
    
    public static Class<?>[] parameterInfoToTypes(final MBeanParameterInfo[] paramInfo) throws ClassNotFoundException {
        return parameterInfoToTypes(paramInfo, ClassUtils.getDefaultClassLoader());
    }
    
    public static Class<?>[] parameterInfoToTypes(final MBeanParameterInfo[] paramInfo, final ClassLoader classLoader) throws ClassNotFoundException {
        Class<?>[] types = null;
        if (paramInfo != null && paramInfo.length > 0) {
            types = (Class<?>[])new Class[paramInfo.length];
            for (int x = 0; x < paramInfo.length; ++x) {
                types[x] = ClassUtils.forName(paramInfo[x].getType(), classLoader);
            }
        }
        return types;
    }
    
    public static String[] getMethodSignature(final Method method) {
        final Class<?>[] types = method.getParameterTypes();
        final String[] signature = new String[types.length];
        for (int x = 0; x < types.length; ++x) {
            signature[x] = types[x].getName();
        }
        return signature;
    }
    
    public static String getAttributeName(final PropertyDescriptor property, final boolean useStrictCasing) {
        if (useStrictCasing) {
            return StringUtils.capitalize(property.getName());
        }
        return property.getName();
    }
    
    public static ObjectName appendIdentityToObjectName(final ObjectName objectName, final Object managedResource) throws MalformedObjectNameException {
        final Hashtable<String, String> keyProperties = objectName.getKeyPropertyList();
        keyProperties.put("identity", ObjectUtils.getIdentityHexString(managedResource));
        return ObjectNameManager.getInstance(objectName.getDomain(), keyProperties);
    }
    
    public static Class<?> getClassToExpose(final Object managedBean) {
        return ClassUtils.getUserClass(managedBean);
    }
    
    public static Class<?> getClassToExpose(final Class<?> clazz) {
        return ClassUtils.getUserClass(clazz);
    }
    
    public static boolean isMBean(final Class<?> clazz) {
        return clazz != null && (DynamicMBean.class.isAssignableFrom(clazz) || getMBeanInterface(clazz) != null || getMXBeanInterface(clazz) != null);
    }
    
    public static Class<?> getMBeanInterface(final Class<?> clazz) {
        if (clazz == null || clazz.getSuperclass() == null) {
            return null;
        }
        final String mbeanInterfaceName = clazz.getName() + "MBean";
        final Class<?>[] interfaces;
        final Class<?>[] implementedInterfaces = interfaces = clazz.getInterfaces();
        for (final Class<?> iface : interfaces) {
            if (iface.getName().equals(mbeanInterfaceName)) {
                return iface;
            }
        }
        return getMBeanInterface(clazz.getSuperclass());
    }
    
    public static Class<?> getMXBeanInterface(final Class<?> clazz) {
        if (clazz == null || clazz.getSuperclass() == null) {
            return null;
        }
        final Class<?>[] interfaces;
        final Class<?>[] implementedInterfaces = interfaces = clazz.getInterfaces();
        for (final Class<?> iface : interfaces) {
            boolean isMxBean = iface.getName().endsWith("MXBean");
            if (JmxUtils.mxBeanAnnotationAvailable) {
                final Boolean checkResult = MXBeanChecker.evaluateMXBeanAnnotation(iface);
                if (checkResult != null) {
                    isMxBean = checkResult;
                }
            }
            if (isMxBean) {
                return iface;
            }
        }
        return getMXBeanInterface(clazz.getSuperclass());
    }
    
    public static boolean isMXBeanSupportAvailable() {
        return JmxUtils.mxBeanAnnotationAvailable;
    }
    
    static {
        mxBeanAnnotationAvailable = ClassUtils.isPresent("javax.management.MXBean", JmxUtils.class.getClassLoader());
        logger = LogFactory.getLog(JmxUtils.class);
    }
    
    private static class MXBeanChecker
    {
        public static Boolean evaluateMXBeanAnnotation(final Class<?> iface) {
            final MXBean mxBean = iface.getAnnotation(MXBean.class);
            return (mxBean != null) ? Boolean.valueOf(mxBean.value()) : null;
        }
    }
}
