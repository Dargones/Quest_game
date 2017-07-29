package graph;

import java.awt.*;

/**
 * Created by alexanderfedchin on 4/26/17.
 */
public class Space{
    private Sector[] added;
    private Space[] children;
    public Space parent;
    public String name;

    public Space(String name, Sector[] added, Space[] children) {
        this.added = added;
        this.children = children;
        if (children != null)
            for (int i = 0; i < children.length; i++)
                children[i].parent = this;
        if (added != null)
            for (int i = 0; i < added.length; i++)
                added[i].parent = this;
        parent = null;
        this.name = name;
    }

    public void refreshPainted(boolean refreshBorders) {
        if (children != null)
            for (int i = 0; i < children.length; i++)
                children[i].refreshPainted(refreshBorders);
        if (added != null)
            for (int i = 0; i < added.length; i++)
                added[i].refreshPainted(refreshBorders);
    }

    public void paint(Graphics2D g) {
        if (children != null)
            for (int i = 0; i < children.length; i++)
                children[i].paint(g);
        if (added != null)
            for (int i = 0; i < added.length; i++)
                added[i].paint(g);
    }
}
