package game.cards;

import player.Player;
import factory.MessageFactory;
import game.decks.*;

public class Skip implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public Skip() {
		this.name = "Skip";
		this.description = "(Immediately end your turn without drawing a card)";
		this.ableToTarget = false;
	}

	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
	}

	@Override
	public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
		player.sendMessage(MessageFactory.createMessage("Skip", null, 0));
		player.setNumberOfTurns(player.getNumberOfTurns() - 1);
		if (player.getNumberOfTurns() == 0) {
			return target;
		} else {
			return player;
		}
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