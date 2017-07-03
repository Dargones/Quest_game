package geometry;


import graph.Test;


/**
 * Created by alexanderfedchin on 7/1/17.
 *
 * A class that represents a ray in space. A ray has certain direction, which is given by the angle, the origin, and the
 * line that it lies in. Rays are immutable.
 */
public class Ray {
    public final AlgebraicLine line;
    public final MyPoint origin;
    public final double angle; //an angle in radians [-PI/2, 3PI/2) that sets the direction of the vector


    /**
     * Basic constructor. Takes two points, and calculates all the rest (teh angle and line parameters)
     * @param origin
     * @param example
     */
    public Ray (MyPoint origin, MyPoint example) {
        this.line = new AlgebraicLine(origin, example);
        this.origin = origin;
        //calculating the angle corresponding to this Ray
        if (line.b == 0) {
            if (example.y > origin.y)
                angle =  Math.PI / 2;
            else
                angle = -Math.PI / 2;
        } else {
            if (example.x > origin.x)
                angle = Math.atan(-line.a / line.b);
            else
                angle = Math.atan(-line.a / line.b) + Math.PI;
        }
    }


    /**
     * Another version of the constructor that only takes an angle and the point of origin
     * @param angle
     * @param origin
     */
    public Ray(double angle, MyPoint origin) {
        // it is at first necessary to ensure that the angle is within [-PI/2, 3PI/2)
        while (angle >= 3* Math.PI/2)
            angle -= Math.PI* 2;
        while (angle < -Math.PI/2)
            angle += Math.PI *2;
        this.origin = origin;
        this.angle = angle;

        if ((angle == Math.PI/2) || (angle == -Math.PI/2))
            line = new AlgebraicLine(1, 0 ,-origin.x);
        else {
            double tangent = Math.tan(angle);
            line = new AlgebraicLine(-tangent, 1, tangent * origin.x - origin.y);
        }
    }

    /**
     * Another constructor that allows one to construct a ray from a line segment, where the first point of the line
     * segment is the origin, and the second one is just an arbitrary point along the ray.
     * @param s
     */
    public Ray(LineSegment s) {
        this(s.p1, s.p2);
    }


    /**
     * Takes a point that is along the line associated with this ray (note that this is a PRECONDITION), and checks,
     * whether this point is in the right direction (i.e. whether it is along the ray). If the point is the origin,
     * returns true
     * @param pointToCheck
     * @return
     */
    public boolean correctDirection (MyPoint pointToCheck) {
        if (pointToCheck.equals(origin)) return true;
        if ((angle <= -Math.PI / 4) || (angle > 5 * Math.PI / 4))
            return pointToCheck.y < origin.y;
        else if (angle < Math.PI / 4)
            return pointToCheck.x > origin.x;
        else if (angle < 3 * Math.PI / 4)
            return pointToCheck.y > origin.y;
        else
            return pointToCheck.x < origin.x;
    }

    /**
     * Checks if one angle is between the other two. The two angles should be given such that the arc they create is
     * from the rightAngle towards the leftAngle with increasing angleValue. That said, rightAngle may only be greater
     * than the leftAngle, if the arc they restrict contains -PI/2.
     * @param leftAngle
     * @param rightAngle
     * @return
     */
    public boolean isIn(double leftAngle, double rightAngle) {
        if (leftAngle > rightAngle)
            return (angle <= leftAngle) && (angle >= rightAngle);
        else return (angle <= leftAngle) || (angle >= rightAngle); // in this second case the arc restricted by
        //the angles contains -PI/2
    }

    /**
     * debugging
     * @param args
     */
    public static void main(String args[]) {
        System.out.println("This is the debug run of Ray.java");
        MyPoint origin = new MyPoint(0,0);
        boolean noBugs = true;
        Ray one = new Ray(Math.PI/2, origin);
        Test.testDouble("A ray created directed to PI/2", new String[] {"a", "b", "c", "angle"}, new Double[] {1., 0., 0., Math.PI/2},
                new Double[] {one.line.a, one.line.b, one.line.c, one.angle});

        one = new Ray(3*Math.PI/2, origin);
        Test.testDouble("A ray created directed to -PI/2", new String[] {"a", "b", "c", "angle"}, new Double[] {1., 0., 0., -Math.PI/2},
                new Double[] {one.line.a, one.line.b, one.line.c, one.angle});

        one = new Ray(Math.PI, origin);
        Test.testDouble("A ray created directed to PI", new String[] {"a", "b", "c", "angle"}, new Double[] {0., 1., 0., Math.PI},
                new Double[] {one.line.a, one.line.b, one.line.c, one.angle});

        one = new Ray(0, origin);
        Test.testDouble("A ray created directed to -PI", new String[] {"a", "b", "c", "angle"}, new Double[] {0., 1., 0., 0.},
                new Double[] {one.line.a, one.line.b, one.line.c, one.angle});

        one = new Ray(Math.PI/6, origin);
        Test.testDouble("A ray created directed to PI/6", new String[] {"a", "b", "c", "angle"}, new Double[] {-1/Math.sqrt(3), 1., 0., Math.PI/6},
                new Double[] {one.line.a, one.line.b, one.line.c, one.angle});

        origin = new MyPoint((int)(Math.sqrt(3)* 1000000),9000000);
        one = new Ray(-Math.PI*7/3, origin);
        Test.testDouble("A ray created directed to -PI/3", new String[] {"a", "b", "c", "angle"}, new Double[] {Math.sqrt(3), 1., -12000000., -Math.PI/3},
                new Double[] {one.line.a, one.line.b, one.line.c, one.angle});
        
        Test.verdict();
    }
}
