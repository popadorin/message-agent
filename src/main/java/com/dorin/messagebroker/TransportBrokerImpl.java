package com.dorin.messagebroker;

import com.dorin.transport.TransportServer;

public class TransportBrokerImpl implements TransportBroker {
    private TransportServer transportServer;

    public TransportBrokerImpl() {
        new Thread(() -> transportServer = new TransportServer(8878)).start();

    }

    @Override
    public void sendToAll(String message) {
        transportServer.sendToAllClients(message);
    }

    @Override
    public void listenToIncomingMessages() {

    }

    @Override
    public void close() {
        transportServer.stop();
    }
}
