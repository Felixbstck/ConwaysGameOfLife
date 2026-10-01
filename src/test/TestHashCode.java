package test;

import gameoflife.*;

public class TestHashCode {
    public static void main(String[] args){
        boolean ok = true;

        Node a = Node.leaf(0,1,1,0);
        Node b = Node.leaf(1,0,1,0);
        Node c = Node.of(b,a,a,b);
        
        Node d = Node.of(b,a,a,b);

        ok = ok && c.hashCode() == d.hashCode();

        System.out.println(ok ? "No errors Detected within the hashcode" : "Errors detected");
    }

}