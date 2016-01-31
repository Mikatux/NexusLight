package eu.mayeur.mickael.nexuslight.light;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import eu.mayeur.mickael.nexuslight.core.App;
import eu.mayeur.mickael.nexuslight.core.BaseAsyncTask;

/**
 * Created by Mika on 1/26/2016.
 */
public class LightsController {

    private static final int TIMEOUT = 5;
    private static LightsController sInstance;
    private boolean mWorkingFine;
    private boolean mDisconnected;
    private LightNetwork mNetworkContext;
   // private LightSerial mSerialContext;

    private ArrayList<Integer> mPreviousColors = new ArrayList<Integer>();

    public static LightsController get() {
        if (sInstance == null) {
            sInstance = new LightsController();
        }
        return sInstance;
    }
    public void init() {
        mWorkingFine = false;
        mDisconnected = false;
        mNetworkContext = new LightNetwork();
        //mSerialContext = new LightSerial();
    }
    public void changeColor(ArrayList<Integer> colors) {
       // Log.v("lightcontroller", "change color");

        if (true || mWorkingFine && colors.equals(mPreviousColors)) {
            mNetworkContext.setColor(colors);
            //mSerialContext.setColor(colors);
            mPreviousColors = colors;
        }
        else{
            Log.v("lightcontroller", "change color error "+ mWorkingFine);

        }
    }
    private void startRocking() {
        Log.v("lightcontroller", "start rocking");

        // App.bus().post(new SuccessEvent());
        mWorkingFine = true;
      //  mNetworkContext.getAllLightsCollection().setPowerState(LFXTypes.LFXPowerState.ON);
    }
    public void start() {
        Log.v("lightcontroller", "start");

        mNetworkContext.connect();
        if (!mWorkingFine) {
            Log.v("lightcontroller", "start timeout");

            new TimeoutTask().start();
        } else {
            startRocking();
        }
    }

    public void stop() {
        mDisconnected = true;

        if (mNetworkContext != null && mWorkingFine) {
            mNetworkContext.disconnect();
        }
        /*
        if (mSerialContext != null && mWorkingFine) {
            mSerialContext.disconnect();
        }
        */
    }
    class TimeoutTask extends BaseAsyncTask {

        @Override
        public void inBackground() {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException ignored) {
            }
        }

        @Override
        public void postExecute() {

            int numLights = mNetworkContext.getNbLight();
           // int numLights = mSerialContext.getNbLight();
            Log.v("lightcontroller", "post execute"+ numLights);

            if (numLights == 0 || mDisconnected) {
                Log.v("lightcontroller", "bad post execute");

                // App.bus().post(new ErrorEvent(R.string.no_lights_found));
            } else {
                startRocking();
            }
        }
    }

    public void signalStop() {
        int color = App.get().getResources().getColor(android.R.color.white);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        mNetworkContext.setColor(colors);
       // mSerialContext.setColor(colors);
    }
}
