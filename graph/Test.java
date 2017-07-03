package graph;


/**
 * Created by alexanderfedchin on 7/2/17.
 *
 * A module that provides basic instruments for testing
 */
public class Test {
    private static final double MARGIN_OF_ERROR = 1./Math.pow(10,6); //margin of error for the tests
    private static boolean noBugs = true;

    /**
     * Checks if the difference between the two double values is within the MARGIN_OF_ERROR
     * @param one
     * @param two
     * @return
     */
    public static boolean roughlyEqual(double one, double two) {
        if (one == 0)
            return ((two < MARGIN_OF_ERROR) && (two > -MARGIN_OF_ERROR));
        if (two == 0)
            return ((one < MARGIN_OF_ERROR) && (one > -MARGIN_OF_ERROR));
        return (((one-two)/one < MARGIN_OF_ERROR) && ((one-two)/two < MARGIN_OF_ERROR) &&
                ((one-two)/one > -MARGIN_OF_ERROR) && ((one-two)/two > -MARGIN_OF_ERROR));
    }

    /**
     * Prints the leading Message. Then checks if all the values in predicted[] are equal to those in actual[]. Returns
     * true, if there are no mistakes. Otherwise returns false and prints what parameters do not match. All parameters
     * should be of the same type
     * @param leadingMessage the leading message to print
     * @param parameterNames the names of parameters (these are needed in case any errors might be found)
     * @param predicted      predicted values for all parameters
     * @param actual         the values that computer gave as output
     * @return
     */
    public static <T> void test(String leadingMessage, String[] parameterNames, T[] predicted, T[] actual) {
        System.out.println(leadingMessage);
        if ((parameterNames == null) && (predicted == null) && (actual == null)) {
            System.out.println("There are no arguments as it should be.\n");
            return;
        }
        if (!checkLength(parameterNames, predicted, actual)) return;
        for (int i = 0; i < predicted.length; i++)
            if (!predicted[i].equals(actual[i])) {
                System.out.println("Parameter "+parameterNames[i]+" should be set to "+predicted[i].toString()+ " but" +
                        " it is set to "+actual[i].toString()+" instead.");
                noBugs = false;
            }
        System.out.println("");
    }


    /**
     * Does the same as test, but allows a margin of error of MARGIN_OF_ERROR and thus works only with double.
     * @param leadingMessage the leading message to print
     * @param parameterNames the names of parameters (these are needed in case any errors might be found)
     * @param predicted      predicted values for all parameters
     * @param actual         the values that computer gave as output
     * @return
     */
    public static void testDouble(String leadingMessage, String[] parameterNames, Double[] predicted, Double[] actual) {
        System.out.println(leadingMessage);
        if ((parameterNames == null) && (predicted == null) && (actual == null)) {
            System.out.println("There are no arguments as it should be.\n");
            return;
        }
        if (!checkLength(parameterNames, predicted, actual)) return;
        for (int i = 0; i < predicted.length; i++)
            if (!roughlyEqual(predicted[i], actual[i])) {
                System.out.println("Parameter "+parameterNames[i]+" should be set to "+predicted[i].toString()+ " but" +
                        " it is set to "+actual[i].toString()+" instead.");
                noBugs = false;
            }
        System.out.println("");
    }


    /**
     * Checks teh length of the three arrays from test or testDouble. The one case that is left untested is then all
     * the there arrays are equal to null.
     * @param parameterNames
     * @param predicted
     * @param actual
     * @param <T>
     * @return
     */
    public static <T> boolean checkLength(String[] parameterNames, T[] predicted, T[] actual) {
        if ((parameterNames == null)  || (predicted == null) || (actual == null)) {
            if (parameterNames != predicted) {
                System.out.println("There is a mistake in the test setting. Check the number of arguments.\n");
                noBugs = false;
                return false;
            }
            if (predicted == null) {
                System.out.println("There should be no arguments, but the computer found some.\n");
                noBugs = false;
                return false;
            }
            System.out.println("There should be some arguments, but the computer found none.\n");
            noBugs = false;
            return false;
        }
        if (parameterNames.length != predicted.length) {
            System.out.println("There is a mistake in the test setting. Check the number of arguments.\n");
            noBugs = false;
            return false;
        }
        if (predicted.length < actual.length) {
            System.out.println("There should be less arguments, than the computer found.\n");
            noBugs = false;
            return false;
        }
        if (predicted.length > actual.length) {
            System.out.println("There should be more arguments, than the computer found.\n");
            noBugs = false;
            return false;
        }
        return true;
    }


    /**
     * prints whether there have been found any bugs or not
     * @return
     */
    public static void verdict() {
        if (noBugs)
            System.out.println("There have been no bugs found. Congrats!");
        else
            System.out.println("There are bugs. You know what to do.");
    }

    /**
     * sets noBugs to true
     * @return
     */
    public static void resetBugsCount() {
        noBugs = true;
    }
}
