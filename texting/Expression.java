package texting;


import java.util.Stack;


/**
 * Created by alexanderfedchin on 7/24/17.
 *
 * This class represents an expression. Every expression is associated with a specific class. It can only use
 * variables of that class. The expression is represented in a postfix notation.
 */
public class Expression {
    private Type returnType;
    private Operator[] ops; //operators listed in the order in which they should be applied
    private int[] varsIndexes;
    private Variable[] constants;
    private TokenType[] tokenOrder; //this is the order in which operators, variables and constants should go. While
    //evaluating the expression the code should put corresponding values on the stack, if the next element of the
    //tokenOrder is VARIABLE or CONSTANT, and remove the necessary number of elements from the stakc, in case the next
    //element in the tokenOrder is OPERATOR
    //TODO how to manage functions that do not return any values

    /**
     * The very basic constructor that uses Tokenizer to extract Expression out of a String
     * @param str
     * @param expressionType the type to which this expression belongs. I.e. the variables and functions to which this
     *                       class has direct access
     */
    public Expression(String str, Type expressionType) {
        Tokenizer tokenizer = new Tokenizer(str, expressionType.vars, expressionType.ops);
        this.ops = tokenizer.getOpsFinal();
        this.varsIndexes = tokenizer.getVarsFinal();
        this.constants = tokenizer.getConstantsFinal();
        this.returnType = tokenizer.getReturnType();
    }

    /**
     * This method evaluates the expression given a specific object on which to work on. The GameObject should be of the
     * same type as the Expression was. (this is not checked)
     * TODO should that be checked (see above) ?
     * @param obj
     * @return
     */
    public Object evaluate(GameObject obj) {
        Stack<Object> values = new Stack(); //this Stack of values is filled with values from
        int varsP = 0;
        int constP = 0;
        int opP = 0;
        int orderP = 0;
        while (orderP != tokenOrder.length) {
            switch (tokenOrder[orderP]) { //note that tokenOrder[i] cannot be TOKEN or FUNCTION after being processed by the Tokenizer
                case VARIABLE: {
                    values.add(obj.variables[varsIndexes[varsP++]].value);
                    break;
                }
                case CONSTANT: {
                    values.add(constants[constP++].value);
                    break;
                }
                default: { //the same as case OPERATOR would be but faster (is it really?)
                    Object[] pars = new Object[ops[opP].numberOfParameters];
                    for (int j = pars.length - 1; j >= 0; j--)
                        pars[j] = values.pop();
                    values.add(ops[opP++].func.evaluate(obj, pars));
                }
            }
            orderP++;
        }
        return values.pop();
    }

    /**
     * This function should only be used with those expressions whose return type is Boolean. Does the same as evaluate
     * but returns the boolean value
     * @param obj
     * @return
     */
    public boolean evaluateCondition(GameObject obj) {
        if (returnType != Type.BOOLEAN) {
            System.out.println("Warning: type mismatch");
            return false;
        }
        return evaluate(obj) == Boolean.TRUE;
    }
}
