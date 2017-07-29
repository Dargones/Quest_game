package graph;


import geometry.Geometry;
import geometry.LineSegment;
import geometry.MyPoint;
import geometry.Ray;

import java.awt.*;

/**
 * Created by alexanderfedchin on 4/26/17.
 * Sector is represents a convex polygon
 */
public class Sector {
    public Space parent; // the space that this Sector belongs two
    private Edge[] edges; // the edges that separate this Sector from the others
    public MyPoint center;


    /**
     * Basic constructor. Nothing special
     */
    public Sector(Edge[] edges) {
        this.edges = edges;
        parent = null;
    }

    /**
     * Basic constructor. Nothing special
     */
    public Sector() {
        this(null);
    }

    public Edge[] getEdges() {
        return edges;
    }

    public void setEdges(Edge[] edges) {
        this.edges = edges;
        double x = 0;
        double y = 0;
        for (Edge edge: edges) {
            x += edge.segment.p1.x + edge.segment.p2.x;
            y += edge.segment.p1.y + edge.segment.p2.y;
        }
        center = new MyPoint(x / (2 * edges.length), y / (2 * edges.length));
        for (int i = 0; i < edges.length - 2; i++) {
            int j = 1;
            while ((!edges[i].segment.p1.equals(edges[i + 1].segment.p1))&&(!edges[i].segment.p1.equals(edges[i + 1].segment.p2))
                &&(!edges[i].segment.p2.equals(edges[i + 1].segment.p1))&&(!edges[i].segment.p2.equals(edges[i + 1].segment.p2))) {
                Edge tmp = edges[i + 1];
                edges[i + 1] = edges[i + 1 + j];
                edges[i + 1 + j] = tmp;
                j ++;
            }
        }
    }

    public void calculateDistances() {

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
                if ((edges[i].sectorOne == this)||(Map.mergedIn(this, edges[i].sectorOne)))
                    edges[i].sectorTwo.drawSector(from, radius, radiusSqMinusCoordSq, newRayLeft, newRayRight, edges[i]);
                else
                    edges[i].sectorOne.drawSector(from, radius, radiusSqMinusCoordSq, newRayLeft, newRayRight, edges[i]);
            }
        }
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
