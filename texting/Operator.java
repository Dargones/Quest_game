package texting;

import java.util.Arrays;

/**
 * Created by alexanderfedchin on 7/23/17.
 */
public class Operator implements Token, Comparable<Operator>{
    public static final IndexedPair<Operator>[] STANDART; //operators that should be used with infix notation and are
    //indexed by the priority value.
    public static final Operator STANDART_F[]; //operators that should be used with prefix notation
    public final int numberOfParameters;
    public final Type returnType;
    public final Type[] parType;
    public final Function func;
    private final String name;

    static {
        STANDART = (IndexedPair<Operator>[])(new Object[20]);
        STANDART[0] = new IndexedPair( new Operator("+",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) + Integer.parseInt(b[1]))), 3);
        STANDART[1] = new IndexedPair( new Operator("-",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) - Integer.parseInt(b[1]))), 3);
        STANDART[2] = new IndexedPair( new Operator("-",1, Type.INTEGER, new Type[] {Type.INTEGER},
                (a, b) -> String.valueOf(- Integer.parseInt(b[0]))), 0);
        STANDART[3] = new IndexedPair( new Operator("*",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) * Integer.parseInt(b[1]))), 2);
        STANDART[4] = new IndexedPair( new Operator("/",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) / Integer.parseInt(b[1]))), 2);
        STANDART[5] = new IndexedPair( new Operator("^",2, Type.INTEGER, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Math.pow(Integer.parseInt(b[0]), Integer.parseInt(b[1])))), 1);
        STANDART[6] = new IndexedPair( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.INTEGER, Type.INTEGER},
                (a, b) -> String.valueOf(Integer.parseInt(b[0])==Integer.parseInt(b[1]))), 5);

        STANDART[7] = new IndexedPair( new Operator("+",2, Type.DOUBLE, new Type[] {Type.DOUBLE, Type.DOUBLE},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) + Integer.parseInt(b[1]))), 3);
        STANDART[8] = new IndexedPair( new Operator("-",2, Type.DOUBLE, new Type[] {Type.DOUBLE, Type.DOUBLE},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) - Integer.parseInt(b[1]))), 3);
        STANDART[9] = new IndexedPair( new Operator("-",1, Type.DOUBLE, new Type[] {Type.DOUBLE},
                (a, b) -> String.valueOf(- Integer.parseInt(b[0]))), 0);
        STANDART[10] = new IndexedPair( new Operator("*",2, Type.DOUBLE, new Type[] {Type.DOUBLE, Type.DOUBLE},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) * Integer.parseInt(b[1]))), 2);
        STANDART[11] = new IndexedPair( new Operator("/",2, Type.DOUBLE, new Type[] {Type.DOUBLE, Type.DOUBLE},
                (a, b) -> String.valueOf(Integer.parseInt(b[0]) / Integer.parseInt(b[1]))), 2);
        STANDART[12] = new IndexedPair( new Operator("^",2, Type.DOUBLE, new Type[] {Type.DOUBLE, Type.DOUBLE},
                (a, b) -> String.valueOf(Math.pow(Integer.parseInt(b[0]), Integer.parseInt(b[1])))), 1);
        STANDART[13] = new IndexedPair( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.DOUBLE, Type.DOUBLE},
                (a, b) -> String.valueOf(Integer.parseInt(b[0])==Integer.parseInt(b[1]))), 5);

        STANDART[14] = new IndexedPair( new Operator("||",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> String.valueOf(Boolean.getBoolean(b[0])||Boolean.getBoolean(b[1]))), 5);
        STANDART[15] = new IndexedPair( new Operator("&&",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> String.valueOf(Boolean.getBoolean(b[0])&&Boolean.getBoolean(b[1]))), 4);
        STANDART[16] = new IndexedPair( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN, Type.BOOLEAN},
                (a, b) -> String.valueOf(Boolean.getBoolean(b[0])==Boolean.getBoolean(b[1]))), 4);
        STANDART[17] = new IndexedPair( new Operator("!",2, Type.BOOLEAN, new Type[] {Type.BOOLEAN},
                (a, b) -> String.valueOf(!Boolean.getBoolean(b[0]))), 0);

        STANDART[18] = new IndexedPair( new Operator("+",2, Type.STRING, new Type[] {Type.STRING, Type.STRING},
                (a, b) -> b[0] + b[1]), 3);
        STANDART[19] = new IndexedPair( new Operator("==",2, Type.BOOLEAN, new Type[] {Type.STRING, Type.STRING},
                (a, b) -> String.valueOf(b[0]==b[1])), 5);

        STANDART_F = new Operator[];
        Arrays.sort(STANDART);
    }

    public Operator (String name, int numberOfParameters, Type returnType, Type[] parType, Function func) {
        this.numberOfParameters = numberOfParameters;
        this.returnType = returnType;
        this.parType = parType;
        this.func = func;
        this.name = name;
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
