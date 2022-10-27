package game.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import communication.messages.Message;
import communication.network.Client;
import communication.network.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import factory.CardsFactory;

import game.decks.DiscardPile;
import game.decks.MainDeck;
import player.Player;

public class AllCardsTest {
    DiscardPile discardPile;
    MainDeck drawPile;
    ObjectInputStream inFromClient;
    ObjectOutputStream outToClient;
    Socket connectionSocket;
    Server server;
    Client client;
    Message m;
    Player player;
    Player target;

    @BeforeEach
    public void initAllCardsTests() throws Exception {
        server = new Server(2, 0);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> Assertions.assertDoesNotThrow(() -> {
            server.addPlayersAndBots();
        }));
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
        client = new Client();
        client.client("127.0.0.1");
        discardPile = new DiscardPile();
        drawPile = new MainDeck();
        player = server.getPlayers().get(1);
        target = server.getPlayers().get(0);
    }

    @AfterEach
    public void tearDown() {
        server.closeSocket();
    }

    @Test
    public void AttackCard() {
        Card attackCard = CardsFactory.createCard("Attack");
        assertEquals(target, attackCard.action(discardPile, drawPile, player, target));
        assertEquals("Attacked", target.getState());
        int turns = target.getNumberOfTurns();
        assertEquals(2, turns);

    }

    @Test
    public void CatCard() {
        Card catCard = CardsFactory.createCard("TacoCat");
        player.getHand().add(catCard, 0);
        assertEquals("TacoCat", catCard.getName());
        assertEquals(target, catCard.action(discardPile, drawPile, player, target));
        assertEquals(false, player.getHand().containsCardWithName(catCard.getName()));
        assertEquals(true, discardPile.getCards().contains(catCard));

    }

    @Test
    public void ExplodingKittenCardNoDefuse() {
        Card attack = CardsFactory.createCard("Attack");
        Card nope = CardsFactory.createCard("Nope");
        Card skip = CardsFactory.createCard("Skip");
        player.getHand().add(attack, 0);
        player.getHand().add(nope, 0);
        player.getHand().add(skip, 0);
        Card explodingKittenCard = CardsFactory.createCard("ExplodingKitten");
        assertEquals(target, explodingKittenCard.action(discardPile, drawPile, player, target));
        assertEquals(true, discardPile.getCards().contains(explodingKittenCard));
        assertEquals(true, discardPile.getCards().contains(attack));
        assertEquals(true, discardPile.getCards().contains(nope));
        assertEquals(true, discardPile.getCards().contains(skip));
        assertEquals(true, player.getExploded()); // If exploded the player cant take more turns
    }

    @Test
    public void NopeCard() {
        Card nopeCard = CardsFactory.createCard("Nope");
        player.getHand().add(nopeCard, 0);
        assertEquals(null, nopeCard.action(discardPile, drawPile, player, target));
        assertEquals(false, player.getHand().containsCardWithName(nopeCard.getName()));
        assertEquals(true, discardPile.getCards().contains(nopeCard));
    }

    @Test
    public void SeeTheFutureCard() {
        Card seeTheFutureCard = CardsFactory.createCard("SeeTheFuture");
        assertEquals(player, seeTheFutureCard.action(discardPile, drawPile, player, target));
        assertEquals(false, player.getHand().containsCardWithName(seeTheFutureCard.getName()));
    }

    @Test
    public void ShuffleCard() {
        Card shuffleCard = CardsFactory.createCard("Shuffle");
        ArrayList<String> expansions = new ArrayList<String>();
        expansions.add("Exploding kittens");
        drawPile.populateDeck(2, expansions);
        MainDeck noShuffle = new MainDeck();
        noShuffle.populateDeck(2, expansions); // populateDeck() always populates the same way
        boolean shuffled = false;
        assertEquals(player, shuffleCard.action(discardPile, drawPile, player, target));
        for (int i = 0; i < drawPile.getCurrentSize(); i++) {
            if (!noShuffle.getCards().get(i).getName().equals(drawPile.getCards().get(i).getName())) {
                shuffled = true;
            }
        }
        assertEquals(true, shuffled);
        assertEquals(false, player.getHand().containsCardWithName(shuffleCard.getName()));
    }

    @Test
    public void SkipCard() throws ClassNotFoundException, IOException, InterruptedException {
        Card skipCard = CardsFactory.createCard("Skip");
        assertEquals(server.getPlayers().get(
                0),
                skipCard.action(discardPile, drawPile,
                        player, target));
        TimeUnit.SECONDS.sleep(1);
        assertEquals("You skiped a turn without drawing a new card\n",
                client.getNextMessage().getMessage());

    }

    @Test
    public void ExplodingKittenCard() {

    }

}
