package graph;

import geometry.MyPoint;
import geometry.Ray;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by alexanderfedchin on 7/12/17.
 *
 * Class that unites everything that is to be
 */
public class Map {
    public static double VIEW_RADIUS = 700;
    public static double ANGLE_DIFFERENCE = Math.PI/2;
    private MyPoint position;
    private Sector[] homeSector; // the Sector(s) you are in. If you are in two sectors you are on the edge between them,
    // if there are more than two sectors, then you are on the point that they share in common.
    private Space rootSpace;
    private MyPoint mouse;
    private boolean somethingMoved; //if this variable is false, then there is no need to recalculate all the intersections etc.
    public static FileReader fileR;
    public static BufferedReader fileBR;

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
                if (data[0].equals("link:"))
                    edges[j][k] = edges[Integer.parseInt(data[1])][Integer.parseInt(data[2])];
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
        coord = lines[i].split(" ");
        homeSector = new Sector[coord.length];
        for (int j = 0; j < homeSector.length; j++)
            homeSector[j] = s[Integer.parseInt(coord[j])];

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
        if ((displX == 0) && (displY == 0))
            return;

        //TODO home Sector change

        somethingMoved = true;
    }

    public void paint (Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.black);
        g.fillOval(position.x-5, position.y-5, 10, 10);

        if (somethingMoved) {
            rootSpace.refreshPainted(true);
            Ray straight = new Ray(position, mouse);
            Ray rayLeft = new Ray(straight.angle + ANGLE_DIFFERENCE, position);
            Ray rayRight = new Ray(straight.angle - ANGLE_DIFFERENCE, position);
            homeSector[0].drawSector(position, VIEW_RADIUS, rayLeft, rayRight);
        } else
            rootSpace.refreshPainted(false);
        rootSpace.paint(g);
    }
}
