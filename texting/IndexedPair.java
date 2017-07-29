package texting;

/**
 * Created by alexanderfedchin on 7/29/17.
 * Represents a pair of an object and a postion/int_value corresponding to it
 */
public class IndexedPair<T> {
    private final T o; //object
    private final int p; //position

    public IndexedPair(T o, int p) {
        this.p = p;
        this.o = o;
    }
}
