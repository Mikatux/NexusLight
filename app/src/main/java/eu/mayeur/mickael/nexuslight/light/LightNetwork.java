package eu.mayeur.mickael.nexuslight.light;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import eu.mayeur.mickael.nexuslight.core.Config;

/**
 * Created by Mika on 1/26/2016.
 */
public class LightNetwork {
    private ArrayList<Integer> prevColor = null;


    public static byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }
    public void setColor(ArrayList<Integer> color) {
        prevColor = color;

        List<Byte> colors = new ArrayList<>();
        int value = 3*9; // the 9 first pixels are black :/
        for (int i = 0; i < color.size(); i++) { // WS2811 1pixel = 3 leds
                colors.add(((byte) i));
                colors.add(((byte) Math.min(Color.red(color.get(i)) * 1.4, 255)));
                colors.add(((byte) Math.min(Color.green(color.get(i)) * 1.4, 255)));
                colors.add(((byte) Math.min(Color.blue(color.get(i)) * 1.4, 255)));

        }

        int server_port = 12345;
        DatagramSocket s = null;

        try {
            s = new DatagramSocket();
            InetAddress local = InetAddress.getByName(Config.IP);
            byte[] message = toByteArray(colors);
            DatagramPacket p = new DatagramPacket(message, message.length,local,server_port);
            s.send(p);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // byte coucou[] = new byte[(Config.VIRTUAL_DISPLAY_WIDTH * 2 + Config.VIRTUAL_DISPLAY_HEIGHT * 2) * 3];
/*
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
/*
        }

        //Log.v("network", "sending RED :" + coucou[477] + "GREEN :" + coucou[478] + "BLUE :" + coucou[479]);

        if (wsOpen) {
            prevColor = color;

            mWebSocketClient.send("S");
            //mWebSocketClient.send(coucou);
            // mWebSocketClient.s
        }
        */
    }

    public int getNbLight() {
        return 12;
    }
}
