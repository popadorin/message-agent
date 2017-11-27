package com.dorin.models;

import com.dorin.messagebroker.TransportBroker;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SubscribersManager implements Observer {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private List<Subscriber> subscribers = new ArrayList<>();
    private TransportBroker transportBroker;

    public SubscribersManager(TransportBroker transportBroker) {
        this.transportBroker = transportBroker;
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    @Override
    public void update(Observable o, Object arg) {
        // depending on message information (id and message) send that to corresponding subscribers

        LOGGER.info("Have been updated, with: " + arg);
        MessageInfo messageInfo = (MessageInfo) arg;
        System.out.println("messageInfo from update: " + messageInfo);

        for (Subscriber subscriber : subscribers) {
            if (subscriber.getChannel() == null) {
                continue;
            }

            if (subscriber.getChannel().equals(messageInfo.getChannel())) {
                LOGGER.info("SEND " + messageInfo.getMessage() +
                        "TO " + subscriber.getId());

                transportBroker.send(subscriber.getId(), messageInfo.getMessage());
            }

        }


    }
}
