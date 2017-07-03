package graph;

import geometry.LineSegment;
import geometry.MyPoint;


/**
 * Created by alexanderfedchin on 4/26/17.
 *
 * An edge is a line segment that is of certain type (see EdgeType.java) and separates two sectors. Only the edge type
 * can be modified, everything else remains constant
 */
public class Edge {
    public final Sector sectorOne; //one of the two sectors that this edge separates from each other. the order is irrelevant
    public final Sector sectorTwo; //the other sector
    public final LineSegment segment; //corresponding lineSegment
    private EdgeType type;

    /**
     * Basic constructor. Note that the two points are passed to the constructor rather than a singe LineSegment
     * @param sectorOne
     * @param sectorTwo
     * @param p1
     * @param p2
     * @param type
     */
    public Edge(Sector sectorOne, Sector sectorTwo, MyPoint p1, MyPoint p2, EdgeType type) {
        this.sectorOne = sectorOne;
        this.sectorTwo = sectorTwo;
        segment = new LineSegment(p1, p2);
        setType(type);
    }

    /**
     * Sets the type of an edge
     * @param type
     */
    public void setType(EdgeType type) {
        this.type = type;
    }

    /**
     * returns the current state (type) of an edge
     * @return
     */
    public EdgeType getType() {
        return type;
    }
}
