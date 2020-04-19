package ugr.gbv.cognimobile.utilities;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import ugr.gbv.cognimobile.R;

public class ErrorHandler {
    private static volatile ErrorHandler instantiated;


    private ErrorHandler() {

        if (instantiated != null) {
            throw new RuntimeException("Use .getInstance() to instantiate TestDataSender");
        }
    }

    public static ErrorHandler getInstance() {
        if (instantiated == null) {
            synchronized (ErrorHandler.class) {
                if (instantiated == null) instantiated = new ErrorHandler();
            }
        }

        return instantiated;
    }

    public void displayError(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(R.string.error_occurred));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.continue_next_task), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


}

