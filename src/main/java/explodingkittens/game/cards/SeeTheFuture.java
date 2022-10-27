package game.cards;

import factory.MessageFactory;
import game.decks.*;
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