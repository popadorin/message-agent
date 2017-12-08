package com.dorin.messagebroker;

import com.dorin.models.*;
import com.dorin.helpers.MQBackuper;
import org.apache.log4j.Logger;

import java.util.*;

public class MessageBroker implements Observer {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    public final TransportBrokerImpl transport = new TransportBrokerImpl();
    private static final String MQ_PATH = "./src/main/resources/messagequeue-backup";
    private static final MQBackuper mqBackuper = new MQBackuper(MQ_PATH);

    private static BrokerFacade brokerFacade;
    private CommandProvider provider;

    private MessageBroker() {
        transport.listenToMessages();
        transport.addObserver(this);
        brokerFacade = new BrokerFacade(transport);
        provider = new CommandProvider(brokerFacade);
    }

    @Override
    public void update(Observable o, Object arg) {
        MessageInfo inputInfo = (MessageInfo) arg;

        LOGGER.info("update with arg - " + arg);
        Optional<Command> command = provider.getCommand(inputInfo);

        command.ifPresent(Command::execute);
    }



    public static void main(String[] args) {
        try {
            LOGGER.info("MessageBroker started!");
            System.out.println("Broker commands: LS, BACKUP, GET BACKUP, VIEW, SEND, EXIT");

            new MessageBroker();

            boolean isStopped = false;
            while (!isStopped) {
                String userInput = new Scanner(System.in).nextLine();

                switch (userInput.toUpperCase().trim()) {
                    case "LS":
                        System.out.println("List all the messages:");

                        System.out.println("From general:");
                        brokerFacade.getGeneralMessageQueue().getQueue().forEach(System.out::println);

                        System.out.println("\nFrom persistent queues:");
                        for (String channel : brokerFacade.getPersistantQueues().keySet()) {
                            System.out.println("Queue: " + channel);
                            MessageQueue messageQueue = brokerFacade.getPersistantQueues().get(channel);
                            messageQueue.getQueue().forEach(System.out::println);
                        }

                        System.out.println("\nFrom nonpersistent queues:");
                        for (String channel : brokerFacade.getNonpersistantQueues().keySet()) {
                            System.out.println("Queue: " + channel);
                            MessageQueue messageQueue = brokerFacade.getNonpersistantQueues().get(channel);
                            messageQueue.getQueue().forEach(System.out::println);
                        }
                        break;
                    case "VIEW TOPICS":
                        brokerFacade.getQueueTopics().forEach(System.out::println);
                        break;
//                    case "BACKUP GENERAL MQ":
//                        mqBackuper.backupMessageQueue(brokerFacade.getGeneralMessageQueue());
//                        break;
//                    case "VIEW":
//                        System.out.println("Insert queue name:");
//                        String queueName = new Scanner(System.in).nextLine().toUpperCase();
//                        if (!brokerFacade.existsInQueues(queueName)) {
//                            LOGGER.error("There is no queue - " + queueName);
//                        } else {
//                            System.out.println("Messages:");
//                            if (brokerFacade.getPersistantQueues().containsKey(queueName)) {
//                                brokerFacade.getPersistantQueues().get(queueName).getQueue().forEach(System.out::println);
//                            } else {
//                                brokerFacade.getNonpersistantQueues().get(queueName).getQueue().forEach(System.out::println);
//                            }
//                        }
//                        break;
                    case "VIEW SUBSCRIBERS":
                        System.out.println("Subscribers:");
                        brokerFacade.getSubscribersManager().getSubscribers().forEach(System.out::println);
                        break;
                    case "EXIT":
                        brokerFacade.getTransport().close();
                        isStopped = true;
                        break;
                    default:
                        LOGGER.error("No such command");
                        break;
                }

            }

            LOGGER.info("Close MessageBroker");
        } catch (Exception ex) {
//            mqBackuper.backupMessageQueue(generalMessageQueue);

            LOGGER.error("Broker has been broken");
        }
    }

}
