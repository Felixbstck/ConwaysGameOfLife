package test;

import gameoflife.*;

public class TestHashCode {
    public static void main(String[] args){
        boolean ok = true;

        Node a = new Node(0,1,1,0);
        Node b = new Node(1,0,1,0);
        Node c = new Node(b,a,a,b);
        
        Node d = new Node(b,a,a,b);

        ok = ok && c.hashCode() == d.hashCode();

        System.out.println(ok ? "No errors Detected within the hashcode" : "Errors detected");
    }

}