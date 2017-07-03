package geometry;

import graph.Test;


/**
 * Created by alexanderfedchin on 7/1/17.
 *
 * This is the class that implements a set of static method, which allow to intersect various geometric elements
 */
public class Intersection {

    /**
     * finds the intersection point of two given lines
     * @param l1
     * @param l2
     * @return
     */
    public static MyPoint intersectLineAndLine(AlgebraicLine l1, AlgebraicLine l2) {
        // the first cases are for the pair of parallel lines
        if ((l1.b != 0) && (l2.b != 0)) {
            if (l1.a / l1.b == l2.a / l2.b)
                return null;
        } else if ((l1.b == 0) && (l2.b == 0))
            return null;
        // the following solution only works if l1.a == 0
        if (l1.a == 0)
            return new MyPoint(round(((l1.c * l2.b) / l1.b - l2.c) / l2.a),round (-l1.c / l1.b));
        // the following is the most general case
        double y = (-l2.c + (l2.a / l1.a) * l1.c) / (- (l2.a / l1.a) * l1.b + l2.b);
        return new MyPoint(round((-l1.b * y - l1.c) / l1.a), round(y));
    }

    /**
     * finds all the points, where a line and a circle intersect
     * @param origin
     * @param radiusSqMinusCoordSq
     * @param line
     * @return
     */
    public static MyPoint[] intersectCircleAndLine(MyPoint origin, double radiusSqMinusCoordSq, AlgebraicLine line) {
        if (line.a != 0) {
            //a, b, anc c that follow are the coefficient in the quadratic equation for y:
            double a = Math.pow(line.b / line.a,2) + 1;
            double b = 2 * (line.b * (line.c / line.a + origin.x) / line.a - origin.y);
            double c = Math.pow(line.c / line.a ,2) - radiusSqMinusCoordSq + 2 * line.c * origin.x / line.a;
            double[] roots = quadraticEquationSolutions(a, b, c);
            if (roots == null)
                return null;
            MyPoint[] result = new MyPoint[roots.length];
            for (int i = 0; i < roots.length; i ++) 
                result[i] = new MyPoint(round(-line.b * roots[i] /line.a - line.c/line.a), round(roots[i]));
            return result;
        } else {
            //b, anc c that follow are the coefficient in the quadratic equation for x (a = 1):
            double b = -2* origin.x;
            double c = -radiusSqMinusCoordSq + Math.pow(line.c / line.b ,2) + 2 * line.c * origin.y / line.b;
            double[] roots = quadraticEquationSolutions(1, b, c);
            if (roots == null)
                return null;
            MyPoint[] result = new MyPoint[roots.length];
            for (int i = 0; i < roots.length; i ++)
                result[i] = new MyPoint(round(roots[i]), round(-line.c / line.b));
            return result;
        }
    }

    
    public static MyPoint[] intersectCircleAndSegment(MyPoint origin, double radiusSqMinusCoordSq, LineSegment s) {
        MyPoint[] hypo = intersectCircleAndLine(origin, radiusSqMinusCoordSq, new AlgebraicLine(s));
        if (hypo == null)
            return null;
        if ((hypo[0].x <= Math.min(s.p1.x, s.p2.x)) || (hypo[0].x >= Math.max(s.p1.x, s.p2.x))) {
            if ((hypo.length > 1)&&((hypo[1].x <= Math.min(s.p1.x, s.p2.x)) || (hypo[1].x >= Math.max(s.p1.x, s.p2.x))))
                //TODO should you check y-s?
                return hypo;
            else
                return new MyPoint[]{hypo[0]};
        } else {
            if ((hypo.length > 1)&&(hypo[1].x <= Math.min(s.p1.x, s.p2.x)) || (hypo[1].x >= Math.max(s.p1.x, s.p2.x)))
                return new MyPoint[]{hypo[1]};
            else
                return null;
        }
    }
    
    
    public static MyPoint intersectSegmentAndSegment(LineSegment s1, LineSegment s2) {
        LineSegment sector = new LineSegment(new MyPoint(Math.max(Math.min(s1.p1.x, s1.p2.x), Math.min(s2.p1.x, s2.p2.x)),
                Math.max(Math.min(s1.p1.y, s1.p2.y), Math.min(s2.p1.y, s2.p2.y))),
                new MyPoint(Math.min(Math.max(s1.p1.x, s1.p2.x), Math.max(s2.p1.x, s2.p2.x)),
                Math.min(Math.max(s1.p1.y, s1.p2.y), Math.max(s2.p1.y, s2.p2.y))));
        if ((sector.p1.x > sector.p2.x) || (sector.p1.y > sector.p2.y))
            return null;
        MyPoint hypo = intersectLineAndLine(new AlgebraicLine(s1), new AlgebraicLine(s2));
        if (hypo == null) return null;
        if ((hypo.x < sector.p1.x) || (hypo.x > sector.p2.x) || (hypo.y < sector.p1.y) || (hypo.y > sector.p2.y))
            return null;
        return hypo;
    }

    public static MyPoint intersectRayAndSegment(Ray r, LineSegment s) {
        MyPoint hypo = intersectLineAndLine(new AlgebraicLine(s), r.line);
        if (hypo == null) return null;
        if ((hypo.x < Math.min(s.p1.x, s.p2.x)) || (hypo.x > Math.max(s.p1.x, s.p2.x)) ||
                (hypo.y < Math.min(s.p1.y, s.p2.y)) || (hypo.y > Math.max(s.p1.y, s.p2.y))|| !r.correctDirection(hypo))
            return null;
        return hypo;
    }


    /**
     * Solves the given quadratic equation ax^2 + bx + c = 0 (in real numbers). Returns the array of solutions or
     * null if there are none
     * @param a should not be equal to zero
     * @param b
     * @param c
     * @return
     */
    private static double[] quadraticEquationSolutions(double a, double b, double c) {
        if (a==0) {
            System.out.println("This is not a quadratic equation");
            return null;
        }
        double discriminant = b * b -4 * a * c;
        if (discriminant > 0) {
            discriminant = Math.sqrt(discriminant);
            return new double[]{(-b + discriminant) / (2 * a), (-b - discriminant) / (2 * a)};
        } else if (discriminant == 0)
            return new double[] {-b / (2 * a)};
        else
            return null;
    }

    /**
     * Rounds double to the nearest integer
     * @param d
     * @return
     */
    private static int round (double d) {
        if (d > 0) return (int) (d + 0.5);
        return (int) (d - 0.5);
    }

    public static void main (String args[]) {
        System.out.println("This is the debug run of Intersection.java");
        System.out.println("Checking the quadraticEquationSolutions method");

        Double[] results = toDoubleArray(quadraticEquationSolutions(1,0,-4));
        Test.testDouble("An equation constructed: x^2 - 4 = 0", new String[] {"root1", "root2"}, new Double[] {2., -2.}, results);

        results = toDoubleArray(quadraticEquationSolutions(1,0,4));
        Test.testDouble("An equation constructed: x^2 + 4 = 0", null, null, results);

        results = toDoubleArray(quadraticEquationSolutions(1,4,3));
        Test.testDouble("An equation constructed: x^2 + 4x + 3 = 0", new String[] {"root1", "root2"}, new Double[] {-1., -3.}, results);

        results = toDoubleArray(quadraticEquationSolutions(2,-8,8));
        Test.testDouble("An equation constructed: 2x^2 - 8x + 8 = 0", new String[] {"root"}, new Double[] {2.}, results);

        results = toDoubleArray(quadraticEquationSolutions(2,-8,16));
        Test.testDouble("An equation constructed: 2x^2 - 8x + 16 = 0", null, null, results);

        results = toDoubleArray(quadraticEquationSolutions(3,18,0));
        Test.testDouble("An equation constructed: 3x^2 + 18x = 0", new String[] {"root1", "root2"}, new Double[] {0., -6.}, results);

        Test.verdict();
        Test.resetBugsCount();
        System.out.println("Checking the intersectLineAndLine method");

        MyPoint[] result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(0,1,3), new AlgebraicLine(0,1,4))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: y + 3 = 0, and y + 4 = 0", null, null, result);

        result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(1,0,-6), new AlgebraicLine(1,0,4325436))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: x - 6 = 0, and x + 4325436 = 0", null, null, result);

        result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(-2,1,-6), new AlgebraicLine(-2,1,4))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: -2x + y - 6 = 0, and -2x + y + 4 = 0", null, null, result);

        result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(0,1,-6), new AlgebraicLine(-0.5,1,0))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: y - 6 = 0, and -x/2 + y = 0", new String[] {"root"}, new MyPoint[] {new MyPoint(12, 6)}, result);

        result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(0,2,4), new AlgebraicLine(-1./7,1,-2))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: 2y + 4 = 0, and -x/7 + y - 2 = 0", new String[] {"root"}, new MyPoint[] {new MyPoint(-28, -2)}, result);

        result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(-3./2,1,-3), new AlgebraicLine(27,3,-135))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: -3x/2 + y - 3 = 0, and 27x + 3y - 135 = 0", new String[] {"root"}, new MyPoint[] {new MyPoint(4, 9)}, result);

        result = new MyPoint[] {intersectLineAndLine(new AlgebraicLine(-3./2,1,-3), new AlgebraicLine(-9./2,1,9))};
        if (result[0] == null) result = null;
        Test.test("Two lines were intersected: -3x/2 + y - 3 = 0, and -9x/2 + y + 9 = 0", new String[] {"root"}, new MyPoint[] {new MyPoint(4, 9)}, result);

        Test.verdict();
        Test.resetBugsCount();
        System.out.println("Checking the intersectCircleAndLine method");

        double radiusSqMinusCoordSq = 400;
        MyPoint origin = new MyPoint(0,0);
        LineSegment segment = new LineSegment(new MyPoint(0,-40), new MyPoint(0,40));
        AlgebraicLine line = new AlgebraicLine(segment);
        MyPoint[] results2 = intersectCircleAndLine(origin, radiusSqMinusCoordSq, line);
        if (results2[0] == null) results2 = null;
        Test.test("A line and a circle were intersected: x = 0, and x^2 + y^2 = 20", new String[] {"root1", "root2"},
                new MyPoint[] {new MyPoint(0, 20), new MyPoint(0, -20)}, results2);

        segment = new LineSegment(new MyPoint(0,-20), new MyPoint(0,10));
        line = new AlgebraicLine(segment);
        results2 = intersectCircleAndLine(origin, radiusSqMinusCoordSq, line);
        if (results2[0] == null) results2 = null;
        Test.test("A line and a circle were intersected: x = 0, and x^2 + y^2 = 20", new String[] {"root1", "root2"},
                new MyPoint[] {new MyPoint(0, 20), new MyPoint(0, -20)}, results2);

        segment = new LineSegment(new MyPoint(-20,-80), new MyPoint(-20,80));
        line = new AlgebraicLine(segment);
        results2 = intersectCircleAndLine(origin, radiusSqMinusCoordSq, line);
        if (results2[0] == null) results2 = null;
        Test.test("A line and a circle were intersected: x = -20, and x^2 + y^2 = 20", new String[] {"root"},
                new MyPoint[] {new MyPoint(-20, 0)}, results2);

        radiusSqMinusCoordSq = 25;
        segment = new LineSegment(new MyPoint(0,0), new MyPoint(6,8));
        line = new AlgebraicLine(segment);
        results2 = intersectCircleAndLine(origin, radiusSqMinusCoordSq, line);
        if (results2[0] == null) results2 = null;
        Test.test("A line and a circle were intersected: y = 4x/3, and x^2 + y^2 = 5", new String[] {"root1", "root2"},
                new MyPoint[] {new MyPoint(3, 4), new MyPoint(-3, -4)}, results2);

        Test.verdict();
        Test.resetBugsCount();
    }

    /**
     * A method complementary to the testing mechanism that allows the convertion of a double[] to a Double[]
     * @param a
     * @return
     */
    private static Double[] toDoubleArray(double[] a) {
        if (a == null)
            return null;
        Double[] result = new Double[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = a[i];
        return result;
    }
}
