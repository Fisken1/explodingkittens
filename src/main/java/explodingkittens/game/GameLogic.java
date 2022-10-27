package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import communication.messages.Choice;
import communication.messages.Message;
import communication.network.Server;
import factory.*;
import game.cards.Card;
import game.decks.*;
import player.Player;
import utility.interactableCardThread;

public class GameLogic {
    private Player nextPlayer = null;
    private MainDeck drawPile;

    private DiscardPile discardPile;
    private Server server;
    private ArrayList<Player> allPlayers;
    private ArrayList<Choice> allCardNames;
    private Player currentPlayer;

    /**
     * @param numPlayers number of players to start the game with
     * @param numBots    number of bots to start the game with
     */
    public GameLogic(int numPlayers, int numBots) {
        this.discardPile = new DiscardPile();
        this.drawPile = new MainDeck();
        this.server = new Server(numPlayers, numBots);
        this.allPlayers = server.getPlayers();
    }

    /**
     * @param startPlayer
     * @param deck
     * @throws Exception
     */
    public void startGame(int startingPlayer) throws Exception {

        currentPlayer = allPlayers.get(startingPlayer); // CHNAGE THIS BACK TO "STARTPLAYER"
        int playersLeft = allPlayers.size();
        allCardNames = drawPile.allCardNames();
        Message welcomeMessage = MessageFactory
                .createMessage("WelcomeMessage", null, 0);
        notifyAllPlayers(currentPlayer, welcomeMessage, welcomeMessage);

        while (playersLeft > 1) {
            Message options = MessageFactory
                    .createMessage("Options",
                            currentPlayer.playerChoices(discardPile), 1);

            if (!currentPlayer.isBot()) {
                currentPlayer.sendMessage(MessageFactory
                        .createMessage("ClearScreen", null, 0));

                notifyAllPlayers(currentPlayer, MessageFactory
                        .createMessage("It's your turn", null, 0),
                        MessageFactory
                                .createMessage("It's player " + currentPlayer.getId() + " turn", null, 0));

                String response = "";
                while (response == "" && currentPlayer.getNumberOfTurns() > 0 && !currentPlayer.getExploded()) {
                    presentTurns(currentPlayer);

                    presentHand(currentPlayer);

                    presentTargets(currentPlayer);

                    currentPlayer
                            .sendMessage(options);

                    response = currentPlayer.readMessage(false);
                    String[] args = response.split(" ");

                    if (options.choicesContainedAnswer(args[0])
                            || (args.length == 3
                                    && (options.choicesContainedAnswer(args[0] + " " + args[1])))) {

                        if (args[0].equals("Pass")) {
                            nextPlayer = drawCard(currentPlayer);
                            currentPlayer.setNumberOfTurns(currentPlayer.getNumberOfTurns() - 1);
                            response = "";
                        } else if (isCombo(args, currentPlayer)) {
                            nextPlayer = handleCombo(currentPlayer, args);
                        } else {
                            nextPlayer = handleChoice(args, currentPlayer);
                        }

                        if (nextPlayer == null) {
                            presentCorrectSyntax(currentPlayer, response);
                            response = "";
                            continue;
                        }
                    } else {
                        presentNotValid(currentPlayer);
                        response = "";
                    }
                }
                if (currentPlayer.getExploded()) {
                    playersLeft--;
                    presentExploded(currentPlayer);
                }
                currentPlayer = nextPlayer;

                if (currentPlayer.getNumberOfTurns() == 0) {
                    currentPlayer.setNumberOfTurns(1);
                }
                checkWinner(currentPlayer, playersLeft);
            } else {
                // Bots not yet implemented
            }
        }

    }

    private void checkWinner(Player currentPlayer, int playersLeft) {
        if (playersLeft == 1) {
            currentPlayer = nextPlayer;
            Player winner = currentPlayer;
            for (Player notify : allPlayers)
                winner = (!notify.getExploded() ? notify : winner);
            for (Player notify : allPlayers)
                notify.sendMessage(MessageFactory.createMessage(
                        ((notify.getId() == winner.getId()) ? "\nCongrats you" : "Player " + winner.getId())
                                + " have won the game\n\nClosing the game...",
                        null, 0));
            server.closeSocket();
            System.exit(0);
        }
    }

    private void presentNotValid(Player currentPlayer) {
        currentPlayer.sendMessage(MessageFactory.createMessage(
                "NotValid",
                null, 0));
    }

    private void presentExploded(Player currentPlayer) {
        Message exploded = MessageFactory.createMessage(
                "Player " + currentPlayer.getId()
                        + " has exploded",
                null, 0);
        notifyAllPlayers(currentPlayer, exploded, exploded);
    }

    private void presentCorrectSyntax(Player currentPlayer, String response) {
        currentPlayer
                .sendMessage(MessageFactory.createMessage(
                        "Wrong syntax when you entered " + response + " the correct syntax is "
                                + correctSyntax(response),
                        null, 0));
    }

    private void presentTargets(Player currentPlayer) {
        if (currentPlayer.getHand().containsTargetingCard()) {
            currentPlayer.sendMessage(MessageFactory
                    .createMessage("\nYou can interact players with id's: ",
                            getAllPlayerIds(allPlayers, currentPlayer), 0));
        }
    }

    private void presentHand(Player currentPlayer) {
        currentPlayer
                .sendMessage(MessageFactory
                        .createMessage("Hand",
                                currentPlayer.getHand().getCardNamesAsChoices(), 0));
    }

    private void presentTurns(Player currentPlayer) {
        currentPlayer
                .sendMessage(MessageFactory.createMessage("\nYou have " + currentPlayer.getNumberOfTurns()
                        + ((currentPlayer.getNumberOfTurns() > 1) ? " turns" : " turn") + " to take",
                        null, 0));
    }

    /**
     * @param choice
     * @param currentPlayer
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Player handleChoice(String[] choice, Player currentPlayer) throws InterruptedException, ExecutionException {
        Card cardPlayed = currentPlayer.getHand().returnSpecificCardByName(choice[0]);
        Player target = switchCurrentPlayer(currentPlayer);
        if (cardPlayed.isAbleToTarget()) {
            target = findTarget(choice, currentPlayer);
            if (target == null) {
                return null;
            }
        } else if (!cardPlayed.isAbleToTarget() && choice.length >= 2) {
            return null;
        }
        discardPile.add(cardPlayed, 0);
        currentPlayer.getHand().remove(cardPlayed);
        interactCardPlayed(currentPlayer, cardPlayed);
        if (discardPile.getNrNope() % 2 == 0) {
            notifyAllPlayers(currentPlayer,
                    MessageFactory.createMessage(
                            "\nPlayer " + currentPlayer.getId() + " has played a " + cardPlayed.getName() + " card! \n",
                            null, 0),
                    null);
            return cardPlayed.action(discardPile, drawPile, currentPlayer, target);
        }
        Message turnGoesBack = MessageFactory
                .createMessage("Turn goes back to player " + currentPlayer.getId(), null, 0);
        notifyAllPlayers(currentPlayer, turnGoesBack, turnGoesBack);
        return currentPlayer;
    }

    /**
     * @param currentPlayer
     * @param target
     * @param combo
     * @throws NumberFormatException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Player handleCombo(Player currentPlayer, String combo[])
            throws NumberFormatException, InterruptedException, ExecutionException {
        Card comboCard = currentPlayer.getHand().returnSpecificCardByName(combo[0]);
        int comboValue = Integer.valueOf(combo[1]);
        String response;
        Card responseCard;
        Message question;
        Player target = null;
        int nopesPlayed;

        target = findTarget(combo, currentPlayer);
        if (target == null) {
            return null;
        }

        for (int i = 0; i < comboValue; i++) {
            Card cardToRemove = currentPlayer.getHand().returnSpecificCardByName(combo[0]);
            discardPile.add(cardToRemove, 0);
            currentPlayer.getHand().remove(cardToRemove);
        }
        interactCardPlayed(currentPlayer, comboCard);

        if (comboCard.getName().equals("Nope")) {
            nopesPlayed = discardPile.getNrNope() - comboValue;
        } else {
            nopesPlayed = discardPile.getNrNope();
        }

        if (nopesPlayed % 2 == 0) {
            switch (comboValue) {
                case 3:
                    boolean option = false;

                    question = MessageFactory
                            .createMessage("What card do you want to take from player " + target.getId()
                                    + " your choices are\n ", allCardNames, 1);
                    while (!option) {
                        currentPlayer.sendMessage(question);
                        response = currentPlayer.readMessage(false);
                        responseCard = target.getHand().returnSpecificCardByName(response);
                        if (question.choicesContainedAnswer(response)) {
                            option = true;
                        }
                        if (option) {
                            if (target.getHand().containsCardWithName(response)) {
                                currentPlayer.getHand().add(responseCard, comboValue);
                                target.getHand().remove(responseCard);

                                target.sendMessage(MessageFactory.createMessage(
                                        "Player " + currentPlayer.getId() + " took a "
                                                + responseCard.getName()
                                                + " card from you",
                                        null, 0));
                                notifyAllPlayers(currentPlayer, MessageFactory.createMessage(
                                        "Player " + target.getId() + " had a " + responseCard.getName()
                                                + " card in thier hand",
                                        null,
                                        0),
                                        MessageFactory.createMessage(
                                                "\nPlayer " + currentPlayer.getId() + " has played three "
                                                        + comboCard.getName()
                                                        + " cards as three of a kind and tried to steal a " + response
                                                        + " card from player " + target.getId()
                                                        + " and they got one!\n",
                                                null, 0));
                            } else {
                                notifyAllPlayers(currentPlayer, MessageFactory.createMessage("Player " + target.getId()
                                        + " did not have a " + response
                                        + " card in thier hand",
                                        null,
                                        0),
                                        MessageFactory.createMessage(
                                                "\nPlayer " + currentPlayer.getId() + " has played three "
                                                        + comboCard.getName()
                                                        + " cards as three of a kind and tried to steal a " + response
                                                        + " card from player " + target.getId()
                                                        + " but they did not have it\n",
                                                null, 0));
                            }
                        } else {
                            presentNotValid(currentPlayer);
                        }
                    }

                    return currentPlayer;
                case 2:
                    question = MessageFactory.createMessage(
                            "Where in the hand of player " + target.getId()
                                    + " do you want to take a card from:\n",
                            new ArrayList<Choice>(Arrays.asList(new Choice("0", "first positon in the hand"),
                                    new Choice(String.valueOf(Integer.valueOf(target.getHand().getCurrentSize())),
                                            "last position in the hand"))),
                            1);

                    currentPlayer.sendMessage(question);
                    boolean validOption = false;
                    while (!validOption) {
                        try {
                            int positionInDeck = Integer.valueOf(currentPlayer.readMessage(false)).intValue();
                            if (positionInDeck >= Integer.valueOf(question.getChoices().get(0).getChoice())
                                    && positionInDeck <= Integer
                                            .valueOf(question.getChoices().get(1).getChoice())) {
                                responseCard = target.getHand().getCards().get(positionInDeck);
                                currentPlayer.getHand().add(responseCard, 0);
                                target.getHand().remove(responseCard);
                                currentPlayer.sendMessage(MessageFactory.createMessage(
                                        "You got a " + responseCard.getName()
                                                + " card",
                                        null, 0));
                                target.sendMessage(MessageFactory.createMessage(
                                        "Player " + currentPlayer.getId() + " took a "
                                                + responseCard.getName()
                                                + " card from you",
                                        null, 0));
                                validOption = true;
                            } else {
                                presentNotValid(currentPlayer);
                            }

                        } catch (final NumberFormatException e) {
                            currentPlayer.sendMessage(
                                    MessageFactory.createMessage(
                                            "You did not enter a valid position, try again!", null, 0));
                        }
                    }
                    notifyAllPlayers(currentPlayer,
                            MessageFactory.createMessage(
                                    "\nPlayer " + currentPlayer.getId() + " has played a pair of "
                                            + comboCard.getName()
                                            + " cards and stolen a card from player " + target.getId() + "\n",
                                    null, 0),
                            null);
                    return currentPlayer;
            }
        }
        Message turnGoesBack = MessageFactory
                .createMessage("Turn goes back to player " + currentPlayer.getId(), null, 0);
        notifyAllPlayers(currentPlayer, turnGoesBack, turnGoesBack);
        return currentPlayer;
    }

    /**
     * @param args
     * @param currentPlayer
     * @return
     */
    private boolean isCombo(String[] args, Player currentPlayer) {
        if (args.length == 3 && currentPlayer.getHand().occurenceOf(args[0]) >= 2) {
            return true;
        }
        return false;
    }

    /**
     * @param currentPlayer The player whos turn it is
     * @return
     */
    private Player switchCurrentPlayer(Player currentPlayer) {
        int nextID = ((currentPlayer.getId() + 1) < allPlayers.size() ? (currentPlayer.getId()) + 1 : 0);
        Player newCurrentPlayer = allPlayers.get(nextID);
        if (newCurrentPlayer.getExploded()) {
            return switchCurrentPlayer(newCurrentPlayer);
        }
        return newCurrentPlayer;
    }

    /**
     * @param players       All the player that are in the game
     * @param currentPlayer The player whos turn it is
     * @return Return a ArrayList<Choice> containing all player ids
     */
    private ArrayList<Choice> getAllPlayerIds(ArrayList<Player> players, Player currentPlayer) {
        ArrayList<Choice> ids = new ArrayList<Choice>();
        for (Player player : players) {
            if (player.getId() != currentPlayer.getId() && !player.getExploded())
                ids.add(new Choice(String.valueOf(player.getId()), ""));
        }
        return ids;
    }

    /**
     * @param currentPlayer the current player who is drawing a card after a turn
     *                      has ended
     * @return return nothing if the draw pile is empety or if the player drew a
     *         ExplodingKitten card
     */
    private Player drawCard(Player currentPlayer) {
        if (drawPile.getCards().size() == 0) {
            currentPlayer.sendMessage(MessageFactory.createMessage(
                    "Empty",
                    null, 0));
            return switchCurrentPlayer(currentPlayer);
        }
        Card cardPlayerDrew = drawPile.getCards().get(0);
        if (!cardPlayerDrew.getName().equals("ExplodingKitten")) {
            currentPlayer.getHand().getCards().add(cardPlayerDrew);
            drawPile.getCards().remove(cardPlayerDrew);
            currentPlayer.sendMessage(MessageFactory.createMessage(
                    "\nYou drew a " + cardPlayerDrew.getName()
                            + " card\n",
                    null, 0));
            return switchCurrentPlayer(currentPlayer);
        }
        return drawPile.getCards().get(0).action(discardPile, drawPile, currentPlayer,
                switchCurrentPlayer(currentPlayer));
    }

    /**
     * @param id
     * @return
     */
    private Player findTarget(final String[] id, Player currentPlayer) {
        if (id.length == 2 && currentPlayer.getId() != Integer.valueOf(id[1])
                || id.length == 3 && currentPlayer.getId() != Integer.valueOf(id[2])) {
            for (Player target : allPlayers) {

                if (id.length == 2 && target.getId() == Integer.valueOf(id[1]) && !target.getExploded()) {
                    return target;
                }
                if (id.length == 3 && target.getId() == Integer.valueOf(id[2]) && !target.getExploded()) {
                    return target;
                }
            }
        }
        return null;

    }

    /**
     * @param response
     * @return
     */
    private String correctSyntax(String response) {
        switch (response) {

            // Cards with targets
            case "Favor":
                return "Example: Favor 1 -> Favor [player id]";

            // All CatCards
            case "HairyPotatoCat":
                return "Example: HairyPotatoCat 2 1 -> HairyPotatoCat [combo value] [player id]";
            case "Cattermelon":
                return "Example: Cattermelon 2 1 -> Cattermelon [combo value] [player id]";
            case "RainbowRalphingCat":
                return "Example: RainbowRalphingCat 2 1 -> RainbowRalphingCat [combo value] [player id]";
            case "TacoCat":
                return "Example: TacoCat 2 1 -> TacoCat [combo value] [player id]";
            case "OverweightBikiniCat":
                return "Example: OverweightBikiniCat 2 1 -> OverweightBikiniCat [combo value] [player id]";

            // Others
            case "Attack":
                return "Example: Attack -> Attacks next player";
        }
        return "no default syntax avalible";
    }

    /**
     * @param currentPlayer
     * @param currentPlayerMessage
     * @param otherPlayerMessage
     */
    private void notifyAllPlayers(Player currentPlayer, Message currentPlayerMessage, Message otherPlayerMessage) {
        for (Player p : allPlayers) {
            if (!p.isBot()) {
                if (otherPlayerMessage == null) {
                    p.sendMessage(currentPlayerMessage);
                } else {
                    if (p == currentPlayer && currentPlayerMessage != null)
                        p.sendMessage(currentPlayerMessage);
                    else
                        p.sendMessage(otherPlayerMessage);
                }
            }

        }
    }

    /**
     * @param currentPlayer
     * @param cardPlayed
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void interactCardPlayed(Player currentPlayer, Card cardPlayed)
            throws InterruptedException, ExecutionException {
        if (!currentPlayer.isBot()) {
            ExecutorService threadpool = Executors.newFixedThreadPool(allPlayers.size() - 1);
            CompletionService<Player> completionService = new ExecutorCompletionService<Player>(threadpool);
            boolean nopeExists = false;
            int nopePlayed = discardPile.getNrNope();
            // CHANGE
            ArrayList<Choice> choices = new ArrayList<Choice>();
            choices.add(new Choice("\nPlay nope", "Press <Enter>"));
            choices.add(new Choice("\nDont play nope", "wait"));

            for (Player p : allPlayers) {
                if (p.getId() != currentPlayer.getId()) {
                    if (p.getHand().containsCardWithName("Nope")) { // only give the option to interrupt to those who
                        // have a
                        // Nope
                        p.sendMessage(MessageFactory.createMessage(
                                "Action to nope: Player " + currentPlayer.getId() + " played " + cardPlayed.getName(),
                                null,
                                0));
                        p.sendMessage(MessageFactory.createMessage("Nope", choices, 1));
                        nopeExists = true;
                        completionService.submit(new interactableCardThread(p));
                    }
                }
            }
            if (nopeExists) {
                Player nopePlayer = completionService.take().get();
                threadpool.shutdownNow();
                if (nopePlayer != null) {
                    Card nopeCard = nopePlayer.getHand().returnSpecificCardByName("Nope");
                    nopeCard.action(discardPile, drawPile, nopePlayer, nopePlayer);
                    Message playerPlayedNope = MessageFactory.createMessage(
                            "Player " + nopePlayer.getId() + " played a Nope Card\n", null, 0);

                    if (discardPile.getNrNope() > nopePlayed) {
                        notifyAllPlayers(currentPlayer, playerPlayedNope, playerPlayedNope);
                        interactCardPlayed(nopePlayer, nopeCard);
                    }
                }
            }
        } else {
            // Bots not yet implemented
        }

    }

    public void dealCards() {
        for (Player player : allPlayers) {
            player.getHand().add(CardsFactory.createCard("Defuse"), 0);
            for (int i = 0; i < 7; i++) {
                player.getHand().add(drawPile.removeAndDistributeCards(), 0);
            }
        }
    }

    public Server getServer() {
        return server;
    }

    public MainDeck getDrawPile() {
        return drawPile;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

}