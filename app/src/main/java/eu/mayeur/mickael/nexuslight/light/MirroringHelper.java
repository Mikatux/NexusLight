package eu.mayeur.mickael.nexuslight.light;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import eu.mayeur.mickael.nexuslight.core.App;
import eu.mayeur.mickael.nexuslight.core.Config;
import eu.mayeur.mickael.nexuslight.core.BaseAsyncTask;

/**
 * Helper class which deals with Media Projection.
 *
 * @author David Vávra (david@vavra.me)
 */
public class MirroringHelper {

    public static final int PERMISSION_CODE = 42;
    static MirroringHelper sInstance;
    boolean mMirroring = false;
    ImageReader mImageReader;
    private MediaProjectionManager mProjectionManager;
    private DisplayMetrics mMetrics;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    Bitmap mBitmap;

    public static MirroringHelper get() {
        if (sInstance == null) {
            sInstance = new MirroringHelper();
        }
        return sInstance;
    }

    public void init() {
        Log.v("mirror","init");

        mProjectionManager = (MediaProjectionManager) App.get().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMetrics = App.get().getResources().getDisplayMetrics();


    }

    public void askForPermission(Activity activity) {
        mMirroring = true;
        activity.startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
    }

    public void stop() {
        mMirroring = false;
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
    }

    private VirtualDisplay createVirtualDisplay() {
        Log.v("mirror","createVirtualDisplay");

        return mMediaProjection.createVirtualDisplay("LIFX",
                Config.VIRTUAL_DISPLAY_WIDTH, Config.VIRTUAL_DISPLAY_HEIGHT, mMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                null, null /*Callbacks*/, null /*Handler*/);
    }

    public boolean isRunning() {
        return mMirroring;
    }

    public void permissionGranted(int resultCode, Intent data) {
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        Log.v("mirror", "permission granted");
        mVirtualDisplay = createVirtualDisplay();
        mImageReader = ImageReader.newInstance(Config.VIRTUAL_DISPLAY_WIDTH, Config.VIRTUAL_DISPLAY_HEIGHT, PixelFormat.RGBA_8888, 1);
        mVirtualDisplay.setSurface(mImageReader.getSurface());

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                //mImageReader.setOnImageAvailableListener(null, null);
                //    Log.v("miror", "image available");

                        try {
                            //   Log.v("miror", "generating bitmap");

                            Image img = mImageReader.acquireLatestImage();
                            //ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            final Image.Plane[] planes = img.getPlanes();
                            final ByteBuffer buffer = (ByteBuffer) planes[0].getBuffer().rewind();
                            mBitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
                            mBitmap.copyPixelsFromBuffer(buffer);
                            //mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            //bos.close();
                            img.close();
                            //mImageReader.close();

                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                        }
            }
        }, null);
    }

    public void getLatestBitmap(final Listener listener) {
      //  Log.v("mirror","get last bitmap");
       // Log.v("miror", "set surface");
        listener.onBitmapAvailable(mBitmap);
    }

    public interface Listener {
        void onBitmapAvailable(Bitmap bitmap);
    }
}
