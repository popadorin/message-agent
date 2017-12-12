import com.dorin.messagebroker.BrokerFacade;
import com.dorin.messagebroker.TransportBrokerImpl;
import com.dorin.models.MessageQueue;
import com.dorin.models.SubscribersManager;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FacadeTest {

    @Test
    public void testBrokerFacade() {
        BrokerFacade brokerFacade = new BrokerFacade(new TransportBrokerImpl());

        Map<String, MessageQueue> persistantQueues = brokerFacade.getPersistantQueues();
        Map<String, MessageQueue> nonpersistantQueues = brokerFacade.getNonpersistantQueues();
        SubscribersManager subscribersManager = brokerFacade.getSubscribersManager();
        List<String> queueTopics = brokerFacade.getQueueTopics();
        MessageQueue generalMessageQueue = brokerFacade.getGeneralMessageQueue();

        assertNotNull(persistantQueues);
        assertNotNull(nonpersistantQueues);
        assertNotNull(subscribersManager);
        assertNotNull(queueTopics);
        assertNotNull(generalMessageQueue);

        brokerFacade.getTransport().close();
    }
}
