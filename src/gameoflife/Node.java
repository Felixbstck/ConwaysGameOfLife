package gameoflife;

import java.util.HashMap;

import listeners.AbstractListenableModel;

public class Node extends AbstractListenableModel {
    public HashMap<Node, Node> hashMap = new HashMap<>();
    public Node nw, ne, sw, se;
    public int level;
    public int cellNW, cellNE, cellSW, cellSE;
    public Grid grid;


    public Node(Node nw, Node ne, Node sw, Node se) {
        this.ne = ne;
        this.nw = nw;
        this.se = se;
        this.sw = sw;
        this.level = ne.level + 1;
    }

    public Node(int cellNW, int cellNE, int cellSW, int cellSE) {
        this.cellNE = cellNE;
        this.cellNW = cellNW;
        this.cellSE = cellSE;
        this.cellSW = cellSW;
        this.level = 1;
        this.ne = this.nw = this.se = this.sw = null;
    }

    public Node(Grid grid) {
        // Constructs a Node (aka quadtree) from a grid
        this.grid = grid;
        create();
    }

    public int getLevel() {
        return this.level;
    }

    public Node zero(int level) {
        // Constructs an empty Node at given level
        if (level == 1) {
            return new Node(0, 0, 0, 0);
        }
        return new Node(this.zero(level-1), this.zero(level-1), this.zero(level-1), this.zero(level-1));
    }

    public Node expand() {
        Node zero = this.zero(this.level);
        Node nw = new Node(zero, zero, zero, this.nw);
        Node ne = new Node(zero, zero, this.ne, zero);
        Node sw = new Node(zero, this.sw, zero, zero);
        Node se = new Node(this.se, zero, zero, zero);
        return new Node(nw, ne, sw, se);
    }

    public Node centeredSubnode() {
        if (this.level == 2) {
            return new Node(this.nw.cellSE, this.ne.cellSW, this.sw.cellNE, this.se.cellNW);
        }
        return new Node(this.nw.se, this.ne.sw, this.sw.ne, this.se.nw);
    }

    public Node horizontalSubnode(Node l, Node r) {
        if (l.getLevel() == 2) {
            return new Node(l.cellNE, r.cellNW, l.cellSE, r.cellSW);
        }
        return new Node(l.ne, r.nw, l.se, r.sw);
    }

    public Node verticalSubnode(Node t, Node b) {
        if (t.getLevel() == 2) {
            return new Node(t.cellSW, t.cellSE, b.cellNW, b.cellSE);
        }
        return new Node(t.sw, t.se, b.nw, b.se);
    } 

    public Node evolve() {
        if (this.level == 2) {
            Grid grid = new Grid(this);
            grid.nextStep();
            return new Node(grid);
        }
    
        // Recursively evolve child nodes
        Node nw = this.nw.evolve();
        Node ne = this.ne.evolve();
        Node sw = this.sw.evolve();
        Node se = this.se.evolve();
    
        // Construct and return the new node
        return new Node(nw, ne, sw, se);
    }

    public int pop() {
        // Returns the population of a 2x2
        return this.cellNW + this.cellNE + this.cellSW + this.cellSE;
    }

    public String toString() {
        return "Node at level " + this.level;
    }

    public void create() {
        // Constructs itself by recursively calling this function until it gets to a 2x2 Grid
        if (this.grid.getSize() == 2) {
            this.level = 1;
            this.cellNW = this.grid.getCell(0, 0) == true ? 1 : 0;
            this.cellNE = this.grid.getCell(1, 0) == true ? 1 : 0;
            this.cellSW = this.grid.getCell(0, 1) == true ? 1 : 0;
            this.cellSE = this.grid.getCell(1, 1) == true ? 1 : 0;
        } else {
            this.nw = new Node(this.grid.divide("nw"));
            this.ne = new Node(this.grid.divide("ne"));
            this.sw = new Node(this.grid.divide("sw")); 
            this.se = new Node(this.grid.divide("se"));
            this.level = this.nw.level + 1;
        }
    }

    public Node exists() {
        // Checks if Node exists in hashMap
        Node canon = this.hashMap.get(this);
        if (canon != null) {
            return canon;
        }

        // Else we insert it
        this.hashMap.put(this, this);
        return this;
    }

    public void update(Grid grid) {
        this.grid = grid;
        create();
    }

    @Override
    public boolean equals(Object a) {
        return this.hashCode() == a.hashCode() && this.level == ((Node)a).getLevel();
    }

    @Override
    public int hashCode() {
        if (this.level == 1) {
            int res;
            res = this.cellNW == 1 ? 1000 : 0;
            res += this.cellNE == 1 ? 100 : 0;
            res += this.cellSW == 1? 10 : 0;
            res += this.cellSE == 1 ? 1 : 0;
            return String.valueOf(res).hashCode();
        }

        return (String.valueOf(this.nw.hashCode()) + 
                String.valueOf(this.ne.hashCode()) + 
                String.valueOf(this.sw.hashCode()) + 
                String.valueOf(this.se.hashCode())).hashCode();
    }
}



