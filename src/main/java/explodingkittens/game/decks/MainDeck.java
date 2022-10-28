package game.decks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import game.cards.Card;
import game.cards.CardsFactory;
import messages.Choice;

public class MainDeck implements Deck {

	private ArrayList<Card> Cards;

	public MainDeck() {
		this.Cards = new ArrayList<Card>();
	}

	/**
	 * @param playerSize the amount of players in the game
	 * @param expansions the current expansions you want to play with
	 * 
	 *                   Creates a deck with the core expansion in mind then you can
	 *                   add more code later to alter the deck with future cards.
	 *                   Uses the CardsFactory to create cards to add to the deck.
	 */
	public void populateDeck(int playerSize, ArrayList<String> expansions) {
		if (expansions.contains("Exploding kittens")) {
			if (playerSize > 4)
				add(CardsFactory.createCard("Defuse"), 0);
			else {
				for (int i = 0; i < 2; i++) {
					add(CardsFactory.createCard("Defuse"), 0);
				}
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("Attack"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("Favor"), 0);
			}
			for (int i = 0; i < 5; i++) {
				add(CardsFactory.createCard("Nope"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("Shuffle"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("Skip"), 0);
			}
			for (int i = 0; i < 5; i++) {
				add(CardsFactory.createCard("SeeTheFuture"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("HairyPotatoCat"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("Cattermelon"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("RainbowRalphingCat"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("TacoCat"), 0);
			}
			for (int i = 0; i < 4; i++) {
				add(CardsFactory.createCard("OverweightBikiniCat"), 0);
			}
		}
		if (expansions.contains("Streaking kittens")) {
			// Other expansion cards not implemented....
		}
		if (expansions.contains("Imploding kittens")) {
			// Other expansion cards not implemented....
		}
		if (expansions.contains("Barking kittens")) {
			// Other expansion cards not implemented....
		}
	}

	/**
	 * @return the top card of the deck
	 */
	public Card removeAndDistributeCards() {
		Card toRemove = this.Cards.get(0);
		this.Cards.remove(0);
		return toRemove;
	}

	/**
	 * @param playerSize the amount of players in the game
	 * 
	 *                   populates the deck with exploding kittens based on
	 *                   playerSize
	 */
	public void populateDeckWithExplodingKittens(int playerSize) {
		for (int i = 1; i < playerSize; i++) {
			add(CardsFactory.createCard("ExplodingKitten"), 0);
		}

	}

	/**
	 * @return the top 3 cards if the deck
	 */
	public ArrayList<Choice> topThreeCards() {
		ArrayList<Choice> topThreeCards = new ArrayList<Choice>();
		int size;
		if (this.Cards.size() != 1) {
			if (this.Cards.size() >= 3) {
				size = 3;
			} else {
				size = this.Cards.size() - 1;
			}

			for (int i = 0; i < size; i++) {
				topThreeCards.add(new Choice(this.Cards.get(i).getName() + " ", ""));
			}
		} else {
			topThreeCards.add(new Choice(this.Cards.get(0).getName(), ""));
		}
		return topThreeCards;
	}

	/**
	 * shuffles the deck
	 */
	public void shuffle() {
		Collections.shuffle(this.Cards);
	}

	/**
	 * @return all the card names as a ArrayList of Choices
	 * 
	 *         Used when starting up the game to get a ArrayList of all the possible
	 *         cards you can get based on the deck
	 */
	public ArrayList<Choice> allCardNames() {
		ArrayList<String> tempNames = new ArrayList<String>();
		ArrayList<Choice> allCardNames = new ArrayList<Choice>();
		for (Card c : this.getCards()) {
			if (!c.getName().equals("ExplodingKitten"))
				tempNames.add(c.getName());
		}
		HashSet<String> hset = new HashSet<String>(tempNames);
		for (String name : hset) {
			allCardNames.add(new Choice(name, ""));
		}
		return allCardNames;
	}

	@Override
	public void add(Card card, int position) {
		this.Cards.add(position, card);
	}

	@Override
	public int getCurrentSize() {
		return Cards.size();
	}

	@Override
	public ArrayList<Card> getCards() {
		return this.Cards;
	}

}