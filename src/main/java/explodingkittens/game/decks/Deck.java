package game.decks;

import java.util.ArrayList;

import game.cards.Card;

public interface Deck {

	/**
	 * @return the current size of a Deck
	 */
	int getCurrentSize();

	/**
	 * @param card     the card that you want to add
	 * @param position the position that you want to add the card into
	 */
	void add(Card card, int position);

	/**
	 * @return returns all the cards in a Deck as a ArrayList of Cards.
	 */
	ArrayList<Card> getCards();

}