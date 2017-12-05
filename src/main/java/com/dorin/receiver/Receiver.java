package com.dorin.receiver;

import com.dorin.models.Command;
import com.dorin.receiver.commands.*;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Scanner;

public class Receiver implements Observer {
    private static final Logger LOGGER = Logger.getLogger(Receiver.class);
    private final TransportReceiverImpl transport = new TransportReceiverImpl();
    private static CommandProvider provider;

    private Receiver() {
        transport.listenFromBroker();
        transport.addObserver(this);
        provider = new CommandProvider(transport);
    }

    public static void main(String[] args) {
        LOGGER.info("Receiver started");

        new Receiver(); // activate observer

        boolean isStopped = false;
        while (!isStopped) {
            System.out.println("Commands: CREATE, SUBSCRIBE, SEND, EXIT");
            System.out.println("Choose command:");
            String userInput = new Scanner(System.in).nextLine();
            Optional<Command> command = provider.getCommand(userInput);

            isStopped = (command.isPresent() && command.get() instanceof ExitCommand);

            command.ifPresent(Command::execute);
        }

        LOGGER.info("Receiver Stopped");
    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.info("Received message: " + arg);
    }

}
