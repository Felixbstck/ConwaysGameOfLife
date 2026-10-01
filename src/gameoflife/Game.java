package gameoflife;

/*
 * This class is necessary to control the game
 * It contains methods that respond to button clicks in the main interface
 */

public class Game {
    private boolean useHashlife;
    protected Grid grid;
    protected Node quadTree;
    private int stepDelay;
    GameThread gameThread;

    public Game(Grid grid) {
        this.useHashlife = false;
        this.grid = grid;
        this.quadTree = new Node(grid);
        this.stepDelay = 500;
        this.gameThread = new GameThread(this);
        this.gameThread.start();
    }

    public Game(Node quadTree) {
        this.quadTree = quadTree;
    }

    public void setStepDelay(int delay) {
        this.stepDelay = delay;
    }
    
    public int getStepDelay() {
        return this.stepDelay;
    }

    public boolean getUseHashlife() {
        return this.useHashlife;
    }

    public void setUseHashlife(boolean useHashlife) {
        this.useHashlife = useHashlife;
    }

    public Grid getGrid() {
        return this.grid;
    }

    public Node getQuadTree() {
        return this.quadTree;
    }

    public void runGame() {
        this.gameThread.setIsRunning(true);
    }

    public void stopGame() {
        this.gameThread.setIsRunning(false);
    }
}
