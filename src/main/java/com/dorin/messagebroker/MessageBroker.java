package com.dorin.messagebroker;

import com.dorin.models.*;
import com.dorin.helpers.MQBackuper;
import org.apache.log4j.Logger;

import java.util.*;

public class MessageBroker implements Observer {
    private final static Logger LOGGER = Logger.getLogger(MessageBroker.class.getName());
    public final static TransportBrokerImpl transport = new TransportBrokerImpl();
    private static final String MQ_PATH = "./src/main/resources/messagequeue-backup";
    private static final MQBackuper mqBackuper = new MQBackuper(MQ_PATH);
    private static final String NO_SUCH_CHANNEL = "Error: No such channel";
    private static final List<String> queueTopics = new ArrayList<>();

    // subscribers
    private static SubscribersManager subscribersManager;
    // queues
    private static MessageQueue generalMessageQueue = new MessageQueue(null);

    // dynamic queues
    private static Map<String, MessageQueue> persistantQueues = new HashMap<>();
    private static Map<String, MessageQueue> nonpersistantQueues = new HashMap<>();

    private MessageBroker() {
        transport.listenToMessages();
        transport.addObserver(this);
        subscribersManager = new SubscribersManager(transport);
        generalMessageQueue.addObserver(subscribersManager);
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
                        generalMessageQueue.getQueue().forEach(System.out::println);

                        System.out.println("\nFrom persistent queues:");
                        for (String channel : persistantQueues.keySet()) {
                            System.out.println("Queue: " + channel);
                            MessageQueue messageQueue = persistantQueues.get(channel);
                            messageQueue.getQueue().forEach(System.out::println);
                        }

                        System.out.println("\nFrom nonpersistent queues:");
                        for (String channel : nonpersistantQueues.keySet()) {
                            System.out.println("Queue: " + channel);
                            MessageQueue messageQueue = nonpersistantQueues.get(channel);
                            messageQueue.getQueue().forEach(System.out::println);
                        }
                        break;
                    case "VIEW TOPICS":
                        queueTopics.forEach(System.out::println);
                        break;
                    case "BACKUP GENERAL MQ":
                        mqBackuper.backupMessageQueue(generalMessageQueue);
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
                        subscribersManager.getSubscribers().forEach(System.out::println);
                        break;
                    case "SEND":
                        transport.sendToAll(generalMessageQueue.pop());
                        break;
                    case "SEND TO SUBSCRIBERS":
                        for (Subscriber subscriber : subscribersManager.getSubscribers()) {
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

        LOGGER.info("update with arg - " + arg);
        treatMessageInput(inputInfo);
    }

    private void treatMessageInput(MessageInfo inputInfo) {
        switch (inputInfo.getCommandType()) {
            case CREATE:
                createQueue(inputInfo.getChannel(), inputInfo.getChannelType());
                queueTopics.add(inputInfo.getChannel());
                break;
            case SUBSCRIBE:
                treatSubscribe(inputInfo.getId(), inputInfo.getChannel());
                break;
            case GET:
                treatGet(inputInfo.getId(), inputInfo.getChannel());
                break;
            case PUT:
                treatPut(inputInfo.getChannelType(), inputInfo.getChannel(), inputInfo.getMessage());
                break;
            case EXIT:
                for (Subscriber subscriber : subscribersManager.getSubscribers()) {
                    transport.send(subscriber.getId(), new Message("Sender " + inputInfo.getId() + " stopped"));
                }
                break;
            default:
                LOGGER.error("Something wrong with the command-type");
                break;
        }

    }

    private void treatSubscribe(Integer id, String channel) {
        boolean existRegex = false;
        for (String topic : queueTopics) {
            if (topic.matches(channel)) {
                LOGGER.info("Add subscriber to: " + topic);
                subscribersManager.addSubscriber(new Subscriber(id, topic));
                existRegex = true;
            }
        }

        if (!existRegex) {
            if (!existsInQueues(channel)) {
                transport.send(id, new Message("ERROR: There is no such channel!"));
                return;
            }
        }

        LOGGER.info("Add subscriber");
        if (channel == null) {
            subscribersManager.addSubscriber(new Subscriber(id));
        } else {
            subscribersManager.addSubscriber(new Subscriber(id, channel));
        }



    }


    private void treatGet(Integer id, String channel) {
        if (channel == null) {
            transport.send(id, generalMessageQueue.pop());
        } else {
            MessageQueue mq = persistantQueues.get(channel);
            transport.send(id,
                    existsInQueues(channel) ? mq.pop() : new Message(NO_SUCH_CHANNEL));
        }
    }

    private void treatPut(ChannelType channelType, String channel, Message message) {
        if (channel == null) {
            generalMessageQueue.push(message);
        } else {
            if (existsInQueues(channel)) {
                if (persistantQueues.containsKey(channel)) {
                    persistantQueues.get(channel).push(message);
                } else {
                    nonpersistantQueues.get(channel).push(message);
                }
            } else {
                putMessageToQueueByChannel(message, channel,
                        channelType == null ? ChannelType.NONPERSISTENT : channelType);
            }
        }
    }

    private void createQueue(String channel, ChannelType channelType) {
        LOGGER.info("create queue: " + channel  + ", queueType: " + channelType);
        MessageQueue messageQueue = new MessageQueue(channel);
        messageQueue.addObserver(subscribersManager);
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
