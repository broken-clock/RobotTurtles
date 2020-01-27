// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Random;

public abstract class SocketUtils
{
    public static final int PORT_RANGE_MIN = 1024;
    public static final int PORT_RANGE_MAX = 65535;
    private static final Random random;
    
    public static int findAvailableTcpPort() {
        return findAvailableTcpPort(1024);
    }
    
    public static int findAvailableTcpPort(final int minPort) {
        return findAvailableTcpPort(minPort, 65535);
    }
    
    public static int findAvailableTcpPort(final int minPort, final int maxPort) {
        return SocketType.TCP.findAvailablePort(minPort, maxPort);
    }
    
    public static SortedSet<Integer> findAvailableTcpPorts(final int numRequested) {
        return findAvailableTcpPorts(numRequested, 1024, 65535);
    }
    
    public static SortedSet<Integer> findAvailableTcpPorts(final int numRequested, final int minPort, final int maxPort) {
        return SocketType.TCP.findAvailablePorts(numRequested, minPort, maxPort);
    }
    
    public static int findAvailableUdpPort() {
        return findAvailableUdpPort(1024);
    }
    
    public static int findAvailableUdpPort(final int minPort) {
        return findAvailableUdpPort(minPort, 65535);
    }
    
    public static int findAvailableUdpPort(final int minPort, final int maxPort) {
        return SocketType.UDP.findAvailablePort(minPort, maxPort);
    }
    
    public static SortedSet<Integer> findAvailableUdpPorts(final int numRequested) {
        return findAvailableUdpPorts(numRequested, 1024, 65535);
    }
    
    public static SortedSet<Integer> findAvailableUdpPorts(final int numRequested, final int minPort, final int maxPort) {
        return SocketType.UDP.findAvailablePorts(numRequested, minPort, maxPort);
    }
    
    static {
        random = new Random(System.currentTimeMillis());
    }
    
    private enum SocketType
    {
        TCP {
            @Override
            protected boolean isPortAvailable(final int port) {
                try {
                    final ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
                    serverSocket.close();
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
        }, 
        UDP {
            @Override
            protected boolean isPortAvailable(final int port) {
                try {
                    final DatagramSocket socket = new DatagramSocket(port);
                    socket.close();
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
        };
        
        protected abstract boolean isPortAvailable(final int p0);
        
        private int findRandomPort(final int minPort, final int maxPort) {
            final int portRange = maxPort - minPort;
            return minPort + SocketUtils.random.nextInt(portRange);
        }
        
        int findAvailablePort(final int minPort, final int maxPort) {
            Assert.isTrue(minPort > 0, "'minPort' must be greater than 0");
            Assert.isTrue(maxPort > minPort, "'maxPort' must be greater than 'minPort'");
            Assert.isTrue(maxPort <= 65535, "'maxPort' must be less than or equal to 65535");
            final int portRange = maxPort - minPort;
            int searchCounter = 0;
            while (++searchCounter <= portRange) {
                final int candidatePort = this.findRandomPort(minPort, maxPort);
                if (this.isPortAvailable(candidatePort)) {
                    return candidatePort;
                }
            }
            throw new IllegalStateException(String.format("Could not find an available %s port in the range [%d, %d] after %d attempts", this.name(), minPort, maxPort, searchCounter));
        }
        
        SortedSet<Integer> findAvailablePorts(final int numRequested, final int minPort, final int maxPort) {
            Assert.isTrue(minPort > 0, "'minPort' must be greater than 0");
            Assert.isTrue(maxPort > minPort, "'maxPort' must be greater than 'minPort'");
            Assert.isTrue(maxPort <= 65535, "'maxPort' must be less than or equal to 65535");
            Assert.isTrue(numRequested > 0, "'numRequested' must be greater than 0");
            Assert.isTrue(maxPort - minPort >= numRequested, "'numRequested' must not be greater than 'maxPort' - 'minPort'");
            final SortedSet<Integer> availablePorts = new TreeSet<Integer>();
            int attemptCount = 0;
            while (++attemptCount <= numRequested + 100 && availablePorts.size() < numRequested) {
                availablePorts.add(this.findAvailablePort(minPort, maxPort));
            }
            if (availablePorts.size() != numRequested) {
                throw new IllegalStateException(String.format("Could not find %d available %s ports in the range [%d, %d]", numRequested, this.name(), minPort, maxPort));
            }
            return availablePorts;
        }
    }
}
