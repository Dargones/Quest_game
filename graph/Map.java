package graph;

import geometry.Intersection;
import geometry.LineSegment;
import geometry.MyPoint;
import geometry.Ray;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alexanderfedchin on 7/12/17.
 *
 * Class that unites everything that is to be
 */
public class Map {
    public static double VIEW_RADIUS = 700;
    public static double ANGLE_DIFFERENCE = Math.PI/2;
    private MyPoint position;
    private ArrayList<Edge> homeEdges; // the array of all the sectors you are currently in. There may be several of
    //these, but only in the case if you are standing on an edge. Hence, this information is stored in Edge-Sector pairs.
    private Sector home;
    private Space rootSpace;
    private MyPoint mouse;
    private boolean somethingMoved; //if this variable is false, then there is no need to recalculate all the intersections etc.
    public static FileReader fileR;
    public static BufferedReader fileBR;
    public static Sector tempSector; //if the player stays on the vertex, then the sectors around are merged into one for
    //efficiency purposes
    public static Sector[] mergedSectors; //these are the Sectors merged

    public Map(String[] lines) {
        int i = 1;
        MyPoint[] pts = new MyPoint[linesBeforeNextBreak(lines, i)];
        for (i = 1; i < pts.length + 1; i++) {
            String[] coord = lines[i].split(" ");
            pts[i - 1] = new MyPoint(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
        }

        i += 2;
        Sector[] s = new Sector[Integer.parseInt(lines[i])];
        Edge[][] edges = new Edge[s.length][];
        for (int j = 0; j < s.length; j++) {
            s[j] = new Sector();
        }

        i += 3;
        for (int j = 0; j < edges.length; j++) {
            edges[j] = new Edge[linesBeforeNextBreak(lines, i)];
            for (int k = 0; k < edges[j].length; k++) {
                String[] data = lines[i].split(" ");
                if (data.length == 2)
                    edges[j][k] = edges[Integer.parseInt(data[0])][Integer.parseInt(data[1])];
                else {
                    for (EdgeType type: EdgeType.values())
                        if (type.name.equals(data[4])) {
                            if (Integer.parseInt(data[0]) == -1)
                                edges[j][k] = new Edge(null, s[Integer.parseInt(data[1])],
                                    pts[Integer.parseInt(data[2])], pts[Integer.parseInt(data[3])],type);
                            else if (Integer.parseInt(data[1]) == -1)
                                edges[j][k] = new Edge(s[Integer.parseInt(data[0])], null,
                                        pts[Integer.parseInt(data[2])], pts[Integer.parseInt(data[3])],type);
                            else
                                edges[j][k] = new Edge(s[Integer.parseInt(data[0])], s[Integer.parseInt(data[1])],
                                        pts[Integer.parseInt(data[2])], pts[Integer.parseInt(data[3])],type);
                            break;
                        }

                }
                i += 1;
            }
            i += 1;
        }
        for (int j = 0; j < s.length; j++) {
            s[j].setEdges(edges[j]);
        }

        i += 1;
        Space[] spaces = new Space[linesBeforeNextBreak(lines, i)];
        int standAloneSpaces = spaces.length;
        int standAloneSectors = s.length;
        for (int j = 0; j < spaces.length; j++) {
            String[] parts = lines[i].split(";");
            String[] part1 = parts[0].split(" ");
            String[] part2 = parts[1].split(" ");
            Sector[] sectors;
            Space[] children;
            if (part1[1].equals("null"))
                sectors = null;
            else {
                sectors = new Sector[part1.length - 1];
                standAloneSectors -= sectors.length;
                for (int k = 1; k < part1.length; k ++)
                    sectors[k - 1] = s[Integer.parseInt(part1[k])];
            }
            if (part2[0].equals("null"))
                children = null;
            else {
                children = new Space[part2.length];
                standAloneSpaces -= children.length;
                for (int k = 0; k < part2.length; k ++)
                    children[k] = spaces[Integer.parseInt(part2[k])];
            }
            spaces[j] = new Space(part1[0], sectors, children);
            i += 1;
        }

        Sector[] sectors = new Sector[standAloneSectors];
        Space[] children = new Space[standAloneSpaces];
        for (int j = 0; j < spaces.length; j ++)
            if (spaces[j].parent == null) {
                standAloneSpaces --;
                children[standAloneSpaces] = spaces[j];
            }
        for (int j = 0; j < s.length; j ++)
            if (s[j].parent == null) {
                standAloneSectors --;
                sectors[standAloneSectors] = s[j];
            }
        rootSpace = new Space(null, sectors, children);

        i += 2;
        String[] coord = lines[i].split(" ");
        position = new MyPoint(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
        i += 2;
        coord = lines[i].split(" ");
        mouse = new MyPoint(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
        i += 2;
        home = s[Integer.parseInt(lines[i])];
        tempSector = null;
        mergedSectors = null;

        somethingMoved = true;
    }

    public Map (String filename) {
        this(getFileContent(filename));
    }

    public static String[] getFileContent(String file) {
        try {
            fileR = new FileReader(file);
            fileBR = new BufferedReader(fileR);
            String[] lines = new String[Integer.parseInt(fileBR.readLine())];
            for (int i=0;i<lines.length;i++) {
                lines[i] = fileBR.readLine();
            }
            fileBR.close();
            fileR.close();
            return lines;
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + file + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + file + "'");
        }
        return null;
    }

    /**
     * Returs the number of lines that is left before the next empty line or the end of the file appear
     * @param lines
     * @param index
     * @return
     */
    private int linesBeforeNextBreak(String[] lines, int index) {
        int start = index;
        while ((index < lines.length)&&(!lines[index].equals("")))
            index ++;
        return index - start;
    }

    public void setMouse(MyPoint newMouse) {
        somethingMoved = true;
        mouse = newMouse;
    }

    public void displacement(int displX, int displY) {
        MyPoint newPosition = new MyPoint(position.x + displX, position.y + displY);
        newPosition = sectorChange(position, newPosition, null);
        if (!newPosition.equals(position)) {
            position = newPosition;
            somethingMoved = true;
            tempSector = null;
            mergedSectors = null;
        }
    }

    private MyPoint sectorChange(MyPoint oldPosition, MyPoint newPosition, Edge prevEdge) {
        if (oldPosition.equals(newPosition))
            return newPosition;
        //System.out.println("from "+oldPosition+" to "+ newPosition);
        Edge[] edges = home.getEdges();
        for (int i = 0; i <  edges.length; i ++) {
            MyPoint intersection = Intersection.intersectSegmentAndSegment(edges[i].segment, new LineSegment(oldPosition, newPosition));
            if ((intersection == null) || (intersection.equals(oldPosition)))
                continue;
            if (!edges[i].getType().goThrough) {
                newPosition = lastBeforeIntersection(intersection, oldPosition);
                if (!newPosition.equals(oldPosition))
                    homeEdges = null;
                return newPosition;
            }
            ArrayList<Edge> newHomeEdges;
            if (intersection.equals(edges[i].segment.p1))
                newHomeEdges = giveAllEdges(edges[i].segment.p1, edges[i]);
            else if (intersection.equals(edges[i].segment.p2))
                newHomeEdges = giveAllEdges(edges[i].segment.p2, edges[i]);
            else {
                newHomeEdges = new ArrayList<>(1);
                newHomeEdges.add(edges[i]);
            }
            if (newHomeEdges == null) {
                newPosition = lastBeforeIntersection(intersection, oldPosition);
                if (!newPosition.equals(oldPosition))
                    homeEdges = null;
                return newPosition;
            }
            homeEdges = newHomeEdges;
            if (edges[i].sectorOne == home)
                home = edges[i].sectorTwo;
            else
                home = edges[i].sectorOne;
            return sectorChange(intersection, newPosition, edges[i]);
        }
        if ((homeEdges == null)||(isIn(newPosition, home, prevEdge)))
            return newPosition;
        else {
            int index = 0;
            while ((homeEdges.get(index) != prevEdge)&&(prevEdge != null)) {
                index ++;
                if (index == homeEdges.size())
                    index = 0;
            }
            index ++;
            if (index == homeEdges.size())
                index = 0;
            if (homeEdges.get(index).sectorOne == home)
                home = homeEdges.get(index).sectorTwo;
            else
                home = homeEdges.get(index).sectorOne;
            return sectorChange(oldPosition, newPosition, homeEdges.get(index));
        }
    }

    private boolean isIn (MyPoint point, Sector sector, Edge prevEdge) {
        Edge[] edges = sector.getEdges();
        Edge intersected = null;
        for (int i = 0; i < edges.length; i++) {
            MyPoint intersection = Intersection.intersectSegmentAndSegment(edges[i].segment, new LineSegment(sector.center, point));
            if (intersection != null) {
                if ((edges[i] != prevEdge) || (!intersection.equals(point)))
                    return false;
                intersected = edges[i];
            }
        }
        if (intersected == null)
            homeEdges = null;
        else if ((homeEdges.size() != 1) && (!point.equals(intersected.segment.p1)) && (!point.equals(intersected.segment.p2))) {
            homeEdges = new ArrayList<>(1);
            homeEdges.add(intersected);
        }
        return true;
    }

    private MyPoint lastBeforeIntersection(MyPoint intersection, MyPoint startPosition) {
        double step = 1/intersection.distance(startPosition);
        double k = 1;
        MyPoint newEnd;
        do {
            k -= step;
            newEnd = new MyPoint(startPosition.x + (intersection.x - startPosition.x) * k,
                                 startPosition.y + (intersection.y - startPosition.y) * k);
        } while ((newEnd.equals(intersection))&&(k > 0));
        if (k < 0)
            return startPosition;
        return  newEnd;
    }


    private ArrayList<Edge> giveAllEdges(MyPoint point, Edge startEdge) {
        ArrayList<Edge> result = new ArrayList<>(3);
        result.add(startEdge);
        Edge previous = startEdge;
        Sector current = startEdge.sectorOne;
        while (current != startEdge.sectorTwo)
            for (Edge edge: current.getEdges())
                if ((edge != previous) && ((edge.segment.p1.equals(point)) || (edge.segment.p2.equals(point)))) {
                    if (!edge.getType().goThrough)
                        return null;
                    if (edge.sectorOne == current)
                        current = edge.sectorTwo;
                    else
                        current = edge.sectorOne;
                    previous = edge;
                    result.add(edge);
                    break;
                }
        return result;
    }


    public void paint (Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        Edge[] edges = home.getEdges();
        int[] perX = new int[edges.length];
        int[] perY = new int[edges.length];
        MyPoint next;
        if ((edges[0].segment.p1 == edges[1].segment.p1)||(edges[0].segment.p1 == edges[1].segment.p2))
            next = edges[0].segment.p2;
        else
            next = edges[0].segment.p1;
        for (int i = 0; i < edges.length; i++) {
            if (edges[i].segment.p1.equals(next)) {
                perX[i] = edges[i].segment.p1.x;
                perY[i] = edges[i].segment.p1.y;
                next = edges[i].segment.p2;
            } else {
                perX[i] = edges[i].segment.p2.x;
                perY[i] = edges[i].segment.p2.y;
                next = edges[i].segment.p1;
            }
        }
        g.setColor(new Color(233, 221, 170));
        g.fillPolygon(perX, perY, edges.length);

        g.setColor(Color.black);
        g.fillOval(position.x-5, position.y-5, 10, 10);

        if (somethingMoved) {
            rootSpace.refreshPainted(true);
            Ray straight = new Ray(position, mouse);
            Ray rayLeft = new Ray(straight.angle + ANGLE_DIFFERENCE, position);
            Ray rayRight = new Ray(straight.angle - ANGLE_DIFFERENCE, position);
            if ((homeEdges == null)||(homeEdges.size() == 1))
                home.drawSector(position, VIEW_RADIUS, rayLeft, rayRight);
            else {
                if (tempSector == null)
                    findTempSector();
                tempSector.drawSector(position, VIEW_RADIUS, rayLeft, rayRight);
            }
        } else
            rootSpace.refreshPainted(false);
        rootSpace.paint(g);
        g.setColor(Color.black);
        g.setFont(new Font("ZapfDingbats", Font.PLAIN, 18));
        String toPrint = getPositionMessage();
        g.drawString(toPrint, 1400 - (int)g.getFontMetrics().getStringBounds(toPrint, null).getWidth() - 30, 30);
    }

    private void findTempSector() {
        mergedSectors = new Sector[homeEdges.size()];
        int mIndex = 0;
        int length = 0;
        for (Edge edge: homeEdges) {
            length += edge.sectorOne.getEdges().length;
            length += edge.sectorTwo.getEdges().length;
        }
        Edge[] newEdges = new Edge[length / 2 - homeEdges.size() * 2];
        int index = 0;
        Sector next;
        if ((homeEdges.get(0).sectorOne == homeEdges.get(1).sectorOne)||(homeEdges.get(0).sectorOne == homeEdges.get(1).sectorTwo))
            next = homeEdges.get(0).sectorTwo;
        else
            next = homeEdges.get(0).sectorOne;
        for (Edge edge: homeEdges) {
            for (Edge add: next.getEdges()) {
                boolean adding = true;
                for (Edge check : homeEdges)
                    if (check == add) {
                        adding = false;
                        break;
                    }
                if (adding)
                    newEdges[index++] = add;
            }
            mergedSectors[mIndex++] = next;
            if (edge.sectorOne == next) {
                next = edge.sectorTwo;
            } else {
                next = edge.sectorOne;
            }
        }
        tempSector = new Sector(newEdges);
    }

    private class SectorEdge {
        Edge edge;
        Sector sector;

        SectorEdge(Edge edge, Sector sector) {
            this.edge = edge;
            this.sector = sector;
        }
    }

    public static boolean mergedIn(Sector temp, Sector merged) {
        if (tempSector != temp)
            return false;
        for (Sector sector: mergedSectors)
            if (sector == merged)
                return true;
        return false;
    }

    public String getPositionMessage() {
        if (home.parent == rootSpace)
            return "You are somewhere in the world.";
        String result = "You are in the "+ home.parent.name;
        Space current = home.parent;
        while (current.parent != rootSpace) {
            current = current.parent;
            result += " of the " + current.name;
        }
        result += ".";
        return result;
    }
}
