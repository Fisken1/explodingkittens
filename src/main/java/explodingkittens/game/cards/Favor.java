package game.cards;

import factory.MessageFactory;
import game.decks.*;
import player.Player;

public class Favor implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public Favor() {
		this.name = "Favor";
		this.description = "(Force any other player to give you 1 card from their hand. They choose which card to give you)";
		this.ableToTarget = true;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		boolean viableOption = false;
		if (target.getHand().getCurrentSize() == 0)
			viableOption = true; // special case - target has no cards to give
		while (!viableOption) {
			target.sendMessage(MessageFactory.createMessage(
					"Player " + player.getId() +
							" has played a favor card on you, what card do you want to give?\n",
					target.getHand().getCardNamesAsChoices(), 1));
			String name = target.readMessage(false);
			if (target.getHand().containsCardWithName(name)) {
				viableOption = true;
				player.getHand().add(target.getHand().returnSpecificCardByName(name), 0);
				target.getHand().remove(target.getHand().returnSpecificCardByName(name));
				player.sendMessage(
						MessageFactory.createMessage(
								"You got a " + name + " card from player " + target.getId(), null, 0));
			} else {
				target.sendMessage(
						MessageFactory.createMessage(
								"NotValid", null, 0));
			}
		}
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