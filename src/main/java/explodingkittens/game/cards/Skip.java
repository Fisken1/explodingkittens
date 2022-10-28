package game.cards;

import player.Player;
import game.decks.*;
import messages.MessageFactory;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see game.cards.Card#action(game.decks.DiscardPile, game.decks.MainDeck,
	 * player.Player, player.Player)
	 * 
	 * informes the player that they skipped A turn without drawing a card. return
	 * the appropriate Player based on the current amount of turnes of the player
	 * that played this card.
	 */
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