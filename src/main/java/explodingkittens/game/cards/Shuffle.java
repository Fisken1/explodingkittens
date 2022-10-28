package game.cards;

import player.Player;
import game.decks.*;
import messages.MessageFactory;

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

	/* (non-Javadoc)
	 * @see game.cards.Card#action(game.decks.DiscardPile, game.decks.MainDeck, player.Player, player.Player)
	 * 
	 * shuffles the deck and informes the player that the deck has been shuffled 
	 */
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