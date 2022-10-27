package game.decks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import communication.messages.Choice;
import factory.CardsFactory;
import game.cards.Card;

public class MainDeck implements Deck {

	private ArrayList<Card> Cards;

	public MainDeck() {
		this.Cards = new ArrayList<Card>();
	}

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

	public Card removeAndDistributeCards() {
		Card toRemove = this.Cards.get(0);
		this.Cards.remove(0);
		return toRemove;
	}

	public void populateDeckWithExplodingKittens(int playerSize) {
		for (int i = 1; i < playerSize; i++) {
			add(CardsFactory.createCard("ExplodingKitten"), 0);
		}

	}

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

	public void shuffle() {
		Collections.shuffle(this.Cards);
	}

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


	public void add(Card card, int position) {
		this.Cards.add(position, card);
	}


	public int getCurrentSize() {
		return Cards.size();
	}


	public ArrayList<Card> getCards() {
		return this.Cards;
	}

}