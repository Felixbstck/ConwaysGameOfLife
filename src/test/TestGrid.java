package test;

import gameoflife.*;



public class TestGrid {
    
    public static void main(String[] args) {

        boolean ok = true;

        Grid grid=new Grid(8);
        grid.fillEmptyGrid();

        ok = ok && grid.getSize()==8;
        ok = ok && grid.getSize()==8 && grid.getGrid().get(0).size() !=9;

        for(int i=0;i<grid.getSize();i++){
            for(int j=0;j<grid.getSize();j++){
                ok = ok && !grid.getCell(i,j);
            }
        }
        
        ok = ok && grid.getCell(0,0) == false;
        grid.birthCell(0, 0);
        ok = ok && grid.getCell(0,0) == true;

        Grid grid2 = new Grid(2);
        grid2.birthCell(0, 0);
        Grid divideTest = grid2.divide("nw");

        ok = ok && divideTest.getCell(0, 0);

        System.out.println(ok ? "No errors Detected within the Grid Class" : "Errors detected");
    }
}