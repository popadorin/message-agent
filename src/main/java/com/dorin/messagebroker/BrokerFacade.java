package com.dorin.messagebroker;

import com.dorin.models.SubscribersManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokerFacade {
    private final TransportBroker transport;
    private final SubscribersManager subscribersManager;

    private final List<String> queueTopics;

    private final MessageQueue generalMessageQueue;
    // dynamic queues
    private final Map<String, MessageQueue> persistantQueues;
    private final Map<String, MessageQueue> nonpersistantQueues;

    public BrokerFacade(TransportBroker transport) {
        this.transport = transport;
        generalMessageQueue = new MessageQueue(null);
        persistantQueues = new HashMap<>();
        nonpersistantQueues = new HashMap<>();
        subscribersManager = new SubscribersManager(transport);
        queueTopics = new ArrayList<>();

        generalMessageQueue.addObserver(subscribersManager);
    }

    public boolean existsInQueues(String channel) {
        return persistantQueues.containsKey(channel) ||
                nonpersistantQueues.containsKey(channel);
    }

    public TransportBroker getTransport() {
        return transport;
    }

    public SubscribersManager getSubscribersManager() {
        return subscribersManager;
    }

    public List<String> getQueueTopics() {
        return queueTopics;
    }

    public MessageQueue getGeneralMessageQueue() {
        return generalMessageQueue;
    }

    public Map<String, MessageQueue> getPersistantQueues() {
        return persistantQueues;
    }

    public Map<String, MessageQueue> getNonpersistantQueues() {
        return nonpersistantQueues;
    }
}
