package eu.mayeur.mickael.nexuslight.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import eu.mayeur.mickael.nexuslight.MainActivity;
import eu.mayeur.mickael.nexuslight.R;
import eu.mayeur.mickael.nexuslight.core.Config;
import eu.mayeur.mickael.nexuslight.light.ColorExtractor;
import eu.mayeur.mickael.nexuslight.light.LightsController;
import eu.mayeur.mickael.nexuslight.light.MirroringHelper;
import eu.mayeur.mickael.nexuslight.util.SleepTask;

/**
 * Service which does all the work.
 *
 * @author David Vávra (david@vavra.me)
 */
public class LightsService extends Service {
    private MirroringHelper mMirroring;
    private ColorExtractor mColorExtractor;
    private LightsController mLights;
    //private WifiManager.MulticastLock mMulticastLock;
    // private LocalColorSwitcher mLocalSwitcher;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("light service", "create");

        mMirroring = MirroringHelper.get();
        mColorExtractor = ColorExtractor.get();
        mLights = LightsController.get();
        //  mLocalSwitcher = LocalColorSwitcher.get();
        //  App.bus().register(this);
    }

    @Override
    public void onDestroy() {
        //  App.bus().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("light service", "strat conmmand");

        if (intent.getAction().equals("START")) {
            start();
        } else if (intent.getAction().equals("STOP")) {
            stop();
        }
        return START_REDELIVER_INTENT;
    }

    private void start() {
        Log.v("light service", "start");

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new Notification.Builder(this).setSmallIcon(R.drawable.ic_play_action_normal).setContentTitle(getString(R.string
                .mirroring)).setContentText(getString(R.string.tap_to_change))
                .setContentIntent(pi).build();
        startForeground(42, notification);

        // WifiManager wifi;
        // wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //mMulticastLock = wifi.createMulticastLock("lifx");
        //mMulticastLock.acquire();

        mLights.start();
        mColorExtractor.start(mMirroring, new ColorExtractor.Listener() {
            @Override
            public void onColorExtracted(ArrayList<Integer> colors) {
                Log.v("service","receive color"+ colors.toString());

                //  if (!mLocalSwitcher.isRunning()) {
                mLights.changeColor(colors);
                //  }
            }
        });
    }

    private void stop() {
        Log.v("light service", "stop");

        mColorExtractor.stop();
        mMirroring.stop();
        mLights.signalStop();
        new SleepTask(Config.FINAL_DELAY, new SleepTask.Listener() {
            @Override
            public void awoken() {
                mLights.stop();

                stopForeground(true);
                stopSelf();
            }
        }).start();
    }

}
