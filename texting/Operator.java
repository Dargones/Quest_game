package texting;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by alexanderfedchin on 7/23/17.
 *
 * The operator class encompasses all the functions and operators of the language that can be used by the user.
 * The operators that can be used with Numbers, Boolean values, and Strings are predefined here.
 */
public class Operator implements Named, Comparable<Operator>{
    public static final OpWithPriority[] STANDART; //operators that should be used with infix notation and should be
    //called according to their priority value. The higher the prority value, the sooner the operator is called.
    //NOTE1: Two operators that have the same name and the same number of arguments (i.e. those of them, which are
    // both unary or both binary) should have the same priority.
    //NOTE2: Unary operators should have lower priority then the binary operators
    //NOTE3: Unary operators should not change the type of their parameter (i.e. the retuen type should be the same
    // as the parameter type)
    public static final Operator STANDART_F[]; //operators that should be used with prefix notation.
    public final int numberOfParameters; //number of parameters taht a function/operator take
    public final Type returnType; //the return type of the function/operator
    public final Type[] parType; //the types of the parameters
    public final Function func; //the function itself (as a lambda expression that should usually refer to some other
    // function)
    private final String name; //every function should have a name. Make sure to read NOTE1 above.
    

    static {
        //TODO check whether the casting marked as redundant are really redundant
        STANDART = new OpWithPriority[18];
        STANDART[0] = new OpWithPriority( new Operator("+",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) + (Integer)(b[1])), 2);
        STANDART[1] = new OpWithPriority( new Operator("-",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) - (Integer)(b[1])), 2);
        STANDART[2] = new OpWithPriority( new Operator("-",1, Type.INTEGER, new Type[] {Type.INTEGER},
                (a, b) -> - (Integer)(b[0])), 5);
        STANDART[3] = new OpWithPriority( new Operator("*",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) * (Integer)(b[1])), 3);
        STANDART[4] = new OpWithPriority( new Operator("/",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) / (Integer)(b[1])), 3);
        STANDART[5] = new OpWithPriority( new Operator("^",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (int)Math.pow((Integer)(b[0]), (Integer)(b[1]))), 4);
        STANDART[6] = new OpWithPriority( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> ((Integer)(b[0])).equals((Integer)(b[1]))), 1);
        STANDART[13] = new OpWithPriority( new Operator("/",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) % (Integer)(b[1])), 3);
        STANDART[14] = new OpWithPriority( new Operator(">",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0])>(Integer)(b[1])), 0);
        STANDART[15] = new OpWithPriority( new Operator("<",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0])<(Integer)(b[1])), 0);
        STANDART[16] = new OpWithPriority( new Operator("<=",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0])<=(Integer)(b[1])), 0);
        STANDART[17] = new OpWithPriority( new Operator(">=",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0])>=(Integer)(b[1])), 0);

        STANDART[7] = new OpWithPriority( new Operator("||",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> (Boolean)(b[0])||(Boolean)(b[1])), 0);
        STANDART[8] = new OpWithPriority( new Operator("&&",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> (Boolean)(b[0])&&(Boolean)(b[1])), 0);
        STANDART[9] = new OpWithPriority( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> ((Boolean)(b[0])).equals((Boolean)(b[1]))), 1);
        STANDART[10] = new OpWithPriority( new Operator("!",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN},
                (a, b) -> !(Boolean)(b[0])), 5);

        STANDART[11] = new OpWithPriority( new Operator("+",2, Type.STRING, new Type[] {Type.STRING, Type.STRING},
                (a, b) -> (String)b[0] + (String)b[1]), 2);
        STANDART[12] = new OpWithPriority( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.STRING, Type.STRING},
                (a, b) -> ((String)(b[0])).equals((String) (b[1]))), 1);
        Arrays.sort(STANDART); //sort by the name

        STANDART_F = new Operator[4];
        STANDART_F[0] = new Operator("max",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) > (Integer)(b[1]) ? b[0]: b[1]);
        STANDART_F[1] = new Operator("min",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> (Integer)(b[0]) > (Integer)(b[1]) ? b[1]: b[0]);
        STANDART_F[2] = new Operator("toString",1, Type.STRING, new Type[] {Type.INTEGER},
                (a, b) -> b[0]);
        STANDART_F[3] = new Operator("randomInt",1, Type.INTEGER, new Type[] {Type.INTEGER},
                (a, b) -> (new Random()).nextInt((Integer)(b[0])));
        Arrays.sort(STANDART_F);
    }

    public Operator (String name, int numberOfParameters, Type returnType, Type[] parType, Function func) {
        if (numberOfParameters != parType.length)
            System.out.println("In Operator: the number of parameters should equal the length of the parType array");
        this.numberOfParameters = numberOfParameters;
        this.returnType = returnType;
        this.parType = parType;
        this.func = func;
        this.name = name;
    }

    protected static class OpWithPriority implements Named, Comparable<OpWithPriority>{
        Operator o;
        int p; //the higher the priority value, the sooner the operator is called.

        OpWithPriority(Operator o, int p) {
            this.o = o;
            this.p = p;
        }

        @Override
        public String getName() {
            return  o.getName();
        }

        @Override
        public int compareTo(OpWithPriority oWP) {
            return o.compareTo(oWP.o);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Operator o) {
        return name.compareTo(o.name);
    }

    interface Function {
        Object evaluate(GameObject cals, Object[] pars);
    }
}
