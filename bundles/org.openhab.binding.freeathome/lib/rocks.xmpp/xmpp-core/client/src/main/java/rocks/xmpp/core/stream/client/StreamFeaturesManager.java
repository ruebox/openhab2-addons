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

package rocks.xmpp.core.stream.client;

import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stream.StreamFeatureNegotiator;
import rocks.xmpp.core.stream.StreamHandler;
import rocks.xmpp.core.stream.StreamNegotiationException;
import rocks.xmpp.core.stream.StreamNegotiationResult;
import rocks.xmpp.core.stream.model.StreamFeature;
import rocks.xmpp.core.stream.model.StreamFeatures;
import rocks.xmpp.core.tls.model.StartTls;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Manages the various features, which are advertised during stream negotiation.
 * <blockquote>
 * <p><cite><a href="https://xmpp.org/rfcs/rfc6120.html#streams-negotiation">4.3.  Stream Negotiation</a></cite></p>
 * <p>Because the receiving entity for a stream acts as a gatekeeper to the domains it services, it imposes
 * certain conditions for connecting as a client or as a peer server. At a minimum, the initiating entity
 * needs to authenticate with the receiving entity before it is allowed to send stanzas to the receiving
 * entity (for client-to-server streams this means using SASL as described under Section 6). However, the
 * receiving entity can consider conditions other than authentication to be mandatory-to-negotiate, such as
 * encryption using TLS as described under Section 5. The receiving entity informs the initiating entity
 * about such conditions by communicating "stream features": the set of particular protocol interactions that
 * the initiating entity needs to complete before the receiving entity will accept XML stanzas from the
 * initiating entity, as well as any protocol interactions that are voluntary-to-negotiate but that might
 * improve the handling of an XML stream (e.g., establishment of application-layer compression as described
 * in [XEP-0138]).</p>
 * </blockquote>
 * <p>Each feature is associated with a {@linkplain StreamFeatureNegotiator feature negotiator}, which
 * negotiates the particular feature.</p>
 * <p>This class manages these negotiators, receives XML elements and delegates them to the responsible
 * feature negotiator for further processing.</p>
 * <p>It negotiates the stream by sequentially negotiating each stream feature.</p>
 * <p>
 * This class is thread-safe.
 *
 * @author Christian Schudt
 */
public final class StreamFeaturesManager extends Manager implements StreamHandler {

    private final Map<Class<? extends StreamFeature>, CompletableFuture<Void>> featureNegotiationStartedFutures = new ConcurrentHashMap<>();

    /**
     * The features which have been advertised by the server.
     */
    private final HashMap<Class<? extends StreamFeature>, StreamFeature> advertisedFeatures = new HashMap<>();

    /**
     * The list of features, which the server advertised and have not yet been negotiated.
     */
    private final Queue<StreamFeature> featuresToNegotiate = new ArrayDeque<>();

    /**
     * The feature negotiators, which are responsible to negotiate each individual feature.
     */
    private final Set<StreamFeatureNegotiator<? extends StreamFeature>> streamFeatureNegotiators = new CopyOnWriteArraySet<>();

    private CompletableFuture<Void> negotiationCompleted;

    private CompletableFuture<Void> streamFeaturesReceived;

    private boolean streamWillBeRestarted;

    private StreamFeaturesManager(XmppSession xmppSession) {
        super(xmppSession, false);
    }

    @Override
    protected final void initialize() {
        xmppSession.addSessionStatusListener(e -> {
            switch (e.getStatus()) {
                // If we're (re)connecting, make sure any previous features are forgotten.
                case CONNECTING:
                    synchronized (this) {
                        negotiationCompleted = new CompletableFuture<>();
                        streamFeaturesReceived = new CompletableFuture<>();
                        featureNegotiationStartedFutures.clear();
                        advertisedFeatures.clear();
                    }
                    break;
                // If the connection is closed, clear everything.
                case CLOSED:
                    synchronized (this) {
                        featureNegotiationStartedFutures.clear();
                        advertisedFeatures.clear();
                        featuresToNegotiate.clear();
                        streamFeatureNegotiators.clear();
                    }
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Gets the available features, which the server has advertised.
     *
     * @return The features.
     */
    @SuppressWarnings("unchecked")
    public final Map<Class<? extends StreamFeature>, StreamFeature> getFeatures() {
        // return defensive copies of mutable internal fields
        return Collections.unmodifiableMap((HashMap<Class<? extends StreamFeature>, StreamFeature>) advertisedFeatures.clone());
    }

    @SuppressWarnings("unchecked")
    public final <T> List<T> getFeatures(Class<T> clazz) {
        return Collections.unmodifiableList(this.advertisedFeatures.values().stream()
                .filter(extension -> clazz.isAssignableFrom(extension.getClass()))
                .map(extension -> (T) extension)
                .collect(Collectors.toList()));
    }

    /**
     * Adds a new feature negotiator, which is responsible for negotiating an individual feature.
     *
     * @param streamFeatureNegotiator The feature negotiator, which is responsible for the feature.
     */
    public final void addFeatureNegotiator(StreamFeatureNegotiator<? extends StreamFeature> streamFeatureNegotiator) {
        streamFeatureNegotiators.add(streamFeatureNegotiator);
    }

    /**
     * Removes a feature negotiator.
     *
     * @param streamFeatureNegotiator The feature negotiator.
     */
    public final void removeFeatureNegotiator(StreamFeatureNegotiator<? extends StreamFeature> streamFeatureNegotiator) {
        streamFeatureNegotiators.remove(streamFeatureNegotiator);
    }

    /**
     * Processes the {@code <stream:features/>} element and immediately starts negotiating the first feature.
     *
     * @param featuresElement The {@code <stream:features/>} element.
     * @throws StreamNegotiationException If an exception occurred during feature negotiation.
     */
    public final synchronized void processFeatures(StreamFeatures featuresElement) throws StreamNegotiationException {
        streamWillBeRestarted = false;
        streamFeaturesReceived.complete(null);
        List<Object> featureList = featuresElement.getFeatures();

        featuresToNegotiate.clear();

        // Check if a feature is known, that means it must implement Feature and be added to the context.
        featureList.stream().filter(feature -> feature instanceof StreamFeature).sorted().forEach(feature -> {
            StreamFeature f = (StreamFeature) feature;
            advertisedFeatures.put(f.getClass(), f);
            // Store the list of features. Each feature will be negotiated sequentially, if there is a corresponding feature negotiator.
            featuresToNegotiate.add(f);
        });
        // If the receiving entity advertises only the STARTTLS feature [...] the parties MUST consider TLS as mandatory-to-negotiate.
        if (featureList.size() == 1 && featureList.get(0) instanceof StartTls) {
            ((StartTls) featureList.get(0)).setMandatory(true);
        }

        // Immediately start negotiating the first feature.
        negotiateNextFeature();
    }

    /**
     * Tries to process an element, which is a feature or may belong to a feature protocol, e.g. the {@code <proceed/>} element from TLS negotiation.
     *
     * @param element The element.
     * @return True, if the stream needs to be restarted, after a feature has been negotiated.
     * @throws StreamNegotiationException If an exception occurred during feature negotiation.
     */
    @Override
    public final boolean handleElement(Object element) throws StreamNegotiationException {
        StreamNegotiationResult streamNegotiationResult = StreamNegotiationResult.IGNORE;
        // Check if the element is known to any feature negotiator.
        for (StreamFeatureNegotiator<? extends StreamFeature> streamFeatureNegotiator : streamFeatureNegotiators) {
            if (streamFeatureNegotiator.getFeatureClass() == element.getClass() || streamFeatureNegotiator.canProcess(element)) {
                streamNegotiationResult = streamFeatureNegotiator.processNegotiation(element);
                if (streamNegotiationResult != StreamNegotiationResult.IGNORE && element instanceof StreamFeature) {
                    synchronized (this) {
                        streamWillBeRestarted = ((StreamFeature) element).requiresRestart();
                    }
                }
                break;
            }
        }
        // If the feature has been successfully negotiated.
        switch (streamNegotiationResult) {
            case RESTART:
                synchronized (this) {
                    featuresToNegotiate.clear();
                    streamWillBeRestarted = false;
                }
                return true;
            case SUCCESS:
            case IGNORE:
                negotiateNextFeature();
                return false;
            default:
                return false;
        }
    }

    /**
     * Negotiates the next feature. If the feature has been successfully negotiated, the next feature is automatically negotiated.
     *
     * @throws StreamNegotiationException If an exception occurred during feature negotiation.
     */
    private synchronized void negotiateNextFeature() throws StreamNegotiationException {
        final StreamFeature advertisedFeature;
        // Get the next feature
        if ((advertisedFeature = featuresToNegotiate.poll()) != null) {
            final CompletableFuture<Void> streamFuture = featureNegotiationStartedFutures.computeIfAbsent(advertisedFeature.getClass(), k -> new CompletableFuture<>());
            if (!streamFuture.isDone()) {
                handleElement(advertisedFeature);
                // Complete the future, which means, negotiation of the feature has been started.
                streamFuture.complete(null);
            } else {
                // If the feature has been successfully negotiated, immediately go on with the next feature.
                negotiateNextFeature();
            }
        } else if (!streamWillBeRestarted && streamFeaturesReceived.isDone()) {
            for (CompletableFuture<Void> condition : featureNegotiationStartedFutures.values()) {
                condition.complete(null);
            }
            featureNegotiationStartedFutures.clear();
            if (negotiationCompleted != null) {
                negotiationCompleted.complete(null);
            }
        }
    }

    /**
     * Waits until the given feature will be negotiated. If the feature has already been negotiated it immediately returns.
     *
     * @param streamFeature The stream feature class.
     * @return The future which is complete when the negotiation of the stream feature is completed.
     */
    public final Future<Void> awaitNegotiation(Class<? extends StreamFeature> streamFeature) {
        return featureNegotiationStartedFutures.computeIfAbsent(streamFeature, k -> new CompletableFuture<>());
    }

    /**
     * Negotiates all pending features, if any, and waits until all features have been negotiated.
     *
     * @return The future which is complete when all pending features have been negotiated.
     * @throws StreamNegotiationException If the stream negotiation failed.
     */
    public final synchronized Future<Void> completeNegotiation() throws StreamNegotiationException {
        negotiateNextFeature();
        return negotiationCompleted;
    }

    /**
     * Cancels negotiation and releases any locks.
     */
    public void cancelNegotiation() {
        for (Future<Void> condition : featureNegotiationStartedFutures.values()) {
            condition.cancel(false);
        }
        featureNegotiationStartedFutures.clear();
    }
}
