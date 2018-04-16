package de.qaware.cloudid.lib.jsa;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Security;
import java.util.Optional;

/**
 * CloudId client socket factory.
 */
public class CloudIdSocketFactory extends SSLSocketFactory {

    private static final String SOCKET_FACTORY_PROVIDER = "ssl.SocketFactory.provider";

    private static String socketFactoryProvider;

    private final SSLSocketFactory delegate = CloudIdContextFactory.get().getSocketFactory();

    /**
     * Install the client socket factory
     */
    public static synchronized void install() {
        socketFactoryProvider = Security.getProperty(SOCKET_FACTORY_PROVIDER);
        Security.setProperty(SOCKET_FACTORY_PROVIDER, CloudIdSocketFactory.class.getName());
    }

    /**
     * Uninstall the client socket factory
     */
    public static synchronized void uninstall() {

        Security.setProperty(SOCKET_FACTORY_PROVIDER, Optional.ofNullable(socketFactoryProvider).orElse(""));
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
        return delegate.createSocket(socket, s, i, b);
    }

    @Override
    public Socket createSocket(Socket socket, InputStream inputStream, boolean b) throws IOException {
        return delegate.createSocket(socket, inputStream, b);
    }

    @Override
    public Socket createSocket() throws IOException {
        return delegate.createSocket();
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException {
        return delegate.createSocket(s, i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException {
        return delegate.createSocket(s, i, inetAddress, i1);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return delegate.createSocket(inetAddress, i);
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return delegate.createSocket(inetAddress, i, inetAddress1, i1);
    }

}
