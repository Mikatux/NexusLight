package eu.mayeur.mickael.nexuslight.core;

import android.preference.PreferenceManager;

/**
 * Global config which controls app behavior.
 *
 * @author David Vávra (david@vavra.me)
 */
public class Config {
    public static int INITIAL_DELAY = 100; // in ms
    public static int FINAL_DELAY = 100; // in ms
    public static int FREQUENCE_OF_SCREENSHOTS = 80; // in ms max speed
    public static int DURATION_OF_COLOR_CHANGE = 2000; // in ms
    public static float LED_BRIGHTNESS = 0.25f; // 0-1
    public static int LED_DISPLAY_WIDTH = 1; // in px
    public static int LED_DISPLAY_HEIGHT = 1; // in px for 16:9 resolution
    public static int VIRTUAL_DISPLAY_WIDTH = 3; // in px
    public static int VIRTUAL_DISPLAY_HEIGHT = 2; // in px for 16:9 resolution
    public static String IP = "0"; // IP
    //public static final String IP = "192.168.1.32"; // IP

}
