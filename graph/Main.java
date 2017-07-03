package graph;

import geometry.MyPoint;
import geometry.Ray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by alexanderfedchin on 4/30/17.
 */
public class Main extends JPanel implements MouseMotionListener, KeyListener{
    public static double VIEW_RADIUS = 700;
    public static double ANGLE_DIFFERENCE = Math.PI/3;
    public static double SPEED = 5;
    public Sector[] s;
    public MyPoint position, mouse;
    public int goY;
    public int goX;
    public Sector homeSector;

    public static void main(String args[]) {
        JFrame frame = new JFrame();
        frame.setTitle("The_World");
        frame.add(new Main());
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Main() {
        Dimension d = new Dimension(1440, 875);
        setSize(d);
        setPreferredSize(d);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        position = new MyPoint(330,290);
        mouse = new MyPoint(330,380);
        goY = 0;
        goX = 0;

        s = new Sector[6];
        Space space= new Space();
        Edge[][] edges = new Edge[6][];
        for (int i=0;i<edges.length;i++) {
            if ((i == 3) || (i == 4))
                edges[i] = new Edge[5];
            else
                edges[i] = new Edge[4];
            s[i] = new Sector(edges[i], space);
        }

        homeSector = s[2];

        MyPoint c1 = new MyPoint(5,5);
        MyPoint c2 = new MyPoint(1435,5);
        MyPoint c3 = new MyPoint(1435,800);
        MyPoint c4 = new MyPoint(5,800);

        MyPoint one = new MyPoint(300,300);
        MyPoint two = new MyPoint(360,300);
        MyPoint three = new MyPoint(360,360);
        MyPoint four = new MyPoint(300,360);
        MyPoint five = new MyPoint(360,420);
        MyPoint six = new MyPoint(300,420);

        edges[0][0] = new Edge(s[0],s[2], one, two, EdgeType.WALL);
        edges[0][1] = new Edge(s[0],s[4], two, three, EdgeType.WALL);
        edges[0][2] = new Edge(s[0],s[1], three, four, EdgeType.WINDOW);
        edges[0][3] = new Edge(s[0],s[3], four, one, EdgeType.WALL);

        edges[1][0] = new Edge(s[1],s[4], three, five, EdgeType.WALL);
        edges[1][1] = new Edge(s[1],s[5], five, six, EdgeType.WALL);
        edges[1][2] = new Edge(s[1],s[3], six, four, EdgeType.WALL);
        edges[1][3] = edges[0][2];

        edges[2][0] = new Edge(s[2],null, c1, c2, EdgeType.WALL);
        edges[2][1] = new Edge(s[2],s[4], c2, two, EdgeType.WINDOW);
        edges[2][2] = edges[0][0];
        edges[2][3] = new Edge(s[2],s[3], one, c1, EdgeType.WINDOW);

        edges[3][0] = new Edge(s[3],null, c1, c4, EdgeType.WALL);
        edges[3][1] = new Edge(s[3],s[5], c4, six, EdgeType.WINDOW);
        edges[3][2] = edges[1][2];
        edges[3][3] = edges[0][3];
        edges[3][4] = edges[2][3];

        edges[4][0] = new Edge(s[4],null, c2, c3, EdgeType.WALL);
        edges[4][1] = new Edge(s[4],s[5], c3, five, EdgeType.WINDOW);
        edges[4][2] = edges[1][0];
        edges[4][3] = edges[0][1];
        edges[4][4] = edges[2][1];

        edges[5][0] = new Edge(s[5],null, c3, c4, EdgeType.WALL);
        edges[5][1] = edges[3][1];
        edges[5][2] = edges[1][1];
        edges[5][3] = edges[4][1];
    }

    public void paint(Graphics g) {
        if ((goY != 0) || (goX != 0)) {
            MyPoint newPosition = new MyPoint((int)(position.x + SPEED * goX + 0.5), (int)(position.y + SPEED * goY + 0.5));
            Sector newSector = homeSector.sectorChange(position, newPosition);
            if (newSector != null) {
                homeSector = newSector;
                position = newPosition;
            }
        }
        g.setColor(Color.black);
        g.fillOval(position.x, position.y, 10, 10);
        Ray straight = new Ray(position, mouse);
        Ray rayLeft = new Ray(straight.angle+ANGLE_DIFFERENCE, position);
        Ray rayRight = new Ray(straight.angle-ANGLE_DIFFERENCE, position);
        homeSector.drawSector(g,position, VIEW_RADIUS, rayLeft, rayRight);
        repaint();
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 38)
            goY = -1;
        else if (e.getKeyCode() == 40)
            goY = 1;
        else if (e.getKeyCode() == 39)
            goX = 1;
        else if (e.getKeyCode() == 37)
            goX = -1;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if ((e.getKeyCode() == 38) && (goY == -1))
            goY = 0;
        else if ((e.getKeyCode() == 40) && (goY == 1))
            goY = 0;
        else if ((e.getKeyCode() == 39) && (goX == 1))
            goX = 0;
        else if ((e.getKeyCode() == 37) && (goX == -1))
            goX = 0;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
       mouse = new MyPoint(e.getX(), e.getY());
    }
}
