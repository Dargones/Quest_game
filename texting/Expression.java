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
        Stack<String> values = new Stack();
        int varsP = 0;
        int constP = 0;
        int opP = 0;
        int index = 0;
        while (opP != ops.length) {
            if ((varsP != varsIndexes.length) && (varsIndexes[varsP].p == index))
                values.add(obj.variables[varsIndexes[varsP++].o].value);
            else if ((constP != constants.length) && (constants[constP].p == index))
                values.add(constants[constP++].o.value);
            else {
                String[] pars = new String[ops[opP].numberOfParameters];
                for (int j = pars.length - 1; j >= 0; j--)
                    pars[j] = values.pop();
                values.add(ops[opP++].func.evaluate(obj, pars));
            }
            index ++;
        }
        return values.pop();
    }


    public boolean evaluateCondition(GameObject obj) {
        if (ops[ops.length - 1].returnType != Type.BOOLEAN) {
            System.out.println("Warning: type mismatch");
            return false;
        }
        return evaluate(obj) == TRUE;
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
        //being defined. Based on this the correct parameter types for the operators should be calculated.
        ArrayList<Tokenizer> tokens;
        ArrayList<Integer> vars;
        ArrayList<Variable> constants;
        ArrayList<Integer> ops;
        ArrayList<IndexedPair<Operator[]>> funcs;
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

            if (currentT == TokenType.TOKEN) { //if the pointer is inside a token that is to be separated
                if (insideAString) {
                    if (ch == '\"')
                        insideAString = false;
                } else if (ch == '(') {
                    countBrackets ++;
                } else if (ch == ')') {
                    countBrackets --;
                    if (countBrackets == 0) {
                        extractToken(currentT, expression, separationStart, i, objVars, objOps);
                        currentT = null;
                    }
                } else if (ch == ',') {
                    if (countBrackets == 1) {
                        extractToken(currentT, expression, separationStart, i, objVars, objOps);
                        separationStart = i + 1;
                    }
                } else if (ch == '\"')
                    insideAString = true;
                i++;
                return;
            }

            if (currentT == TokenType.CONSTANT) { //if the pointer is inside a constant (not of the BOOLEAN type)
                if (insideAString) {
                    if (ch == '\"') {
                        extractToken(currentT, expression, separationStart, i, objVars, objOps);
                        currentT = null;
                    }
                } else if ((ch != '0') && ((ch < '1') || (ch > '9'))) {
                    extractToken(currentT, expression, separationStart, i, objVars, objOps);
                    if (((ch <= 'z') && (ch >= 'a')) || ((ch <= 'Z') && (ch >= 'A')))
                        currentT = TokenType.VARIABLE;
                    else if (ch == ' ')
                        currentT = null;
                    else
                        currentT = TokenType.OPERATOR;
                    separationStart = i;
                }
                i++;
                return;
            }

            switch (ch) {
                case '(': { //opening parenthesis (TOKEN type)
                    if (currentT != null)
                        extractToken(currentT, expression, separationStart, i, objVars, objOps);
                    currentT = TokenType.TOKEN;
                    //the i increment and how to deal with it?
                    separationStart = i + 1;
                    countBrackets = 1;
                    break;
                }
                case '\"': { //opening quotation mark (constant of type String)
                    if (currentT != null)
                        extractToken(currentT, expression, separationStart, i, objVars, objOps);
                    currentT = TokenType.CONSTANT;
                    separationStart = i + 1;
                    insideAString = true;
                    break;
                }
                case ' ': { // may signify the end of the variable/operator/function name
                    if (currentT != null) {
                        extractToken(currentT, expression, separationStart, i, objVars, objOps);
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
                            extractToken(currentT, expression, separationStart, i, objVars, objOps);
                            currentT = TokenType.CONSTANT;
                            insideAString = false;
                            separationStart = i;
                        }
                        break;
                    }
                    //Now the only possibility are appending to a variable/operator and possibly finishing one
                    if (((ch <= 'z') && (ch >= 'a')) || ((ch <= 'Z') && (ch >= 'A'))) {
                        if (currentT == TokenType.OPERATOR) {
                            extractToken(currentT, expression, separationStart, i, objVars, objOps);
                            currentT = TokenType.VARIABLE;
                            separationStart = i;
                        } else if (currentT == null) {
                            currentT = TokenType.VARIABLE;
                            separationStart = i;
                        }
                    } else {
                        //This is appending to an operator
                        if (currentT == TokenType.VARIABLE) {
                            extractToken(currentT, expression, separationStart, i, objVars, objOps);
                            currentT = TokenType.OPERATOR;
                            separationStart = i;
                        } else if (currentT == null) {
                            currentT = TokenType.OPERATOR;
                            separationStart = i;
                        }
                    }
                }
            }
            i++;
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
                    int numberOfParameters = 2;
                    if ((orderArray.size() == 1) || (orderArray.get(orderArray.size() - 2) == TokenType.OPERATOR))
                        numberOfParameters = 1;
                    boolean thisIstheRightFunction = false;
                    do {
                        if (Operator.STANDART[opIndex].o.numberOfParameters == numberOfParameters)
                            thisIstheRightFunction = true;
                        else
                            opIndex++;
                    } while (!thisIstheRightFunction);
                    ops.add(opIndex);
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
                            int funcIndex = getIndex(objOps, str);
                            if (funcIndex == -1)
                                funcs.add(new IndexedPair<>(Operator.STANDART_F, getIndex(Operator.STANDART_F, str)));
                            else
                                funcs.add(new IndexedPair<>(objOps, funcIndex));
                        }
                    }
                    break;
                }
            }
        }


        private Type getTypeAndMakePostfix(Variable[] objVars) {
            if (returnType != null)
                return returnType;
            else if (orderArray.isEmpty())
                return null;
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

            Stack<Integer> opStack = new Stack<>();
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

            Type lastTypeMet = null;
            Type typeBeforeTheLast = null;

            //Order of the constants

            while (j < orderArray.size()) {
                switch (orderArray.get(j)) {
                    case CONSTANT: {
                        constantsFinal[constFinalI] = new IndexedPair<>(constants.get(constI ++), constFinalI + varFinalI + opFinalI);
                        constFinalI++;
                        typeBeforeTheLast = lastTypeMet;
                        lastTypeMet = constantsFinal[constFinalI - 1].o.type;
                        if (unaryOperatorMet) {
                            operatorsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                            unaryOperatorMet = false;
                        }
                        break;
                    }
                    case OPERATOR: {
                        int nextOperator = ops.get(opI++);
                        while ((!opStack.isEmpty())&&(Operator.STANDART[nextOperator].p >= Operator.STANDART[opStack.peek()].p)) {
                            operatorsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, typeBeforeTheLast)].o;
                            typeBeforeTheLast = lastTypeMet;
                            lastTypeMet = operatorsFinal[opFinalI - 1].returnType;
                        }
                        opStack.push(nextOperator);
                        if (Operator.STANDART[nextOperator].o.numberOfParameters == 1)
                            unaryOperatorMet = true;
                        break;
                    }
                    case VARIABLE: {
                        varsFinal[varFinalI] = new IndexedPair<>(vars.get(varI ++), constFinalI + varFinalI + opFinalI);
                        varFinalI++;
                        typeBeforeTheLast = lastTypeMet;
                        lastTypeMet = objVars[varsFinal[varFinalI - 1].o].type;
                        if (unaryOperatorMet) {
                            operatorsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                            unaryOperatorMet = false;
                        }
                        break;
                    }
                    case FUNCTION: {
                        chooseCorrectFunction(funcI, j, tokI, objVars);
                        function = funcs.get(funcI).o[funcs.get(funcI).p];
                        funcI++;
                        tokensBeforeFunction = function.numberOfParameters;
                        break;
                    }
                    case TOKEN: {
                        Tokenizer thisToken = tokens.get(tokI++);
                        typeBeforeTheLast = lastTypeMet;
                        lastTypeMet = thisToken.getTypeAndMakePostfix(objVars);
                        int lastIndex = varFinalI + constFinalI + opFinalI;
                        for (Operator o: thisToken.operatorsFinal)
                            operatorsFinal[opFinalI ++] = o;
                        for (IndexedPair<Integer> i: thisToken.varsFinal)
                            varsFinal[varFinalI ++] = new IndexedPair<>(i.o, i.p + lastIndex);
                        for (IndexedPair<Variable> i: thisToken.constantsFinal)
                            constantsFinal[constFinalI ++] = new IndexedPair<>(i.o, i.p + lastIndex);
                        if (tokensBeforeFunction > -1) {
                            tokensBeforeFunction --;
                            if (tokensBeforeFunction == 0) {
                                operatorsFinal[opFinalI ++] = function;
                                typeBeforeTheLast = lastTypeMet;
                                lastTypeMet = function.returnType;
                                tokensBeforeFunction = -1;
                                if (unaryOperatorMet) {
                                    operatorsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                                    unaryOperatorMet = false;
                                }
                            }
                        } else if (unaryOperatorMet) {
                            operatorsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                            unaryOperatorMet = false;
                        }
                        break;
                    }
                }
                j++;
            }
            while (!opStack.isEmpty()) {
                operatorsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, typeBeforeTheLast)].o;
                typeBeforeTheLast = lastTypeMet;
                lastTypeMet = operatorsFinal[opFinalI - 1].returnType;
            }
            if (operatorsTotal == 0) {
                if (constantsTotal == 0)
                    returnType = objVars[varsFinal[0].o].type;
                else
                    returnType = constantsFinal[0].o.type;
            } else
                returnType = operatorsFinal[operatorsTotal -1].returnType;
            return returnType;
        }


        private int chooseCorrectOperator(int opIndex, Type par1Type, Type par2Type) {
            //TODo check name
            boolean thisIstheRightOperator = false;
            do {
                if ((Operator.STANDART[opIndex].o.parType[0] == par1Type) && ((par2Type == null) ||
                        (Operator.STANDART[opIndex].o.parType[1] == par2Type)))
                        thisIstheRightOperator = true;
                else
                    opIndex++;
            } while (!thisIstheRightOperator);
            return opIndex;
        }


        private void chooseCorrectFunction(int index, int typeIndex, int tokenIndex, Variable objVars[]) {
            //TODO check name
            int tokensCount = 0;
            while ((typeIndex + tokensCount + 1 < orderArray.size())&&(orderArray.get(typeIndex + tokensCount + 1) == TokenType.TOKEN))
                tokensCount++;
            if ((tokensCount == 1) && (tokens.get(tokenIndex - 1).getTypeAndMakePostfix(objVars) == null))
                tokensCount = 0;
            boolean thisIstheRightFunction = false;
            Operator[] funcArray = funcs.get(index).o;
            int funcIndex = funcs.get(index).p;
            do {
                if (funcArray[funcIndex].numberOfParameters != tokensCount)
                    funcIndex++;
                else {
                    int j;
                    for (j = 0; j < tokensCount; j++) {
                        if (tokens.get(tokenIndex + j).getTypeAndMakePostfix(objVars) != funcArray[funcIndex].parType[j]) {
                            funcIndex++;
                            break;
                        }
                    }
                    if (j == tokensCount)
                        thisIstheRightFunction = true;
                }
            } while (!thisIstheRightFunction);
            funcs.set(index, new IndexedPair<>(funcArray, funcIndex));
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
                        System.out.println(begin + "CONSTANT: "+ constants.get(constI++).value);
                        break;
                    }
                    case OPERATOR: {
                        System.out.println(begin + "OPERATOR: "+ Operator.STANDART[ops.get(opI++)].getName());
                        break;
                    }
                    case VARIABLE: {
                        System.out.println(begin + "VARIABLE: "+ objVars[vars.get(varI++)].getName());
                        break;
                    }
                    case FUNCTION: {
                        System.out.println(begin + "FUNCTION: "+funcs.get(funcI).o[funcs.get(funcI).p].getName());
                        funcI++;
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
                                        new Variable("i", Type.BOOLEAN, "true")};
        Operator[] ops = new Operator[] {new Operator("kek", 2, Type.BOOLEAN, new  Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) == 2 * Integer.parseInt(b[1])))};
        String intro = "(kek(PI - -3, min(3,4)) == i) || (\"kukare)ku\" + \"he(h\" == \"FGH\")";
        //String intro = "-(2 + 3)^(PI-2)/2";
        Expression example = new Expression(intro, ops, vars);
        GameObject shell = new GameObject(vars);
        System.out.println("Result: "+ example.evaluate(shell));
        vars[0].value = "4";
        System.out.println("Result: "+ example.evaluate(shell));
        vars[1].value = "false";
        System.out.println("Result: "+ example.evaluate(shell));
    }
}
