package com.dorin.messagebroker;

import com.dorin.helpers.MessageInfo;
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

    private MessageBroker() {
        transport.addObserver(this);
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("MessageBroker started!");
            System.out.println("Broker commands: BACKUP, GET BACKUP, VIEW, SEND, EXIT");

            try {
                Thread.sleep(2000);    // because the transport could not be initialized yet
                transport.listenToMessages();
            } catch (InterruptedException e) {
                LOGGER.error("thread sleep interrupted exception occurred!");
            }

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

        // send to all subscribers the sent message
        if (inputInfo.getMessage().getCommandType().equals(CommandType.PUT)) {
            Message message = generalMessageQueue.pop();
            for (Subscriber subscriber : subscribers) {
                transport.send(subscriber.getId(), message);
            }
        }
    }

    private void treatMessageInput(MessageInfo inputInfo) {
        switch (inputInfo.getMessage().getCommandType()) {
            case SUBSCRIBE:
                LOGGER.info("Client " + inputInfo.getId() + " wants to subscribe");
                subscribers.add(new Subscriber(inputInfo.getId()));
                break;
            case GET:
                break;
            case PUT:
                generalMessageQueue.push(inputInfo.getMessage());
                break;
            default:
                break;
        }

    }
    
}
