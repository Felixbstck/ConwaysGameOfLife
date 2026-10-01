package gameoflife;

import listeners.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Grid extends AbstractListenableModel {
    private final int SIZE;
    // Our grid uses and array of arrays of booleans
    private List<List<Boolean>> grid = new ArrayList<>();

    public Grid(int size) {
        SIZE = size;

        // Initialises empty grid
        for (int i = 0 ; i < SIZE ; i++) {
            List<Boolean> row = new ArrayList<>();
            for (int j = 0 ; j < SIZE ; j++) {
                row.add(false);
            }
            grid.add(row);
        }
    }
    
    public Grid(int size, List<List<Boolean>> grid) {
        SIZE = size;
        this.grid = grid;
    }

    public Grid(Node quadTree) {
        // Construct a grid from a quadTree
        this((int) Math.pow(2, quadTree.getLevel()));
        this.convertQuadTree(quadTree, 0, 0);
    }

    public void convertQuadTree(Node quadTree, int x, int y) {
        // Constructs the grid recursively by going down levels
        // Once at level 1 it constructs the 2x2 at the correct space
        if (quadTree.level == 1) {
            if (quadTree.cellNW == 1) {
                this.grid.get(y).set(x, true);
            }
            if (quadTree.cellNE == 1) {
                this.grid.get(y).set(x+1, true);
            }
            if (quadTree.cellSW == 1) {
                this.grid.get(y+1).set(x, true);
            }
            if (quadTree.cellSE == 1) {
                this.grid.get(y+1).set(x+1, true);
            }
        } else {
            // Recursively calls itself with adjusted coordinates
            convertQuadTree(quadTree.nw, x, y);
            convertQuadTree(quadTree.ne, (int) (x + Math.pow(2, quadTree.getLevel()-1)), y);
            convertQuadTree(quadTree.sw, x, (int) (y + Math.pow(2, quadTree.getLevel()-1)));
            convertQuadTree(quadTree.se, (int) (x + Math.pow(2, quadTree.getLevel()-1)), (int) (y + Math.pow(2, quadTree.getLevel()-1)));
        }
     }

    public int getSize() {
        return this.SIZE;
    }

    public void showGrid() {
        // Prints grid in terminal
        // Clears the terminal then prints either a star * or full stop .
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
        for (List<Boolean> row : this.grid) {
            for (Boolean cell : row) {
                if (cell) {
                    System.out.printf("*");
                } else {
                    System.out.printf(".");
                }
            }
            System.out.printf("\n");
        }
    }

    public void fillEmptyGrid() {
        // Resets the grid
        // Useful for creating new patterns
        for (int i = 0 ; i < SIZE ; i++) {
            for (int j = 0 ; j < SIZE ; j++) {
                this.grid.get(i).set(j, false);
            }
        }
    }

    public Grid divide(String dir) {
        // Divides the grid based on the cardinal direction
        // Used to construct quadTrees from a grid

        List<List<Boolean>> newgrid = new ArrayList<>();
        
        if (dir == "ne") {
            for (int y = 0 ; y < SIZE/2 ; y++) {
                List<Boolean> row = new ArrayList<Boolean>();
                for (int x = SIZE/2 ; x < SIZE ; x++ ) {
                    row.add(this.getCell(x, y));
                }
                newgrid.add(row);
            }
            return new Grid(SIZE/2, newgrid);
        }
        if (dir == "nw") {
            for (int y = 0 ; y < SIZE/2 ; y++) {
                List<Boolean> row = new ArrayList<Boolean>();
                for (int x = 0 ; x < SIZE/2 ; x++ ) {
                    row.add(this.getCell(x, y));
                }
                newgrid.add(row);
            }
            return new Grid(SIZE/2, newgrid);
        }
        if (dir == "se") {
            for (int y = SIZE/2 ; y < SIZE ; y++) {
                List<Boolean> row = new ArrayList<Boolean>();
                for (int x = SIZE/2 ; x < SIZE ; x++) {
                    row.add(this.getCell(x, y));
                }
                newgrid.add(row);
            }
            return new Grid(SIZE/2, newgrid);
        }
        if (dir == "sw") {
            for (int y = SIZE/2 ; y < SIZE ; y++) {
                List<Boolean> row = new ArrayList<Boolean>();
                for (int x = 0 ; x < SIZE/2 ; x++ ) {
                    row.add(this.getCell(x, y));
                }
                newgrid.add(row);
            }
            return new Grid(SIZE/2, newgrid);
        }
        return null;
    }

    public List<List<Boolean>> getGrid() {
        return this.grid;
    }

    public void fillGrid() {
        // Randomly fills a grid using Random()
        this.fillEmptyGrid();
        Random rand = new Random();
        for (int i = 0 ; i < SIZE ; i++) {
            for (int j = 0 ; j < SIZE ; j++) {
                float f = rand.nextFloat();
                if (f < 0.90) {
                    this.grid.get(i).set(j, false);
                } else {
                    this.grid.get(i).set(j, true);
                }
            }
        }
        firechangement();
    }

    public void nextStep() {
        // This method computes the next generation using a brute force algorithm
        // It checks every single cell and it's neighbours
        // We create a copy of the array and work on it then replace the current array with the copy 

        List<List<Boolean>> newGrid = new ArrayList<>();
        for (int i = 0 ; i < SIZE ; i++) {
            List<Boolean> row = new ArrayList<>();
            for (int j = 0 ; j < SIZE ; j++) {
                row.add(true);
            }
            newGrid.add(row);
        }

        for (int y = 0 ; y < SIZE ; y++) {
            for (int x = 0 ; x < SIZE ; x++) {
                int nbVoisins = countNeighbours(x, y);
                if (nbVoisins == 3) {
                    newGrid.get(y).set(x, true);
                } if (nbVoisins > 3) {
                    newGrid.get(y).set(x, false);
                } if (nbVoisins < 2) {
                    newGrid.get(y).set(x, false);
                } if (nbVoisins == 2) {
                    newGrid.get(y).set(x, getCell(x, y));
                }
            }
        }

        this.grid = newGrid;
        this.firechangement();
    }
    

    public boolean getCell(int x, int y) {
        // Returns the cell at the correct coordinate
        return this.grid.get(y).get(x);
    }


    public int countNeighbours(int x, int y) {
        // Counts the 8 neighbours around the given cell
        // Works even for cells with less than 8 neighbours (cells at the border)

        int nb = 0;
        int left = x-1;
        int right = x+1;
        int up = y-1;
        int down = y+1;

        if (left >= 0) {
            if (up >= 0) {
                if (this.getCell(left, up) && up >= 0)
                {
                    nb++;
                }
            } 

            if (this.getCell(left, y))
            {
                nb++;
            }
        }
        if (left >= 0 && down < SIZE) 
        {
            if (this.getCell(left, down))
            {
                nb++;
            }
        }
        if (up >= 0) {
            if (this.getCell(x, up))
            {
                nb++;
            }
        }

        if (down < SIZE) { 
            if (this.getCell(x, down))
            {
                nb++;
            }
        }

        if (right < SIZE)
        { 
            if (up >= 0)
            {
                if (this.getCell(right, up))
                {nb++;}
            }

            if (this.getCell(right, y))
            {nb++;}
            
            if (down < SIZE) 
            {
                if (this.getCell(right, down))
                {nb++;}
            }
        }
        return nb;
    }

    public void createPattern(String filename) {
        // Creates a pattern from a text file (.txt) 
        try {
            filename = "./patterns/" + filename;
            Scanner scanner = new Scanner(new File(filename));

            this.fillEmptyGrid();
            
            int row = 42;

            while (scanner.hasNextLine()) {
                 

                if (row >= this.SIZE) {
                    scanner.close();
                    throw new Exception("Pattern is bigger than grid");
                }

                String line = scanner.nextLine();
                int col = 42;

                for (char chr : line.toCharArray()) {
                    if (col >= this.SIZE) {
                        scanner.close();
                        throw new Exception("Pattern is larger than grid");
                    }

                    if (chr != '.') {
                        this.grid.get(row).set(col, true);
                    }
                    col++;
                }
                row++;
            }
            scanner.close();
            firechangement();
        }
        
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void birthCell(int x, int y) {
        // Sets the cell at the given coordinate to true or alive
        if (0 <= x && x <= this.SIZE && 0 <= y && y <= this.SIZE) {
            this.grid.get(y).set(x, true);
            firechangement();
        }
    }
    
    public void setGrid(Grid grid) {
        this.grid = grid.getGrid();
        firechangement();
    }
}
