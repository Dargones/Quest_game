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
    private ArrayList<Segment> kSeen; 
    private boolean painted;
    private ArrayList<Border> bordersToDisplay;
    private int old;

    /**
     * Basic constructor. Note that the two points are passed to the constructor rather than a singe LineSegment
     * @param sectorOne
     * @param sectorTwo
     * @param p1
     * @param p2
     * @param type
     */
    public Edge(Sector sectorOne, Sector sectorTwo, MyPoint p1, MyPoint p2, EdgeType type) {
        old = 0;
        this.sectorOne = sectorOne;
        this.sectorTwo = sectorTwo;
        kSeen = new ArrayList<>();
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


    public void paint (Graphics g) {
        if (painted)
            return;
        Graphics2D g2 = (Graphics2D) g;
        if (bordersToDisplay.isEmpty()) {
            if (kSeen != null)
                for (Segment keys: kSeen)
                    draw(g2, keys.min, keys.max, true);
            return;
        }
        if (old != bordersToDisplay.size()) {
            System.out.print("interest");
            old = bordersToDisplay.size();
        }
        ArrayList<Segment> toDisplay = squeeze(bordersToDisplay);
        int kInd;
        Segment keys = null;
        ArrayList<Segment> newKSeen = new ArrayList<>(1);
        double newBegin = -1;
        for (kInd = 0; kInd < kSeen.size(); kInd++) {
            keys = kSeen.get(kInd);
            if (toDisplay.get(0).min > keys.min) {
                if (toDisplay.get(0).min < keys.max) {
                    draw(g2, keys.min, toDisplay.get(0).min, true);
                    newBegin = keys.min;
                    break;
                } else {
                    draw(g2, keys.min, keys.max, true);
                    newKSeen.add(keys);
                }
            } else
                break;
        }

        int i = 0;
        while (i < toDisplay.size()) {
            if (newBegin == -1)
                newBegin = toDisplay.get(i).min;
            draw(g2, toDisplay.get(i).min, toDisplay.get(i).max, false);

            if (keys == null) {
                newKSeen.add(new Segment(newBegin, toDisplay.get(i).max));
                newBegin = -1;
            } else {
                do {
                    if (keys.min <= toDisplay.get(i).max) {
                        if (keys.max > toDisplay.get(i).max) {
                            if ((i >= toDisplay.size() - 1) || (toDisplay.get(i + 1).min > keys.max)) {
                                draw(g2, toDisplay.get(i).max, keys.max, true);
                                newKSeen.add(new Segment(newBegin, keys.max));
                                newBegin = -1;
                            } else {
                                draw(g2, toDisplay.get(i).max, toDisplay.get(i + 1).min, true);
                                break;
                            }
                        } else if ((keys.max == toDisplay.get(i).max) && (newBegin != -1)) {
                            newKSeen.add(new Segment(newBegin, toDisplay.get(i).max));
                            newBegin = -1;
                        }
                    } else {
                        if (i >= toDisplay.size() - 1) {
                            draw(g2, keys.min, keys.max, true);
                            newKSeen.add(keys);
                            newBegin = -1;
                        } else if (toDisplay.get(i + 1).min > keys.min) {
                            if (toDisplay.get(i + 1).min < keys.max) {
                                draw(g2, keys.min, toDisplay.get(i + 1).min, true);
                                newBegin = keys.min;
                                break;
                            } else {
                                draw(g2, keys.min, keys.max, true);
                                newKSeen.add(keys);
                            }
                        } else
                            break;
                    }
                    kInd++;
                    if (kInd < kSeen.size())
                        keys = kSeen.get(kInd);
                    else
                        keys = null;
                } while (keys != null);
            }
            i++;
        }
        if (newBegin != -1) {
            if ((kSeen.size() == 0) || (kSeen.get(kSeen.size() - 1).max < toDisplay.get(toDisplay.size() - 1).max))
                newKSeen.add(new Segment(newBegin, toDisplay.get(toDisplay.size() - 1).max));
            else
                newKSeen.add(new Segment(newBegin, kSeen.get(kSeen.size() - 1).max));
        }
        kSeen = newKSeen;
        painted = true;
    }

    /**
     * PRECONDITION norders is not null
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
            bordersToDisplay.add(new Border(k1, true));
            bordersToDisplay.add(new Border(k2, false));
        } else {
            bordersToDisplay.add(new Border(k2, true));
            bordersToDisplay.add(new Border(k1, false));
        }
    }

    public void refreshPainted() {
        painted = false;
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
    }
}
