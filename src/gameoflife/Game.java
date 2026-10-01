package gameoflife;

/*
 * This class is necessary to control the game
 * It contains methods that respond to button clicks in the main interface
 */

public class Game {
    private static final int MAX_TABLE_SIZE = 1_000_000;

    private boolean useHashlife;
    protected Grid grid;
    protected Node quadTree;
    private int stepDelay;
    GameThread gameThread;
    private boolean treeStale;
    private boolean updatingFromTree;

    public Game(Grid grid) {
        this.useHashlife = false;
        this.grid = grid;
        this.quadTree = Node.fromGrid(grid);
        this.stepDelay = 500;
        this.gameThread = new GameThread(this);
        this.gameThread.start();
        this.treeStale = false; 
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

    public synchronized void gridChanged() {
        if (!this.updatingFromTree) {
            this.treeStale = true;
        }
    }

    /** Advances the simulation by exactly one generation. */
    public synchronized void step() {
        if (this.useHashlife) {
            boolean tooBig = Node.tableSize() > MAX_TABLE_SIZE;
            if (tooBig) {
                Node.clearTable();
            }
            if (tooBig || this.treeStale) {
                // (re)build with fresh canonical nodes
                this.quadTree = Node.fromGrid(this.grid);
                this.treeStale = false;
            }
 
            // Pad with empty space, then take the centre half after one generation:
            // the result has the same size as the grid, and cells outside it stay dead.
            this.quadTree = this.quadTree.expand().nextGeneration();
 
            this.updatingFromTree = true;
            try {
                this.grid.setGrid(new Grid(this.quadTree));
            } finally {
                this.updatingFromTree = false;
            }
        } else {
            // Brute-force algorithm
            this.grid.nextStep();
        }
    }
}
