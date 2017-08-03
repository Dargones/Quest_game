package texting;

import javax.swing.*;
import java.awt.*;


/**
 * Created by alexanderfedchin on 7/16/17.
 *
 * This is a temporary main class that will be deleted in the future. Its goal is to create a GameShell
 */
public class Main extends JFrame {

    public static void main(String args[]) {
        new Main();
    }

    public Main() {
        super();
        Dimension d = new Dimension(1440, 800);
        this.setTitle("The_World");
        this.add(new GameShell(d, new GameObject(null)));
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

}