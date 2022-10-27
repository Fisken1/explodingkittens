package game.decks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.cards.Card;


public class MainDeckTest {
    private MainDeck drawPile;
    ArrayList<String> expansions = new ArrayList<String>();

    @BeforeEach
    public void initMainDeckTests() {
        drawPile = new MainDeck();
        expansions.add("Exploding kittens");
    }

    @Test
    public void coreGameUnder5Players() {
        drawPile.populateDeck(2, expansions);
        int defuse = 0;
        int attack = 0;
        int favor = 0;
        int nope = 0;
        int shuffle = 0;
        int skip = 0;
        int seeTheFuture = 0;
        int hairyPotatoCat = 0;
        int cattermelon = 0;
        int rainbowRalphingCat = 0;
        int tacoCat = 0;
        int overweightBikiniCat = 0;
        for (Card c : drawPile.getCards()) {
            switch (c.getName()) {
                case "Defuse":
                    defuse += 1;
                    break;
                case "Attack":
                    attack += 1;
                    break;
                case "Favor":
                    favor += 1;
                    break;
                case "Nope":
                    nope += 1;
                    break;
                case "Shuffle":
                    shuffle += 1;
                    break;
                case "Skip":
                    skip += 1;
                    break;
                case "SeeTheFuture":
                    seeTheFuture += 1;
                    break;
                case "HairyPotatoCat":
                    hairyPotatoCat += 1;
                    break;
                case "Cattermelon":
                    cattermelon += 1;
                    break;
                case "RainbowRalphingCat":
                    rainbowRalphingCat += 1;
                    break;
                case "TacoCat":
                    tacoCat += 1;
                    break;
                case "OverweightBikiniCat":
                    overweightBikiniCat += 1;
                    break;
                default:
                    break;
            }

        }
        assertEquals(2, defuse);
        assertEquals(4, attack);
        assertEquals(4, favor);
        assertEquals(5, nope);
        assertEquals(4, shuffle);
        assertEquals(4, skip);
        assertEquals(5, seeTheFuture);
        assertEquals(4, hairyPotatoCat);
        assertEquals(4, cattermelon);
        assertEquals(4, rainbowRalphingCat);
        assertEquals(4, tacoCat);
        assertEquals(4, overweightBikiniCat);
    }

    @Test
    public void coreGameAbove4Players() {
        drawPile.populateDeck(5, expansions);
        int defuse = 0;
        int attack = 0;
        int favor = 0;
        int nope = 0;
        int shuffle = 0;
        int skip = 0;
        int seeTheFuture = 0;
        int hairyPotatoCat = 0;
        int cattermelon = 0;
        int rainbowRalphingCat = 0;
        int tacoCat = 0;
        int overweightBikiniCat = 0;
        for (Card c : drawPile.getCards()) {
            switch (c.getName()) {
                case "Defuse":
                    defuse += 1;
                    break;
                case "Attack":
                    attack += 1;
                    break;
                case "Favor":
                    favor += 1;
                    break;
                case "Nope":
                    nope += 1;
                    break;
                case "Shuffle":
                    shuffle += 1;
                    break;
                case "Skip":
                    skip += 1;
                    break;
                case "SeeTheFuture":
                    seeTheFuture += 1;
                    break;
                case "HairyPotatoCat":
                    hairyPotatoCat += 1;
                    break;
                case "Cattermelon":
                    cattermelon += 1;
                    break;
                case "RainbowRalphingCat":
                    rainbowRalphingCat += 1;
                    break;
                case "TacoCat":
                    tacoCat += 1;
                    break;
                case "OverweightBikiniCat":
                    overweightBikiniCat += 1;
                    break;
                default:
                    break;
            }

        }
        assertEquals(1, defuse);
        assertEquals(4, attack);
        assertEquals(4, favor);
        assertEquals(5, nope);
        assertEquals(4, shuffle);
        assertEquals(4, skip);
        assertEquals(5, seeTheFuture);
        assertEquals(4, hairyPotatoCat);
        assertEquals(4, cattermelon);
        assertEquals(4, rainbowRalphingCat);
        assertEquals(4, tacoCat);
        assertEquals(4, overweightBikiniCat);
    }

    @Test
    public void shuffleDeck() {
        drawPile.populateDeck(2, expansions);
        MainDeck noShuffle = new MainDeck();
        noShuffle.populateDeck(2, expansions); // populateDeck() always populates the same way
        drawPile.shuffle(); // Shuffle only one of the decks
        boolean shuffled = false;
        for (int i = 0; i < drawPile.getCurrentSize(); i++) {
            if (!noShuffle.getCards().get(i).getName().equals(drawPile.getCards().get(i).getName())) {
                shuffled = true;
            }
        }
        assertEquals(true, shuffled);

    }

    @Test
    public void checkNrExplodingKitten() {
        drawPile.populateDeckWithExplodingKittens(2);
        int explodingKitten = 0;
        for (Card c : drawPile.getCards()) {
            if (c.getName().equals("ExplodingKitten"))
                explodingKitten += 1;
        }
        assertEquals(1, explodingKitten);

    }
}
