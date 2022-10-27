package communication.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import communication.messages.Choice;
import factory.MessageFactory;
import player.Player;

public class Server {
	private ArrayList<Player> players = new ArrayList<Player>();
	private ServerSocket aSocket;

	private int numberPlayers;
	private int numberOfBots;

	public Server(int numberPlayers, int numberOfBots) {
		this.numberPlayers = numberPlayers;
		this.numberOfBots = numberOfBots;
	}

	public void addPlayersAndBots() throws Exception {
		players.add(new Player(0, false, null, null, null)); // add this instance as a player
		// Open for connections if there are online players
		for (int i = 0; i < this.numberOfBots; i++) {
			players.add(new Player(i + 1, true, null, null, null)); // add a bot
		}
		if (this.numberPlayers > 1 && this.numberPlayers <= 5) {
			aSocket = new ServerSocket(2048);
			int i = 1;
			while (checkInsideBoundaries()) {

				Socket connectionSocket = aSocket.accept();
				ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
				ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
				players.add(new Player(i, false, connectionSocket, inFromClient, outToClient)); // add an online client
				System.out.println("Connected to player " + i);
				ArrayList<Choice> playerId = new ArrayList<Choice>();
				playerId.add(new Choice(String.valueOf(i), ""));
				outToClient
						.writeObject(
								MessageFactory.createMessage("You connected to the server as player ", playerId, 0));
				i++;

			}
		} else {
			throw new Exception("You can't connect less than 1 player or more then 5 players!");
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	private boolean checkInsideBoundaries() {
		if (players.size() < this.numberPlayers) {
			return true;
		}
		return false;
	}

	public void closeSocket() {
		try {
			this.aSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
