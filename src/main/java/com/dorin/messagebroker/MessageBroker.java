package com.dorin.messagebroker;

import com.dorin.models.*;
import com.dorin.helpers.MQBackuper;
import org.apache.log4j.Logger;

import java.util.*;

public class MessageBroker implements Observer {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    private final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static final String MQ_PATH = "./src/main/resources/messagequeue-backup";
    private static final MQBackuper mqBackuper = new MQBackuper(MQ_PATH);

    // subscribers
    private static final List<Subscriber> subscribers = new ArrayList<>();

    // queues
    private static MessageQueue generalMessageQueue = new MessageQueue();
    private static MessageQueue googleMQ = new MessageQueue();
    private static MessageQueue facebookMQ = new MessageQueue();
    private static MessageQueue youtubeMQ = new MessageQueue();

    // dynamic queues
    private static Map<String, MessageQueue> persistantQueues = new HashMap<>();
    private static Map<String, MessageQueue> nonpersistantQueues = new HashMap<>();

    private MessageBroker() {
        transport.listenToMessages();
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("MessageBroker started!");
            System.out.println("Broker commands: BACKUP, GET BACKUP, VIEW, SEND, EXIT");

            new MessageBroker();

            boolean isStopped = false;
            while (!isStopped) {
                String userInput = new Scanner(System.in).nextLine();

                switch (userInput.toUpperCase().trim()) {
                    case "BACKUP GENERAL MQ":
                        mqBackuper.backupMessageQueue(generalMessageQueue);
                        break;
                    case "GET BACKUP GENERAL MQ":
                        generalMessageQueue = mqBackuper.readBackup();
                    case "VIEW GENERAL":
                        System.out.println("Messages:");
                        generalMessageQueue.getQueue().forEach(System.out::println);
                        break;
                    case "VIEW":
                        System.out.println("Insert queue name:");
                        String queueName = new Scanner(System.in).nextLine().toUpperCase();
                        if (!existsInQueues(queueName)) {
                            LOGGER.error("There is no queue - " + queueName);
                        } else {
                            System.out.println("Messages:");
                            if (persistantQueues.containsKey(queueName)) {
                                persistantQueues.get(queueName).getQueue().forEach(System.out::println);
                            } else {
                                nonpersistantQueues.get(queueName).getQueue().forEach(System.out::println);
                            }
                        }
                        break;
                    case "VIEW SUBSCRIBERS":
                        System.out.println("Subscribers:");
                        subscribers.forEach(System.out::println);
                        break;
                    case "SEND":
                        transport.sendToAll(generalMessageQueue.pop());
                        break;
                    case "SEND TO SUBSCRIBERS":
                        for (Subscriber subscriber : subscribers) {
                            transport.send(subscriber.getId(), generalMessageQueue.pop());
                        }
                        break;
                    case "EXIT":
                        transport.close();
                        isStopped = true;
                        break;
                    default:
                        LOGGER.error("No such command");
                        break;
                }

            }

            LOGGER.info("Close MessageBroker");
        } catch (Exception ex) {
            mqBackuper.backupMessageQueue(generalMessageQueue);

            LOGGER.error("Broker has been broken");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        MessageInfo inputInfo = (MessageInfo) arg;

        LOGGER.info("update with: observable - " + o + ", arg - " + arg);
        treatMessageInput(inputInfo);

//        // send to all subscribers the sent message
//        if (inputInfo.getCommandType().equals(CommandType.PUT)) {
//            Message message = generalMessageQueue.pop();
//            for (Subscriber subscriber : subscribers) {
//                transport.send(subscriber.getId(), message);
//            }
//        }
    }

    private void treatMessageInput(MessageInfo inputInfo) {
        switch (inputInfo.getCommandType()) {
            case CREATE:
                createQueue(inputInfo.getChannel(), inputInfo.getChannelType());
                break;
            case SUBSCRIBE:
                if (inputInfo.getMessage() == null) {
                    subscribers.add(new Subscriber(inputInfo.getId()));
                } else {
                    subscribers.add(new Subscriber(inputInfo.getId(), inputInfo.getChannel()));
                }
                break;
            case GET:
                if (inputInfo.getChannel() == null) {
                    transport.send(inputInfo.getId(), generalMessageQueue.pop());
                } else {
                    MessageQueue mq = persistantQueues.get(inputInfo.getChannel());
                    transport.send(inputInfo.getId(),
                            existsInQueues(inputInfo.getChannel()) ? mq.pop() : new Message("ERROR"));
                }
                break;
            case PUT:
                if (inputInfo.getChannel() == null) {
                    generalMessageQueue.push(inputInfo.getMessage());
                } else {
                    if (existsInQueues(inputInfo.getChannel())) {
                        if (persistantQueues.containsKey(inputInfo.getChannel())) {
                            persistantQueues.get(inputInfo.getChannel()).push(inputInfo.getMessage());
                        } else {
                            nonpersistantQueues.get(inputInfo.getChannel()).push(inputInfo.getMessage());
                        }
                    } else {
                        ChannelType channelType = inputInfo.getChannelType();
                        putMessageToQueueByChannel(inputInfo.getMessage(), inputInfo.getChannel(),
                                channelType == null ? ChannelType.NONPERSISTENT : channelType);
                    }
                }
                break;
            default:
                LOGGER.error("Something wrong with the command-type");
                break;
        }

    }

    private void createQueue(String channel, ChannelType channelType) {
        LOGGER.info("create queue: " + channel  + ", queueType: " + channelType);
        MessageQueue messageQueue = new MessageQueue();
        if (channelType.equals(ChannelType.PERSISTENT)) {
            persistantQueues.put(channel, messageQueue);
        } else {
            nonpersistantQueues.put(channel, messageQueue);
        }
    }

    private static boolean existsInQueues(String channel) {
        return persistantQueues.containsKey(channel) ||
                nonpersistantQueues.containsKey(channel);
    }

    private void putMessageToQueueByChannel(Message message, String channel, ChannelType channelType) {
        if (!existsInQueues(channel)) {
            createQueue(channel, channelType);
        }

        switch (channelType) {
            case PERSISTENT:
                persistantQueues.get(channel).push(message);
                break;
            case NONPERSISTENT:
                nonpersistantQueues.get(channel).push(message);
                break;
            default:
                LOGGER.error("Something wrong with channel-type");
                break;
        }
    }

}
