package texting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by alexanderfedchin on 7/16/17.
 *
 * This is a graphic interface provided for a GameObject
 */
public class GameShell extends JPanel {
    GameObject textSource; //the current gameObject that this GameShell is focused on.
    JTextArea text; //the content of teh text field
    JTextArea actionList; //the content of the actionList
    TextField input; //the input field (edited by teh user)

    public GameShell(Dimension dimension, GameObject textSource) {
        super();
        this.textSource = textSource;
        setSize(dimension);
        setPreferredSize(dimension);
        setFocusable(true);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        text = new JTextArea();
        text.setFont(new Font("ZapfDingbats", Font.PLAIN, 30));
        text.setFocusable(false);
        text.setLineWrap(true);
        actionList = new JTextArea();
        actionList.setFont(new Font("ZapfDingbats", Font.PLAIN, 30));
        //actionList.setFocusable(false);
        input = new TextField();
        input.setFont(new Font("ZapfDingbats", Font.PLAIN, 30));
        input.addKeyListener(new java.awt.event.KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==10) { //if "Enter" is pressed, the GameShell triggers the GameObject with the String
                    //taken from the input TextField
                    textSource.act(input.getText());
                    if (textSource.getStateChanged()) {
                        text.setText(textSource.getText());
                        ArrayList<String> actions = textSource.getActions();
                        String tmp = "";
                        if (actions != null)
                            for (int i = 0; i < actions.size(); i ++)
                                tmp += i+" -> "+actions.get(i) + '\n';
                        actionList.setText(tmp);
                        textSource.setStateChanged(false);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        text.setText(textSource.getText());
        ArrayList<String> actions = textSource.getActions();
        String tmp = "";
        if (actions != null)
            for (int i = 0; i < actions.size(); i ++)
                tmp += i+" -> "+actions.get(i) + '\n';
        actionList.setText(tmp);
        this.add(text);
        this.add(actionList);
        this.add(input);
    }
}
