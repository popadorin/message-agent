import com.dorin.models.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    @Test
    public void testGetContent() {
        Message message = new Message("message content");

        assertEquals(message.getContent(), "message content");
    }
}
