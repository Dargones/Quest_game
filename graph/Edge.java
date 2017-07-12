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
    private static final Color SHADED = new Color(176, 188, 189); // the color that all the shaded edges will be assigned
    public final Sector sectorOne; //one of the two sectors that this edge separates from each other. the order is irrelevant
    public final Sector sectorTwo; //the other sector
    public final LineSegment segment; //corresponding lineSegment
    private EdgeType type;
    private ArrayList<Segment> kSeen; 
    private boolean painted;
    private ArrayList<Border> bordersToDisplay;

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
        kSeen = new ArrayList<>(1);
        segment = new LineSegment(p1, p2);
        setType(type);
        painted = false;
        bordersToDisplay = new ArrayList<>(2);
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


    public void paint (Graphics2D g) {
        if (painted)
            return;
        ArrayList<Segment> toDisplay = squeeze(bordersToDisplay);
        if (toDisplay.size() == 0) {
            for (Segment seen : kSeen)
                draw(g, seen.min, seen.max, true);
            return;
        }
        if (kSeen.size() == 0) {
            for (Segment display : toDisplay)
                draw(g, display.min, display.max, false);
            kSeen = toDisplay;
            return;
        }
        ArrayList<Segment> newKSeen = new ArrayList<>(1);
        double newBegin = -1;
        int i = 0; //iterator for toDisplay
        int j = 0; //iterator for kSeen
        while (i < toDisplay.size()) {
            if (j == kSeen.size()) {
                if (newBegin == -1)
                    newBegin = toDisplay.get(i).min;
                newKSeen.add(new Segment(newBegin, toDisplay.get(i).max));
                newBegin = -1;
                draw(g, toDisplay.get(i).min, toDisplay.get(i).max, false);
                i++;
            } else if (toDisplay.get(i).max < kSeen.get(j).max) {
                if (toDisplay.get(i).max >= kSeen.get(j).min) {
                    if (kSeen.get(j).min < toDisplay.get(i).min) {
                        if (newBegin == -1)
                            newBegin = kSeen.get(j).min;
                        draw(g, kSeen.get(j).min, toDisplay.get(i).min, true);
                    }
                    kSeen.get(j).min = toDisplay.get(i).max;
                    if (newBegin == -1)
                        newBegin = toDisplay.get(i).min;
                } else {
                    if (newBegin == -1)
                        newBegin = toDisplay.get(i).min;
                    newKSeen.add(new Segment(newBegin, toDisplay.get(i).max));
                    newBegin = -1;
                }
                draw(g, toDisplay.get(i).min, toDisplay.get(i).max, false);
                i++;
            } else {
                if (toDisplay.get(i).min >= kSeen.get(j).min) {
                    if (newBegin == -1)
                        newBegin = kSeen.get(j).min;
                    if (toDisplay.get(i).min <= kSeen.get(j).max)
                        kSeen.get(j).max = toDisplay.get(i).min;
                    else {
                        newKSeen.add(new Segment(newBegin, kSeen.get(j).max));
                        newBegin = -1;
                    }
                    draw(g, kSeen.get(j).min, kSeen.get(j).max, true);
                } else {
                    if (newBegin == -1)
                        newBegin = toDisplay.get(i).min;
                }
                j++;
            }
        }
        while (j < kSeen.size()) {
            if (newBegin == -1)
                newBegin = kSeen.get(j).min;
            draw(g, kSeen.get(j).min, kSeen.get(j).max, true);
            newKSeen.add(new Segment(newBegin, kSeen.get(j).max));
            newBegin = -1;
            j++;
        }
        kSeen = newKSeen;
        painted = true;
    }

    /**
     * PRECONDITION borders is not null
     * @param borders
     * @return
     */
    public ArrayList<Segment> squeeze (ArrayList<Border> borders) {
        int i = 0;
        ArrayList<Segment> result = new ArrayList<Segment>(borders.size()/2);
        Collections.sort(borders);
        while (i < borders.size()) {
            int numberOfOpenings = 1;
            int opening = i;
            i ++;
            while (numberOfOpenings != 0) {
                if (borders.get(i).opening)
                    numberOfOpenings++;
                else
                    numberOfOpenings--;
                i ++;
            }
            result.add(new Segment(borders.get(opening).value, borders.get(i - 1).value));
        }
        return result;
    }


    public void draw (Graphics2D g, double k1, double k2, boolean isShaded) {
        if (isShaded)
            g.setColor(SHADED);
        else
            g.setColor(type.color);
        g.setStroke(type.style);

        MyPoint tempMin = new MyPoint(segment.p1.x + (segment.p2.x - segment.p1.x) * k1, segment.p1.y + (segment.p2.y - segment.p1.y) * k1);
        MyPoint tempMax = new MyPoint(segment.p1.x + (segment.p2.x - segment.p1.x) * k2, segment.p1.y + (segment.p2.y - segment.p1.y) * k2);
        g.drawLine(tempMin.x, tempMin.y, tempMax.x, tempMax.y);
    }


    public void update (LineSegment s) {
        if (type == EdgeType.PSEUDO)
            return;
        double k1 = segment.p1.distance(s.p1) / segment.p1.distance(segment.p2);
        double k2 = segment.p1.distance(s.p2) / segment.p1.distance(segment.p2);
        if (k1 < k2) {
            bordersToDisplay.add(new Border(k1, true));
            bordersToDisplay.add(new Border(k2, false));
        } else {
            bordersToDisplay.add(new Border(k2, true));
            bordersToDisplay.add(new Border(k1, false));
        }
    }

    public void refreshPainted(boolean refreshBorders) {
        painted = false;
        if (refreshBorders)
            bordersToDisplay = new ArrayList<>(2);
    }

    private class Border implements Comparable {
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
    
    private class Segment {
        double min;
        double max;
        
        Segment (double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "("+min+","+max+")";
        }
    }

    public static void main (String[] args) {
        //IMPORTANT. Commment all parts of the paint method that involve g, and remove g parameter in order to
        //make these tests work.
        /*Edge edge = new Edge(null, null, new MyPoint(0,0), new MyPoint(100,0), EdgeType.WALL);
        edge.update(new LineSegment(new MyPoint(0, 0), new MyPoint(100, 0)));
        edge.paint();
        Double[] results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 0 is displayed", new String[] {"border0.0", "border0.1"}, new Double[] {0., 1.}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 0), new MyPoint(100, 0)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 1 is displayed", new String[] {"border0.0", "border0.1"}, new Double[] {0., 1.}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 0), new MyPoint(10, 0)));
        edge.update(new LineSegment(new MyPoint(50, 0), new MyPoint(60, 0)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 2 is displayed", new String[] {"border0.0", "border0.1"}, new Double[] {0., 1.}, results);

        edge = new Edge(null, null, new MyPoint(0,0), new MyPoint(0,100), EdgeType.WALL);
        edge.update(new LineSegment(new MyPoint(0, 0), new MyPoint(0, 10)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 3 is displayed", new String[] {"border0.0", "border0.1"}, new Double[] {0., 0.1}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 50), new MyPoint(0, 60)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 4 is displayed", new String[] {"border0.0", "border0.1", "border1.0", "border01.1"},
                new Double[] {0., .1, 0.5, 0.6}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 15), new MyPoint(0, 5)));
        edge.update(new LineSegment(new MyPoint(0, 45), new MyPoint(0, 80)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 5 is displayed", new String[] {"border0.0", "border0.1", "border1.0", "border01.1"},
                new Double[] {0., .15, 0.45, 0.8}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 15), new MyPoint(0, 50)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 6 is displayed", new String[] {"border0.0", "border0.1"},
                new Double[] {0., 0.8}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 15), new MyPoint(0, 50)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 7 is displayed", new String[] {"border0.0", "border0.1"},
                new Double[] {0., 0.8}, results);

        edge = new Edge(null, null, new MyPoint(0,0), new MyPoint(0,100), EdgeType.WALL);
        edge.update(new LineSegment(new MyPoint(0, 18), new MyPoint(0, 34)));
        edge.update(new LineSegment(new MyPoint(0, 47), new MyPoint(0, 48)));
        edge.update(new LineSegment(new MyPoint(0, 64), new MyPoint(0, 80)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 8 is displayed", new String[] {"border0.0", "border0.1", "border1.0", "border1.1",
                "border2.0", "border2.1"}, new Double[] {0.18, 0.34, 0.47, 0.48, 0.64, 0.80}, results);

        edge.refreshPainted();
        edge.update(new LineSegment(new MyPoint(0, 73), new MyPoint(0, 80)));
        edge.paint();
        results = convertionFunctionForDebugPurposes(edge.kSeen);
        Test.testDouble("An edge 9 is displayed", new String[] {"border0.0", "border0.1", "border1.0", "border1.1",
                "border2.0", "border2.1"}, new Double[] {0.18, 0.34, 0.47, 0.48, 0.64, 0.80}, results);

        Test.verdict();*/
    }

    public static Double[] convertionFunctionForDebugPurposes (ArrayList<Segment> list) {
        if ((list == null)||(list.size() == 0))
            return null;
        Double[] result = new Double[list.size() * 2];
        int i = 0;
        for (Segment segment: list) {
            result[i] = segment.min;
            result[i + 1] = segment.max;
            i += 2;
        }
        return result;
    }
}
