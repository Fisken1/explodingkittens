package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.cards.Card;
import game.cards.CardsFactory;
import game.decks.*;
import messages.Choice;
import messages.Message;
import messages.MessageFactory;
import network.Server;
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
     * 
     *                   creates the discard pile, draw pile and server. Then gets
     *                   all the players from the server.
     * 
     */
    public GameLogic(int numPlayers, int numBots) {
        this.discardPile = new DiscardPile();
        this.drawPile = new MainDeck();
        this.server = new Server(numPlayers, numBots);
        this.allPlayers = server.getPlayers();
    }

    /**
     * @param startPlayer the randomly choosen player who starts
     * @throws Exception throws if something goes wrong in handlecombo or
     *                   handlechoice
     * 
     *                   Presents all the players with a welcome message then
     *                   presents the current player with his hand, optionsn number
     *                   of turns, targets and so on. Then waits for a response from
     *                   the current player. If the choices the player has contain
     *                   this response we check if the response is a combo or a
     *                   normal card. Then the card is handled by the appropriate
     *                   function. else we tell the player that the response is not
     *                   valid try again. This will go on
     *                   ntil the player has no more turns. After a player has
     *                   played thier turns we check if we have a winner. if we do
     *                   we exit the game else it is the turn of the next player.
     */
    public void startGame(int startingPlayer) throws Exception {

        setCurrentPlayer(allPlayers.get(startingPlayer));
        int playersLeft = allPlayers.size();
        allCardNames = drawPile.allCardNames();
        Message welcomeMessage = MessageFactory
                .createMessage("WelcomeMessage", null, 0);
        notifyAllPlayers(currentPlayer, welcomeMessage, welcomeMessage);

        while (playersLeft > 1) {
            Message options = MessageFactory
                    .createMessage("Options",
                            currentPlayer.getHand().playerChoices(), 1);

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
                            nextPlayer = playPass();
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
                setCurrentPlayer(nextPlayer);

                if (currentPlayer.getNumberOfTurns() == 0) {
                    currentPlayer.setNumberOfTurns(1);
                }
                if (checkWinner(currentPlayer, playersLeft)) {
                    server.closeSocket();
                    System.exit(0);
                }
            } else {
                // Bots not yet implemented
            }
        }

    }

    /**
     * @return the next player whos turn it is
     * 
     *         Removes a turn from the currentplayers turn
     */
    private Player playPass() {
        currentPlayer.setNumberOfTurns(currentPlayer.getNumberOfTurns() - 1);
        return drawCard(currentPlayer);
    }

    /**
     * @param currentPlayer the player whos turn it is
     * @param playersLeft   the amount of players left in the game
     * @return true if a winner has been found else false
     * 
     *         Checks if we have got a winner then notify all the players that we
     *         have a winner else continue the game
     */
    public boolean checkWinner(Player currentPlayer, int playersLeft) {
        if (playersLeft == 1) {
            setCurrentPlayer(nextPlayer);
            Player winner = currentPlayer;
            for (Player notify : allPlayers)
                winner = (!notify.getExploded() ? notify : winner);
            for (Player notify : allPlayers)
                notify.sendMessage(MessageFactory.createMessage(
                        ((notify.getId() == winner.getId()) ? "\nCongrats you" : "Player " + winner.getId())
                                + " have won the game\n\nClosing the game...",
                        null, 0));

            return true;
        }
        return false;
    }

    /**
     * @param currentPlayer the player whos turn it is
     * 
     *                      The answer currentPlayer gave was not valid so notify
     *                      the player that is was not valid
     * 
     */
    private void presentNotValid(Player currentPlayer) {
        currentPlayer.sendMessage(MessageFactory.createMessage(
                "NotValid",
                null, 0));
    }

    /**
     * @param currentPlayer the player whos turn it is
     * 
     *                      presents who exploded to all players
     * 
     */
    private void presentExploded(Player currentPlayer) {
        Message exploded = MessageFactory.createMessage(
                "Player " + currentPlayer.getId()
                        + " has exploded",
                null, 0);
        notifyAllPlayers(currentPlayer, exploded, exploded);
    }

    /**
     * @param currentPlayer the player whos turn it is
     * @param input         the input from currentPlayer
     * 
     *                      If you enter the wrong syntax you present the player
     *                      with the correct one if one exists
     */
    private void presentCorrectSyntax(Player currentPlayer, String input) {
        currentPlayer
                .sendMessage(MessageFactory.createMessage(
                        "Wrong syntax when you entered " + input + " the correct syntax is "
                                + correctSyntax(
                                        input),
                        null, 0));
    }

    /**
     * @param currentPlayer the player whos turn it is
     * 
     *                      presents all avaliable targets for the player if the
     *                      player has a target card
     */
    private void presentTargets(Player currentPlayer) {
        if (currentPlayer.getHand().containsTargetingCard()) {
            currentPlayer.sendMessage(MessageFactory
                    .createMessage("\nYou can interact with players that have id's: ",
                            getAllPlayerIds(allPlayers, currentPlayer), 0));
        }
    }

    /**
     * @param currentPlayer the player whos turn it is
     * 
     *                      presents the hand of the player to the player
     */
    private void presentHand(Player currentPlayer) {
        currentPlayer
                .sendMessage(MessageFactory
                        .createMessage("Hand",
                                currentPlayer.getHand().getCardNamesAsChoices(), 0));
    }

    /**
     * @param currentPlayer the player whos turn it is
     * 
     *                      presents the amount of turn you have left
     */
    private void presentTurns(Player currentPlayer) {
        currentPlayer
                .sendMessage(MessageFactory.createMessage("\nYou have " + currentPlayer.getNumberOfTurns()
                        + ((currentPlayer.getNumberOfTurns() > 1) ? " turns" : " turn") + " to take",
                        null, 0));
    }

    /**
     * @param choice        the input you gave split up
     * @param currentPlayer the player whos turn it is
     * @return a player based on conditions
     * @throws InterruptedException if something goes wrong with
     *                              interactCardPlayed()
     * @throws ExecutionException   if something goes wrong with
     *                              interactCardPlayed()
     * 
     *                              If the card is targetable find target, if the
     *                              target is bad return null and tell player to try
     *                              again. If the lenght of you input is to long for
     *                              a certain card return null. Discard the card
     *                              then see if anyone wants to play a nope card. If
     *                              no one played a nope card then do the action of
     *                              the card and return that player. Else return
     *                              currentPlayer.
     * 
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
     * @param currentPlayer the player whos turn it is
     * @param combo         the input given by the player
     * @throws NumberFormatException if the player dont give a int when asked
     * @throws InterruptedException  handles interactCardPlayed
     * @throws ExecutionException    handles interactCardPlayed
     * 
     *                               This function first finds the card you just
     *                               played then tries to find the target, if not
     *                               found return null else continue. then remove
     *                               the specified amount of the card you played
     *                               from your hand and add them to the discard
     *                               pile. Then see if anyone wants to play a nope.
     *                               if the combocard played was nope remove that
     *                               many nope from the discard pile then check
     *                               if the number of nopes played is even then
     *                               continue else return currentPlayer.
     * 
     *                               If you played a three of a kind you play go
     *                               fish (finns i sjön) and if the player has the
     *                               card you asked for you get it else return the
     *                               current player. If the value entered by the
     *                               player was not valid present that.
     * 
     *                               If you played a pair you can pick a place in
     *                               the targets deck to get that card. The card you
     *                               stole will be presented. If the value
     *                               entered by the player was not valid present
     *                               that.
     * 
     * 
     * 
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
                case 3: // Three of a kind
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
                case 2: // Pair
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
     * @param args          the input you gave but split up
     * @param currentPlayer the player whos turn it is
     * @return true if the lenght if args is three (example: TacoCat 2 0) and
     *         currentPlayers hand contain greater or eaqual to two of the cards you
     *         want to play. Else false.
     */
    private boolean isCombo(String[] args, Player currentPlayer) {
        if (args.length == 3 && currentPlayer.getHand().occurenceOf(args[0]) >= 2) {
            return true;
        }
        return false;
    }

    /**
     * @param currentPlayer the player whos turn it is
     * @return the next player whos turn it is
     * 
     *         It tries to return the next player inm allPlayers but if that player
     *         is exploded then recursivly call the function to find a suitable
     *         target!
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
     * @return Return a ArrayList<Choice> containing all player ids except the
     *         currentPlayers id or if a player has exploded
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
     * @return return the next player if the draw pile is empty or if the player
     *         dont draw a exploding kittens. Else return the player who is returned
     *         by using the action on the exploding kitten card.
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
     * @param id contains the id we want to find aswell as the other inputs you gave
     *           to know where to look
     * @return the target if the target is not exploded or if the target is not
     *         yourself else null (no target found)
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
     * @param response the input you gave
     * @return the correct syntax if it exsits.
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
     * @param currentPlayer        the player whos turn it is
     * @param currentPlayerMessage the message you want to send to currentPlayer
     * @param otherPlayerMessage   the message you want to send to all other players
     * 
     *                             sends messgaes to all players and you can modify
     *                             what message you send to the currentPlayer and
     *                             what Message you send to all other players.
     * 
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
     * @param currentPlayer the player whos turn it is
     * @param cardPlayed    the card that was just played
     * @throws InterruptedException handels recursion calls
     * @throws ExecutionException   handels recursion calls
     * 
     *                              This functions checks if the current player is a
     *                              bot if not we create a thread pool with the size
     *                              of all players -1 as you cant nope yourself.
     *                              Then it creates a completion service with the
     *                              threadpool. Then it creates a arraylist
     *                              containing the choices you have, either play
     *                              nope or no. Then for all players who have a nope
     *                              card that is not yourself ask them if they want
     *                              to nope. If anyone press enter and thereby using
     *                              a
     *                              nopecard the completion service takes the first
     *                              thread who press nope and then recursivly calls
     *                              this funciton until no one wants to nope or if
     *                              no one has any nope cards left with the nope
     *                              card just played and the person who used nope as
     *                              the currentPlayer.
     * 
     */
    private void interactCardPlayed(Player currentPlayer, Card cardPlayed)
            throws InterruptedException, ExecutionException {
        if (!currentPlayer.isBot()) {
            ExecutorService threadpool = Executors.newFixedThreadPool(allPlayers.size() - 1);
            CompletionService<Player> completionService = new ExecutorCompletionService<Player>(threadpool);
            boolean nopeExists = false;
            int nopePlayed = discardPile.getNrNope();
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

    /**
     * Deal cards to all players
     */
    public void dealCards() {
        for (Player player : allPlayers) {
            for (int i = 0; i < 7; i++) {
                player.getHand().add(drawPile.removeAndDistributeCards(), 0);
            }
        }
    }

    /**
     * Deal defuse cards to all players
     */
    public void dealDefuseCards() {
        for (Player player : allPlayers) {
            player.getHand().add(CardsFactory.createCard("Defuse"), 0);
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

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}