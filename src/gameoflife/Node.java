package gameoflife;

import java.util.HashMap;

/**
 * Immutable quadtree node for Hashlife.
 *
 * Level 1 = 2x2 cells (stored as ints), level k = 2^k x 2^k cells.
 * Every node is created through leaf() / of(), which return a single shared
 * (canonical) instance per distinct pattern. Because of that, two nodes are
 * equal exactly when their children are the same objects, and results can be
 * cached on the node itself.
 */
public final class Node {
    public final Node nw, ne, sw, se;                 // null at level 1
    public final int cellNW, cellNE, cellSW, cellSE;  // only meaningful at level 1
    public final int level;

    private final int hash;       // computed once at construction
    private Node next;            // cache: centre half after exactly 1 generation
    private Node result;          // cache: centre half after 2^(level-2) generations

    private static final HashMap<Node, Node> TABLE = new HashMap<>();

    // ---------------------------------------------------------------- creation

    private Node(int a, int b, int c, int d) {
        this.nw = this.ne = this.sw = this.se = null;
        this.cellNW = a; this.cellNE = b; this.cellSW = c; this.cellSE = d;
        this.level = 1;
        this.hash = a * 8 + b * 4 + c * 2 + d;
    }

    private Node(Node nw, Node ne, Node sw, Node se) {
        this.nw = nw; this.ne = ne; this.sw = sw; this.se = se;
        this.cellNW = this.cellNE = this.cellSW = this.cellSE = 0;
        this.level = nw.level + 1;
        this.hash = 31 * (31 * (31 * nw.hash + ne.hash) + sw.hash) + se.hash;
    }

    public static Node leaf(int a, int b, int c, int d) {
        return canon(new Node(a, b, c, d));
    }

    public static Node of(Node nw, Node ne, Node sw, Node se) {
        return canon(new Node(nw, ne, sw, se));
    }

    private static Node canon(Node n) {
        Node existing = TABLE.get(n);
        if (existing != null) {
            return existing;
        }
        TABLE.put(n, n);
        return n;
    }

    public static int tableSize() {
        return TABLE.size();
    }

    /** Empties the canonical table. Every Node you still hold must be rebuilt afterwards (see Game.step). */
    public static void clearTable() {
        TABLE.clear();
    }

    /** Builds a quadtree from a grid whose size is a power of two (>= 2). */
    public static Node fromGrid(Grid g) {
        return build(g, 0, 0, g.getSize());
    }

    private static Node build(Grid g, int x, int y, int size) {
        if (size == 2) {
            return leaf(g.getCell(x, y) ? 1 : 0,
                        g.getCell(x + 1, y) ? 1 : 0,
                        g.getCell(x, y + 1) ? 1 : 0,
                        g.getCell(x + 1, y + 1) ? 1 : 0);
        }
        int h = size / 2;
        return of(build(g, x, y, h), build(g, x + h, y, h),
                  build(g, x, y + h, h), build(g, x + h, y + h, h));
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node n = (Node) o;
        if (this.level != n.level) return false;
        if (this.level == 1) {
            return cellNW == n.cellNW && cellNE == n.cellNE
                && cellSW == n.cellSW && cellSE == n.cellSE;
        }
        // children are canonical, so reference comparison is exact (no hash-collision risk)
        return nw == n.nw && ne == n.ne && sw == n.sw && se == n.se;
    }

    // ------------------------------------------------------------- utilities

    public int getLevel() {
        return level;
    }

    public int pop() {
        return cellNW + cellNE + cellSW + cellSE;     // population of a level-1 node
    }

    @Override
    public String toString() {
        return "Node at level " + level;
    }

    public static Node zero(int level) {
        if (level == 1) return leaf(0, 0, 0, 0);
        Node z = zero(level - 1);
        return of(z, z, z, z);
    }

    /** Level k -> level k+1, with this node's pattern in the centre. Requires level >= 2. */
    public Node expand() {
        Node z = zero(level - 1);
        return of(of(z, z, z, nw), of(z, z, ne, z), of(z, sw, z, z), of(se, z, z, z));
    }

    // Sub-nodes used by the recursion (the node must be at least level 2)

    private Node centre() {
        if (level == 2) {
            return leaf(nw.cellSE, ne.cellSW, sw.cellNE, se.cellNW);
        }
        return of(nw.se, ne.sw, sw.ne, se.nw);
    }

    private static Node horiz(Node l, Node r) {       // l, r at level >= 2
        return of(l.ne, r.nw, l.se, r.sw);
    }

    private static Node vert(Node t, Node b) {        // t, b at level >= 2
        return of(t.sw, t.se, b.nw, b.ne);
    }

    // ---------------------------------------------------------------- Hashlife

    /**
     * The centre half of this node (level - 1) after exactly ONE generation.
     * Requires level >= 2. Used for the step-by-step display.
     */
    public Node nextGeneration() {
        if (next != null) return next;
        Node r;
        if (level == 2) {
            r = baseCase();
        } else {
            Node n00 = nw,            n01 = horiz(nw, ne), n02 = ne;
            Node n10 = vert(nw, sw),  n11 = centre(),      n12 = vert(ne, se);
            Node n20 = sw,            n21 = horiz(sw, se), n22 = se;

            // phase 1: take the centre of each of the 9 sub-nodes (no time passes)
            Node c00 = n00.centre(), c01 = n01.centre(), c02 = n02.centre();
            Node c10 = n10.centre(), c11 = n11.centre(), c12 = n12.centre();
            Node c20 = n20.centre(), c21 = n21.centre(), c22 = n22.centre();

            // phase 2: the four overlapping blocks each advance one generation
            r = of(of(c00, c01, c10, c11).nextGeneration(),
                   of(c01, c02, c11, c12).nextGeneration(),
                   of(c10, c11, c20, c21).nextGeneration(),
                   of(c11, c12, c21, c22).nextGeneration());
        }
        return next = r;
    }

    /**
     * The centre half of this node (level - 1) after 2^(level-2) generations.
     * Requires level >= 2. This is the "big jump" form of Hashlife.
     */
    public Node result() {
        if (result != null) return result;
        Node r;
        if (level == 2) {
            r = baseCase();
        } else {
            Node n00 = nw,            n01 = horiz(nw, ne), n02 = ne;
            Node n10 = vert(nw, sw),  n11 = centre(),      n12 = vert(ne, se);
            Node n20 = sw,            n21 = horiz(sw, se), n22 = se;

            Node r00 = n00.result(), r01 = n01.result(), r02 = n02.result();
            Node r10 = n10.result(), r11 = n11.result(), r12 = n12.result();
            Node r20 = n20.result(), r21 = n21.result(), r22 = n22.result();

            r = of(of(r00, r01, r10, r11).result(),
                   of(r01, r02, r11, r12).result(),
                   of(r10, r11, r20, r21).result(),
                   of(r11, r12, r21, r22).result());
        }
        return result = r;
    }

    /** 4x4 -> centre 2x2 after one generation, computed directly. */
    private Node baseCase() {
        int[][] c = new int[4][4];
        c[0][0] = nw.cellNW; c[0][1] = nw.cellNE; c[1][0] = nw.cellSW; c[1][1] = nw.cellSE;
        c[0][2] = ne.cellNW; c[0][3] = ne.cellNE; c[1][2] = ne.cellSW; c[1][3] = ne.cellSE;
        c[2][0] = sw.cellNW; c[2][1] = sw.cellNE; c[3][0] = sw.cellSW; c[3][1] = sw.cellSE;
        c[2][2] = se.cellNW; c[2][3] = se.cellNE; c[3][2] = se.cellSW; c[3][3] = se.cellSE;
        return leaf(rule(c, 1, 1), rule(c, 1, 2), rule(c, 2, 1), rule(c, 2, 2));
    }

    private static int rule(int[][] c, int row, int col) {
        int n = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) {
                    n += c[row + dr][col + dc];
                }
            }
        }
        boolean alive = c[row][col] == 1 ? (n == 2 || n == 3) : n == 3;
        return alive ? 1 : 0;
    }
}