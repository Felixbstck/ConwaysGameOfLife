package test;

import gameoflife.*;

public class TestNode{

    public static void main(String[] args){
        boolean ok = true;
        Node a = new Node(0,1,1,0);
        Node b = new Node(1,0,1,0);
        Node c = new Node(b,a,a,b);
        /** on teste si toutes les valeurs de a renvoie correctement la valeur cellnw cellne cellsw cellse ainsi qu'avec le level pour vérifier 
         * si il est bien initialiser à 1 après cela on fait la même chose avec un noeud c de taille 2 afin de vérifier si c est créer comme on le souhaite  */
        ok = ok && a.cellNW == 0; 
        ok = ok && a.cellNE == 1;
        ok = ok && a.cellSW == 1;
        ok = ok && a.cellSE == 0;
        ok = ok && a.getLevel() == 1;

        ok = ok && c.nw  == b;
        ok = ok && c.ne == a;
        ok = ok && c.sw == a;
        ok = ok && c.se == b;
        ok = ok && c.getLevel() == 2;
        
        System.out.println(ok ? "No errors Detected within the Grid Class" : "Errors detected");
    }

}


