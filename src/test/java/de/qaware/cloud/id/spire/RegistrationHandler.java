package de.qaware.cloud.id.spire;

import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spire.api.registration.RegistrationGrpc.RegistrationBlockingStub;
import spire.common.Common;

import java.util.function.Supplier;

import static spire.api.registration.RegistrationGrpc.newBlockingStub;

@RequiredArgsConstructor
public class RegistrationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationHandler.class);

    private final Supplier<Channel> channelSupplier;

    private static Common.Empty newRequest() {
        return Common.Empty.newBuilder().build();
    }

    public void register(String selector, String parentId, String id) {

    }

    public void fetchEntries() {
        RegistrationBlockingStub registrationGrpc = newBlockingStub(channelSupplier.get());

        Common.RegistrationEntries entries = registrationGrpc.fetchEntries(newRequest());

        LOGGER.info("entries: {}", entries);
    }
}
