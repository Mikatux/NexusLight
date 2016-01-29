package eu.mayeur.mickael.nexuslight.light;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by Mika on 1/26/2016.
 */
public class LightNetwork {

    public void connect(){

    }
    public void setColor(int lightId, int color){
        Log.v("network","sending"+color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
    }
    public void disconnect(){

    }
    public int getNbLight(){
        return 12;
    }
}
