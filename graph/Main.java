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
    public static double ANGLE_DIFFERENCE = Math.PI/2;
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

        position = new MyPoint(90,98);
        mouse = new MyPoint(90,100);
        goY = 0;
        goX = 0;

        s = new Sector[30];
        Space space= new Space();
        Edge[][] edges = new Edge[30][];

        MyPoint p01 = new MyPoint(5,5);
        MyPoint p02 = new MyPoint(1400,5);
        MyPoint p03 = new MyPoint(1400,700);
        MyPoint p04 = new MyPoint(5,700);
        MyPoint p05 = new MyPoint(5,100);
        MyPoint p06 = new MyPoint(1400,100);
        MyPoint p07 = new MyPoint(5,650);
        MyPoint p08 = new MyPoint(1400,650);
        MyPoint p09 = new MyPoint(100,100);
        MyPoint p10 = new MyPoint(1100,100);
        MyPoint p11 = new MyPoint(600,100);
        MyPoint p12 = new MyPoint(100,600);
        MyPoint p13 = new MyPoint(100,650);
        MyPoint p14 = new MyPoint(1100,650);
        MyPoint p15 = new MyPoint(500,100);
        MyPoint p16 = new MyPoint(100,200);
        MyPoint p17 = new MyPoint(100,400);
        MyPoint p18 = new MyPoint(100,610);
        MyPoint p19 = new MyPoint(100,640);
        MyPoint p20 = new MyPoint(110,650);
        MyPoint p21 = new MyPoint(150,650);
        MyPoint p22 = new MyPoint(140,650);
        MyPoint p23 = new MyPoint(1100,600);
        MyPoint p24 = new MyPoint(1100,400);
        MyPoint p25 = new MyPoint(1100,570);
        MyPoint p26 = new MyPoint(150,640);
        MyPoint p27 = new MyPoint(150,610);
        MyPoint p28 = new MyPoint(150,600);
        MyPoint p29 = new MyPoint(140,600);
        MyPoint p30 = new MyPoint(110,600);
        MyPoint p31 = new MyPoint(450,600);
        MyPoint p32 = new MyPoint(540,570);
        MyPoint p33 = new MyPoint(540,600);
        MyPoint p34 = new MyPoint(600,400);
        MyPoint p35 = new MyPoint(540,400);
        MyPoint p36 = new MyPoint(500,400);
        MyPoint p37 = new MyPoint(450,400);
        MyPoint p38 = new MyPoint(450,450);
        MyPoint p39 = new MyPoint(600,150);
        MyPoint p40 = new MyPoint(600,200);
        MyPoint p41 = new MyPoint(600,300);
        MyPoint p42 = new MyPoint(500,200);
        MyPoint p43 = new MyPoint(500,300);
        MyPoint p44 = new MyPoint(540,300);
        MyPoint p45 = new MyPoint(560,300);
        MyPoint p46 = new MyPoint(200,200);
        MyPoint p47 = new MyPoint(340,200);
        MyPoint p48 = new MyPoint(400,200);
        MyPoint p49 = new MyPoint(200,230);
        MyPoint p50 = new MyPoint(200,260);
        MyPoint p51 = new MyPoint(200,290);
        MyPoint p52 = new MyPoint(200,320);
        MyPoint p53 = new MyPoint(200,350);
        MyPoint p54 = new MyPoint(200,380);
        MyPoint p55 = new MyPoint(200,400);
        MyPoint p56 = new MyPoint(290,400);
        MyPoint p57 = new MyPoint(310,400);
        MyPoint p58 = new MyPoint(400,400);
        MyPoint p59 = new MyPoint(400,380);
        MyPoint p60 = new MyPoint(400,320);
        MyPoint p61 = new MyPoint(400,290);
        MyPoint p62 = new MyPoint(400,230);
        MyPoint p63 = new MyPoint(290,380);
        MyPoint p64 = new MyPoint(310,380);
        MyPoint p65 = new MyPoint(340,380);
        MyPoint p66 = new MyPoint(340,350);
        MyPoint p67 = new MyPoint(340,320);
        MyPoint p68 = new MyPoint(290,320);
        MyPoint p69 = new MyPoint(260,290);
        MyPoint p70 = new MyPoint(260,320);
        MyPoint p71 = new MyPoint(290,260);

        edges[7] = new Edge[12];
        edges[8] = new Edge[3];
        edges[9] = new Edge[8];
        edges[10] = new Edge[3];
        edges[11] = new Edge[9];
        edges[26] = new Edge[3];
        edges[27] = new Edge[3];
        edges[18] = new Edge[8];
        edges[16] = new Edge[5];
        edges[25] = new Edge[6];
        edges[24] = new Edge[3];
        edges[13] = new Edge[4];
        edges[21] = new Edge[4];
        edges[22] = new Edge[4];
        edges[23] = new Edge[5];
        edges[20] = new Edge[6];
        edges[29] = new Edge[6];
        edges[28] = new Edge[5];
        edges[19] = new Edge[4];
        edges[14] = new Edge[7];
        edges[12] = new Edge[10];
        edges[15] = new Edge[13];
        edges[17] = new Edge[4];
        edges[0] = new Edge[8];
        edges[1] = new Edge[9];
        edges[2] = new Edge[7];
        edges[3] = new Edge[9];
        edges[4] = new Edge[7];
        edges[6] = new Edge[10];
        edges[5] = new Edge[7];

        for (int i = 0; i < edges.length; i++)
            s[i] = new Sector(edges[i], space);

        homeSector = s[0];

        edges[0][0] = new Edge(s[0],null, p01, p02, EdgeType.WALL);
        edges[0][1] = new Edge(s[0],null, p02, p06, EdgeType.WALL);
        edges[0][2] = new Edge(s[0],s[5], p06, p10, EdgeType.PSEUDO);
        edges[0][3] = new Edge(s[0],s[4], p10, p11, EdgeType.PSEUDO);
        edges[0][4] = new Edge(s[0],s[3], p15, p11, EdgeType.WALL);
        edges[0][5] = new Edge(s[0],s[2], p15, p09, EdgeType.PSEUDO);
        edges[0][6] = new Edge(s[0],s[1], p05, p09, EdgeType.PSEUDO);
        edges[0][7] = new Edge(s[0],null, p05, p01, EdgeType.WALL);

        edges[1][0] = edges[0][6];
        edges[1][1] = new Edge(s[1],s[2], p09, p16, EdgeType.PSEUDO);
        edges[1][2] = new Edge(s[1],s[6], p16, p17, EdgeType.PSEUDO);
        edges[1][3] = new Edge(s[1],s[7], p17, p12, EdgeType.PSEUDO);
        edges[1][4] = new Edge(s[1],s[8], p12, p18, EdgeType.PSEUDO);
        edges[1][5] = new Edge(s[1],s[9], p18, p19, EdgeType.WALL);
        edges[1][6] = new Edge(s[1],s[10], p13, p19, EdgeType.PSEUDO);
        edges[1][7] = new Edge(s[1],s[11], p13, p07, EdgeType.PSEUDO);
        edges[1][8] = new Edge(s[1],null, p05, p07, EdgeType.WALL);

        edges[2][0] = edges[0][5];
        edges[2][1] = new Edge(s[2],s[3], p15, p42, EdgeType.WALL);
        edges[2][2] = new Edge(s[2],s[13], p42, p48, EdgeType.PSEUDO);
        edges[2][3] = new Edge(s[2],s[12], p48, p47, EdgeType.WALL);
        edges[2][4] = new Edge(s[2],s[14], p47, p46, EdgeType.WALL);
        edges[2][5] = new Edge(s[2],s[6], p16, p46, EdgeType.PSEUDO);
        edges[2][6] = edges[1][1];

        edges[3][0] = edges[0][4];
        edges[3][1] = new Edge(s[3],s[4], p11, p39, EdgeType.WALL);
        edges[3][2] = new Edge(s[3],s[4], p39, p40, EdgeType.WINDOW);
        edges[3][3] = new Edge(s[3],s[4], p40, p41, EdgeType.WALL);
        edges[3][4] = new Edge(s[3],s[15], p45, p41, EdgeType.WALL);
        edges[3][5] = new Edge(s[3],s[15], p44, p45, EdgeType.DOOR_OPENED);
        edges[3][6] = new Edge(s[3],s[15], p43, p44, EdgeType.WALL);
        edges[3][7] = new Edge(s[3],s[13], p43, p42, EdgeType.WALL);
        edges[3][8] = edges[2][1];

        edges[4][0] = edges[0][3];
        edges[4][1] = new Edge(s[4],s[5], p10, p24, EdgeType.PSEUDO);
        edges[4][2] = new Edge(s[4],s[16], p24, p34, EdgeType.WALL);
        edges[4][3] = new Edge(s[4],s[15], p34, p41, EdgeType.PSEUDO);
        edges[4][4] = edges[3][3];
        edges[4][5] = edges[3][2];
        edges[4][6] = edges[3][1];

        edges[5][0] = edges[0][2];
        edges[5][1] = new Edge(s[5],null, p08, p06, EdgeType.WALL);
        edges[5][2] = new Edge(s[5],s[11], p08, p14, EdgeType.PSEUDO);
        edges[5][3] = new Edge(s[5],s[18], p14, p23, EdgeType.PSEUDO);
        edges[5][4] = new Edge(s[5],s[17], p23, p25, EdgeType.WALL);
        edges[5][5] = new Edge(s[5],s[16], p25, p24, EdgeType.WALL);
        edges[5][6] = edges[4][1];

        edges[6][0] = edges[2][5];
        edges[6][1] = new Edge(s[6],s[14], p46, p49, EdgeType.WALL);
        edges[6][2] = new Edge(s[6],s[14], p49, p50, EdgeType.WINDOW);
        edges[6][3] = new Edge(s[6],s[14], p50, p51, EdgeType.WALL);
        edges[6][4] = new Edge(s[6],s[19], p51, p52, EdgeType.WALL);
        edges[6][5] = new Edge(s[6],s[20], p52, p53, EdgeType.WINDOW);
        edges[6][6] = new Edge(s[6],s[20], p53, p54, EdgeType.WALL);
        edges[6][7] = new Edge(s[6],s[21], p54, p55, EdgeType.PSEUDO);
        edges[6][8] = new Edge(s[6],s[7], p55, p17, EdgeType.PSEUDO);
        edges[6][9] = edges[1][2];

        edges[7][0] = edges[6][8];
        edges[7][1] = new Edge(s[7],s[21], p55, p56, EdgeType.PSEUDO);
        edges[7][2] = new Edge(s[7],s[22], p56, p57, EdgeType.DOOR_OPENED);
        edges[7][3] = new Edge(s[7],s[23], p57, p58, EdgeType.PSEUDO);
        edges[7][4] = new Edge(s[7],s[15], p58, p37, EdgeType.PSEUDO);
        edges[7][5] = new Edge(s[7],s[24], p37, p38, EdgeType.PSEUDO);
        edges[7][6] = new Edge(s[7],s[25], p38, p31, EdgeType.WALL);
        edges[7][7] = new Edge(s[7],s[18], p31, p28, EdgeType.PSEUDO);
        edges[7][8] = new Edge(s[7],s[26], p28, p29, EdgeType.PSEUDO);
        edges[7][9] = new Edge(s[7],s[9], p29, p30, EdgeType.WALL);
        edges[7][10] = new Edge(s[7],s[8], p30, p12, EdgeType.PSEUDO);
        edges[7][11] = edges[1][3];

        edges[8][0] = edges[7][10];
        edges[8][1] = new Edge(s[8],s[9], p30, p18, EdgeType.DOOR_OPENED);
        edges[8][2] = edges[1][4];

        edges[9][0] = edges[8][1];
        edges[9][1] = edges[7][9];
        edges[9][2] = new Edge(s[9],s[26], p29, p27, EdgeType.WALL);
        edges[9][3] = new Edge(s[9],s[18], p27, p26, EdgeType.WALL);
        edges[9][4] = new Edge(s[9],s[27], p26, p22, EdgeType.WALL);
        edges[9][5] = new Edge(s[9],s[11], p22, p20, EdgeType.WALL);
        edges[9][6] = new Edge(s[9],s[10], p20, p19, EdgeType.WALL);
        edges[9][7] = edges[1][5];

        edges[10][0] = edges[1][6];
        edges[10][1] = edges[9][6];
        edges[10][2] = new Edge(s[10],s[11], p20, p13, EdgeType.PSEUDO);

        edges[11][0] = edges[1][7];
        edges[11][1] = edges[10][2];
        edges[11][2] = edges[9][5];
        edges[11][3] = new Edge(s[11],s[27], p22, p21, EdgeType.PSEUDO);
        edges[11][4] = new Edge(s[11],s[18], p21, p14, EdgeType.PSEUDO);
        edges[11][5] = edges[5][2];
        edges[11][6] = new Edge(s[11],null, p08, p03, EdgeType.WALL);
        edges[11][7] = new Edge(s[11],null, p03, p04, EdgeType.WALL);
        edges[11][8] = new Edge(s[11],null, p04, p07, EdgeType.WALL);

        edges[26][0] = edges[9][2];
        edges[26][1] = edges[7][8];
        edges[26][2] = new Edge(s[26],s[18], p28, p27, EdgeType.PSEUDO);

        edges[27][0] = edges[11][3];
        edges[27][1] = edges[9][4];
        edges[27][2] = new Edge(s[27],s[18], p26, p21, EdgeType.PSEUDO);

        edges[18][0] = edges[7][7];
        edges[18][1] = new Edge(s[18],s[25], p31, p33, EdgeType.WALL);
        edges[18][2] = new Edge(s[18],s[17], p33, p23, EdgeType.WALL);
        edges[18][3] = edges[5][3];
        edges[18][4] = edges[11][4];
        edges[18][5] = edges[27][2];
        edges[18][6] = edges[9][3];
        edges[18][7] = edges[26][2];

        edges[17][0] = edges[18][2];
        edges[17][1] = edges[5][4];
        edges[17][2] = new Edge(s[17],s[16], p25, p32, EdgeType.WALL);
        edges[17][3] = new Edge(s[17],s[25], p32, p33, EdgeType.PSEUDO);

        edges[16][0] = new Edge(s[16],s[15], p35, p34, EdgeType.WALL);
        edges[16][1] = edges[4][2];
        edges[16][2] = edges[5][5];
        edges[16][3] = edges[17][2];
        edges[16][4] = new Edge(s[16],s[25], p32, p35, EdgeType.PSEUDO);

        edges[25][0] = new Edge(s[25],s[24], p38, p36, EdgeType.WALL);
        edges[25][1] = new Edge(s[25],s[15], p36, p35, EdgeType.DOOR_OPENED);
        edges[25][2] = edges[16][4];
        edges[25][3] = edges[17][3];
        edges[25][4] = edges[18][1];
        edges[25][5] = edges[7][6];

        edges[24][0] = edges[7][5];
        edges[24][1] = edges[25][0];
        edges[24][2] = new Edge(s[24],s[15], p37, p36, EdgeType.PSEUDO);

        edges[13][0] = edges[2][2];
        edges[13][1] = edges[3][7];
        edges[13][2] = new Edge(s[13],s[15], p43, p62, EdgeType.PSEUDO);
        edges[13][3] = new Edge(s[13],s[12], p62, p48, EdgeType.WALL);

        edges[15][0] = edges[13][2];
        edges[15][1] = edges[3][4];
        edges[15][2] = edges[3][5];
        edges[15][3] = edges[3][6];
        edges[15][4] = edges[4][3];
        edges[15][5] = edges[16][0];
        edges[15][6] = edges[25][1];
        edges[15][7] = edges[24][2];
        edges[15][8] = edges[7][4];
        edges[15][9] = new Edge(s[15],s[23], p58, p59, EdgeType.PSEUDO);
        edges[15][10] = new Edge(s[15],s[28], p59, p60, EdgeType.WALL);
        edges[15][11] = new Edge(s[15],s[12], p60, p61, EdgeType.WALL);
        edges[15][12] = new Edge(s[15],s[12], p61, p62, EdgeType.WINDOW);

        edges[21][0] = edges[6][7];
        edges[21][1] = edges[7][1];
        edges[21][2] = new Edge(s[21],s[22], p56, p63, EdgeType.WALL);
        edges[21][3] = new Edge(s[21],s[20], p63, p54, EdgeType.WALL);

        edges[22][0] = edges[21][2];
        edges[22][1] = edges[7][2];
        edges[22][2] = new Edge(s[22],s[23], p64, p57, EdgeType.WALL);
        edges[22][3] = new Edge(s[22],s[29], p63, p64, EdgeType.PSEUDO);

        edges[23][0] = edges[22][2];
        edges[23][1] = edges[7][3];
        edges[23][2] = edges[15][9];
        edges[23][3] = new Edge(s[23],s[28], p59, p65, EdgeType.WALL);
        edges[23][4] = new Edge(s[23],s[29], p65, p64, EdgeType.WALL);

        edges[20][0] = new Edge(s[20],s[19], p52, p70, EdgeType.PSEUDO);
        edges[20][1] = new Edge(s[20],s[12], p70, p68, EdgeType.WALL);
        edges[20][2] = new Edge(s[20],s[29], p68, p63, EdgeType.WALL);
        edges[20][3] = edges[6][5];
        edges[20][4] = edges[6][6];
        edges[20][5] = edges[21][3];

        edges[29][0] = new Edge(s[29],s[12], p68, p67, EdgeType.PSEUDO);
        edges[29][1] = new Edge(s[29],s[28], p67, p66, EdgeType.PSEUDO);
        edges[29][2] = new Edge(s[29],s[28], p66, p65, EdgeType.WALL);
        edges[29][3] = edges[23][4];
        edges[29][4] = edges[22][3];
        edges[29][5] = edges[20][2];

        edges[28][0] = new Edge(s[28],s[12], p60, p67, EdgeType.WALL);
        edges[28][1] = edges[15][10];
        edges[28][2] = edges[23][3];
        edges[28][3] = edges[29][1];
        edges[28][4] = edges[29][2];

        edges[19][0] = new Edge(s[19],s[14], p51, p69, EdgeType.PSEUDO);
        edges[19][1] = new Edge(s[19],s[12], p69, p70, EdgeType.WALL);
        edges[19][2] = edges[20][0];
        edges[19][3] = edges[6][4];

        edges[14][0] = edges[19][0];
        edges[14][1] = edges[6][1];
        edges[14][2] = edges[6][2];
        edges[14][3] = edges[6][3];
        edges[14][4] = edges[2][4];
        edges[14][5] = new Edge(s[14],s[12], p47, p71, EdgeType.PSEUDO);
        edges[14][6] = new Edge(s[14],s[12], p71, p69, EdgeType.WALL);

        edges[12][0] = edges[2][3];
        edges[12][1] = edges[13][3];
        edges[12][2] = edges[15][11];
        edges[12][3] = edges[15][12];
        edges[12][4] = edges[28][0];
        edges[12][5] = edges[29][0];
        edges[12][6] = edges[20][1];
        edges[12][7] = edges[19][1];
        edges[12][8] = edges[14][5];
        edges[12][9] = edges[14][6];
    }

    public void paint(Graphics g) {
        if ((goY != 0) || (goX != 0)) {
            MyPoint newPosition = null;
            Sector newSector = null;
            double i = SPEED;
            while ((newSector == null) && (i > SPEED/2)) {
                newPosition = new MyPoint(position.x + i * goX, position.y + i * goY);
                newSector = homeSector.sectorChange(position, newPosition);
                i -= 1;
            }
            if (newSector != null) {
                homeSector = newSector;
                position = newPosition;
            }
        }
        g.setColor(Color.black);
        g.fillOval(position.x-5, position.y-5, 10, 10);
        Ray straight = new Ray(position, mouse);
        Ray rayLeft = new Ray(straight.angle+ANGLE_DIFFERENCE, position);
        Ray rayRight = new Ray(straight.angle-ANGLE_DIFFERENCE, position);
        homeSector.drawSector(position, VIEW_RADIUS, rayLeft, rayRight);

        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].edges.length; j++)
                    s[i].edges[j].paint(g);
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].edges.length; j++)
                s[i].edges[j].refreshPainted();
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
