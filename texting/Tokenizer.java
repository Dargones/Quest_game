package texting;

import java.util.ArrayList;
import java.util.Stack;

/**
 * This class processes the String and separates it into tokens and converts it into postfix notation.
 * Tokens can be of 5 types:
 * - other Tokens (these are what is separated by the parenthesis)
 * - constants. Constant values of INTEGER, BOOLEAN or STRING types
 * - variables. These are "non-static" variables
 * - operators. Some of the set of predefined binary and unary operations that are used with infix notation and have order
 *   of precedence.
 * - predefined_functions. All the other functions that are used with prefix notation
 *
 * the orderArray is an arrayList that stores the information about the order in which the tokens appear. The values
 * corresponding to these 5 types are: TOKEN, CONSTANT, VARIABLE, OPERATOR, and FUNCTION
 *
 * the primarily goal of that class is to convert a String to an Expression
 */
public class Tokenizer {

    //the following variables are needed while separating the String into tokens (processNextCharacter, extractToken)
    private int i; //the index of the next character to process
    private TokenType currentT; //the type of the Token that is currently processed (if known)
    private int separationStart; //the index of the beginning of the token processed
    private boolean insideAString = false; // this variable indicates whether the pointer is inside a string constant.
    // the variable is needed in two cases. Firstly, if a bracket is met inside the string constant, the
    // bracket should be processed as a part of that constant. Secondly, this variable is useful to distinguish
    //between the string and the integer variable types.
    private int countBrackets = 0; //number of opening brackets met.


    //these variables represent the result of tokenization. At this point the expression is still stored in a
    //human-readable infix/prefix notation, with every bracket and every part of the bracket separated by the comma
    //being stored as an element of teh Tokenizer class. The orderArray stores the information about the order in which
    //the tokens go. Here is an examples of the tokenization:
    //Input: 2*(5+6)==a-max(b,8)^2
    //Output:
    //tokens =              {Tokenizer1,                    Tokenizer2,                 Tokenizer3},           where
    //        tokens =      null                            null                        null
    //        vars =        null                            {b}                         null
    //        constants =   {5,6}                           null                        {8}
    //        ops =         {+}                             null                        null
    //        funcs =       null                            null                        null
    //        orderArray =  {CONSTANT, OPERATOR, CONSTANT}  {VARIABLE}                  {CONSTANT}
    //vars = {a}
    //constants = {2,2}
    //ops = {*, ==, -, ^}
    //funcs = {max}
    //orderArray = {CONSTANT, OPERATOR, TOKEN, OPERATOR, VARIABLE, OPERATOR, FUNCTION, TOKEN, TOKEN, OPERATOR, CONSTANT}
    //Note that operators and functions that have been found at this stage may depend on the actual functions implied,
    //since functions of the same name may have different parameter types. The correct functions are defined at the 
    //later stage (getTypeAndMakePostfix)
    private ArrayList<Tokenizer> tokens;
    private ArrayList<Integer> vars;
    private ArrayList<Variable> constants;
    private ArrayList<Integer> ops;
    private ArrayList<IndexedPair<Operator[]>> funcs;
    //TODO since indexedPair is only used for funcs, wouldn't it be useful to adjust the Class name for this specific purpose
    private ArrayList<TokenType> orderArray;

    //the following variables represent the result of the converting the String into postfix notation. The structure is
    //the same as in the Expression class.
    private int[] varsFinal;
    private Variable[] constantsFinal;
    private Operator[] opsFinal;
    private TokenType[] orderArrayFinal;
    private Type returnType; //the return type of the tokenized expression

    /** This is the recursive constructor
     * @param expression expression to extract the tokens from
     * @param objVars    the variables of the object that this expression operates on
     * @param objOps     the operators of the object that this expression operates on
     */
    Tokenizer(String expression, Variable[] objVars, Operator[] objOps) {
        varsFinal = null;
        constantsFinal = null;
        opsFinal = null;
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
                    insideAString = false;
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
            operatorsTotal += t.opsFinal.length;
            constantsTotal += t.constantsFinal.length;
            variableTotal += t.varsFinal.length;
        }
        varsFinal = new int[variableTotal];
        opsFinal = new Operator[operatorsTotal];
        constantsFinal = new Variable[constantsTotal];
        orderArrayFinal = new TokenType[variableTotal+operatorsTotal+constantsTotal];

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
                    orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.VARIABLE;
                    constantsFinal[constFinalI++] = constants.get(constI ++);
                    typeBeforeTheLast = lastTypeMet;
                    lastTypeMet = constantsFinal[constFinalI - 1].type;
                    if (unaryOperatorMet) {
                        orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
                        opsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                        unaryOperatorMet = false;
                    }
                    break;
                }
                case OPERATOR: {
                    int nextOperator = ops.get(opI++);
                    while ((!opStack.isEmpty())&&(Operator.STANDART[nextOperator].p <= Operator.STANDART[opStack.peek()].p)) {
                        orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
                        opsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, typeBeforeTheLast)].o;
                        typeBeforeTheLast = lastTypeMet;
                        lastTypeMet = opsFinal[opFinalI - 1].returnType;
                    }
                    opStack.push(nextOperator);
                    if (Operator.STANDART[nextOperator].o.numberOfParameters == 1)
                        unaryOperatorMet = true;
                    break;
                }
                case VARIABLE: {
                    varsFinal[varFinalI] = vars.get(varI ++);
                    orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.CONSTANT;
                    varFinalI++;
                    typeBeforeTheLast = lastTypeMet;
                    lastTypeMet = objVars[varsFinal[varFinalI - 1]].type;
                    if (unaryOperatorMet) {
                        orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
                        opsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
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
                    for (TokenType type: thisToken.orderArrayFinal) {
                        int oI = 0;
                        int vI = 0;
                        int cI = 0;
                        orderArrayFinal[constFinalI + varFinalI + opFinalI] = type;
                        switch (type) {
                            case VARIABLE: {
                                varsFinal[varFinalI++] = thisToken.varsFinal[vI++];
                                break;
                            }
                            case CONSTANT: {
                                constantsFinal[constFinalI++] = thisToken.constantsFinal[cI++];
                                break;
                            }
                            default:  // i.e. OPERATOR
                                opsFinal[opFinalI++] = thisToken.opsFinal[opI++];
                        }
                    }
                    if (tokensBeforeFunction > -1) {
                        tokensBeforeFunction --;
                        if (tokensBeforeFunction == 0) {
                            orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
                            opsFinal[opFinalI ++] = function;
                            typeBeforeTheLast = lastTypeMet;
                            lastTypeMet = function.returnType;
                            tokensBeforeFunction = -1;
                            if (unaryOperatorMet) {
                                orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
                                opsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                                unaryOperatorMet = false;
                            }
                        }
                    } else {
                        typeBeforeTheLast = lastTypeMet;
                        lastTypeMet = thisToken.getTypeAndMakePostfix(objVars);
                        if (unaryOperatorMet) {
                            orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
                            opsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, null)].o;
                            unaryOperatorMet = false;
                        }
                    }
                    break;
                }
            }
            j++;
        }
        while (!opStack.isEmpty()) {
            orderArrayFinal[constFinalI + varFinalI + opFinalI] = TokenType.OPERATOR;
            opsFinal[opFinalI++] = Operator.STANDART[chooseCorrectOperator(opStack.pop(), lastTypeMet, typeBeforeTheLast)].o;
            typeBeforeTheLast = lastTypeMet;
            lastTypeMet = opsFinal[opFinalI - 1].returnType;
        }
        if (operatorsTotal == 0) {
            if (constantsTotal == 0)
                returnType = objVars[varsFinal[0]].type;
            else
                returnType = constantsFinal[0].type;
        } else
            returnType = opsFinal[operatorsTotal -1].returnType;
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
        if ((tokensCount == 1) && (tokens.get(tokenIndex).getTypeAndMakePostfix(objVars) == null))
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
        System.out.println();
    }

    public int[] getVarsFinal() {
        return varsFinal;
    }

    public Variable[] getConstantsFinal() {
        return constantsFinal;
    }

    public Operator[] getOpsFinal() {
        return opsFinal;
    }

    public TokenType[] getOrderArrayFinal() {
        return orderArrayFinal;
    }

    public Type getReturnType() {
        return returnType;
    }

    /**
     * A function taht takes an array of Tokens (of Variables or Operators that are named) that is sorted by the name
     * and searches for the first entry with the same name.
     * @param arr the array to searches in
     * @param key the key to search for
     * @param <T> the type of the array
     * @return if no eligible element was found, return -1, otherwise return the index of such an element
     */
    public static <T extends Named> int getIndex(T[] arr, String key) {
        if (arr == null)
            return -1;
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
     * Created by alexanderfedchin on 8/25/17.
     *
     * A very basic pair of elements, one of which is integer, and the other is generic
     */
    private class IndexedPair<T> {
        public final T o; //object
        public final int p; //position

        public IndexedPair(T o, int p) {
            this.p = p;
            this.o = o;
        }
    }
}

