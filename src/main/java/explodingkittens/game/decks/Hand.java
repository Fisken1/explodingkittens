package game.decks;

import java.util.*;

import game.cards.Card;
import game.cards.CatCard;
import messages.Choice;

public class Hand implements Deck {

	private ArrayList<Card> Cards;

	public Hand() {
		this.Cards = new ArrayList<Card>();
	}

	/**
	 * @param name the name of the card you are looking for
	 * @return true if the hand contains the card, false if the hand dosent contain
	 *         the card
	 */
	public boolean containsCardWithName(String name) {
		for (Card card : this.Cards) {
			if (card.getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * @return all the cards in the hand as a ArrayList of Choices to present to the
	 *         player in a ClosedMessage.
	 */
	public ArrayList<Choice> getCardNamesAsChoices() {
		ArrayList<Choice> cardsInHand = new ArrayList<Choice>();
		for (Card card : this.Cards) {
			cardsInHand.add(new Choice(card.getName(), ""));
		}
		return cardsInHand;
	}

	/**
	 * @return the name of all the cards in the hand as a ArrayList of strings
	 */
	private List<String> getCardNames() {
		List<String> cardsInHand = new ArrayList<String>();
		for (Card card : this.Cards) {
			cardsInHand.add(card.getName());
		}
		return cardsInHand;
	}

	/**
	 * @param card card to remove
	 * 
	 *             Removes the card you wants from the hand
	 */
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

	/**
	 * @return true if the hand contains any targeting cards
	 */
	public boolean containsTargetingCard() {
		for (Card card : this.Cards) {
			if (card.isAbleToTarget())
				return true;
		}
		return false;
	}

	/**
	 * @param name the name of the card you want to check
	 * @return how many times the card occur in the hand
	 */
	public int occurenceOf(String name) {
		return Collections.frequency(getCardNames(), name);
	}

	@Override
	public void add(Card card, int position) {
		this.Cards.add(card);
	}

	@Override
	public int getCurrentSize() {
		return Cards.size();
	}

	@Override
	public ArrayList<Card> getCards() {
		return this.Cards;
	}

	/**
	 * @return a ArrayList of Choices containing all player choices. It checks if
	 *         the cards already exists if that is the case it breaks. Else it
	 *         checks how many times the card exists and if 2 exist add the option
	 *         to play it as a pair, if 3 or more exsist add the option to play it
	 *         as 3 of as kind
	 */
	public ArrayList<Choice> playerChoices() {
		boolean contains = false;
		ArrayList<Choice> choices = new ArrayList<Choice>();
		for (Card card : getCards()) {
			for (Choice c : choices) {
				if (c.getChoice().contains(card.getName())) {
					contains = true;
					break;
				}
			}
			if (choices.size() > 0) {
				if (!contains) {
					if (card.getClass() != CatCard.class)
						choices.add(new Choice(card.getName(), card.getDescription()));
					int count = occurenceOf(card.getName());
					if (count >= 2) {
						choices.add(new Choice(card.getName() + " " + 2, "(You can play this card as a pair)"));
					}
					if (count >= 3) {
						choices.add(
								new Choice(card.getName() + " " + 3, "(You can play this card as three of a kind)"));
					}
					contains = false;
				}
			} else if (!contains) {
				choices.add(new Choice(card.getName(), card.getDescription()));
			}
			contains = false;
		}
		choices.add(new Choice("Pass", "(End you turn and draw a card)"));
		return choices;
	}

}