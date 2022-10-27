package game.cards;

import player.Player;
import factory.MessageFactory;
import game.decks.*;

public class Shuffle implements Card {
	private String name;
	private String description;
	private boolean ableToTarget;

	public Shuffle() {
		this.name = "Shuffle";
		this.description = "(Shuffle the Draw Pile thoroughly)";
		this.ableToTarget = false;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		drawPile.shuffle();
		player.sendMessage(MessageFactory.createMessage("Shuffle", null, 0));
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