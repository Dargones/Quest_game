package texting;


import java.math.BigDecimal;

/**
 * Created by alexanderfedchin on 7/18/17.
 */
public class GameCommand {
    public GameCommand.Type type;
    public String str;
    public GameCommand[] edges;
    private Variable[] variables;

    public GameCommand() {
        set(null, null, null, null);
    }

    public void set(GameCommand.Type type, String str, GameCommand[] edges, Variable[] variables) {
        this.variables = variables;
        this.type = type;
        this.str = str;
        this.edges = edges;
    }

    public GameCommand next() {
        switch (type) {
            case SETTING: {
                Expression expr = new Expression(str);
                for (int i = 1; i < variables.length; i++) {
                    expr = expr.with(variables[i].name, variables[i].value);
                }
                variables[0].value = expr.eval();
            }
            case ACTION:
                return edges[0];
            case CONDITION: {
                Expression expr = new Expression(str);
                for (int i = 0; i < variables.length; i++) {
                    expr = expr.with(variables[i].name, variables[i].value);
                }
                if (expr.eval().equals(new BigDecimal(0))) {
                    if (edges.length > 1)
                        return edges[1];
                } else
                    return edges[0];
            }
            default:
                return null;
        }
    }

    private enum Type {
        POSITION(10),
        ACTION(1),
        SETTING(1),
        CONDITION(2);

        public final int maxEdges;

        GameCommandType(int maxEdges) {
            this.maxEdges = maxEdges;
        }
    }

}
