import com.dorin.messagebroker.CommandType;
import com.dorin.messagebroker.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    @Test
    public void testGetContent() {
        Message message = new Message(CommandType.PUT, "message content");

        assertEquals(message.getContent(), "message content");
    }

    @Test
    public void testGetCommand() {
        Message message = new Message(CommandType.GET, "some content");

        assertEquals(message.getCommandType(), CommandType.GET);
    }
}
