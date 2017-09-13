package texting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alexanderfedchin on 7/16/17.
 */
public class GameObject {
    private boolean stateChanged;
    private String text;
    private ArrayList<String> actionText;
    private ArrayList<GameCommand> actions;
    public Variable[] variables;
    public static FileReader fileR;
    public static BufferedReader fileBR;

    public GameObject(String filename) {
        String[] lines = getFileContent(filename);
        int i = 1;
        variables = new Variable[linesBeforeNextBreak(lines, i)];
        for (i = 1; i < variables.length + 1; i++) {
            String[] coord = lines[i].split(" ");
            Type type = null;
            for (Type t: Type.values())
                if (t.ID.equals(coord[0])) {
                    type = t;
                    break;
                }
            String tmp = coord[2];
            if (coord[2].charAt(0)=='\"')
                tmp = coord[2].substring(1);
            if (coord.length > 3) {
                for (int j = 3; j < coord.length; j++) {
                    if (coord[j].charAt(coord[j].length() - 1) == '\"')
                        tmp += " " + coord[j].substring(0,coord[j].length() - 1);
                    else
                        tmp += " " + coord[j];
                }
            }
            variables[i - 1] = new Variable(coord[1], type, tmp);
        }

        Arrays.sort(variables);

        i += 2;
        GameCommand[] commands = new GameCommand[linesBeforeNextBreak(lines, i)];
        for (int j = 0; j < commands.length; j++)
            commands[j] = new GameCommand();

        for (int j = 0; j < commands.length; j++) {
            String line = lines[i];
            int k = 0;
            while (line.charAt(k)!=' ')
                k++;
            GameCommand.Type type = null;
            for (GameCommand.Type t: GameCommand.Type.values())
                if (t.name.equals(line.substring(0,k))) {
                    type = t;
                    break;
                }
            while (line.charAt(k)==' ')
                k++;
            boolean insideAString = false;
            int startIndex = k;
            while ((k < line.length())&&(insideAString || line.charAt(k) != ' ')) {
                if (line.charAt(k) == '\"')
                    insideAString = !insideAString;
                k++;
            }
            String str = line.substring(startIndex, k);
            while ((k < line.length())&&(line.charAt(k)==' '))
                k++;

            GameCommand[] edges;
            if (k == line.length())
                edges = null;
            else {
                String[] coord = line.substring(k).split(" ");
                edges = new GameCommand[coord.length];
                for (k = 0; k < edges.length; k++)
                    edges[k] = commands[Integer.parseInt(coord[k])];
            }
            commands[j].set(type, str, edges, null, variables);
            i++;
        }

        GameCommand current = commands[0];
        text = current.expr.evaluate(this);
        actionText = new ArrayList<>();
        actions = new ArrayList<>(current.edges.length);
        for (int j = 0; j < current.edges.length; j++) {
            GameCommand thisOption = current.edges[j];
            while ((thisOption != null) && (thisOption.type != GameCommand.Type.ACTION))
                thisOption = thisOption.next(this);
            if (thisOption == null)
                continue;
            actionText.add(thisOption.expr.evaluate(this));
            actions.add(thisOption);
        }
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

    public ArrayList<String> getActions() {
        return actionText;
    }

    public void act(String command) {
        stateChanged = false;
        GameCommand current = null;
        /*for (int i = 0; i < actionText.size(); i++)
            if (command.equals(actionText.get(i))) {
                current = actions.get(i);
                stateChanged = true;
                break;
            }*/
        try {
            current = actions.get(Integer.parseInt(command));
        } catch (Exception e) {
            System.out.println("Write a number please");
            return;
        }

        while (current.type != GameCommand.Type.POSITION)
            current = current.next(this);
        text = current.expr.evaluate(this);
        if (current.edges == null) {
            actions = null;
            actionText = new ArrayList<>();
            actionText.add("You won. Game over.");
        } else {
            actionText = new ArrayList<>();
            actions = new ArrayList<>(current.edges.length);
            for (int i = 0; i < current.edges.length; i++) {
                GameCommand thisOption = current.edges[i];
                while ((thisOption != null) && (thisOption.type != GameCommand.Type.ACTION))
                    thisOption = thisOption.next(this);
                if (thisOption == null)
                    continue;
                actionText.add(thisOption.expr.evaluate(this));
                actions.add(thisOption);
            }
        }
        stateChanged = true;
    }

    public static String[] getFileContent(String file) {
        //TODO merge with the function from Map.java
        try {
            fileR = new FileReader(file);
            fileBR = new BufferedReader(fileR);
            String[] lines = new String[Integer.parseInt(fileBR.readLine())];
            for (int i=0;i<lines.length;i++) {
                lines[i] = fileBR.readLine();
            }
            fileBR.close();
            fileR.close();
            return lines;
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + file + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + file + "'");
        }
        return null;
    }

    /**
     * Returs the number of lines that is left before the next empty line or the end of the file appear
     * @param lines
     * @param index
     * @return
     */
    private int linesBeforeNextBreak(String[] lines, int index) {
        //TODO merge with the function from Map.java
        int start = index;
        while ((index < lines.length)&&(!lines[index].equals("")))
            index ++;
        return index - start;
    }
}
