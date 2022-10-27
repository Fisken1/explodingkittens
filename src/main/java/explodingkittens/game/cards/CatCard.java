package game.cards;

import game.decks.*;
import player.Player;

public class CatCard implements Card {

    private String name;
    private String description;
    private boolean ableToTarget;

    public CatCard(String name) {
        this.name = name;
        this.description = "(These cards are powerless on their own, but if you collect 2 or 3 they can be played as combos)";
        this.ableToTarget = true;
    }

    @Override
    public boolean isAbleToTarget() {
        return ableToTarget;
    }

    @Override
    public Player action(DiscardPile discardPile, MainDeck drawPile, Player player, Player target) {
        discardPile.add(this, 0);
        player.getHand().remove(this);
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