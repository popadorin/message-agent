package com.dorin.messagebroker;

import com.dorin.messagebroker.commands.*;
import com.dorin.models.Command;
import com.dorin.models.MessageInfo;
import org.apache.log4j.Logger;

import java.util.Optional;

public class CommandProvider {
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());
    private BrokerFacade brokerFacade;

    public CommandProvider(BrokerFacade brokerFacade) {
        this.brokerFacade = brokerFacade;
    }

    public Optional<Command> getCommand(MessageInfo inputInfo) {
        switch (inputInfo.getCommandType()) {
            case CREATE:
                return Optional.of(new CreateCommand(brokerFacade, inputInfo));
            case SUBSCRIBE:
                return Optional.of(new SubscribeCommand(brokerFacade, inputInfo));
            case GET:
                return Optional.of(new GetCommand(brokerFacade, inputInfo));
            case PUT:
                return Optional.of(new PutCommand(brokerFacade, inputInfo));
            case EXIT:
                return Optional.of(new ExitCommand(brokerFacade, inputInfo.getId()));
            default:
                LOGGER.error("Something wrong with the command-type");
                return Optional.empty();
        }

    }
}
