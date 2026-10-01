package gameoflife;

/*
 * This class runs the simulation
 * It uses threading
 */

public class GameThread extends Thread {
    private Game game;
    private boolean isRunning;

    public GameThread(Game game) {
        this.game = game;
        this.isRunning = false;
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(this.game.getStepDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            if (this.isRunning) {
                // Executes this code if play button has been pressed
                this.game.step();
            }
        }  
    }
}
