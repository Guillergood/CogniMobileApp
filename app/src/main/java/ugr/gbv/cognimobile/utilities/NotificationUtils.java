package ugr.gbv.cognimobile.utilities;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.activities.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 909;
    private static final int ARTICLE_NOTIFICATION_ID = 1009;
    private static final String ACTION_DISMISS_NOTIFICATION = "ACTION_DISMISS_NOTIFICATION";
    private static volatile NotificationUtils instantiated;

    private NotificationUtils(){
        if (instantiated != null){
            throw new RuntimeException("Use .getInstance() to instantiate NotificationUtils");
        }
    }

    public static NotificationUtils getInstance() {
        if (instantiated == null) {
            synchronized (NotificationUtils.class) {
                if (instantiated == null){
                    instantiated = new NotificationUtils();
                }
            }
        }

        return instantiated;
    }


    public void notifyNewTestsAvailable(int result,Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyNewTestsAvailableOreo(result,context);
        }
        else{
            notifyNewTestsAvailableLessThanOreo(result,context);
        }
    }
    
    


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void notifyNewTestsAvailableOreo(int tests, Context context){

        String notificationTitle = context.getString(R.string.app_name);

        String notificationText = context.getString(R.string.tests_availables,tests);

        /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
        int smallArtResourceId = R.drawable.ic_app_notification;
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

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
        String id = context.getString(R.string.cognimobile_channel);
// The user-visible name of the channel.
        CharSequence name = context.getString(R.string.app_name);
// The user-visible description of the channel.
        String description = context.getString(R.string.cognimobile_channel);
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



        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ACTION_IGNORE_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action action = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_test_24dp_black),
                context.getString(R.string.accept_notification),
                pendingIntent).build();
        
        Notification notification = new Notification.Builder(context,id)
                .setSmallIcon(smallArtResourceId) //your app icon
                .setBadgeIconType(smallArtResourceId) //your app icon
                .setLargeIcon(largeIcon)
                .setChannelId(id)
                .setContentTitle(notificationTitle)
                .setAutoCancel(true).setContentIntent(pendingIntent)
                .setNumber(ARTICLE_NOTIFICATION_ID)
                .setColor(context.getColor(R.color.colorAccent))
                .setContentText(notificationText)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(notificationText))
                .setWhen(System.currentTimeMillis())
                .addAction(action)
                .build();

        if(notificationManager != null)
            notificationManager.notify(ARTICLE_NOTIFICATION_ID, notification);
        
    }



    private void notifyNewTestsAvailableLessThanOreo(int result, Context context) {
        String notificationTitle = context.getString(R.string.app_name);

        String notificationText = context.getString(R.string.tests_availables, result);

        /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
        int smallArtResourceId = R.drawable.ic_app_notification;

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
                        .setSmallIcon(smallArtResourceId)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                        .addAction(ignoreReminderAction(context))
                        .setAutoCancel(true);

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

    private static NotificationCompat.Action ignoreReminderAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, MainActivity.class);
        ignoreReminderIntent.setAction(ACTION_DISMISS_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action
                (R.drawable.ic_check_black_24dp,
                        context.getString(R.string.accept_notification),
                        pendingIntent);
    }
}
