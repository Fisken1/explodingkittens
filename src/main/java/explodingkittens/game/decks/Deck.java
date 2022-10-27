package game.decks;

import java.util.ArrayList;

import game.cards.Card;

public interface Deck {

	int getCurrentSize();

	void add(Card card, int position);

	ArrayList<Card> getCards();

}