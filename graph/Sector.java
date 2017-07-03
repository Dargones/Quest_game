package graph;

import geometry.Intersection;
import geometry.LineSegment;
import geometry.MyPoint;
import geometry.Ray;

import java.awt.Graphics;

/**
 * Created by alexanderfedchin on 4/26/17.
 * Sector is represents a convex polygon
 */
public class Sector {
    private Space parent;
    private Edge[] edges;

    public Sector(Edge[] edges, Space parent) {
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
        if (newRayLeft.isIn(rayLeft.angle,rayRight.angle)) {
            if (newRayRight.isIn(rayLeft.angle, rayRight.angle)) {
                if (newRayRight.isIn(newRayLeft.angle, rayRight.angle))
                    return  segment;
                return new LineSegment(segment.p2, segment.p1);
            } else {
                MyPoint rightPoint = Intersection.intersectRayAndSegment(rayRight, segment);
                //if (rightPoint != null)
                    return new LineSegment(segment.p1,rightPoint);
                //return new LineSegment(Intersection.intersectRayAndSegment(rayLeft, segment), segment.p1);
            }
        } else {
            MyPoint leftPoint = Intersection.intersectRayAndSegment(rayLeft, segment);
            if (newRayRight.isIn(rayLeft.angle, rayRight.angle)) {
                //if (leftPoint != null)
                    return new LineSegment(leftPoint,segment.p2);
                //return new LineSegment(segment.p2, Intersection.intersectRayAndSegment(rayRight, segment));
            } else {
                if (leftPoint != null) {
                    MyPoint rightPoint = Intersection.intersectRayAndSegment(rayRight, segment);
                    if (rightPoint != null) //TODO Should you check this?
                        return new LineSegment(leftPoint, Intersection.intersectRayAndSegment(rayRight, segment));
                }
                return null;
            }
        }
    }

    public void drawSector(Graphics g, MyPoint from, double radius, Ray rayLeft, Ray rayRight) {
        drawSector(g, from, radius, radius * radius - from.x * from.x - from .y * from.y, rayLeft, rayRight, null);
    }
    
    private void drawSector(Graphics g, MyPoint from, double radius, double radiusSqMinusCoordSq, Ray rayLeft, Ray rayRight, Sector previous) {
        for (int i = 0; i < edges.length; i++) {
            LineSegment segment = checkDistanceOfSight(edges[i].segment, from, radius, radiusSqMinusCoordSq);
            if (segment == null) continue;
            segment = checkAreaOfSight(segment, from, rayLeft, rayRight);
            if (segment == null) continue;
            Ray newRayLeft = new Ray(from, segment.p1);
            Ray newRayRight = new Ray(from, segment.p2);

            g.drawLine(segment.p1.x, segment.p1.y, segment.p2.x, segment.p2.y);

            if (edges[i].getType().visThrough == true) {
                if (edges[i].sectorOne == this) {
                    if ((edges[i].sectorTwo != previous) && (edges[i].sectorTwo != null))
                        edges[i].sectorTwo.drawSector(g, from, radius, radiusSqMinusCoordSq, newRayLeft, newRayRight, this);
                } else if ((edges[i].sectorOne != previous) && (edges[i].sectorOne != null)) {
                    edges[i].sectorOne.drawSector(g, from, radius, radiusSqMinusCoordSq, newRayLeft, newRayRight, this);
                }
            }
        }
    }


    public Sector sectorChange(MyPoint oldPosition, MyPoint newPosition) {
        return sectorChange(oldPosition, newPosition, null);
    }

    public Sector sectorChange(MyPoint oldPosition, MyPoint newPosition, Sector previous) {
        for (int i = 0; i< edges.length; i ++) {
            if (Intersection.intersectSegmentAndSegment(edges[i].segment, new LineSegment(oldPosition, newPosition)) != null) {
                if (edges[i].sectorOne == this) {
                    if (edges[i].sectorTwo == null)
                        return null;
                    else if (edges[i].sectorTwo != previous)
                        return edges[i].sectorTwo.sectorChange(oldPosition,newPosition,this);
                } else {
                    if (edges[i].sectorOne == null)
                        return null;
                    else if (edges[i].sectorOne != previous)
                        return edges[i].sectorOne.sectorChange(oldPosition,newPosition,this);
                }
            }
        }
        return this;
    }
}
