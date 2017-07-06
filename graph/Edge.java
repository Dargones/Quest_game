package graph;

import geometry.LineSegment;
import geometry.MyPoint;

import java.awt.*;
import java.util.*;


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
    private double kmin, kmax; //TODO: Handle a case than the two parts of teh edge were shown, but what remains in between was not
    private boolean painted;
    private ArrayList<Border> ks;

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
        kmin = 1.;
        kmax = 0.;
        segment = new LineSegment(p1, p2);
        setType(type);
        painted = false;
        ks = new ArrayList<>(2);
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

    public void paint (Graphics g) {
        if (painted)
            return;
        Graphics2D g2 = (Graphics2D) g;
        if (ks.isEmpty()) {
            if (kmin < kmax)
                draw(g2, kmin, kmax, true);
            return;
        }

        Collections.sort(ks);

        int i = 0;
        if (ks.get(i).value > kmin) {
            if (ks.get(i).value < kmax)
                draw(g2, kmin, ks.get(i).value, true);
            else
                draw(g2, kmin, kmax, true);
        }

        while (i < ks.size()) {
            int numberOfOpenings = 1;
            int opening = i;
            i ++;

            while (numberOfOpenings != 0) {
                if (ks.get(i).opening)
                    numberOfOpenings++;
                else
                    numberOfOpenings--;
                i ++;
            }
            draw(g2, ks.get(opening).value, ks.get(i - 1).value, false);

            if ((ks.get(i - 1).value >= kmin) && (ks.get(i -1).value < kmax)) {
                if (i < ks.size())
                    draw(g2, ks.get(i - 1).value, ks.get(i).value, true);
                else
                    draw(g2, ks.get(i - 1).value, kmax, true);
            }
        }
        if (ks.get(0).value < kmin)
            kmin = ks.get(0).value;
        if (ks.get(ks.size() - 1).value > kmax)
            kmax = ks.get(ks.size() - 1).value;
        painted = true;
    }

    public void draw (Graphics2D g2, double k1, double k2, boolean isShaded) {
        if (isShaded)
            g2.setColor(new Color(176, 188, 189));
        else if (type == EdgeType.DOOR_OPENED)
            g2.setColor(new Color(74, 146, 92));
        else
            g2.setColor(Color.BLACK);
        if (type.visThrough && !type.goThrough)
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.f, new float[] {4.f}, 0.f));
        else
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        MyPoint tempMin = new MyPoint(segment.p1.x + (segment.p2.x - segment.p1.x) * k1, segment.p1.y + (segment.p2.y - segment.p1.y) * k1);
        MyPoint tempMax = new MyPoint(segment.p1.x + (segment.p2.x - segment.p1.x) * k2, segment.p1.y + (segment.p2.y - segment.p1.y) * k2);
        g2.drawLine(tempMin.x, tempMin.y, tempMax.x, tempMax.y);
    }


    public void update (LineSegment s) {
        double k1 = segment.p1.distance(s.p1) / segment.p1.distance(segment.p2);
        double k2 = segment.p1.distance(s.p2) / segment.p1.distance(segment.p2);
        if (k1 < k2) {
            ks.add(new Border(k1, true));
            ks.add(new Border(k2, false));
        } else {
            ks.add(new Border(k2, true));
            ks.add(new Border(k1, false));
        }
    }

    public void refreshPainted() {
        painted = false;
        ks = new ArrayList<>(2);
    }

    private class Border implements Comparable{
        Double value;
        boolean opening;

        Border(Double value, boolean opening) {
            this.value = value;
            this.opening = opening;
        }

        @Override
        public int compareTo(Object o) {
            return value.compareTo(((Border) o).value);
        }
    }
}
