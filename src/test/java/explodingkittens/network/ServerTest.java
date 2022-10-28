package network;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import network.Server;

public class ServerTest {
    @Test
    public void ConnectToManyPlayers() {
        Server server = new Server(6, 0);
        Exception exception = assertThrows(Exception.class, () -> {
            server.addPlayersAndBots();
        });
        String expectedMessage = "You can't connect less than 1 player or more then 5 players!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void ConnectToFewPlayers() {
        Server server = new Server(1, 0);
        Exception exception = assertThrows(Exception.class, () -> {
            server.addPlayersAndBots();
        });
        String expectedMessage = "You can't connect less than 1 player or more then 5 players!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
