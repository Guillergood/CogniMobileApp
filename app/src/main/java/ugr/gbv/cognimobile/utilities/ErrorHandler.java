package ugr.gbv.cognimobile.utilities;

import ugr.gbv.cognimobile.interfaces.LoadDialog;

public class ErrorHandler {
    private static LoadDialog callback;

    public static void setCallback(LoadDialog handler) {
        callback = handler;
    }

    public static void displayError(String message) {
        if (callback != null)
            callback.loadDialog(message);
    }


}

