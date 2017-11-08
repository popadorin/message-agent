import com.dorin.models.CommandType;
import com.dorin.models.Message;
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
