package ugr.gbv.myapplication.utilities;

import android.content.Context;

/**
 * Created by Guillermo on 25/08/2017.
 */

public class NotificationTasks {
    private static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }

    }

}