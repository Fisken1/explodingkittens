package game.cards;

import game.decks.*;
import messages.MessageFactory;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see game.cards.Card#action(game.decks.DiscardPile, game.decks.MainDeck,
	 * player.Player, player.Player)
	 * 
	 * Sets the targets state to attacked to keep track, then checks the current
	 * players state to see if it is attacked. If that is the case we give the
	 * target 2 + the current amount of turns the current player has
	 * else set the targets turn to 2. Then return the target!
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see game.cards.Card#isAbleToTarget()
	 */
	@Override
	public boolean isAbleToTarget() {
		return ableToTarget;
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
