package com.dorin.messagebroker.commands;

import com.dorin.messagebroker.BrokerFacade;
import com.dorin.models.ChannelType;
import com.dorin.models.Command;
import com.dorin.models.Message;
import com.dorin.models.MessageInfo;
import org.apache.log4j.Logger;

public class PutCommand implements Command {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    private BrokerFacade brokerFacade;

    private String channel;
    private Message message;
    private ChannelType channelType;

    public PutCommand(BrokerFacade brokerFacade, MessageInfo info) {
        this.brokerFacade = brokerFacade;
        this.channel = info.getChannel();
        this.message = info.getMessage();
        this.channelType = info.getChannelType();
    }

    @Override
    public void execute() {
        if (channel == null) {
            brokerFacade.getGeneralMessageQueue().push(message);
        } else {
            if (brokerFacade.existsInQueues(channel)) {
                if (brokerFacade.getPersistantQueues().containsKey(channel)) {
                    brokerFacade.getPersistantQueues().get(channel).push(message);
                } else {
                    brokerFacade.getNonpersistantQueues().get(channel).push(message);
                }
            } else {
                putMessageToQueueByChannel(message, channel,
                        channelType == null ? ChannelType.NONPERSISTENT : channelType);
            }
        }
    }

    private void putMessageToQueueByChannel(Message message, String channel, ChannelType channelType) {
        if (!brokerFacade.existsInQueues(channel)) {
            Command command = new CreateCommand(brokerFacade, channel, channelType);
            command.execute();
        }

        switch (channelType) {
            case PERSISTENT:
                brokerFacade.getPersistantQueues().get(channel).push(message);
                break;
            case NONPERSISTENT:
                brokerFacade.getNonpersistantQueues().get(channel).push(message);
                break;
            default:
                LOGGER.error("Something wrong with channel-type");
                break;
        }
    }
}
