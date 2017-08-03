package texting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by alexanderfedchin on 7/24/17.
 */
public class Expression {
    private static final String TRUE = Boolean.TRUE.toString();
    private Operator[] ops;
    private IndexedPair<Integer>[] varsIndexes;
    private IndexedPair<Variable>[] constants;


    public Expression(String expression, Operator[] operators, Variable[] vars) {
        Tokenizer kek = new Tokenizer(expression, vars, operators);
        kek.print("", vars);
        ops = kek.operatorsFinal;
        varsIndexes = kek.varsFinal;
        constants = kek.constantsFinal;
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
                result.add(constants[constP].o.value);
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
    private <T extends Named> int getIndex(T[] arr, String key) {
        //binary search
        int lo = 0;
        int hi = arr.length - 1;
        while (lo < hi) {
            int mid = (lo + hi)/2;
            if (arr[mid].getName().equals(key)) {
                lo = mid;
                break;
            } else if (arr[mid].getName().compareTo(key) > 0 )
                hi = mid - 1;
            else
                lo = mid + 1;
        }
        if (arr[lo].getName().equals(key)) {
            while ((lo > 0) && (arr[lo - 1].getName().equals(key)))
                lo--;
            return lo;
        }
        return -1;
    }

    /**
     * This class processes the String and separates it into tokens. Tokens can be of 5 types:
     * - other Tokens (these are what is separated by the parenthesis)
     * - constants. These can represent different types, but inside the code they all are stored as Strings
     * - variables. These are "non-static" variables
     * - operators. A set of predefined binary and unary operations that are used with infix notation and have order
     *   of precedence
     * - predefined_functions. All the other functions that are used with prefix notation
     * 
     * the orderArray is an arrayList that stores the information about the order in which the tokens appear. The values
     * corresponding to these 5 types are: TOKEN, CONSTANT, VARIABLE, OPERATOR, and FUNCTION
     * 
     * the class also has convertToPostfix method that transformes everything using the postfix notation
     */
    private class Tokenizer {
        //TODO rewrite all the system in a way that at first all the tokens get separated, with operators' priorities
        //being defined. Based on this the correct parameter types for the operators should be calculated.
        ArrayList<Tokenizer> tokens;
        ArrayList<Integer> vars;
        ArrayList<Variable> constants;
        ArrayList<Operator.OpWithPriority> ops;
        ArrayList<Operator> funcs;
        ArrayList<TokenType> orderArray;

        //the following variables are only needed during the tokenizing and are insignificant then extracting the
        //result:

        int i; //the index of the next character to process
        TokenType currentT; //the type of the Token that is currently processed (if known)
        int separationStart; //the index of the beginning of the token processed
        boolean insideAString = false; // this variable indicates whether the pointer is inside a string constant.
        // the variable is needed in two cases. Firstly, if a bracket is met inside the string constant, the
        // bracket should be processed as a part of that constant. Secondly, this variable is useful to distinguish
        //between the string and the integer variable types.
        int countBrackets = 0; //number of opening brackets met.


        //the following variables represent the result of the tokenization
        Type returnType;
        IndexedPair<Integer>[] varsFinal;
        IndexedPair<Variable>[] constantsFinal;
        Operator[] operatorsFinal;

        /** This is the recursive constructor
         * @param expression expression to extract the tokens from 
         * @param objVars    the variables of the object that this expression operates on
         * @param objOps     the operators of the object that this expression operates on
         */
        Tokenizer(String expression, Variable[] objVars, Operator[] objOps) {
            varsFinal = null;
            constantsFinal = null;
            operatorsFinal = null;
            //TODO Special characters inside the Strings such as \", etc.
            tokens = new ArrayList<>(2);
            vars = new ArrayList<>(2);
            constants = new ArrayList<>(2);
            ops = new ArrayList<>(2);
            funcs = new ArrayList<>(2);
            orderArray = new ArrayList<>(10);
            returnType = null;
            i = 0;
            currentT = null;
            separationStart = 0;
            insideAString = false;
            countBrackets = 0;
            expression += " ";
            while (i < expression.length()) {
               processNextCharacter(expression, objVars, objOps);
            }
            getTypeAndMakePostfix(objVars);
        }


        private void processNextCharacter(String expression, Variable[] objVars, Operator[] objOps) {
            char ch = expression.charAt(i);

            TokenType oldT = currentT;
            int oldStart = separationStart;
            boolean callExtract = false;
            if (currentT == TokenType.TOKEN) { //if the pointer is inside a token that is to be separated
                if (insideAString) {
                    if (ch == '\"')
                        insideAString = false;
                } else if (ch == '(') {
                    countBrackets ++;
                } else if (ch == ')') {
                    countBrackets --;
                    if (countBrackets == 0) {
                        //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                        callExtract = true;
                        currentT = null;
                    }
                } else if (ch == ',') {
                    if (countBrackets == 1) {
                        //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                        callExtract = true;
                        separationStart = i + 1;
                    }
                } else if (ch == '\"')
                    insideAString = true;
            } else if (currentT == TokenType.CONSTANT) { //if the pointer is inside a constant (not of the BOOLEAN type)
                if ((insideAString) && (ch == '\"')) {
                    callExtract = true;
                    currentT = null;
                } else if ((!insideAString) && ((ch != '0') && ((ch <= '1') || (ch >= '9')))) {
                    callExtract = true;
                    if (((ch <= 'z') && (ch >= 'a')) || ((ch <= 'Z') && (ch >= 'A')))
                        currentT = TokenType.VARIABLE;
                    else if (ch == ' ')
                        currentT = null;
                    else
                        currentT = TokenType.OPERATOR;
                    separationStart = i;
                }
            } else {
                switch (ch) {
                    case '(': { //opening parenthesis (TOKEN type)
                        if (currentT != null) {
                            //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                            callExtract = true;
                        }
                        currentT = TokenType.TOKEN;
                        //the i increment and how to deal with it?
                        separationStart = i + 1;
                        countBrackets = 1;
                        break;
                    }
                    case '\"': { //opening quotation mark (constant of type String)
                        if (currentT != null) {
                            //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                            callExtract = true;
                        }
                        currentT = TokenType.CONSTANT;
                        separationStart = i + 1;
                        insideAString = true;
                        break;
                    }
                    case ' ': { // may signify the end of the variable/operator/function name
                        if (currentT != null) {
                            //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                            callExtract = true;
                            currentT = null;
                        }
                        separationStart = i + 1;
                        break;
                    }
                    default: { // not inside a token or a string constant and not a space, that is one of the following:
                        // - a part of spelling of variable name, operator or a constant (of BOOLEAN or INTEGER type).
                        // constants of the integer type may only begin here
                        if ((ch == '0') || (ch >= '1') && (ch <= '9')) {
                            if (currentT == null) {
                                currentT = TokenType.CONSTANT;
                                insideAString = false;
                                separationStart = i;
                            } else if (currentT == TokenType.OPERATOR) {
                                callExtract = true;
                                //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                                currentT = TokenType.CONSTANT;
                                insideAString = false;
                                separationStart = i;
                            }
                            break;
                        }
                        //Now the only possibility are appending to a variable/operator and possibly finishing one
                        if (((ch <= 'z') && (ch >= 'a')) || ((ch <= 'Z') && (ch >= 'A'))) {
                            if (currentT == TokenType.OPERATOR) {
                                callExtract = true;
                                //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                                currentT = TokenType.VARIABLE;
                                separationStart = i;
                            } else if (currentT == null) {
                                currentT = TokenType.VARIABLE;
                                separationStart = i;
                            }
                        } else {
                            //This is appending to an operator
                            if (currentT == TokenType.VARIABLE) {
                                callExtract = true;
                                //extractToken(currentT, expression, separationStart, i, objVars, objOps);
                                currentT = TokenType.OPERATOR;
                                separationStart = i;
                            } else if (currentT == null) {
                                currentT = TokenType.OPERATOR;
                                separationStart = i;
                            }
                        }
                    }
                }
            }
            i++;
            if (callExtract)
                extractToken(oldT, expression, oldStart, i - 1, objVars, objOps);
        }


        private Type getTypeAndMakePostfix(Variable[] objVars) {
            if (returnType != null)
                return returnType;
            int operatorsTotal = ops.size() + funcs.size();
            int constantsTotal = constants.size();
            int variableTotal = vars.size();
            for (Tokenizer t: tokens) {
                operatorsTotal += t.operatorsFinal.length;
                constantsTotal += t.constantsFinal.length;
                variableTotal += t.varsFinal.length;
            }
            varsFinal = new IndexedPair[variableTotal];
            operatorsFinal = new Operator[operatorsTotal];
            constantsFinal = new IndexedPair[constantsTotal];

            LinkedList<Operator.OpWithPriority> opStack = new LinkedList<>();
            int j = 0;
            int constI = 0;
            int opI = 0;
            int varI = 0;
            int funcI = 0;
            int tokI = 0;

            int opFinalI = 0;
            int varFinalI = 0;
            int constFinalI = 0;

            boolean unaryOperatorMet = false;
            int tokensBeforeFunction = -1;
            Operator function = null;
            while (j < orderArray.size()) {
                switch (orderArray.get(j)) {
                    case CONSTANT: {
                        constantsFinal[constFinalI ++] = new IndexedPair<>(constants.get(constI ++), constFinalI + varFinalI);
                        if (unaryOperatorMet) {
                            operatorsFinal[opFinalI++] = opStack.pop().o;
                            unaryOperatorMet = false;
                        }
                        break;
                    }
                    case OPERATOR: {
                        Operator.OpWithPriority nextOperator = ops.get(opI++);
                        if ((opStack.isEmpty())||(nextOperator.p < opStack.peek().p))
                            opStack.add(nextOperator);
                        else {
                            operatorsFinal[opFinalI ++] = opStack.pop().o;
                            opStack.add(nextOperator);
                        }
                        if (nextOperator.o.numberOfParameters == 1)
                            unaryOperatorMet = true;
                        break;
                    }
                    case VARIABLE: {
                        varsFinal[varFinalI ++] = new IndexedPair<>(vars.get(varI ++), constFinalI + varFinalI);
                        if (unaryOperatorMet) {
                            operatorsFinal[opFinalI++] = opStack.pop().o;
                            unaryOperatorMet = false;
                        }
                        break;
                    }
                    case FUNCTION: {
                        function = funcs.get(funcI ++);
                        tokensBeforeFunction = function.numberOfParameters;
                        break;
                    }
                    case TOKEN: {
                        Tokenizer thisToken = tokens.get(tokI++);
                        for (Operator o: thisToken.operatorsFinal)
                            operatorsFinal[opFinalI ++] = o;
                        int lastIndex = varFinalI + constFinalI;
                        for (IndexedPair<Integer> i: thisToken.varsFinal)
                            varsFinal[varFinalI ++] = new IndexedPair<>(i.o, i.p + lastIndex);
                        for (IndexedPair<Variable> i: thisToken.constantsFinal)
                            constantsFinal[constFinalI ++] = new IndexedPair<>(i.o, i.p + lastIndex);
                        if (tokensBeforeFunction > -1) {
                            tokensBeforeFunction --;
                            if (tokensBeforeFunction == 0) {
                                operatorsFinal[opFinalI ++] = function;
                                tokensBeforeFunction = -1;
                            }
                        } else if (unaryOperatorMet) {
                            operatorsFinal[opFinalI++] = opStack.pop().o;
                            unaryOperatorMet = false;
                        }
                        break;
                    }
                }
                j++;
            }
            while (!opStack.isEmpty())
                operatorsFinal[opFinalI ++] = opStack.pop().o;
            if (operatorsTotal == 0) {
                if (constantsTotal == 0)
                    returnType = objVars[varsFinal[0].o].type;
                else
                    returnType = constantsFinal[0].o.type;
            } else
                returnType = operatorsFinal[operatorsTotal -1].returnType;
            return returnType;
        }
        
        
        private Type getLastTokenType (TokenType tokenType, Variable[] objVars) {
            switch (tokenType) {
                case VARIABLE:
                    return objVars[vars.get(vars.size() - 1)].type;
                case TOKEN:
                    return tokens.get(tokens.size() - 1).getTypeAndMakePostfix(objVars);
                case CONSTANT:
                    return  constants.get(constants.size() - 1).type;
                case FUNCTION:
                    return funcs.get(funcs.size() - 1).returnType;
                case OPERATOR:
                    return ops.get(ops.size() - 1).o.returnType;
                default:
                    return null;
            }
        }


        private void extractToken(TokenType type, String expression, int startIndex, int endIndex, Variable[] objVars, Operator[] objOps) {
            String str = expression.substring(startIndex, endIndex);
            if (type != TokenType.VARIABLE)
                orderArray.add(type);
            switch (type) {
                case TOKEN: {
                    tokens.add(new Tokenizer(str, objVars, objOps));
                    break;
                }
                case CONSTANT: {
                    //Boolean type is always considered to be a Variable until it is resolved that the name of the
                    //variable is "true" or "false".
                    if (insideAString)
                        constants.add(new Variable(null, Type.STRING, str));
                    else
                        constants.add(new Variable(null, Type.INTEGER, str));
                    break;
                }
                case OPERATOR: {
                    int opIndex = getIndex(Operator.STANDART, str);
                    ops.add(Operator.STANDART[opIndex]);
                    boolean isUnary = false;
                    Type previousTokenType = null;
                    if ((orderArray.size() == 1) || (orderArray.get(orderArray.size() - 2) == TokenType.OPERATOR))
                        isUnary = true;
                    else {
                        int j = orderArray.size() - 2;
                        int opI = ops.size() - 2;
                        Operator.OpWithPriority chosen = null;
                        while (j >= 0) {
                            if (orderArray.get(j) == TokenType.OPERATOR) {
                                if (ops.get(opI).p >= Operator.STANDART[opIndex].p)
                                    break;
                                else if ((chosen == null) || (chosen.p < ops.get(opI).p))
                                    chosen = ops.get(opI);
                            }
                            j--;
                        }
                        if (chosen != null)
                            previousTokenType = chosen.o.returnType;
                        else
                            previousTokenType = getLastTokenType(orderArray.get(j + 1), objVars);
                    }

                    int previousSize = orderArray.size();
                    int newOperatorIndex = ops.size() - 1;
                    int currentSize = previousSize;
                    Operator.OpWithPriority chosen = null;
                    do {
                        processNextCharacter(expression, objVars, objOps);
                        if (orderArray.size() != currentSize) {
                            currentSize++;
                            if (orderArray.get(orderArray.size() - 1) == TokenType.OPERATOR) {
                                if ((ops.get(ops.size() - 1).p >= Operator.STANDART[opIndex].p))
                                    break;
                                else if ((chosen == null) || (chosen.p < ops.get(ops.size() - 1).p))
                                    chosen = ops.get(ops.size() - 1);
                            }
                        }
                    } while (i < expression.length());

                    Type nextTokenType;
                    if (chosen == null)
                        nextTokenType = getLastTokenType(orderArray.get(previousSize), objVars);
                    else
                        nextTokenType = chosen.o.returnType;
                    
                    boolean thisIstheRightFunction;
                    
                    do {
                        thisIstheRightFunction = true;
                        if (isUnary) {
                            if ((Operator.STANDART[opIndex].o.numberOfParameters != 1) || 
                                    (Operator.STANDART[opIndex].o.parType[0] != previousTokenType)) {
                                thisIstheRightFunction = false;
                                opIndex ++;
                            }
                        } else {
                            if ((Operator.STANDART[opIndex].o.numberOfParameters != 2) || 
                                    (Operator.STANDART[opIndex].o.parType[0] != previousTokenType) || 
                                    (Operator.STANDART[opIndex].o.parType[1] != nextTokenType)) {
                                thisIstheRightFunction = false;
                                opIndex ++;
                            }
                        }
                    } while (!thisIstheRightFunction);
                    ops.set(newOperatorIndex, Operator.STANDART[opIndex]);
                    break;
                }
                case VARIABLE: {
                    if ((str.equals(Boolean.TRUE.toString()))||(str.equals(Boolean.FALSE.toString()))) {
                        orderArray.add(TokenType.CONSTANT);
                        constants.add(new Variable(null, Type.BOOLEAN ,str));
                    } else {
                        int currIndex = endIndex;
                        while ((currIndex < expression.length() - 1) && (expression.charAt(currIndex) == ' '))
                            currIndex ++;
                        if (expression.charAt(currIndex) != '(') { // there are no parenthesis after a title, hence this
                            //is a variable and not an operator
                            orderArray.add(TokenType.VARIABLE);
                            vars.add(getIndex(objVars, str));
                        } else {
                            orderArray.add(TokenType.FUNCTION);
                            int tokensCount = -orderArray.size();
                            do {
                                processNextCharacter(expression, objVars, objOps);
                            } while ((i < expression.length()) && (currentT == TokenType.TOKEN));
                            int funcIndex = getIndex(objOps, str);
                            Operator[] funcArray;
                            if (funcIndex == -1) {
                                funcIndex = getIndex(Operator.STANDART_F, str);
                                funcArray = Operator.STANDART_F;
                            } else
                                funcArray = objOps;

                            boolean thisIstheRightFunction;
                            tokensCount += orderArray.size();
                            do {
                                thisIstheRightFunction = true;
                                if (funcArray[funcIndex].numberOfParameters != tokensCount) {
                                    funcIndex++;
                                    thisIstheRightFunction = false;
                                } else {
                                    for (int j = tokensCount; j > 0; j--) {
                                        if (tokens.get(tokens.size() - j).getTypeAndMakePostfix(objVars) != funcArray[funcIndex].parType[tokensCount - j]) {
                                            funcIndex++;
                                            thisIstheRightFunction = false;
                                            break;
                                        }
                                    }
                                }
                            } while (!thisIstheRightFunction);
                            funcs.add(funcArray[funcIndex]);
                        }
                    }
                    break;
                }
            }
        }


        void print(String begin, Variable[] objVars) {
            int constI = 0;
            int opI = 0;
            int varI = 0;
            int funcI = 0;
            int tokI = 0;
            for (TokenType t: orderArray) {
                switch (t) {
                    case CONSTANT: {
                        System.out.println(begin + "CONSTANT: "+ constants.get(constI++));
                        break;
                    }
                    case OPERATOR: {
                        System.out.println(begin + "OPERATOR: "+ ops.get(opI++).getName());
                        break;
                    }
                    case VARIABLE: {
                        System.out.println(begin + "VARIABLE: "+ objVars[vars.get(varI++)].getName());
                        break;
                    }
                    case FUNCTION: {
                        System.out.println(begin + "FUNCTION: "+funcs.get(funcI++).getName());
                        break;
                    }
                    case TOKEN: {
                        System.out.println(begin + "TOKEN: ");
                        tokens.get(tokI++).print(begin + "\t", objVars);
                        break;
                    }
                }
            }
        }

    }

    /**
     * All the possible types of tokens described in the javadoc for the Tokens class.
     */
    private enum TokenType {
        TOKEN,
        VARIABLE,
        CONSTANT,
        OPERATOR,
        FUNCTION;
    }

    private class IndexedPair<T> {
        private final T o; //object
        private final int p; //position

        public IndexedPair(T o, int p) {
            this.p = p;
            this.o = o;
        }
    }


    public static void main(String[] pars) {
        Variable[] vars = new Variable[] {new Variable("PI", Type.INTEGER, "3"),
                                        new Variable("i", Type.BOOLEAN, "True")};
        Operator[] ops = new Operator[] {new Operator("kek", 2, Type.BOOLEAN, new  Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) == 2 * Integer.parseInt(b[1])))};
        String intro = "(kek(PI - 2^ (7+1), 6) == i) || (\"kukare)ku\" + \"he(h\" == \"FGH\")";
        Expression example = new Expression(intro, ops, vars);
        GameObject shell = new GameObject(vars);
        System.out.println("Result: "+ example.evaluate(shell));
    }
}
