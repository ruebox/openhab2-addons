/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Christian Schudt
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

package rocks.xmpp.core.stream.server;

import rocks.xmpp.core.stream.StreamNegotiationResult;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.core.stream.model.StreamFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Negotiates stream features from a server perspective.
 * <p>
 * Each feature which shall be advertised during stream negotiation, must be {@linkplain #registerStreamFeatureNegotiator(ServerStreamFeatureNegotiator) registered} first.
 *
 * @author Christian Schudt
 * @see ServerStreamFeatureNegotiator
 */
public final class ServerStreamFeaturesManager {

    private final Map<ServerStreamFeatureNegotiator<? extends StreamFeature>, StreamFeature> toBeNegotiated = new ConcurrentHashMap<>();

    /**
     * Registers a stream features negotiator.
     *
     * @param streamFeaturesNegotiator The negotiator.
     */
    public final void registerStreamFeatureNegotiator(final ServerStreamFeatureNegotiator<? extends StreamFeature> streamFeaturesNegotiator) {
        toBeNegotiated.put(streamFeaturesNegotiator, streamFeaturesNegotiator.createStreamFeature());
    }

    /**
     * Gets the stream features which shall be advertised. Different stream features may be advertised during different
     * phases of the stream negotiation. E.g. the first and only feature should be STARTTLS, then SASL and only then Resource Binding.
     *
     * @return The stream features.
     */
    public final Collection<StreamFeature> getStreamFeatures() {
        final List<StreamFeature> streamFeatures = new ArrayList<>(toBeNegotiated.values());
        streamFeatures.sort(null);
        final int size = streamFeatures.size();
        for (int i = 0; i < size; i++) {
            final StreamFeature streamFeature = streamFeatures.get(i);
            // Only advertise one mandatory at a time, if it requires a restart.
            // Remaining stream features will be offered on the next features advertisement.
            // Remove all stream features, which are not required now.
            if (streamFeature.isMandatory() && streamFeature.requiresRestart()) {
                for (int j = size - 1; j > i; j--) {
                    streamFeatures.remove(j);
                }
                break;
            }
        }
        return streamFeatures;
    }

    /**
     * Handles an inbound element and tries to find a negotiator which feels responsible to negotiate the element.
     *
     * @param element The stream element.
     * @return The negotiation result. If no negotiator was found returns {@link StreamNegotiationResult#IGNORE}
     */
    public final StreamNegotiationResult handleElement(final StreamElement element) {
        for (ServerStreamFeatureNegotiator<? extends StreamFeature> streamNegotiator : toBeNegotiated.keySet()) {
            StreamNegotiationResult result = streamNegotiator.processNegotiation(element);
            switch (result) {
                case RESTART:
                case SUCCESS:
                    toBeNegotiated.remove(streamNegotiator);
                    return result;
                default:
            }
        }
        return StreamNegotiationResult.IGNORE;
    }
}