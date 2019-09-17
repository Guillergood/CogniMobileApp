package ugr.gbv.myapplication.utilities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.activities.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Guillermo on 25/08/2017.
 */

public class NotificationUtils{
    private static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";


    public static final int ARTICLE_NOTIFICATION_ID = 3004;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 3005;


    public static void notifyCorrectUpdate(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyCorrectUpdateOreo(context);
        }
        else {
            String notificationTitle = context.getString(R.string.app_name);

            /*
             * NotificationCompat Builder is a very convenient way to build backward-compatible
             * notifications. In order to use it, we provide a context and specify a color for the
             * notification, a couple of different icons, the title for the notification, and
             * finally the text of the notification, which in our case in a summary of today's
             * forecast.
             */
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);



            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, Integer.toString(ARTICLE_NOTIFICATION_ID))
                            .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                            .setContentTitle(notificationTitle)
                            .setSmallIcon(R.drawable.ic_app_notification)
                            .addAction(ignoreReminderAction(context))
                            .setAutoCancel(false);

            /*
             * This Intent will be triggered when the user clicks the notification. In our case,
             * we want to open Sunshine to the DetailActivity to display the newly updated weather.
             */
            Intent detailIntentForToday = new Intent(context, MainActivity.class);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);


            /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
            if (notificationManager != null) {
                notificationManager.notify(ARTICLE_NOTIFICATION_ID, notificationBuilder.build());
            }
        }

    }



    @TargetApi(Build.VERSION_CODES.O)
    private static void notifyCorrectUpdateOreo(Context context){

        String notificationTitle = context.getString(R.string.app_name);

        /*
         * NotificationCompat Builder is a very convenient way to build backward-compatible
         * notifications. In order to use it, we provide a context and specify a color for the
         * notification, a couple of different icons, the title for the notification, and
         * finally the text of the notification, which in our case in a summary of today's
         * forecast.
         */

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
// The id of the channel.
        String id = "my_channel_01";
// The user-visible name of the channel.
        CharSequence name = "Notificaciones";
// The user-visible description of the channel.
        String description = "Constitucion_channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
// Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
        mChannel.setLightColor(context.getColor(R.color.colorAccent));
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ACTION_IGNORE_PENDING_INTENT_ID, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context,id)
                .setChannelId(id)
                .setContentTitle(notificationTitle)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_app_notification)
                .setContentIntent(pendingIntent)
                .setNumber(ARTICLE_NOTIFICATION_ID)
                .setColor(context.getColor(R.color.colorAccent))
                .setWhen(System.currentTimeMillis())
                .build();

        if(notificationManager != null)
            notificationManager.notify(ARTICLE_NOTIFICATION_ID, notification);

    }



    static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private static NotificationCompat.Action ignoreReminderAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, syncIntentService.class);
        ignoreReminderIntent.setAction(ACTION_DISMISS_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action
                (R.drawable.ic_action_bluetooth,
                        context.getString(R.string.app_name),
                        pendingIntent);
    }

}
