package game.cards;

import game.decks.*;
import player.Player;

public interface Card {
	String getName();

	String getDescription();

	Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target);

	boolean isAbleToTarget();
}