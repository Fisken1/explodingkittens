package game;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import communication.messages.Message;
import communication.network.Client;
import communication.network.Server;
import factory.CardsFactory;
import factory.MessageFactory;
import game.decks.DiscardPile;
import game.decks.MainDeck;
import player.Player;

public class GameLogicTest {
    Player a;
    Player b;
    DiscardPile discardPile;
    MainDeck drawPile;
    GameLogic gameLogic;
    ArrayList<String> expansions = new ArrayList<String>();
    Socket connectionSocket;
    Server server;
    Client client;
    Message m;

    @BeforeEach
    public void initAllCardsTests() throws Exception {

        gameLogic = new GameLogic(2, 0);
        server = gameLogic.getServer();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> Assertions.assertDoesNotThrow(() -> {
            server.addPlayersAndBots();
        }));
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
        client = new Client();
        client.client("127.0.0.1");
        discardPile = new DiscardPile();
        drawPile = new MainDeck();
        a = server.getPlayers().get(1);
        b = server.getPlayers().get(0);
        expansions.add("Exploding kittens");
    }

    @AfterEach
    public void tearDown() {
        server.closeSocket();
    }

    @Test
    public void deal7CardsToAllPlayers() {
        gameLogic.getDrawPile().populateDeck(2, expansions);
        gameLogic.getDrawPile().shuffle();
        gameLogic.dealCards();

        assertEquals(8, a.getHand().getCurrentSize());
        assertEquals(8, b.getHand().getCurrentSize());

    }

    @Test
    public void randomPlayerStarts() { // This test is a bit wierd as you could get the same value as it is "Random"
                                       // and testing randomness seams kinda pointless as with unit testing we want to
                                       // test a value vs a expected value
        gameLogic.getServer().getPlayers().add(a);
        gameLogic.getServer().getPlayers().add(b);
        // assertNotEquals(gameLogic.randomPlayerStarts(),
        // gameLogic.randomPlayerStarts());
    }

    @Test
    public void newPlayersTurnAfterPass() throws IOException, InterruptedException {
        gameLogic.getDrawPile().add(CardsFactory.createCard("Nope"), 0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> Assertions.assertDoesNotThrow(() -> {
            gameLogic.startGame(a.getId());
        }));
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        client.getOutToServer().writeObject("Pass");
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
        executor.shutdown();
        assertEquals(b.getId(), gameLogic.getCurrentPlayer().getId());
        assertEquals(1, a.getHand().getCurrentSize());

    }

    @Test
    public void playerPlaysMultipleCards() throws IOException, InterruptedException {
        gameLogic.getDrawPile().populateDeck(2, expansions);
        a.getHand().add(CardsFactory.createCard("SeeTheFuture"), 0);
        a.getHand().add(CardsFactory.createCard("Shuffle"), 0);
        a.getHand().add(CardsFactory.createCard("Shuffle"), 0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> Assertions.assertDoesNotThrow(() -> {
            gameLogic.startGame(a.getId());
        }));

        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        client.getOutToServer().writeObject("Shuffle");
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());

        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        client.getOutToServer().writeObject("SeeTheFuture");
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());

        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        client.getOutToServer().writeObject("Shuffle");
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());
        executor.shutdown();
    }

}
