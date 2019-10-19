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

package rocks.xmpp.dns;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A DNS message.
 *
 * @author Christian Schudt
 * @see <a href="https://tools.ietf.org/html/rfc1035#section-4.1">RFC 1035 4.1. Format</a>
 */
final class Message {

    final short id;

    private final boolean query;

    private final OpCode opCode;

    private final boolean authoritativeAnswer;

    private final boolean truncation;

    private final boolean recursionDesired;

    private final boolean recursionAvailable;

    final ResponseCode responseCode;

    private final short questionCount;

    private final short answerCount;

    private final short nameServerCount;

    private final short additionalRecordsCount;

    private final List<Question> questions = new ArrayList<>();

    private final List<ResourceRecord> answers = new ArrayList<>();

    private final List<ResourceRecord> nameServers = new ArrayList<>();

    Message(ByteBuffer data) {

        this.id = data.getShort();
        short header = data.getShort();
        this.query = (header >> 15 & 1) == 0;
        this.opCode = OpCode.values()[(header >> 11 & 0xf)];
        this.authoritativeAnswer = (header >> 10 & 1) == 1;
        this.truncation = (header >> 9 & 1) == 1;
        this.recursionDesired = (header >> 8 & 1) == 1;
        this.recursionAvailable = (header >> 7 & 1) == 1;
        this.responseCode = ResponseCode.values()[header & 0xf];
        this.questionCount = data.getShort();
        this.answerCount = data.getShort();
        this.nameServerCount = data.getShort();
        this.additionalRecordsCount = data.getShort();
        for (short i = 0; i < this.questionCount; i++) {
            this.questions.add(new Question(data));
        }
        for (short i = 0; i < this.answerCount; i++) {
            this.answers.add(new ResourceRecord(data));
        }
        for (short i = 0; i < this.nameServerCount; i++) {
            this.nameServers.add(new ResourceRecord(data));
        }
        // Parsing additional records has caused problems for some answers.
        // We don't need it anyway for SRV and TXT, so ignore them.
    }

    Message(Question... question) {
        this.questions.addAll(Arrays.asList(question));
        this.id = (short) (ThreadLocalRandom.current().nextInt(1 << 16) - (1 << 15));
        this.query = true;
        this.opCode = OpCode.STANDARD_QUERY;
        this.authoritativeAnswer = false;
        this.truncation = false;
        this.recursionDesired = true;
        this.recursionAvailable = false;
        this.responseCode = ResponseCode.OK;
        this.questionCount = (short) question.length;
        this.answerCount = 0;
        this.nameServerCount = 0;
        this.additionalRecordsCount = 0;
    }

    /**
     * Converts the header to a byte buffer.
     *
     * @return The buffer.
     */
    final byte[] toByteArray() {

        final ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byteBuffer.putShort(id);
        short header = 0;
        header = (short) (header | (query ? 0 : 1) << 15);
        header = (short) (header | opCode.ordinal() << 11);
        header = (short) (header | (authoritativeAnswer ? 1 : 0) << 10);
        header = (short) (header | (truncation ? 1 : 0) << 9);
        header = (short) (header | (recursionDesired ? 1 : 0) << 8);
        header = (short) (header | (recursionAvailable ? 1 : 0) << 7);
        header = (short) (header | responseCode.ordinal());
        byteBuffer.putShort(header);
        byteBuffer.putShort(questionCount);
        byteBuffer.putShort(answerCount);
        byteBuffer.putShort(nameServerCount);
        byteBuffer.putShort(additionalRecordsCount);

        byte[] array = byteBuffer.array();
        for (int i = 0; i < questionCount; i++) {
            array = concatArrays(array, this.questions.get(i).toByteArray());
        }
        return array;
    }

    static byte[] concatArrays(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Specifies kind of query in this
     * message. This value is set by the originator of a query
     * and copied into the response
     */
    enum OpCode {
        /**
         * A standard query (QUERY)
         */
        STANDARD_QUERY,

        /**
         * An inverse query (IQUERY)
         */
        INVERSE_QUERY,

        /**
         * A server status request (STATUS)
         */
        SERVER_STATUS_REQUEST
    }

    /**
     * Set as part of responses.
     */
    enum ResponseCode {
        /**
         * No error condition
         */
        OK,

        /**
         * The name server was
         * unable to process this query due to a
         * problem with the name server.
         */
        FORMAT_ERROR,

        /**
         * The name server was
         * unable to interpret the query.
         */
        SERVER_FAILURE,

        /**
         * Meaningful only for
         * responses from an authoritative name
         * server, this code signifies that the
         * domain name referenced in the query does
         * not exist.
         */
        NAME_ERROR,

        /**
         * The name server does
         * not support the requested kind of query.
         */
        NOT_IMPLEMENTED,

        /**
         * The name server refuses to
         * perform the specified operation for
         * policy reasons.  For example, a name
         * server may not wish to provide the
         * information to the particular requester,
         * or a name server may not wish to perform
         * a particular operation (e.g., zone transfer) for particular data.
         */
        REFUSED
    }

    /**
     * Gets the questions.
     *
     * @return The questions.
     */
    public final List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    /**
     * Gets the answers.
     *
     * @return The answers.
     */
    public final List<ResourceRecord> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    /**
     * Gets the name server records.
     *
     * @return The name server records.
     */
    public final List<ResourceRecord> getNameServers() {
        return Collections.unmodifiableList(nameServers);
    }

    /**
     * Gets the additional resource records.
     *
     * @return The additional resource records.
     */
    public final List<ResourceRecord> getAdditionalRecords() {
        return Collections.emptyList();
    }
}
