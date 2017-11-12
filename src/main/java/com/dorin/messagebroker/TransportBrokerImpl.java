package com.dorin.messagebroker;

import com.dorin.helpers.BytesInfo;
import com.dorin.models.Message;
import com.dorin.transport.TransportServer;
import com.dorin.helpers.MessageInfo;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

public class TransportBrokerImpl extends Observable implements TransportBroker, Observer {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private TransportServer transportServer;

    public TransportBrokerImpl() {
        LOGGER.info("Started");
        transportServer = new TransportServer(8878);
    }

    @Override
    public void listenToMessages() {
        transportServer.addObserver(this);
    }

    @Override
    public void sendToAll(Message message) {
        byte[] serializedMessage = SerializationUtils.serialize(message);
        transportServer.sendToAllClients(serializedMessage);
    }

    @Override
    public void send(Integer id, Message message) {
        byte[] serializedMessage = SerializationUtils.serialize(message);
        transportServer.send(id, serializedMessage);
    }

    @Override
    public void close() {
        transportServer.stop();
    }

    @Override
    public void update(Observable o, Object arg) {
        BytesInfo bytesInfo = (BytesInfo) arg;
        MessageInfo messageInfo = SerializationUtils.deserialize(bytesInfo.getObjectBytes());
        messageInfo.setId(bytesInfo.getId());

        setChanged();
        notifyObservers(messageInfo);
    }
}
