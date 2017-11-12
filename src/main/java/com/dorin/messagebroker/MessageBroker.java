package com.dorin.messagebroker;

import com.dorin.helpers.MessageInfo;
import com.dorin.models.Channel;
import com.dorin.models.CommandType;
import com.dorin.models.Message;
import com.dorin.models.Subscriber;
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

                switch (userInput.toUpperCase()) {
                    case "BACKUP":
                        mqBackuper.backupMessageQueue(generalMessageQueue);
                        break;
                    case "GET BACKUP":
                        generalMessageQueue = mqBackuper.readBackup();
                    case "VIEW":
                        System.out.println("Messages:");
                        generalMessageQueue.getQueue().forEach(System.out::println);
                        break;
                    case "VIEW GOOGLEMQ":
                        System.out.println("GoogleMQ messages:");
                        googleMQ.getQueue().forEach(System.out::println);
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
            case SUBSCRIBE:
                if (inputInfo.getMessage().getContent() == null) {
                    LOGGER.info("Client " + inputInfo.getId() + " wants to subscribe");
                    subscribers.add(new Subscriber(inputInfo.getId()));
                } else {
                    LOGGER.info("Client " + inputInfo.getId() + " wants to subscribe to channel " +
                            inputInfo.getMessage().getContent());

                    treatConcreteSubscribers(inputInfo);
                }
                break;
            case GET:
                break;
            case PUT:
                if (inputInfo.getChannel() == null) {
                    generalMessageQueue.push(inputInfo.getMessage());
                } else {
                    switch (inputInfo.getChannel()) {
                        case GOOGLE:
                            googleMQ.push(inputInfo.getMessage());
                            break;
                        case FACEBOOK:
                            facebookMQ.push(inputInfo.getMessage());
                            break;
                        case YOUTUBE:
                            youtubeMQ.push(inputInfo.getMessage());
                            break;
                        default:
                            generalMessageQueue.push(inputInfo.getMessage());
                            break;
                    }
                }
                break;
            default:
                break;
        }

    }

    private void treatConcreteSubscribers(MessageInfo inputInfo) {
        switch (inputInfo.getMessage().getContent().toUpperCase()) {
            case "GOOGLE":
                subscribers.add(new Subscriber(inputInfo.getId(), Channel.GOOGLE));
                break;
            case "FACEBOOK":
                subscribers.add(new Subscriber(inputInfo.getId(), Channel.FACEBOOK));
                break;
            case "YOUTUBE":
                subscribers.add(new Subscriber(inputInfo.getId(), Channel.YOUTUBE));
                break;
            default:
                break;
        }
    }

}
