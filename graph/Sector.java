package graph;

import geometry.Intersection;
import geometry.LineSegment;
import geometry.MyPoint;
import geometry.Ray;

/**
 * Created by alexanderfedchin on 4/26/17.
 * Sector is represents a convex polygon
 */
public class Sector {
    private static int max_index = 0;
    public int index;
    private Space parent;
    public Edge[] edges;

    public Sector(Edge[] edges, Space parent) {
        this.index = max_index;
        max_index ++;
        this.edges = edges;
        this.parent = parent;
    }

    private LineSegment checkDistanceOfSight (LineSegment initial, MyPoint from, double radius, double radiusSqMinusCoordSq) {
        if (from.distance(initial.p1) <= radius) {
            if (from.distance(initial.p2) <= radius)
                return initial;
            else {
                MyPoint[] newPoints = Intersection.intersectCircleAndSegment(from, radiusSqMinusCoordSq, initial);
                return new LineSegment(initial.p1, newPoints[0]);
            }
        } else {
            if (from.distance(initial.p2) <= radius) {
                MyPoint[] newPoints = Intersection.intersectCircleAndSegment(from, radiusSqMinusCoordSq, initial);
                return new LineSegment(initial.p2, newPoints[0]);
            } else {
                MyPoint[] newPoints = Intersection.intersectCircleAndSegment(from, radiusSqMinusCoordSq, initial);
                if ((newPoints != null) &&(newPoints.length == 2))
                    return new LineSegment(newPoints[0], newPoints[1]);
                else
                    return null;
            }
        }
    }

    private LineSegment checkAreaOfSight (LineSegment segment, MyPoint from, Ray rayLeft, Ray rayRight) {
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

    public void drawSector(MyPoint from, double radius, Ray rayLeft, Ray rayRight) {
        drawSector(from, radius, radius * radius - from.x * from.x - from .y * from.y, rayLeft, rayRight, null);
    }
    
    private void drawSector(MyPoint from, double radius, double radiusSqMinusCoordSq, Ray rayLeft, Ray rayRight, Edge previous) {
        for (int i = 0; i < edges.length; i++) {
            LineSegment segment = checkDistanceOfSight(edges[i].segment, from, radius, radiusSqMinusCoordSq);
            if (segment == null) continue;
            segment = checkAreaOfSight(segment, from, rayLeft, rayRight);
            if (segment == null) continue;
            Ray newRayLeft = new Ray(from, segment.p1);
            Ray newRayRight = new Ray(from, segment.p2);

            if (edges[i].getType() != EdgeType.PSEUDO)
                edges[i].update(segment);

            if ((edges[i].getType().visThrough)&&(edges[i] != previous)) {
                if (edges[i].sectorOne == this)
                    edges[i].sectorTwo.drawSector(from, radius, radiusSqMinusCoordSq, newRayLeft, newRayRight, edges[i]);
                else
                    edges[i].sectorOne.drawSector(from, radius, radiusSqMinusCoordSq, newRayLeft, newRayRight, edges[i]);
            }
        }
    }


    public Sector sectorChange(MyPoint oldPosition, MyPoint newPosition) {
        return sectorChange(oldPosition, newPosition, null);
    }

    public Sector sectorChange(MyPoint oldPosition, MyPoint newPosition, Edge previous) {
        for (int i = 0; i<  edges.length; i ++) {
            if (edges[i] == previous)
                continue;
            MyPoint intersection = Intersection.intersectSegmentAndSegment(edges[i].segment, new LineSegment(oldPosition, newPosition));
            if (intersection == null)
                continue;
            if ((!intersection.equals(oldPosition)) && (!intersection.equals(newPosition))) {
                if (edges[i].sectorOne == this) {
                    if ((edges[i].sectorTwo == null)|| !edges[i].getType().goThrough)
                        return null;
                    else
                        return edges[i].sectorTwo.sectorChange(oldPosition,newPosition,edges[i]);
                } else {
                    if ((edges[i].sectorOne == null)|| !edges[i].getType().goThrough)
                        return null;
                    else
                        return edges[i].sectorOne.sectorChange(oldPosition,newPosition,edges[i]);
                }
            } else
                return null;
        }
        return this;
    }
}
