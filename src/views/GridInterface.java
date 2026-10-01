package views;

import gameoflife.Game;
import gameoflife.Grid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

/*
 * This class creates the interface for the Grid
 * It essentially goes over every cell and fills it with a specified colour
 * depending on whether it's alive or not
 */

public class GridInterface extends JPanel {
    private final int SIZE;
    private Grid grid;
    private Game game;
    private int cellSize;

    public GridInterface(int size, Game game) {

        this.SIZE = size;
        this.grid = game.getGrid();
        this.game = game;

        this.cellSize = this.SIZE / this.grid.getSize();
        if (this.cellSize == 0) {
            this.cellSize = 1;
        }
        this.setPreferredSize(new Dimension(this.cellSize * this.grid.getSize(), this.cellSize * this.grid.getSize()));
        
        this.clickCellEvent();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0 ; y < this.grid.getSize() ; y++) {
            for (int x = 0 ; x < this.grid.getSize() ; x++) {
                // Si la cellule est vivante elle est de couleur orange sinon blanche
                if (this.grid.getCell(x, y)) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x*this.cellSize, y*this.cellSize, this.cellSize, this.cellSize);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(x*this.cellSize, y*this.cellSize, this.cellSize, this.cellSize);
                }
            }
        }
    }

    public void changeCellState(int x, int y) {
        this.grid.birthCell(x, y);
    }

    /*
     * This function handles all the mouse events
     */
    public void clickCellEvent() {
        this.addMouseListener(new MouseListener() {
            private Timer timer;
            @Override
            public void mouseClicked(MouseEvent e) {
                // Switches the selected or clicked cell's state using the mouse's coordinates
                changeCellState((int) e.getX() / cellSize, (int) e.getY() / cellSize);
            }

            public void mouseEntered(MouseEvent e) {
            
            }

            public void mouseExited(MouseEvent e) {
        
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // This method lets the user drag the mouse when clicked and paint the grid
                // It works by using a Timer() every milisecond it reads where the mouse is and births the cell on which the mouse is located
                GridInterface.this.game.stopGame();
                changeCellState((int) e.getX() / cellSize, (int) e.getY() / cellSize);
                timer = new Timer(1, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                        SwingUtilities.convertPointFromScreen(mousePosition, GridInterface.this);

                        changeCellState((int) mousePosition.getX() / cellSize, (int) mousePosition.getY() / cellSize);
                    }
                });
                timer.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
            GridInterface.this.game.runGame();
        }
    });
    }    
}