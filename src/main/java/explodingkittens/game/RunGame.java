package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import communication.network.Client;

public class RunGame {
	private static ArrayList<String> expansions = new ArrayList<String>(Arrays.asList("Exploding kittens"));

	public static void main(String argv[]) {
		runMainMenu(argv);
	}

	private static void runMainMenu(String[] argv) {
		boolean done = false;
		if (argv.length == 2) {
			try (Scanner scanchoice = new Scanner(System.in)) {
				System.out.println();
				int choiceentry = -1;
				while (!done) {
					choiceentry = -1;
					System.out.println("Enter one of the following commands:");
					System.out.println("1 - Play");
					System.out.println(
							"2 - Select expansions, current expansions selected: " + expansions.toString());
					System.out.println("3 - Exit");
					while (choiceentry < 1 || choiceentry > 3) {

						if (scanchoice.hasNextInt())
							choiceentry = scanchoice.nextInt();
					}

					switch (choiceentry) {
						case 1:
							done = true;
							initRunGame(argv);
							break;
						case 2:
							choiceentry = -1;
							System.out.println("What expansions do you want to play with: ");
							System.out.println("1 - Streaking kittens");
							System.out.println("2 - Imploding kittens");
							System.out.println("3 - Barking kittens");
							System.out.println("4 - All");
							System.out.println("5 - Remove all");
							while (choiceentry < 1 || choiceentry > 5) {
								if (scanchoice.hasNextInt())
									choiceentry = scanchoice.nextInt();
								switch (choiceentry) {
									case 1:
										if (!expansions.contains("Streaking kittens")) {
											expansions.add("Streaking kittens");
											break;
										}
									case 2:
										if (!expansions.contains("Imploding kittens")) {
											expansions.add("Imploding kittens");
											break;
										}

										break;
									case 3:
										if (!expansions.contains("Barking kittens")) {
											expansions.add("Barking kittens");
											break;
										}

										break;
									case 4:
										expansions.clear();
										expansions.add("Exploding kittens");
										expansions.add("Streaking kittens");
										expansions.add("Imploding kittens");
										expansions.add("Barking kittens");
										break;

									case 5:
										expansions.clear();
										expansions.add("Exploding kittens");
										break;
									default:
										System.out.println("Not a valid choice!");
										break;

								}
							}
							break;
						case 3:
							System.out.println("Exiting the game... ");
							System.exit(0);
							break;

					}
				}
			} finally {

			}
		} else

		{
			initRunGame(argv);
		}
	}

	private static void initRunGame(String[] args) {
		try {
			new RunGame(args);
		} catch (Exception e) {
			System.out.println("Can't start the game " + e.getMessage());
		}
	}

	public RunGame(String[] params) throws Exception {
		if (params.length == 2) {
			this.initGame(Integer.valueOf(params[0]).intValue(), Integer.valueOf(params[1]).intValue());
		} else if (params.length == 1) {
			Client client = new Client();
			client.client(params[0]);
		} else {
			System.out.println("Server syntax: java ExplodingKittens numPlayers numBots");
			System.out.println("Client syntax: IP");
		}
	}

	public int randomPlayerStarts(int nrPlayers) {
		Random rnd = new Random();
		return rnd.nextInt(nrPlayers);
	}

	public void initGame(int numPlayers, int numBots) {

		try {
			GameLogic gameLogic = new GameLogic(numPlayers, numBots);

			try {
				gameLogic.getServer().addPlayersAndBots();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			gameLogic.getDrawPile().populateDeck(gameLogic.getServer().getPlayers().size(), expansions);

			gameLogic.getDrawPile().shuffle();

			gameLogic.dealCards();

			gameLogic.getDrawPile().populateDeckWithExplodingKittens(gameLogic.getServer().getPlayers().size());

			gameLogic.getDrawPile().shuffle();

			gameLogic.startGame(randomPlayerStarts(numPlayers));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}