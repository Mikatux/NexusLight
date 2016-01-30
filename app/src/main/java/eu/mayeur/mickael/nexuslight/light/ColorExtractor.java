package eu.mayeur.mickael.nexuslight.light;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import eu.mayeur.mickael.nexuslight.core.Config;
import eu.mayeur.mickael.nexuslight.util.SleepTask;

/**
 * Periodically extracts color from a bitmap.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ColorExtractor {


    private static ColorExtractor sInstance;
    private boolean mRunning = true;

    public static ColorExtractor get() {
        if (sInstance == null) {
            sInstance = new ColorExtractor();
        }
        return sInstance;
    }

    public void start(final MirroringHelper mirroring, final Listener listener) {
        mRunning = true;
        new SleepTask(Config.INITIAL_DELAY, new SleepTask.Listener() {
            @Override
            public void awoken() {
                extractBitmap(mirroring, listener);
            }
        }).start();
    }

    private void extractBitmap(final MirroringHelper mirroring, final Listener listener) {
        if (mRunning) {
            mirroring.getLatestBitmap(new MirroringHelper.Listener() {
                @Override
                public void onBitmapAvailable(final Bitmap bitmap) {
                    Log.v("colorext", "get  bitmap");
                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    for (int i = 0; i < Config.VIRTUAL_DISPLAY_WIDTH; i++) {
                        colors.add( bitmap.getPixel(i, 0));
                    }
                    for (int i = 0; i<Config.VIRTUAL_DISPLAY_HEIGHT ; i++) {
                        colors.add(bitmap.getPixel(Config.VIRTUAL_DISPLAY_WIDTH - 1, i));


                    }
                    for (int i = Config.VIRTUAL_DISPLAY_WIDTH-1; i>=0; i--) {
                        colors.add(bitmap.getPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT-1));

                    }
                    for (int i = Config.VIRTUAL_DISPLAY_HEIGHT-1; i >=0 ; i--) {
                        colors.add(bitmap.getPixel(0, i));

                    }
                    listener.onColorExtracted(colors);
                    new SleepTask(Config.FREQUENCE_OF_SCREENSHOTS, new SleepTask.Listener() {
                        @Override
                        public void awoken() {
                            extractBitmap(mirroring, listener);
                        }
                    }).start();
                    //Log.v("colorext", "send color");


                    // parse bitmap to color array
                    /*
                    Palette.generateAsync(bitmap, 25, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            bitmap.recycle();
                            int defaultColor = App.get().getResources().getColor(R.color.not_recognized);
                            final int color = palette.getVibrantColor(defaultColor);
                            listener.onColorExtracted(color);
                            new SleepTask(Config.FREQUENCE_OF_SCREENSHOTS, new SleepTask.Listener() {
                                @Override
                                public void awoken() {
                                    extractBitmap(mirroring, listener);
                                }
                            }).start();
                        }
                    });
                    */
                }
            });
        }
    }

    public void stop() {
        mRunning = false;
    }

    public interface Listener {
        public void onColorExtracted(ArrayList<Integer> color);
    }
}
