package geometry;

/**
 * Created by alexanderfedchin on 7/1/17.
 *
 * A class that represents a point in a 2D Space. The point is immutable
 */
public class MyPoint {
    public final int x, y;

    /**
     * Basic constructor
     * @param x
     * @param y
     */
    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Basic constructor
     * @param x
     * @param y
     */
    public MyPoint(double x, double y) {
        this.x = round(x);
        this.y = round(y);
    }

    /**
     * Euclidean distance
     * @param otherPoint
     * @return
     */
    public double distance(MyPoint otherPoint) {
        return Math.sqrt(Math.pow(x - otherPoint.x, 2)+Math.pow(y - otherPoint.y, 2));
    }

    /**
     * For the purposes of debug
     * @return
     */
    public String toString() {
        return "("+x+","+y+")";
    }

    /**
     * The two points are equal only if the corresponding coordinates are equal. If the other object is null,
     * return false
     * @param obj
     * @return
     */
    public boolean equals(Object obj){
        if (obj == null)
            return false;
        MyPoint otherPoint = (MyPoint)obj;
        return ((this.x == otherPoint.x)&&(this.y == otherPoint.y));
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
}
