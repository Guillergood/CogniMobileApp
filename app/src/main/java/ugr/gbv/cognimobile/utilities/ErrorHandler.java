package ugr.gbv.cognimobile.utilities;

import ugr.gbv.cognimobile.interfaces.LoadDialog;

/**
 * Singleton static class that displays errors.
 */
public class ErrorHandler {
    private static LoadDialog callback;

    /**
     * Sets the callback from the actual activity
     * When settled, this will allow the app to command the actual activity to display an error.
     *
     * @param handler the callback to call the method.
     */
    public static void setCallback(LoadDialog handler) {
        callback = handler;
    }

    /**
     * Allows the app to display a message showing the error message.
     *
     * @param message the error that have just happened.
     */
    public static void displayError(String message) {
        if (callback != null)
            callback.loadDialog(message,
                    (Object[]) null);
    }

    /**
     * Allows the app to display a message showing the error message.
     *
     * @param message the error that have just happened.
     */
    public static void displayError(String message, Object ...args) {
        if (callback != null)
            callback.loadDialog(message, args);
    }


}

