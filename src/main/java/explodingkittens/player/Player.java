package player;

import java.net.Socket;
import java.util.Scanner;

import game.decks.*;
import messages.Message;

import java.io.*;

public class Player {
	private int playerID;
	private boolean online;
	private boolean isBot;

	private Socket connection;
	private boolean exploded = false;
	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;
	public Hand hand;
	private int secondsToInterruptWithNope = 5;
	private int numberOfTurnsToTake = 1;
	private String state;

	Scanner in = new Scanner(System.in);

	/**
	 * @param playerID     the id of the player
	 * @param isBot        true if the player is a bot else false
	 * @param connection   the socket from the server
	 * @param inFromClient a ObjectInputStream given from the sever to handle
	 *                     messages from the client
	 * @param outToClient  a ObjectOutputStream given from the sever to handle
	 *                     messages to the client
	 */
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

	/**
	 * @param message the message you want to send to the client if online else
	 *                print the in the console
	 */
	public void sendMessage(Message message) {
		if (!this.isBot) {
			if (online) {
				try {
					outToClient.writeObject(message);
				} catch (Exception e) {
				}
			} else if (!isBot) {
				System.out.println(message.getContents() + message.getChoicesAsString());
			}
		} else {
			// Bots not yet implemented
		}
	}

	/**
	 * @param interruptable if the reading of the message from the sever is
	 *                      interruptable or not
	 * @return if online the word from the client else check if the message is
	 *         interruptable then make it possible to press enter to nope else take
	 *         input from terminal via a scanner
	 */
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

	public ObjectInputStream getInFromClient() {
		return inFromClient;
	}

	public ObjectOutputStream getOutToClient() {
		return outToClient;
	}

	public boolean isBot() {
		return isBot;
	}

}
