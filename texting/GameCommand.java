package texting;



/**
 * Created by alexanderfedchin on 7/18/17.
 */
public class GameCommand {
    public GameCommand.Type type;
    public Expression expr;
    public GameCommand[] edges;
    public int varToSet;

    public GameCommand() {

    }

    public void set(GameCommand.Type type, String str, GameCommand[] edges, Operator[] operators, Variable[] variables) {
        this.type = type;
        this.edges = edges;
        if (type == Type.SETTING) {
            int i = 0;
            while (str.charAt(i) == ' ')
                i++;
            int startIndex = i;
            while ((str.charAt(i) != '=') && (str.charAt(i) != ' '))
                i++;
            varToSet = Tokenizer.getIndex(variables, str.substring(startIndex, i));
            while ((str.charAt(i) == ' ') || (str.charAt(i) == '='))
                i++;
            this.expr = new Expression(str.substring(i), operators, variables);
        } else {
            this.expr = new Expression(str, operators, variables);
            varToSet = -1;
        }
    }

    public GameCommand next(GameObject obj) {
        switch (type) {
            case SETTING: {
                obj.variables[varToSet].value = expr.evaluate(obj);
            }
            case ACTION:
                return edges[0];
            case CONDITION: {
                if (!expr.evaluateCondition(obj)) {
                    if (edges.length > 1)
                        return edges[1];
                } else
                    return edges[0];
            }
            default:
                return null;
        }
    }

    protected enum Type {
        POSITION(10, "position"),
        ACTION(1, "action"),
        SETTING(1, "setting"),
        CONDITION(2, "condition");

        public final String name;
        public final int maxEdges;

        Type(int maxEdges, String name) {
            this.maxEdges = maxEdges;
            this.name = name;
        }
    }

}
