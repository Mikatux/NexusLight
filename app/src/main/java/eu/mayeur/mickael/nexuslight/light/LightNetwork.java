package eu.mayeur.mickael.nexuslight.light;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.mayeur.mickael.nexuslight.core.Config;

/**
 * Created by Mika on 1/26/2016.
 */
public class LightNetwork {
    WebSocketClient mWebSocketClient;
    private static boolean wsOpen = false;
    private ArrayList<Integer> prevColor = null;


    public void connect() {

        URI uri;
        try {
            uri = new URI("ws://" + Config.IP);
            Log.i("Websocket", "Connection to " + Config.IP);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                wsOpen = true;
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("Websocket", "Message " + s);

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                wsOpen = false;


            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                wsOpen = false;

            }
        };
        mWebSocketClient.connect();
    }

    public void setColor(ArrayList<Integer> color) {
       // byte coucou[] = new byte[(Config.VIRTUAL_DISPLAY_WIDTH * 2 + Config.VIRTUAL_DISPLAY_HEIGHT * 2) * 3];

        int value = 3*9; // the 9 first pixels are black :/
        for (int i = value; i < color.size(); i+=3) { // WS2811 1pixel = 3 leds
            if (wsOpen) {
                //mWebSocketClient.send("[" + i + "]" + "[" + Color.red(color.get(i)) + "]" + "[" + Color.green(color.get(i)) + "]" + "[" + Color.blue(color.get(i)) + "]");
                if(prevColor == null || !color.get(i).equals(prevColor.get(i))) {
                    mWebSocketClient.send((i - value) / 3 + " " + Math.min((int)Color.red(color.get(i))*1.4,255) + " " + (int)Color.blue(color.get(i))*0.7 + " " + (int)Color.green(color.get(i)));
                }
               // if (i-value==0)
               // Log.v("network", "sending for 0 :" + (i-value)/6 + " " + Color.red(color.get(i)) + " " + Color.blue(color.get(i)) + " " + Color.green(color.get(i)));

            }
/*
            coucou[i * 3] = 0;//(byte) (Color.red(color.get(i))-128);
            coucou[i * 3 + 1] = (byte)255;//(byte) (Color.green(color.get(i))-128);
            coucou[i * 3 + 2] = 0;//(byte) (Color.blue(color.get(i))-128);
            if (coucou[i * 3] < 10 && coucou[i * 3 + 1] < 10 && coucou[i * 3 + 2] < 10) {
               // Log.v("network", "error "+ i);

            }
//*/
        }

        //Log.v("network", "sending RED :" + coucou[477] + "GREEN :" + coucou[478] + "BLUE :" + coucou[479]);

        if (wsOpen) {
            prevColor = color;

            mWebSocketClient.send("S");
            //mWebSocketClient.send(coucou);
            // mWebSocketClient.s
        }
    }

    public void disconnect() {
        mWebSocketClient.close();
        wsOpen = false;
    }


    public int getNbLight() {
        return 12;
    }
}
