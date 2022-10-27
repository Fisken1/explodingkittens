package game.cards;

import factory.MessageFactory;
import game.decks.*;
import player.Player;

public class Attack implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public Attack() {
		this.name = "Attack";
		this.description = "(Do not draw any cards. Instead, immediately force the next player to take 2 turns in a row)";
		this.ableToTarget = false;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		target.setState("Attacked");
		target.sendMessage(MessageFactory.createMessage("Attack", null, 0));
		if (player.getState().equals("Attacked")) {
			target.setNumberOfTurns(2 + player.getNumberOfTurns());
			player.setState("Normal");
		} else {
			target.setNumberOfTurns(2);
		}
		player.setNumberOfTurns(0);
		return target;
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
