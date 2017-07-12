package geometry;

/**
 * Created by alexanderfedchin on 7/12/17.
 *
 * Includes a number of more specific geometry methods used by other classes.
 */
public class Geometry {

    /**
     * Returns the part of the given Segment that is within the radius of sight from a given point
     * @param segment              the segment to view
     * @param from                 point from which this segment is to be viewed
     * @param radius               the radius of view
     * @param radiusSqMinusCoordSq radius of the circle squared minus the squared coordinates of the origin. (It makes
     *                             sense to precalculate this value if the same circle is to be intersected multiple
     *                             times). Example: if the circle has the radius 5 and the origin (4,6), then this
     *                             parameter should be equal to 25 - (16 + 36) = -27
     * @return
     */
    public static LineSegment checkDistanceOfSight (LineSegment segment, MyPoint from, double radius, double radiusSqMinusCoordSq) {
        if (from.distance(segment.p1) <= radius) {
            if (from.distance(segment.p2) <= radius)
                return segment;
            else {
                MyPoint[] newPoints = Intersection.intersectCircleAndSegment(from, radiusSqMinusCoordSq, segment);
                return new LineSegment(segment.p1, newPoints[0]);
            }
        } else {
            if (from.distance(segment.p2) <= radius) {
                MyPoint[] newPoints = Intersection.intersectCircleAndSegment(from, radiusSqMinusCoordSq, segment);
                return new LineSegment(segment.p2, newPoints[0]);
            } else {
                MyPoint[] newPoints = Intersection.intersectCircleAndSegment(from, radiusSqMinusCoordSq, segment);
                if ((newPoints != null) &&(newPoints.length == 2))
                    return new LineSegment(newPoints[0], newPoints[1]);
                else
                    return null;
            }
        }
    }

    /**
     * Returns the part of the segment that is within the sector of view. rayLeft and rayRight are the two boundaries
     * of this sector with the sector covered if going from rayRight to rayLeft by increasing the angle
     * @param segment
     * @param from
     * @param rayLeft
     * @param rayRight
     * @return
     */
    public static LineSegment checkAreaOfSight (LineSegment segment, MyPoint from, Ray rayLeft, Ray rayRight) {
        Ray newRayLeft = new Ray(from, segment.p1);
        Ray newRayRight = new Ray(from, segment.p2);
        MyPoint leftPoint = Intersection.intersectRayAndSegment(rayLeft, segment);
        MyPoint rightPoint = Intersection.intersectRayAndSegment(rayRight, segment);
        if (leftPoint != null) {
            if (leftPoint.equals(segment.p1))
                newRayLeft = rayLeft;
            else if (leftPoint.equals(segment.p2))
                newRayRight = rayLeft;
        }
        if (rightPoint != null) {
            if (rightPoint.equals(segment.p1))
                newRayLeft = rayRight;
            else if (rightPoint.equals(segment.p2))
                newRayRight = rayRight;
        }
        if (newRayLeft.isIn(rayLeft.angle,rayRight.angle)) {
            if (newRayRight.isIn(rayLeft.angle, rayRight.angle)) {
                if (newRayRight.isIn(newRayLeft.angle, rayRight.angle))
                    return  segment;
                return new LineSegment(segment.p2, segment.p1);
            } else {
                if ((rightPoint != null) && (!rightPoint.equals( segment.p1))) {
                    return new LineSegment(segment.p1, rightPoint);
                } else {
                    if ((leftPoint == null) || leftPoint.equals( segment.p1))
                        return null;
                    return new LineSegment(leftPoint, segment.p1);
                }
            }
        } else {
            if (newRayRight.isIn(rayLeft.angle, rayRight.angle)) {
                if ((leftPoint != null) && (!leftPoint.equals(segment.p2))) {
                    return new LineSegment(leftPoint, segment.p2);
                } else {
                    if ((rightPoint == null) || rightPoint.equals(segment.p2))
                        return null;
                    return new LineSegment(segment.p2, rightPoint);
                }
            } else {
                if (leftPoint != null)
                    return new LineSegment(leftPoint, rightPoint);
                return null;
            }
        }
    }

}
