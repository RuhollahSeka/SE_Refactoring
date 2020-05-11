package errorhandler;

import log.Log;

/**
 * Created by Alireza on 6/28/2015.
 */
public class ErrorHandler {
    private ErrorHandler() {}

    public static boolean hasError = false;

    public static void printError(String msg) {
        hasError = true;
        Log.print(msg);
    }
}
