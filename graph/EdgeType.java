package graph;

import java.awt.*;

/**
 * Created by alexanderfedchin on 4/26/17.
 *
 * This is a list of different edge types. Ideally, depending on the type, the edges will be drawn slightly differently
 */
public enum EdgeType {

    WALL(false, false, Color.BLACK, new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), "wall"),
    WINDOW(true, false, Color.BLACK, new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.f, new float[] {4.f}, 0.f), "window"),
    DOOR_OPENED(true, true,  new Color(74, 146, 92), new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), "door"),
    DOOR_CLOSED(false, false, new Color(74, 146, 92), new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.f, new float[] {4.f}, 0.f), "door"),
    BARRICADE(true, false, new Color(80, 96, 146), new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.f, new float[] {4.f}, 0.f), "barricade"),
    PSEUDO(true, true, null, null, "pseudo"); //PSEUDO are the edges taht are never drawn

    public final boolean visThrough; // if visThrough is true, then the player can see through this edge (i. e., this
    // might be a pseudo edge,a window, etc.)
    public final boolean goThrough; // if goThrough is true, then the player can go through this edge.
    public final Color color;       // the color in which the edges of this type will be colored unless they are shaded
    public final BasicStroke style; // the style that should be used to draw the edge of this type
    public final String name; // the name of this EdgeType

    /**
     * Basic constructor
     * @param visThrough
     */
    EdgeType (boolean visThrough, boolean goThrough, Color color, BasicStroke style, String name) {
        this.visThrough = visThrough;
        this.goThrough = goThrough;
        this.color = color;
        this.style = style;
        this.name = name;
    }
}
