package texting;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by alexanderfedchin on 7/16/17.
 */
public class GameObject {
    private boolean stateChanged;
    private String text;
    private String actionText;
    private ArrayList<GameCommand> actions;
    public Variable[] variables;

    public GameObject(Variable[] variables) {
        this.variables = variables;
        //TODO
        text = "";
        actionText = "";
        stateChanged = false;
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public boolean getStateChanged() {
        return stateChanged;
    }

    public String getText() {
        return text;
    }

    public String getActions() {
        return actionText;
    }

    public void act(String command) {
        stateChanged = false;
        GameCommand current = null;
        for (GameCommand action: actions)
            if (command.equals(action.str)) {
                current = action;
                stateChanged = true;
                break;
            }
        if (!stateChanged)
            return;
        while (current.type != GameCommand.Type.POSITION)
            current = current.next();
        text = current.str;
        actionText = "";
        actions = new ArrayList<>(current.edges.length);
        for (int i = 0; i < current.edges.length; i++) {
            GameCommand thisOption = current.edges[i];
            while ((thisOption != null) && (thisOption.type != GameCommand.Type.ACTION))
                thisOption = thisOption.next();
            if (thisOption == null)
                continue;
            actionText += thisOption.str+"\n";
            actions.add(thisOption);
        }
    }
}
