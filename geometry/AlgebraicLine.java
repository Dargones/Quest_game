package geometry;


import graph.Test;

/**
 * Created by alexanderfedchin on 7/1/17.
 *
 * represents a line ax+by+c=0. AlgebraicLine is immutable
 */
public class AlgebraicLine {
    public final double a, b, c;

    /**
     * Basic constructor
     * @param a
     * @param b
     * @param c
     */
    public AlgebraicLine (double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * A constructor that takes to point and creates a line from them
     * @param p1
     * @param p2
     */
    public AlgebraicLine (MyPoint p1, MyPoint p2) {
        if (p1.x == p2.x) {
            a = 1;
            b = 0;
            c = -p1.x;
        } else {
            a = (double) (p2.y - p1.y) / (p1.x - p2.x);
            b = 1;
            c = -a * p1.x - p1.y;
        }
    }

    /**
     * A version of the previous constructor that takes a line segment instead of the two points as a parameter
     * @param segment
     */
    public AlgebraicLine (LineSegment segment) {
        this(segment.p1, segment.p2);
    }
    
    public static void main(String args[]) {
        System.out.println("This is the debug run of AlgebraicLine.java");
        AlgebraicLine line = new AlgebraicLine(new MyPoint(50,20), new MyPoint(50,30));
        Test.testDouble("A line created from the points (50,20) and (50,30)", new String[] {"a", "b", "c"},
                new Double[] {1., 0., -50.}, new Double[] {line.a, line.b, line.c});

        line = new AlgebraicLine(new MyPoint(20,50), new MyPoint(30,50));
        Test.testDouble("A line created from the points (20,50) and (30,50)", new String[] {"a", "b", "c"},
                new Double[] {0., 1., -50.}, new Double[] {line.a, line.b, line.c});

        line = new AlgebraicLine(new MyPoint(-30,30), new MyPoint(30,-30));
        Test.testDouble("A line created from the points (-30,30) and (30,-30)", new String[] {"a", "b", "c"},
                new Double[] {1., 1., 0.}, new Double[] {line.a, line.b, line.c});

        line = new AlgebraicLine(new MyPoint(0,0), new MyPoint(40,60));
        Test.testDouble("A line created from the points (0,0) and (40,60)", new String[] {"a", "b", "c"},
                new Double[] {-3./2, 1., 0.}, new Double[] {line.a, line.b, line.c});

        line = new AlgebraicLine(new MyPoint(0,20), new MyPoint(40,80));
        Test.testDouble("A line created from the points (0,20) and (40,80)", new String[] {"a", "b", "c"},
                new Double[] {-3./2, 1., -20.}, new Double[] {line.a, line.b, line.c});

        Test.verdict();
    }
}
