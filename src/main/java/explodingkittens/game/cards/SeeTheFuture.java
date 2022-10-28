package game.cards;

import game.decks.*;
import messages.MessageFactory;
import player.Player;

public class SeeTheFuture implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public SeeTheFuture() {
		this.name = "SeeTheFuture";
		this.description = "(Privately view the top 3 cards from the Draw Pile and put them back in the same order)";
		this.ableToTarget = false;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see game.cards.Card#action(game.decks.DiscardPile, game.decks.MainDeck,
	 * player.Player, player.Player)
	 * 
	 * Calls the method topThreeCards() to get the top 3 cards from the draw pile
	 * when shows them to the player. return player.
	 */
	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		player.sendMessage(MessageFactory.createMessage(
				"SeeTheFuture", drawPile.topThreeCards(), 0));
		return player;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

}