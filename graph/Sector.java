package graph;

import geometry.*;

import java.awt.*;

/**
 * Created by alexanderfedchin on 4/26/17.
 * Sector is represents a convex polygon
 */
public class Sector {
    public Space parent; // the space that this Sector belongs two
    private Edge[] edges; // the edges that separate this Sector from the others

    /**
     * Basic constructor. Nothing special
     */
    public Sector() {
        edges = null;
        parent = null;
    }

    public void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    public void setParent(Edge[] edges) {
        this.edges = edges;
    }

    public void drawSector(MyPoint from, double radius, Ray rayLeft, Ray rayRight) {
        drawSector(from, radius, radius * radius - from.x * from.x - from .y * from.y, rayLeft, rayRight, null);
    }
    
    private void drawSector(MyPoint from, double radius, double radiusSqMinusCoordSq, Ray rayLeft, Ray rayRight, Edge previous) {
        for (int i = 0; i < edges.length; i++) {
            if (edges[i] == previous)
                continue;
            LineSegment segment = Geometry.checkDistanceOfSight(edges[i].segment, from, radius, radiusSqMinusCoordSq);
            if (segment == null) continue;
            segment = Geometry.checkAreaOfSight(segment, from, rayLeft, rayRight);
            if (segment == null) continue;
            Ray newRayLeft = new Ray(from, segment.p1);
            Ray newRayRight = new Ray(from, segment.p2);

            edges[i].update(segment);

            if (edges[i].getType().visThrough) {
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
        //TODO Allow being inside two or more sectors at once
        //TODO Manage Stackoverflow error that appears here
        for (int i = 0; i<  edges.length; i ++) {
            if (edges[i] == previous)
                continue;
            MyPoint intersection = Intersection.intersectSegmentAndSegment(edges[i].segment, new LineSegment(oldPosition, newPosition));
            if (intersection == null)
                continue;
            if (intersection.equals(newPosition))
                return null;
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
        }
        return this;
    }

    public void paint(Graphics2D g) {
        for (Edge edge: edges)
            edge.paint(g);
    }

    public void refreshPainted(boolean refreshBorders) {
        for (Edge edge: edges)
            edge.refreshPainted(refreshBorders);
    }
}
