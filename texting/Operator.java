package texting;

import java.util.Arrays;

/**
 * Created by alexanderfedchin on 7/23/17.
 */
public class Operator implements Named, Comparable<Operator>{
    public static final OpWithPriority[] STANDART; //operators that should be used with infix notation and are
    //indexed by the priority value.
    //NOTE: Two operators that have teh same name and the same number of argumnets (i.e. those of them, whoch are
    // both unary or both binary) should have the same priority.
    //NOTE2: Unary operators shpuld have lower priority then the binary operators
    public static final Operator STANDART_F[]; //operators that should be used with prefix notation
    public final int numberOfParameters;
    public final Type returnType;
    public final Type[] parType;
    public final Function func;
    private final String name;

    static {
        STANDART = new OpWithPriority[14];
        STANDART[0] = new OpWithPriority( new Operator("+",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) + Integer.parseInt(b[1]))), 3);
        STANDART[1] = new OpWithPriority( new Operator("-",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) - Integer.parseInt(b[1]))), 3);
        STANDART[2] = new OpWithPriority( new Operator("-",1, Type.INTEGER, new Type[] {Type.INTEGER},
                (a, b) -> String.valueOf(- Integer.parseInt(b[0]))), 0);
        STANDART[3] = new OpWithPriority( new Operator("*",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) * Integer.parseInt(b[1]))), 2);
        STANDART[4] = new OpWithPriority( new Operator("/",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) / Integer.parseInt(b[1]))), 2);
        STANDART[5] = new OpWithPriority( new Operator("^",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf((int)Math.pow(Integer.parseInt(b[0]), Integer.parseInt(b[1])))), 1);
        STANDART[6] = new OpWithPriority( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0])==Integer.parseInt(b[1]))), 5);
        STANDART[13] = new OpWithPriority( new Operator("/",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) % Integer.parseInt(b[1]))), 2);

        STANDART[7] = new OpWithPriority( new Operator("||",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> String.valueOf(Boolean.valueOf(b[0])||Boolean.valueOf(b[1]))), 5);
        STANDART[8] = new OpWithPriority( new Operator("&&",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> String.valueOf(Boolean.valueOf(b[0])&&Boolean.valueOf(b[1]))), 4);
        STANDART[9] = new OpWithPriority( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> String.valueOf(Boolean.valueOf(b[0])==Boolean.valueOf(b[1]))), 4);
        STANDART[10] = new OpWithPriority( new Operator("!",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN},
                (a, b) -> String.valueOf(!Boolean.valueOf(b[0]))), 0);

        STANDART[11] = new OpWithPriority( new Operator("+",2, Type.STRING, new Type[] {Type.STRING, Type.STRING},
                (a, b) -> b[0] + b[1]), 3);
        STANDART[12] = new OpWithPriority( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.STRING, Type.STRING},
                (a, b) -> String.valueOf(b[0]==b[1])), 5);
        Arrays.sort(STANDART);

        STANDART_F = new Operator[2];
        STANDART_F[0] = new Operator("max",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> Integer.parseInt(b[0]) > Integer.parseInt(b[1]) ? b[0]: b[1]);
        STANDART_F[1] = new Operator("min",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> Integer.parseInt(b[0]) > Integer.parseInt(b[1]) ? b[1]: b[0]);
        Arrays.sort(STANDART_F);
    }

    public Operator (String name, int numberOfParameters, Type returnType, Type[] parType, Function func) {
        this.numberOfParameters = numberOfParameters;
        this.returnType = returnType;
        this.parType = parType;
        this.func = func;
        this.name = name;
    }

    protected static class OpWithPriority implements Named, Comparable<OpWithPriority>{
        Operator o;
        int p;

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
        String evaluate(GameObject cals, String[] pars);
    }
}
