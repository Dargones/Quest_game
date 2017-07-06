package geometry;

/**
 * Created by alexanderfedchin on 7/1/17.
 *
 * LineSegment represents a segment of a line bound by two points. The line segment is immutable
 */
public class LineSegment {
    public final MyPoint p1, p2; //the two ends of the segment. The order is irrelevant


    /**
     * Basic constructor
     * @param p1
     * @param p2
     */
    public LineSegment(MyPoint p1, MyPoint p2){
        //if (p1.equals(p2))
            // System.out.println("Warning: a sector of length zero created");
        this.p1 = p1;
        this.p2 = p2;
    }
}
