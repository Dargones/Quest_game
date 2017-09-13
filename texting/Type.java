package texting;

/**
 * Created by alexanderfedchin on 7/23/17.
 *
 * A list of types that can be used by the person who creates games. These include: Integer, String, Boolean as well as
 * the types created by the programmer, such as GameInterface, GamePerson, GameFurniture, etc. All these types have a
 * unique String identifier. The types created by the programmer also have a list of functions that can be applied to
 * the elements of these types.
 */
public enum Type {
    INTEGER("Integer", null, null),
    STRING("String", null, null),
    BOOLEAN("Boolean", null, null);

    public final String ID; //unique String identifier
    public final Operator[] ops; //the list of functions (operators) that can be called by the person who creates games
    public final Variable[] vars; //the list of variables that belong to the class of this type with the initial values

    Type(String ID, Operator[] ops, Variable[] vars) {
        this.ID = ID;
        this.ops = ops;
        this.vars = vars;
    }

}
