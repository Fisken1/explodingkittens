package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import messages.ClosedQuestion;
import messages.Message;

public class Client {
	private int secondsToInterruptWithNope = 5;
	private ObjectOutputStream outToServer;
	private ObjectInputStream inFromServer;
	private Message nextMessage = null;

	/**
	 * @param ipAddress the ip adress you want to connect to
	 * @throws Exception if it cant read from the server
	 * 
	 *                   Connects to the server and as long as the socket is not
	 *                   closed try to read from the server. If the next messgae is
	 *                   a closed question you wait for input from the client. If
	 *                   the message contains <Enter> you can wait to send " " (dont
	 *                   play nope) or press enter (play nope).
	 */
	public void client(String ipAddress) throws Exception {
		// Connect to server
		final Socket aSocket = new Socket(ipAddress, 2048);
		outToServer = new ObjectOutputStream(aSocket.getOutputStream());
		inFromServer = new ObjectInputStream(aSocket.getInputStream());
		// Get the hand of apples from server
		ExecutorService threadpool = Executors.newFixedThreadPool(1);
		Runnable receive = new Runnable() {
			public void run() {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while (!aSocket.isClosed()) {
					try {

						nextMessage = (Message) inFromServer.readObject();

						System.out.println(nextMessage.getContents() + nextMessage.getChoicesAsString());

						if (nextMessage.getChoicesAsString().contains("<Enter>")) { // Press <Enter> to play Nope
							try {
								int millisecondsWaited = 0;

								while (!br.ready() && millisecondsWaited < (secondsToInterruptWithNope * 1000)) {
									Thread.sleep(200);
									millisecondsWaited += 200;
								}

								if (br.ready()) {
									outToServer.writeObject(br.readLine());
								} else
									outToServer.writeObject(" ");

							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}

						if (nextMessage.getClass() == ClosedQuestion.class) { // if the question requires a answer then
							// try to read from client
							try {
								outToServer.writeObject(br.readLine());
							} catch (IOException e) {
								e.printStackTrace();
							}

						}

					} catch (Exception e) {
						System.exit(0);
					}

				}
				try {
					aSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		threadpool.execute(receive);
	}

	public Message getNextMessage() {
		return nextMessage;
	}

	public ObjectOutputStream getOutToServer() {
		return outToServer;
	}

	public ObjectInputStream getInFromServer() {
		return inFromServer;
	}
}