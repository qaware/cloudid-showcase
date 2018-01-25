package de.qaware.cloud.id.spire;

import io.grpc.ManagedChannelBuilder;

/**
 * A simple functional interface that creates a new channel for accessing the grpc based spire agent and server.
 *
 * @param <T> The actual type of the created channel.
 */
@FunctionalInterface
public interface ChannelFactory<T extends ManagedChannelBuilder<T>> {
    /**
     * Creates the actual channel builder.
     *
     * @return The created channel builder.
     */
    ManagedChannelBuilder<T> createChannel();
}
