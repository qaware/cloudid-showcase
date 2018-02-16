package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.ChannelFactory;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spire.api.registration.RegistrationGrpc;
import spire.common.Common;

public class RegistrationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationHandler.class);
    private final ChannelFactory<?> channelFactory;

    public RegistrationHandler(ChannelFactory<?> channelFactory) {
        this.channelFactory = channelFactory;
    }


    public void register(String selector, String parentId, String id) {

    }

    public void fetchEntries() {
        ManagedChannel channel = channelFactory.createChannel().build();
        RegistrationGrpc.RegistrationBlockingStub registrationGrpc = RegistrationGrpc.newBlockingStub(channel);
        Common.RegistrationEntries entries = registrationGrpc.fetchEntries(Common.Empty.newBuilder().build());

        LOGGER.info("entries: {}");
    }
}
