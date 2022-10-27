package game.cards;

import factory.MessageFactory;
import game.decks.*;
import player.Player;

public class ExplodingKitten implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public ExplodingKitten() {
		this.name = "ExplodingKitten";
		this.description = "(Unless you have a Defuse Card, you are dead)";
		this.ableToTarget = false;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		discardPile.add(this, 0);
		drawPile.getCards().remove(this);
		if (!player.getHand().containsCardWithName("Defuse")) {
			player.sendMessage(
					MessageFactory.createMessage("Oh no you drew a Exploding kitten, you exploded!", null, 0));
			player.setExploded(true);
			player.getHand().getCards().clear();
			return target;
		}
		player.getHand().add(this, 0);
		Card defuse = player.getHand().returnSpecificCardByName("Defuse");
		return defuse.action(discardPile, drawPile, player, target);
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