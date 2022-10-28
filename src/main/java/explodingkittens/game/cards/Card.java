package game.cards;

import game.decks.*;
import player.Player;

public interface Card {
	String getName();

	String getDescription();

	/**
	 * @param discardPile the current discard pile
	 * @param drawPile    the current draw pile
	 * @param player      the player who played the card
	 * @param target      the target of the card (can be the same as player for some
	 *                    cards)
	 * @return the player who will get the next turn, either the current player or
	 *         the target
	 * 
	 *         In general handles the action for each card! See each card for a more
	 *         detailed explanation.
	 */
	Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target);

	/**
	 * @return true id the card is able to target
	 */
	boolean isAbleToTarget();
}