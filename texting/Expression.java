package texting;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by alexanderfedchin on 7/24/17.
 */
public class Expression {
    private static final String TRUE = Boolean.TRUE.toString();
    private Operator[] ops;
    private IndexedPair<Integer>[] varsIndexes;
    private IndexedPair<String>[] constants;


    public Expression(String expression, Operator[] operators, Variable[] vars) {

    }


    public String evaluate(GameObject obj) {
        Stack<String> vars = getVars(obj.variables);
        for (int i = 0; i < ops.length; i++) {
            Operator operator = ops[i];
            String[] pars = new String[operator.numberOfParameters];
            for (int j = 0; j < pars.length; j++)
                pars[j] = vars.pop();
            vars.add(operator.func.evaluate(obj, pars));
        }
        return vars.pop();
    }

    public boolean evaluateCondition(GameObject obj) {
        if (ops[ops.length - 1].returnType != Type.BOOLEAN) {
            System.out.println("Warning: type mismatch");
            return false;
        }
        return evaluate(obj) == TRUE;
    }

    private Stack<String> getVars(Variable[] objVars) {
        Stack<String> result =  new Stack<>();
        int varsP = varsIndexes.length;
        int constP = constants.length;
        while ((varsP != 0) || (constP != 0)) {
            if ((constP == 0) || (varsIndexes[varsP].p > constants[constP].p))
                result.add(objVars[varsIndexes[varsP].o].value);
            else
                result.add(constants[constP].o);
        }
        return result;
    }

    /**
     * A function taht takes an array of Tokens (of Variables or Operators that are named) that is sorted by the name
     * and searches for the first entry with the same name.
     * @param arr the array to searches in
     * @param key the key to search for
     * @param <T> the type of the array
     * @return if no eligible element was found, return -1, otherwise return the index of such an element
     */
    private <T extends Token> int getIndex(T[] arr, String key) {
        //binary search
        int lo = 0;
        int hi = arr.length - 1;
        while (lo < hi) {
            int mid = (lo + hi)/2;
            if (arr[mid].getName().equals(key)) {
                lo = mid;
                hi = mid;
            } else if (arr[mid].getName().compareTo(key) > 0 )
                hi = mid - 1;
            else
                lo = mid + 1;
        }
        if (arr[lo].getName().equals(key)) {
            while ((lo > 0) && (arr[lo].getName().equals(key)))
                lo--;
            return lo;
        }
        return -1;
    }

    /**
     * This class processes the String and separates it into tokens. Tokens can be of 5 types:
     * - other Tokens (these are what is separated by the parenthesis)
     * - constants. These can represent diferent types, but inside the code they all are stored as Strings
     * - variables. These are "non-static" variables
     * - operators. A set of predefined binary operations that are used with infix notation and have order of precedence
     * - functions. All the other functions that are used with prefix notation
     * 
     * the orderArray is an arrayList that stores the information about the order in which the tokens appear. The values
     * corresponding to these 5 types are: TOKEN, CONSTANT, VARIABLE, OPERATOR, and FUNCTION
     * 
     * the class also has convertToPostfix method that transformes everything using the postfix notation
     */
    private class Tokens {
        ArrayList<Tokens> tokens;
        ArrayList<Variable> vars;
        ArrayList<String> constants;
        ArrayList<Operator> ops;
        ArrayList<Operator> funcs;
        ArrayList<TokenType> orderArray;

        /** This is the recursive constructor
         * @param expression expression to extract the tokens from 
         * @param objVars    the variables of the object that this expression operates on
         * @param objOps     the operators of the object that this expression operates on
         */
        Tokens(String expression, Variable[] objVars, Operator[] objOps) {
            int i = 0;
            TokenType currentT = null;
            int separationStart = 0;
            String current = "";
            while (i < expression.length()) {
                char ch = expression.charAt(i);
                if (currentT == TokenType.TOKEN) {
                    if (ch == ')') {
                        orderArray.add(TokenType.TOKEN);
                        tokens.add(new Tokens(expression.substring(separationStart + 1, i), objVars, objOps)); //TODO check substring parameters requirements
                        currentT = null;
                    }
                    i ++;
                    continue;
                }
                if (currentT == TokenType.CONSTANT) {
                    if (ch == '\"') {
                        orderArray.add(TokenType.CONSTANT);
                        constants.add(expression.substring(separationStart + 1, i));
                        currentT = null;
                    }
                    i ++;
                    continue;
                }
                switch (ch) {
                    case ' ': {
                        if (!current.equals("")) {
                            if (currentT == TokenType.OPERATOR) {
                                orderArray.add(TokenType.OPERATOR);
                                ops.add(Operator.STANDART[getIndex(Operator.STANDART, current)]);
                            } else if (currentT == TokenType.VARIABLE) {
                                int index = getIndex(objVars, current);
                                if (index == -1) {
                                    orderArray.add(TokenType.FUNCTION);
                                    funcs.add(objOps[getIndex(objOps, current)]);
                                } else {
                                    orderArray.add(TokenType.VARIABLE);
                                    vars.add(objVars[index]);
                                }
                            }
                            current = "";
                        }
                        break;
                    }
                    case '(': {
                        currentT = TokenType.TOKEN;
                        separationStart = i;
                        break;
                    }
                    case '\"': {
                        currentT = TokenType.CONSTANT;
                        separationStart = i;
                        break;
                    }
                    default: {
                        if (((ch < 'z') && (ch > 'a')) || ((ch < 'Z') && (ch > 'A')) || ((current != "") && ((ch == '0') || (ch > '1') && (ch < '9')))) {
                            if ((current != "") && (currentT == TokenType.OPERATOR)) {
                                orderArray.add(TokenType.OPERATOR);
                                ops.add(Operator.STANDART[getIndex(Operator.STANDART, current)]);
                                current = "";
                            }
                            current += ch;
                            currentT = TokenType.VARIABLE;
                        } else {
                            if ((current != "") && (currentT == TokenType.VARIABLE)) {
                                int index = getIndex(objVars, current);
                                if (index == -1) {
                                    orderArray.add(TokenType.FUNCTION);
                                    funcs.add(objOps[getIndex(objOps, current)]);
                                } else {
                                    orderArray.add(TokenType.VARIABLE);
                                    vars.add(objVars[index]);
                                }
                                current = "";
                            }
                            current += ch;
                            currentT = TokenType.OPERATOR;
                        }
                    }
                }
                i ++;
            }
        }
        
        void convertToPostfix() {
            
        }

    }

    /**
     * All the possible types of tokens described in the javadoc for Tokens class.
     */
    private enum TokenType {
        TOKEN,
        VARIABLE,
        CONSTANT,
        OPERATOR,
        FUNCTION;
    }
    
    
    public static void main() {
        Variable[] vars = new Variable[] {new Variable("PI", Type.INTEGER, "3"),
                                        new Variable("i", Type.BOOLEAN, "True")};
        Operator[] ops = new Operator[] {new Operator("kek", 2, Type.BOOLEAN, new  Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) == 2 * Integer.parseInt(b[1])))};
        String intro = "kek(PI - \"2\", \"6\") == i";
    }
}
