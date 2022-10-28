package game.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import game.decks.DiscardPile;
import game.decks.MainDeck;
import network.Client;
import network.Server;
import player.Player;

public class AllCardsTest {
    DiscardPile discardPile;
    MainDeck drawPile;

    Player player;
    Player target;

    @BeforeEach
    public void initAllCardsTests() {
        discardPile = new DiscardPile();
        drawPile = new MainDeck();
        player = new Player(0, false, null, null, null);
        target = new Player(1, false, null, null, null);
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
    public void AttackCardIfJustAttacked() {
        Card attackCard = CardsFactory.createCard("Attack");
        player.setNumberOfTurns(10);
        player.setState("Attacked");
        assertEquals(target, attackCard.action(discardPile, drawPile, player, target));
        assertEquals("Attacked", target.getState());
        int turns = target.getNumberOfTurns();
        assertEquals(12, turns);

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
        explodingKittenCard.action(discardPile, drawPile, player, target);
        assertEquals(true, discardPile.getCards().contains(explodingKittenCard));
        assertEquals(true, discardPile.getCards().contains(attack));
        assertEquals(true, discardPile.getCards().contains(nope));
        assertEquals(true, discardPile.getCards().contains(skip));
        assertEquals(true, player.getExploded()); // If exploded the player cant take more turns
    }

    @Test
    public void ExplodingKittenCardWithDefuse() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Server server = new Server(2, 0);
        executor.execute(() -> Assertions.assertDoesNotThrow(() -> {
            server.addPlayersAndBots();
        }));
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);

        Client client = new Client();
        client.client("127.0.0.1");

        Card defuse = CardsFactory.createCard("Defuse");
        server.getPlayers().get(1).getHand().add(defuse, 0);
        Card explodingKittenCard = CardsFactory.createCard("ExplodingKitten");
        TimeUnit.SECONDS.sleep(2);
        ExecutorService executorb = Executors.newSingleThreadExecutor();
        executorb.execute(() -> Assertions.assertDoesNotThrow(() -> {
            explodingKittenCard.action(discardPile, drawPile, server.getPlayers().get(1),
                    target);
        }));
        executorb.awaitTermination(4000, TimeUnit.MILLISECONDS);

        client.getOutToServer().writeObject("0");

        assertEquals(false, server.getPlayers().get(1).getExploded());
        TimeUnit.SECONDS.sleep(1);
        assertEquals(explodingKittenCard, drawPile.getCards().get(0));
        server.closeSocket();
        executor.shutdown();
        executorb.shutdown();

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
        int currentTurns = player.getNumberOfTurns();
        skipCard.action(discardPile, drawPile,
                player, target);
        assertEquals(currentTurns - 1, player.getNumberOfTurns());
        assertEquals(0, player.getHand().getCurrentSize());
    }

}
