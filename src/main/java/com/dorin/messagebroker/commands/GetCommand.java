package com.dorin.messagebroker.commands;

import com.dorin.messagebroker.BrokerFacade;
import com.dorin.models.MessageQueue;
import com.dorin.messagebroker.TransportBroker;
import com.dorin.models.Command;
import com.dorin.models.Message;
import com.dorin.models.MessageInfo;
import org.apache.log4j.Logger;

public class GetCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private static final String NO_SUCH_CHANNEL = "Error: No such channel";

    private BrokerFacade brokerFacade;

    private String channel;
    private Integer id;
    private TransportBroker transport;

    public GetCommand(BrokerFacade brokerFacade, MessageInfo info) {
        this.brokerFacade = brokerFacade;
        this.channel = info.getChannel();
        this.id = info.getId();
        this.transport = brokerFacade.getTransport();
    }

    @Override
    public void execute() {
        if (channel == null) {
            transport.send(id, brokerFacade.getGeneralMessageQueue().pop());
        } else {
            MessageQueue mq = brokerFacade.getPersistantQueues().get(channel);
            transport.send(id,
                    brokerFacade.existsInQueues(channel) ? mq.pop() : new Message(NO_SUCH_CHANNEL));
        }
    }
}
