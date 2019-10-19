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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * An implementation of the SOCKS5 protocol as used by XMPP.
 *
 * @author Christian Schudt
 * @see <a href="http://tools.ietf.org/html/rfc1928">SOCKS Protocol Version 5</a>
 * @see <a href="https://xmpp.org/extensions/xep-0065.html">XEP-0065: SOCKS5 Bytestreams</a>
 */
final class Socks5Protocol {

    private Socks5Protocol() {
    }

    /**
     * Establishes a SOCKS5 client connection.
     *
     * @param socket             The socket.
     * @param destinationAddress The destination address.
     * @throws java.io.IOException If the SOCKS5 connection could not be established.
     */
    static void establishClientConnection(Socket socket, String destinationAddress, int destinationPort) throws IOException {

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        OutputStream outputStream = socket.getOutputStream();

        /*
            The client connects to the server, and sends a version
            identifier/method selection message:

                   +----+----------+----------+
                   |VER | NMETHODS | METHODS  |
                   +----+----------+----------+
                   | 1  |    1     | 1 to 255 |
                   +----+----------+----------+
         */

        outputStream.write(new byte[]{(byte) 0x05, (byte) 0x01, (byte) 0x00}); // 0x00 == NO AUTHENTICATION REQUIRED
        outputStream.flush();

        /*
            The server selects from one of the methods given in METHODS, and
            sends a METHOD selection message:

                         +----+--------+
                         |VER | METHOD |
                         +----+--------+
                         | 1  |   1    |
                         +----+--------+
         */
        byte[] response = new byte[2];
        inputStream.readFully(response);

        // If the server supports version 5 and has accepted the no-authentication method
        if (response[0] == (byte) 0x05 && response[1] == (byte) 0x00) {

            /*
                The SOCKS request is formed as follows:

                  +----+-----+-------+------+----------+----------+
                  |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
                  +----+-----+-------+------+----------+----------+
                  | 1  |  1  | X'00' |  1   | Variable |    2     |
                  +----+-----+-------+------+----------+----------+

             */

            byte[] dstAddr = destinationAddress.getBytes(StandardCharsets.UTF_8);
            byte[] dstPort = new byte[]{(byte) (destinationPort >>> 8), (byte) destinationPort};
            byte[] requestDetails = new byte[]{
                    (byte) 0x05, // protocol version: X'05'
                    (byte) 0x01, // CMD: CONNECT X'01'
                    (byte) 0x00, // RESERVED
                    (byte) 0x03, // ATYP, Hardcoded to 3 (DOMAINNAME) in this usage
                    (byte) dstAddr.length // The first octet of the address field contains the number of octets of name that follow
            };
            byte[] request = new byte[7 + dstAddr.length];
            System.arraycopy(requestDetails, 0, request, 0, requestDetails.length);
            System.arraycopy(dstAddr, 0, request, requestDetails.length, dstAddr.length);
            System.arraycopy(dstPort, 0, request, requestDetails.length + dstAddr.length, dstPort.length);

            outputStream.write(request);
            outputStream.flush();


            /*
                The server evaluates the request, and returns a reply formed as follows:

                    +----+-----+-------+------+----------+----------+
                    |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
                    +----+-----+-------+------+----------+----------+
                    | 1  |  1  | X'00' |  1   | Variable |    2     |
                    +----+-----+-------+------+----------+----------+
             */

            byte[] reply = readRequestOrReply(inputStream);

            if (reply[1] == 0) {  // X'00' succeeded
                request[1] = 0;
                // When replying to the Target in accordance with Section 6 of RFC 1928, the Proxy MUST set the BND.ADDR and BND.PORT to the DST.ADDR and DST.PORT values provided by the client in the connection request.
                if (!(Arrays.equals(reply, request))) {
                    throw new IOException("Verification failed.");
                }
            } else {
                throw new IOException("SOCKS5 server returned error code " + reply[1]);
            }
        } else {
            throw new IOException("Unable to connect to SOCKS5 server.");
        }
    }

    /**
     * Establishes a SOCKS5 connection over the given socket.
     *
     * @param socket The socket.
     * @return The connection id.
     * @throws java.io.IOException If a SOCKS5 protocol violation occurred.
     */
    static String establishServerConnection(Socket socket, Collection<String> allowedAddresses) throws IOException {

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        OutputStream outputStream = socket.getOutputStream();

        /*
            The client connects to the server, and sends a version
            identifier/method selection message:

                   +----+----------+----------+
                   |VER | NMETHODS | METHODS  |
                   +----+----------+----------+
                   | 1  |    1     | 1 to 255 |
                   +----+----------+----------+
         */

        if (inputStream.read() != (byte) 0x05) {
            throw new IOException("Client provided invalid SOCKS version.");
        }

        // The NMETHODS field contains the number of method identifier octets that
        // appear in the METHODS field.
        byte[] methods = new byte[inputStream.read()];
        inputStream.readFully(methods);

        /*
            The server selects from one of the methods given in METHODS, and
            sends a METHOD selection message:

                         +----+--------+
                         |VER | METHOD |
                         +----+--------+
                         | 1  |   1    |
                         +----+--------+
         */

        // Check if the client provided a "NO AUTHENTICATION REQUIRED" method (0x00).
        boolean noAuthRequired = false;
        for (byte method : methods) {
            if (method == (byte) 0x00) {
                noAuthRequired = true;
                break;
            }
        }
        outputStream.write((byte) 0x05); // VER

        if (noAuthRequired) {
            // If the client provided the "no auth" method, everything is fine.
            outputStream.write((byte) 0x00);
            outputStream.flush();
        } else {
            // If the selected METHOD is X'FF', none of the methods listed by the
            // client are acceptable, and the client MUST close the connection.
            outputStream.write((byte) 0xFF);
            outputStream.flush();
            throw new IOException("Client provided unsupported authentication methods.");
        }

        // Client now sends:

        /*
             The SOCKS request is formed as follows:

                 +----+-----+-------+------+----------+----------+
                 |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
                 +----+-----+-------+------+----------+----------+
                 | 1  |  1  | X'00' |  1   | Variable |    2     |
                 +----+-----+-------+------+----------+----------+
         */

        byte[] request = readRequestOrReply(inputStream);
        String dstAddr = new String(request, 5, request[4], StandardCharsets.UTF_8);  // request[4] has the length of the address.

        /*
            The server evaluates the request, and
            returns a reply formed as follows:

                +----+-----+-------+------+----------+----------+
                |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
                +----+-----+-------+------+----------+----------+
                | 1  |  1  | X'00' |  1   | Variable |    2     |
                +----+-----+-------+------+----------+----------+
         */

        if (!allowedAddresses.contains(dstAddr)) {
            request[1] = (byte) 0x05; // X'05' Connection refused
            outputStream.write(request);
            outputStream.flush();
            throw new IOException("Connection refused");
        }

        request[1] = (byte) 0x00; // REP    Reply field: X'00' succeeded
        outputStream.write(request);
        outputStream.flush();

        return new String(request, 5, request[4], StandardCharsets.UTF_8);
    }

    /**
     * Reads a client request:
     * <pre>
     * +----+-----+-------+------+----------+----------+
     * |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
     * +----+-----+-------+------+----------+----------+
     * | 1  |  1  | X'00' |  1   | Variable |    2     |
     * +----+-----+-------+------+----------+----------+
     * </pre>
     * or a server reply:
     * <pre>
     * +----+-----+-------+------+----------+----------+
     * |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
     * +----+-----+-------+------+----------+----------+
     * | 1  |  1  | X'00' |  1   | Variable |    2     |
     * +----+-----+-------+------+----------+----------+
     * </pre>
     * which are structured identically.
     *
     * @param in The input stream.
     * @return The read bytes.
     * @throws java.io.IOException If the SOCKS version or address type is invalid.
     */
    private static byte[] readRequestOrReply(DataInputStream in) throws IOException {
        byte[] firstHalf = new byte[5];
        in.readFully(firstHalf, 0, 5);

        if (firstHalf[0] != (byte) 0x05) {
            throw new IOException("Invalid SOCKS version.");
        }

        if (firstHalf[3] != (byte) 0x03) {
            throw new IOException("Unsupported SOCKS5 address type");
        }

        int addressLength = firstHalf[4];

        byte[] message = new byte[7 + addressLength];
        System.arraycopy(firstHalf, 0, message, 0, firstHalf.length);

        // Read the address and port.
        in.readFully(message, firstHalf.length, addressLength + 2);
        return message;
    }
}
