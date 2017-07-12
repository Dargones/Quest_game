package graph;

import geometry.MyPoint;

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
    private static int SPEED = 5;
    private int goY;
    private int goX;
    private Map map;

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
        map = new Map("Content.txt");
    }

    public void paint(Graphics g) {
        map.displacement(goX * SPEED, goY * SPEED);
        map.paint(g);
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
       map.setMouse(new MyPoint(e.getX(), e.getY()));
    }
}
