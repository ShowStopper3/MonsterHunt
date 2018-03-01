package coding.test.game.conversation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ConversationManagerTest {
    ConversationServices cm;

    @Before
    public void setUp() {
        cm = ConversationServices.getInstance();
    }

    @Test 
    public void testCreation() {
        assertNotNull(cm);
        assertTrue(cm instanceof ConversationServices);
    }
}
