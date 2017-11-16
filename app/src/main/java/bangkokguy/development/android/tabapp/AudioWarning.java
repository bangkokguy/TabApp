package bangkokguy.development.android.tabapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bangkokguy on 11/11/17.
 *
 */

class AudioWarning {
    private final static String TAG = AudioWarning.class.getSimpleName();

    private final static int AUDIO_CHAFING = 1;
    private final static int AUDIO_GENTLE_ALARM = 2;
    private final static int AUDIO_JUST_LIKE_MAGIC = 3;
    private final static int AUDIO_SOLEMN = 4;
    private final static int AUDIO_TWIRL = 5;
    private final static int AUDIO_WET = 6;

    private NotificationManager nm = null;
    private int notifyId = 1;
    private Context context = null;

    AudioWarning(Context context) {
        this.context = context;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    Notification getNotification (int type) {
        return getNotification(type, "", "");
    }

    Notification getNotification (int type, CharSequence text) {
        return getNotification(type, text, "");
    }

    Notification getNotification (int type, CharSequence text, CharSequence explanation) {

        if (type < 0 || type > 6) {
            throw new NullPointerException("type parameter out of range");
        }

        Notification noti = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "some_channel_id";
            CharSequence channelName = "Some Channel";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = null;

            notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(notificationChannel);

            Log.d(TAG, "Uri-->"+resourceToUri(context, R.raw.chafing).toString());
            notificationChannel.setSound(getNotificationSoundUri(context, type), new AudioAttributes.Builder().build());

            //
            String groupId = "some_group_id";
            CharSequence groupName = "Some Group";
            nm.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));
            //
            List<NotificationChannelGroup> notificationChannelGroups = new ArrayList();
            notificationChannelGroups.add(new NotificationChannelGroup("group_one", "Group One"));
            notificationChannelGroups.add(new NotificationChannelGroup("group_two", "Group Two"));
            notificationChannelGroups.add(new NotificationChannelGroup("group_three", "Group Three"));

            nm.createNotificationChannelGroup(new NotificationChannelGroup("group_one", "Group One"));
            //
            noti = new Notification.Builder(context, channelId)
                    .setContentTitle(text)
                    .setContentText(explanation)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            //

        } else {
            noti = new Notification.Builder(context)
                    .setContentTitle(text)
                    .setContentText(explanation)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(getNotificationSoundUri(context, type))
                    .build();
        }
        return noti;
    }

    void notify (int type, CharSequence text, CharSequence explanation) {
        nm.notify(notifyId++, getNotification(type, text, explanation));
    }
    void notify (int type, CharSequence text) {
        nm.notify(notifyId++, getNotification(type, text));
    }

    private static Uri getNotificationSoundUri (Context context, int type) {
        int resID = 0;
        switch (type) {
            case AUDIO_CHAFING:
                resID = R.raw.chafing;
                break;
            case AUDIO_GENTLE_ALARM:
                resID = R.raw.gentle_alarm;
                break;
            case AUDIO_JUST_LIKE_MAGIC:
                resID = R.raw.just_like_magic;
                break;
            case AUDIO_SOLEMN:
                resID = R.raw.solemn;
                break;
            case AUDIO_TWIRL:
                resID = R.raw.twirl;
                break;
            case AUDIO_WET:
                resID = R.raw.wet;
                break;
        }
        return resourceToUri(context, resID);
    }

    private static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }


}
