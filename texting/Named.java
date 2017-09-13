package texting;

/**
 * Created by alexanderfedchin on 7/24/17.
 *
 * This interface is created to unite Operator and Variable classes. Every element of these two classes has a name, so
 * that an array of such elements can be sorted by it. Then, the binary search can be used to find a specific element
 * inside such an array.
 */
public interface Named {
    String getName();
}
