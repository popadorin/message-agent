package com.dorin.messagebroker.commands;

import com.dorin.messagebroker.BrokerFacade;
import com.dorin.messagebroker.TransportBroker;
import com.dorin.models.Command;
import com.dorin.models.Message;
import com.dorin.models.MessageInfo;
import com.dorin.models.Subscriber;
import org.apache.log4j.Logger;

public class SubscribeCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    private BrokerFacade brokerFacade;

    private String channel;
    private Integer id;
    private TransportBroker transport;

    public SubscribeCommand(BrokerFacade brokerFacade, MessageInfo info) {
        this.brokerFacade = brokerFacade;
        this.channel = info.getChannel();
        this.id = info.getId();
        this.transport = brokerFacade.getTransport();
    }

    @Override
    public void execute() {
        boolean existRegex = false;
        for (String topic : brokerFacade.getQueueTopics()) {
            if (topic.matches(channel)) {
                LOGGER.info("Add subscriber to: " + topic);
                brokerFacade.getSubscribersManager().addSubscriber(new Subscriber(id, topic));
                existRegex = true;
            }
        }

        if (!existRegex) {
            if (!brokerFacade.existsInQueues(channel)) {
                transport.send(id, new Message("ERROR: There is no such channel!"));
                return;
            }
        }

        LOGGER.info("Add subscriber");
        if (channel == null) {
            brokerFacade.getSubscribersManager().addSubscriber(new Subscriber(id));
        } else {
            brokerFacade.getSubscribersManager().addSubscriber(new Subscriber(id, channel));
        }
    }
}
