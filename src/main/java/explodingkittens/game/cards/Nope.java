package game.cards;

import game.decks.*;
import player.Player;

public class Nope implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public Nope() {
		this.name = "Nope";
		this.description = "(This card cant be played now but it is a option for later stages)";
		this.ableToTarget = false;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		discardPile.add(this, 0);
		player.getHand().remove(this);
		return null;
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