package com.dorin.messagebroker.commands;

import com.dorin.messagebroker.BrokerFacade;
import com.dorin.messagebroker.MessageQueue;
import com.dorin.models.ChannelType;
import com.dorin.models.Command;
import com.dorin.models.MessageInfo;
import org.apache.log4j.Logger;

public class CreateCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    private BrokerFacade brokerFacade;
    private String channel;
    private ChannelType channelType;

    public CreateCommand(BrokerFacade brokerFacade, MessageInfo messageInfo) {
        this.brokerFacade = brokerFacade;
        this.channel = messageInfo.getChannel();
        this.channelType = messageInfo.getChannelType();
    }

    public CreateCommand(BrokerFacade brokerFacade, String channel, ChannelType channelType) {
        this.brokerFacade = brokerFacade;
        this.channel = channel;
        this.channelType = channelType;
    }

    @Override
    public void execute() {
        LOGGER.info("create queue: " + channel  + ", queueType: " + channelType);
        MessageQueue messageQueue = new MessageQueue(channel);
        messageQueue.addObserver(brokerFacade.getSubscribersManager());
        if (channelType.equals(ChannelType.PERSISTENT)) {
            brokerFacade.getPersistantQueues().put(channel, messageQueue);
        } else {
            brokerFacade.getNonpersistantQueues().put(channel, messageQueue);
        }

        brokerFacade.getQueueTopics().add(channel);

    }
}
