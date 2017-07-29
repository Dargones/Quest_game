package texting;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by alexanderfedchin on 7/16/17.
 */
public class GameObject {
    private boolean hold;
    private boolean stateChanged;
    private String text;
    private String actionText;
    private ArrayList<GameCommand> actions;
    public Variable[] variables;

    public GameObject() {
        variables = new Variable[2];
        variables[0] = new Variable("questionAsked", Type.BOOLEAN, String.valueOf(Boolean.FALSE));
        variables[1] = new Variable("relationship", Type.INTEGER, String.valueOf(0));
        GameCommand[] commands = new GameCommand[10];
        for (int i = 0; i < commands.length; i++)
            commands[i] = new GameCommand();
        commands[0].set(GameCommand.Type.POSITION, "You are talking to the powerful magician of Numinor.",
                new GameCommand[] {commands[1], commands[6]}, null);
        commands[1].set(GameCommandType.CONDITION, "questionAsked = 0", new GameCommand[] {commands[2]}, new Variable[] {variables[0]});
        commands[2].set(GameCommandType.ACTION, "Ask him how he became a magician.", new GameCommand[] {commands[3]}, null);
        commands[3].set(GameCommandType.CONDITION, "relationship > 1",
                new GameCommand[] {commands[4], commands[5]}, new Variable[] {variables[1]});
        commands[4].set(GameCommandType.SETTING, "1", new GameCommand[] {commands[8]}, new Variable[] {variables[0]});
        commands[8].set(GameCommandType.POSITION, "You are talking to the powerful magician of Numinor.\n The magician says: I was taught by another magician.",
                new GameCommand[] {commands[1], commands[6]}, null);
        commands[5].set(GameCommandType.POSITION, "You are talking to the powerful magician of Numinor.\n The magician says: I will tell you this one day, but not now.",
                new GameCommand[] {commands[1], commands[6]}, null);
        commands[6].set(GameCommandType.ACTION, "Give the magician a precious stone", new GameCommand[] {commands[7]}, null);
        commands[7].set(GameCommandType.SETTING, "relationship + 1", new GameCommand[] {commands[9]}, new Variable[] {variables[1], variables[1]});
        commands[9].set(GameCommandType.POSITION, "You are talking to the powerful magician of Numinor.\n The magician is pleased that you gave him the precious stone",
                new GameCommand[] {commands[1], commands[6]}, null);
        text = commands[0].str;
        actions = new ArrayList<>(2);
        actions.add(commands[2]);
        actions.add(commands[6]);
        actionText = "";
        for (GameCommand action: actions)
            actionText += action.str + "\n";
        stateChanged = false;
    }

    public boolean getHold() {
        return hold;
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
        while (current.type != GameCommandType.POSITION)
            current = current.next();
        text = current.str;
        actionText = "";
        actions = new ArrayList<>(current.edges.length);
        for (int i = 0; i < current.edges.length; i++) {
            GameCommand thisOption = current.edges[i];
            while ((thisOption != null) && (thisOption.type != GameCommandType.ACTION))
                thisOption = thisOption.next();
            if (thisOption == null)
                continue;
            actionText += thisOption.str+"\n";
            actions.add(thisOption);
        }
    }
}
