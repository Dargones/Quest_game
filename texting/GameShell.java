package texting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by alexanderfedchin on 7/16/17.
 */
public class GameShell extends JPanel {
    GameObject textSource;
    TextArea text;
    TextArea actionList;
    TextField input;

    public GameShell(Dimension dimension, GameObject textSource) {
        super();
        this.textSource = textSource;
        setSize(dimension);
        setPreferredSize(dimension);
        setFocusable(true);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        text = new TextArea();
        text.setFont(new Font("ZapfDingbats", Font.PLAIN, 30));
        //text.setFocusable(false);
        actionList = new TextArea();
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
                if (e.getKeyCode()==10) {
                    textSource.act(input.getText());
                    if (textSource.getStateChanged()) {
                        text.setText(textSource.getText());
                        actionList.setText(textSource.getActions());
                        textSource.setStateChanged(false);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        text.setText(textSource.getText());
        actionList.setText(textSource.getActions());
        this.add(text);
        this.add(actionList);
        this.add(input);
    }
}
