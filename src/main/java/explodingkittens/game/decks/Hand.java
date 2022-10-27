package game.decks;

import java.util.*;

import communication.messages.Choice;
import game.cards.Card;

public class Hand implements Deck {

	private ArrayList<Card> Cards;

	public Hand() {
		this.Cards = new ArrayList<Card>();
	}

	public boolean containsCardWithName(String name) {
		for (Card card : this.Cards) {
			if (card.getName().equals(name))
				return true;
		}
		return false;
	}

	public ArrayList<Choice> getCardNamesAsChoices() {
		ArrayList<Choice> cardsInHand = new ArrayList<Choice>();
		for (Card card : this.Cards) {
			cardsInHand.add(new Choice(card.getName(), ""));
		}
		return cardsInHand;
	}

	public List<String> getCardNames() {
		List<String> cardsInHand = new ArrayList<String>();
		for (Card card : this.Cards) {
			cardsInHand.add(card.getName());
		}
		return cardsInHand;
	}

	public void remove(Card card) {
		this.Cards.remove(card);
	}

	public Card returnSpecificCardByName(String name) {
		for (Card card : this.Cards) {
			if (card.getName().equals(name))
				return card;
		}
		return null;
	}

	public boolean containsTargetingCard() {
		for (Card card : this.Cards) {
			if (card.isAbleToTarget())
				return true;
		}
		return false;
	}

	public int occurenceOf(String name) {
		return Collections.frequency(getCardNames(), name);
	}


	public void add(Card card, int position) {
		this.Cards.add(card);
	}

	public void shuffle() {
		Collections.shuffle(this.Cards);
	}


	public int getCurrentSize() {
		return Cards.size();
	}


	public ArrayList<Card> getCards() {
		return this.Cards;
	}

}