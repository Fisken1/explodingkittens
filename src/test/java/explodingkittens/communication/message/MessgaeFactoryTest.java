package communication.message;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import communication.messages.*;
import factory.MessageFactory;

public class MessgaeFactoryTest {
    @Test
    public void createInformation() {
        Message expected = new Information("Test");
        assertEquals(expected, MessageFactory.createMessage("Test", null, 0));
    }

    @Test
    public void createClosedQuestion() {
        ArrayList<Choice> choices = new ArrayList<Choice>();
        choices.add(new Choice("1", "a"));
        choices.add(new Choice("2", "b"));
        Message expected = new ClosedQuestion("Test", choices);
        assertEquals(expected, MessageFactory.createMessage("Test", choices, 1));
    }

    @Test
    public void createRhetoricalQuestion() {
        ArrayList<Choice> choices = new ArrayList<Choice>();
        choices.add(new Choice("1", "a"));
        choices.add(new Choice("2", "b"));
        Message expected = new RhetoricalQuestion("Test", choices);
        assertEquals(expected, MessageFactory.createMessage("Test", choices, 0));
    }
}
