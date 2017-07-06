package graph;

/**
 * Created by alexanderfedchin on 4/26/17.
 *
 * This is a list of different edge types. Ideally, depending on the type, the edges will be drawn slightly differently
 */
public enum EdgeType {

    WALL(false, false),
    WINDOW(true, false),
    DOOR_OPENED(true, true),
    DOOR_CLOSED(false, false),
    BARRICADE(true, false),
    PSEUDO(true, true);

    public final boolean visThrough; // if visThrough is true, then the player can see through this edge (i. e., this
    // might be a pseudo edge,a window, etc.)
    public final boolean goThrough; // if goThrough is true, then the player can go through this edge.

    /**
     * Basic constructor
     * @param visThrough
     */
    EdgeType (boolean visThrough, boolean goThrough) {
        this.visThrough = visThrough;
        this.goThrough = goThrough;
    }
}
