package test;

import gameoflife.*;


public class TestEquals {
    public static void main(String[] args) {

        boolean ok = true;

        Node a = Node.leaf(0,0,0,0);
        Node b = Node.leaf(0,0,0,0);

        ok = ok && a.equals(b);
        System.out.println(ok ? "No errors Detected within the Grid Class" : "Errors detected");
    }
}
