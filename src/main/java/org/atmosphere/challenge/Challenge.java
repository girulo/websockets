package org.atmosphere.challenge;

import java.io.IOException;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedService(interceptors = { AtmosphereResourceLifecycleInterceptor.class,
                                HeartbeatInterceptor.class
                                }, path = "/challenge")
public final class Challenge {
    private static final Logger logger = LoggerFactory.getLogger(Challenge.class);
    private static final String HEADER = "Authentication";

    /**
     * By the use of Atmosphere Framework this method will attend the GET requests, will connect the clients and will
     * suspend their connections in order to brodcast the messages
     */
    @Ready
    public final void onReady(final AtmosphereResource atmosphereResource) throws IOException {

        logger.info("Browser {} connected.", atmosphereResource.uuid());
    }

    /**
     * This method will be called when the client disconnect or the connection has been close by an unexpected situation
     * This method is standard like in the documentation
     */
    @Disconnect
    public final void onDisconnect(final AtmosphereResourceEvent event) {
        if (event.isCancelled())
            logger.info("Browser {} unexpectedly disconnected", event.getResource().uuid());
        else if (event.isClosedByClient())
            logger.info("Browser {} closed the connection", event.getResource().uuid());
    }

    /**
     * This method will get the POST requests that has a TextMessage as a payload in the request body. Like the
     * challenge proposed, it will check for a header named "Authentication" and will check that the value of such
     * header (a senderID) is the same that the one comes in the payload. If that happens, the message will be
     * broadcasted to all the connected clients
     */
    @Message(encoders = { TextMessageKtEncoderDecoder.class }, decoders = { TextMessageKtEncoderDecoder.class })
//    @Message
    public final TextMessageKt onMessage(AtmosphereResource atmosphereResource,
                                       final TextMessageKt message) throws IOException {

        AtmosphereRequest atmosphereRequest = atmosphereResource.getRequest();

        String headerValue = atmosphereRequest.getHeader(HEADER);
        if (headerValue != null) {
            String headerSenderId = headerValue.split(" ")[1];
            if (headerSenderId.equals(message.getSenderId().toString())) {
                logger.info("{} just send {}", message.getSenderId(), message.getPushMessage());
                return message;
            } else {
                logger.error("Header Authentication Bearer: {} does not match the senderId {} included in the message",
                             headerSenderId,
                             message.getSenderId());
                return null;
            }
        }
        logger.error("Invalid message. Necessary header authentication");
        return null;
    }

}