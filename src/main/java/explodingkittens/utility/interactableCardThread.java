package utility;

import java.util.concurrent.Callable;

import player.Player;

public class interactableCardThread implements Callable<Player> {
    private Player p;

    public interactableCardThread(Player p) {
        this.p = p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     * A synchronized call used when alowing players to play nope cards. If the
     * player press enter return that player else return null. If there is a error
     * when reading from the player throw a exeption
     */
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
