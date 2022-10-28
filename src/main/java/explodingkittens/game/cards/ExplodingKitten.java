package game.cards;

import game.decks.*;
import messages.MessageFactory;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see game.cards.Card#action(game.decks.DiscardPile, game.decks.MainDeck,
	 * player.Player, player.Player)
	 * 
	 * Discards the card then looks to see if the player has a Defuse card if that
	 * is the case we use that card else informes the player that they exploded then
	 * discards all the cards in their hand and returnes the target to set it as the
	 * next current player
	 */
	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		discardPile.add(this, 0);
		drawPile.getCards().remove(this);
		if (!player.getHand().containsCardWithName("Defuse")) {
			player.sendMessage(
					MessageFactory.createMessage("Oh no you drew a Exploding kitten, you exploded!", null, 0));
			player.setExploded(true);
			for (int i = 0; i < player.getHand().getCards().size(); i++) {
				discardPile.add(player.getHand().getCards().get(i), 0);
			}
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