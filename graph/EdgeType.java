package graph;

/**
 * Created by alexanderfedchin on 4/26/17.
 *
 * This is a list of different edge types. Ideally, depending on the type, the edges will be drawn slightly differently
 */
public enum EdgeType {

    WALL(false),
    WINDOW(true),
    DOOR_OPENED(true),
    DOOR_CLOSED(false),
    BARRICADE(true);

    public final boolean visThrough; // if visThrough is true, then the player can see through this edge (i. e., this
    // might be a pseudo edge,a window, etc.)

    /**
     * Basic constructor
     * @param visThrough
     */
    EdgeType (boolean visThrough) {
        this.visThrough = visThrough;
    }
}
