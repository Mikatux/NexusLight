package eu.mayeur.mickael.nexuslight.core;

import android.app.Application;

import eu.mayeur.mickael.nexuslight.light.MirroringHelper;
import eu.mayeur.mickael.nexuslight.light.LightsController;

/**
 * Main application object.
 *
 * @author David Vávra (david@vavra.me)
 */
public class App extends Application {

    static App sInstance;
    private MirroringHelper mMirroring;
    private LightsController mLights;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        mMirroring = MirroringHelper.get();
        mLights = LightsController.get();
        mMirroring.init();
        mLights.init();
    }

    public static App get() {
        return sInstance;
    }

}
