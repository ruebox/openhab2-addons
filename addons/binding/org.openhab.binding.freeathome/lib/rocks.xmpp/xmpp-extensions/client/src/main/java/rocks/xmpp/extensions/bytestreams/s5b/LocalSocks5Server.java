/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Christian Schudt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package rocks.xmpp.extensions.bytestreams.s5b;

import rocks.xmpp.core.XmppException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A local SOCKS5 server which runs as a singleton on the local machine on port 1080 (default SOCKS port).
 *
 * @author Christian Schudt
 */
final class LocalSocks5Server {

    private static final Logger logger = Logger.getLogger(LocalSocks5Server.class.getName());

    final Collection<String> allowedAddresses = new CopyOnWriteArrayList<>();

    private int port = 1080; // The default port for SOCKS5.

    private volatile ServerSocket serverSocket;

    private final Map<String, Socket> socketMap = new ConcurrentHashMap<>();

    /**
     * Starts the local SOCKS5 server.
     */
    public void start() {
        if (serverSocket == null) {
            // Use double-checked locking idiom.
            synchronized (this) {
                if (serverSocket == null) {
                    try {
                        serverSocket = new ServerSocket(getPort());
                        Thread thread = new Thread(() -> {
                            while (serverSocket != null) {
                                Socket socket = null;
                                try {
                                    socket = serverSocket.accept();
                                    socketMap.put(Socks5Protocol.establishServerConnection(socket, allowedAddresses), socket);
                                } catch (IOException e) {
                                    if (socket != null) {
                                        try {
                                            socket.close();
                                        } catch (IOException e1) {
                                            logger.log(Level.WARNING, e.getMessage(), e);
                                        }
                                    }
                                }
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Stops the server.
     */
    public synchronized void stop() {
        if (serverSocket != null) {
            try {
                // This will close the socket and interrupts the accept() method.
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    /**
     * Gets the port. If no port has been set, the default port (1080) is returned.
     *
     * @return The port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port this local server will run on.
     *
     * @param port The port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the port.
     *
     * @return The port.
     */
    public String getAddress() throws XmppException {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new XmppException(e);
        }
    }

    /**
     * Gets the socket for the destination address.
     *
     * @param destinationAddress The destination address.
     * @return The socket.
     */
    public Socket getSocket(String destinationAddress) {
        return socketMap.remove(destinationAddress);
    }
}
