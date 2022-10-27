package player;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import communication.messages.Choice;
import communication.messages.Message;
import game.cards.Card;
import game.cards.CatCard;
import game.decks.*;

import java.io.*;

public class Player {
	private int playerID;
	private boolean online;
	private boolean isBot;

	private Socket connection;
	private boolean exploded = false;
	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;
	private Hand hand;
	private int secondsToInterruptWithNope = 5;
	private int numberOfTurnsToTake = 1;
	private String state;

	Scanner in = new Scanner(System.in);

	public Player(int playerID, boolean isBot, Socket connection, ObjectInputStream inFromClient,
			ObjectOutputStream outToClient) {
		this.state = "Normal";
		this.playerID = playerID;
		this.connection = connection;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.isBot = isBot;
		this.hand = new Hand();
		if (connection == null)
			this.online = false;
		else
			this.online = true;
	}

	public ObjectInputStream getInFromClient() {
		return inFromClient;
	}

	public ObjectOutputStream getOutToClient() {
		return outToClient;
	}

	public boolean isBot() {
		return isBot;
	}

	public void sendMessage(Message message) {
		if (!this.isBot) {
			if (online) {
				try {
					outToClient.writeObject(message);
				} catch (Exception e) {
				}
			} else if (!isBot) {
				System.out.println(message.getMessage() + message.getChoicesAsString());
			}
		} else {
			// Bots not yet implemented
		}
	}

	public String readMessage(boolean interruptable) {
		String word = " ";
		if (!this.isBot) {
			if (online)
				try {
					synchronized (inFromClient) {
						word = (String) inFromClient.readObject();
					}
				} catch (Exception e) {
					System.out.println("Reading from client failed: " +
							e.getMessage());
				}

			else
				try {
					if (interruptable) {
						BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						int millisecondsWaited = 0;
						while (!br.ready() &&
								millisecondsWaited < (secondsToInterruptWithNope * 1000)) {
							Thread.sleep(200);
							millisecondsWaited += 200;
						}
						if (br.ready())
							return br.readLine();
					} else {
						in = new Scanner(System.in);
						word = in.nextLine();
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		} else {
			// Bots not yet implemented
		}
		return word;
	}

	public ArrayList<Choice> playerChoices(Deck discardPile) {
		boolean contains = false;
		ArrayList<Choice> choices = new ArrayList<Choice>();
		for (Card card : this.hand.getCards()) {
			for (Choice c : choices) {
				if (c.getChoice().contains(card.getName())) {
					contains = true;
					break;
				}
			}
			if (choices.size() > 0) {
				if (!contains) {
					if (card.getClass() != CatCard.class)
						choices.add(new Choice(card.getName(), card.getDescription()));
					int count = this.getHand().occurenceOf(card.getName());
					if (count >= 2) {
						choices.add(new Choice(card.getName() + " " + 2, "(You can play this card as a pair)"));
					}
					if (count >= 3) {
						choices.add(
								new Choice(card.getName() + " " + 3, "(You can play this card as three of a kind)"));
					}
					contains = false;
				}
			} else if (!contains) {
				choices.add(new Choice(card.getName(), card.getDescription())); // WILL NOT WORK I THINK
			}
			contains = false;
		}
		choices.add(new Choice("Pass", "(End you turn and draw a card)"));
		return choices;
	}

	public Hand getHand() {
		return this.hand;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String newState) {
		this.state = newState;
	}

	public Integer getNumberOfTurns() {
		return numberOfTurnsToTake;
	}

	public void setNumberOfTurns(Integer turns) {
		this.numberOfTurnsToTake = turns;
	}

	public boolean getExploded() {
		return this.exploded;
	}

	public void setExploded(boolean exploded) {
		this.exploded = exploded;
	}

	public Integer getId() {
		return this.playerID;
	}

}
