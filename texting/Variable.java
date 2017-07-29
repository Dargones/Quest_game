package texting;

/**
 * Created by alexanderfedchin on 7/18/17.
 */
public class Variable implements Token {
    public String value;
    public final Type type;
    private final String name;


    public Variable(String name, Type type, String initValue) {
        this.type = type;
        this.value = initValue;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
