package utility;

import java.util.concurrent.Callable;

import player.Player;

public class interactableCardThread implements Callable<Player> {
    private Player p;

    public interactableCardThread(Player p) {
        this.p = p;
    }

    public synchronized Player call() {
        try {
            String nextMessage = p.readMessage(true);
            if (!nextMessage.equals(" ")) {
                return this.p;

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;

    }
}
