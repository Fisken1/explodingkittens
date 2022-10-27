package game.cards;

import java.util.ArrayList;
import java.util.Arrays;

import communication.messages.Choice;
import communication.messages.Message;
import factory.MessageFactory;
import game.decks.*;
import player.Player;

public class Defuse implements Card {

	private String name;
	private String description;
	private boolean ableToTarget;

	public Defuse() {
		this.name = "Defuse";
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

		Card explodingKitten = player.getHand().returnSpecificCardByName("ExplodingKitten");
		player.sendMessage(MessageFactory.createMessage(
				"Oh no you drew a Exploding kitten, but you used a defuse card!", null, 0));

		Message question = MessageFactory.createMessage(
				"Where in the deck do you wish to place the ExplodingKitten? \n",
				new ArrayList<Choice>(Arrays.asList(new Choice("0", "top of the deck"),
						new Choice(String.valueOf(drawPile.getCurrentSize()), "bottom of the deck"))),
				1);

		boolean validOption = false;
		while (!validOption) {

			try {
				player.sendMessage(question);
				int positionInDeck = Integer.valueOf(player.readMessage(false)).intValue();
				if (positionInDeck >= Integer.valueOf(question.getChoices().get(0).getChoice())
						&& positionInDeck <= Integer
								.valueOf(question.getChoices().get(1).getChoice())) {
					drawPile.add(explodingKitten, positionInDeck);
					player.getHand().remove(explodingKitten);
					player.sendMessage(
							MessageFactory.createMessage(
									"You have placed the Exploding kitten in position " + positionInDeck, null, 0));
					validOption = true;
				} else {
					player.sendMessage(
							MessageFactory.createMessage(
									"NotValid", null, 0));
				}
			} catch (final NumberFormatException e) {
				player.sendMessage(
						MessageFactory.createMessage(
								"You did not enter a valid position!", null, 0));
			}

		}
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