package game;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(7, a.getHand().getCurrentSize());
        assertEquals(7, b.getHand().getCurrentSize());

    }

    @Test
    public void deal1DefuseCardToAllPlayers() {
        gameLogic.dealDefuseCards();

        assertEquals(1, a.getHand().getCurrentSize());
        assertEquals(1, b.getHand().getCurrentSize());
        assertEquals("Defuse", a.getHand().getCards().get(0).getName());
        assertEquals("Defuse", b.getHand().getCards().get(0).getName());

    }

    @Test
    public void randomPlayerStarts() { // This test is a bit wierd as you could get the same value as it is "Random"
                                       // and testing randomness seams kinda pointless as with unit testing we want to
                                       // test a value vs a expected value

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
        b.getHand().add(CardsFactory.createCard("Attack"), 0);
        b.getHand().add(CardsFactory.createCard("Skip"), 1);
        a.getHand().add(CardsFactory.createCard("SeeTheFuture"), 0);
        a.getHand().add(CardsFactory.createCard("Shuffle"), 0);
        a.getHand().add(CardsFactory.createCard("TacoCat"), 0);
        a.getHand().add(CardsFactory.createCard("TacoCat"), 0);
        a.getHand().add(CardsFactory.createCard("Cattermelon"), 0);
        a.getHand().add(CardsFactory.createCard("Cattermelon"), 0);
        a.getHand().add(CardsFactory.createCard("Cattermelon"), 0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> Assertions.assertDoesNotThrow(() -> {
            gameLogic.startGame(a.getId());
        }));

        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        client.getOutToServer().writeObject("Shuffle");
        TimeUnit.SECONDS.sleep(1);
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());

        TimeUnit.SECONDS.sleep(1);
        client.getOutToServer().writeObject("SeeTheFuture");
        TimeUnit.SECONDS.sleep(1);
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());

        client.getOutToServer().writeObject("TacoCat 2 0");
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());
        TimeUnit.SECONDS.sleep(1);
        client.getOutToServer().writeObject("0");
        TimeUnit.SECONDS.sleep(1);
        assertEquals(true, gameLogic.getCurrentPlayer().getHand().containsCardWithName("Attack"));

        client.getOutToServer().writeObject("Cattermelon 3 0");
        assertEquals(1, gameLogic.getCurrentPlayer().getNumberOfTurns());
        TimeUnit.SECONDS.sleep(1);
        client.getOutToServer().writeObject("Skip");
        TimeUnit.SECONDS.sleep(1);
        assertEquals(true, gameLogic.getCurrentPlayer().getHand().containsCardWithName("Skip"));
        executor.shutdown();
    }

    @Test
    public void notifyAllPlayersThatWeHaveAWinner() throws InterruptedException {
        b.setExploded(true);
        assertEquals(true, gameLogic.checkWinner(a, 1));

    }

    
}
