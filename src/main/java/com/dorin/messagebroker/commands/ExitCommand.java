package com.dorin.messagebroker.commands;

import com.dorin.messagebroker.BrokerFacade;
import com.dorin.messagebroker.TransportBroker;
import com.dorin.models.Command;
import com.dorin.models.Message;
import com.dorin.models.Subscriber;

public class ExitCommand implements Command {
    private BrokerFacade brokerFacade;

    private TransportBroker transport;
    private Integer id;

    public ExitCommand(BrokerFacade brokerFacade, Integer id) {
        this.brokerFacade = brokerFacade;
        this.transport = brokerFacade.getTransport();
        this.id = id;
    }

    @Override
    public void execute() {
        for (Subscriber subscriber : brokerFacade.getSubscribersManager().getSubscribers()) {
            transport.send(subscriber.getId(), new Message("Sender " + id + " stopped"));
        }
    }
}
