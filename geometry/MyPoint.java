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
     * Euclidean distance
     * @param otherPoint
     * @return
     */
    public double distance(MyPoint otherPoint) {
        return Math.sqrt((double)(x * x + y * y));
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

}
