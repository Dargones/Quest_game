package texting;

/**
 * Created by alexanderfedchin on 7/18/17.
 *
 * This class represents a variable. A value stored as an Object (the weakest part of the whole program).
 */
public class Variable implements Named, Comparable<Variable> {
    public Object value;
    public final Type type;
    private final String name;


    public Variable(String name, Type type, Object initValue) {
        this.type = type;
        this.value = initValue;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Variable o) {
        return name.compareTo(o.name);
    }
}
