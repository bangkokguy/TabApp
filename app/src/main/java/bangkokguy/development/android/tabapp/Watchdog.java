package bangkokguy.development.android.tabapp;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

public class Watchdog extends JobService {

    static private final String  TAG = "Watchdog";

    final static int FAILOVER = 6;                      //AUDIO_WET = 6;

    public Watchdog() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //schedule next run
        Log.d(TAG, "onStartJob");
        ComponentName serviceComponent = new ComponentName(getApplicationContext(), Watchdog.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(100 * 1000); // wait at least
        builder.setOverrideDeadline(130 * 1000); // maximum delay

        PersistableBundle b = new PersistableBundle();
        b.putString("phoneNumber", "phoneNumber");
        b.putString("MSG", "message");
        builder.setExtras(b);

        JobScheduler jobScheduler = getApplicationContext().getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }

        //check whether supervised service is running
        boolean allRight = isMyServiceRunning(NetworkWatchdog.class);
        Log.d(TAG, "allRight="+Boolean.toString(allRight));
        if (!allRight) {
            AudioWarning aw;
            aw = new AudioWarning(this);
            aw.notify(FAILOVER, "WatchDog started");
            //Network Connect Service not running->give audible feedback
        }

        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob");
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        ComponentName serviceComponent = new ComponentName(getApplicationContext(), Watchdog.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(10 * 1000); // wait at least
        builder.setOverrideDeadline(13 * 1000); // maximum delay

        PersistableBundle b = new PersistableBundle();
        b.putString("phoneNumber", "phoneNumber");
        b.putString("MSG", "message");
        builder.setExtras(b);

        JobScheduler jobScheduler = getApplicationContext().getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
    }

}
