package game.decks;

import java.util.ArrayList;

import game.cards.Card;

public class DiscardPile implements Deck {

	private ArrayList<Card> Cards;

	public DiscardPile() {
		this.Cards = new ArrayList<Card>();
	}

	public int getNrNope() {
		int i = 0;
		while (i < getCurrentSize() && Cards.get(i).getName().equals("Nope")) {
			i++;
		}
		return i;
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