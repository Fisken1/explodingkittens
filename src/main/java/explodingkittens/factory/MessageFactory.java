package factory;

import java.util.ArrayList;

import communication.messages.*;

public class MessageFactory {

    public static Message createMessage(String message, ArrayList<Choice> choices, int needAnswer) {
        // Common messages to send to players
        switch (message) {
            // Basic promts
            case "WelcomeMessage":
                return new Information("Welcome to ExplodingKittens!\n");
            case "NotValid":
                return new Information("Not a viable option, try again\n");
            case "Hand":
                return new RhetoricalQuestion("Your hand:\n", choices);
            case "Options":
                return new ClosedQuestion("\nYour options:\n\n", choices);
            case "Empty":
                return new Information("The draw pile is empty\n");
            case "Nope":
                return new RhetoricalQuestion(
                        "Do you want to play a Nope Card (if one was just played you alternate between Nope and Yup)\n",
                        choices); // CHANGE
            case "ClearScreen":
                return new Information(
                        "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
            // Card promts
            case "SeeTheFuture":
                return new RhetoricalQuestion("The top 3 cards from the draw pile are \n", choices);
            case "Attack":
                return new Information("You were attacked\n");
            case "Shuffle":
                return new Information("The draw pile was shuffled\n");
            case "Skip":
                return new Information("You skiped a turn without drawing a new card\n");

        }
        // Otherwise create custom message
        if (message != null && choices == null) {
            return new Information(message);
        } else if (message != null && needAnswer == 0) {
            return new RhetoricalQuestion(message, choices);
        } else {
            return new ClosedQuestion(message, choices);
        }

    }

}
