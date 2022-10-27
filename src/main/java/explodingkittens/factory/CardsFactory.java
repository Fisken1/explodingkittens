package factory;

import game.cards.*;

public class CardsFactory {

    public static Card createCard(String card) {
        switch (card) {
            case "Attack":
                return new Attack();
            case "Defuse":
                return new Defuse();
            case "ExplodingKitten":
                return new ExplodingKitten();
            case "Favor":
                return new Favor();
            case "Nope":
                return new Nope();
            case "SeeTheFuture":
                return new SeeTheFuture();
            case "Shuffle":
                return new Shuffle();
            case "Skip":
                return new Skip();
        }
        return new CatCard(card);
    }
}
