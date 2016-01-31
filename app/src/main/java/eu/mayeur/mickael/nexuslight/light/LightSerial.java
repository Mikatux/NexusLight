package eu.mayeur.mickael.nexuslight.light;

import android.content.Context;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import eu.mayeur.mickael.nexuslight.core.Config;

/**
 * Created by Mika on 1/26/2016.
 */
public class LightSerial {
    private static boolean usbOpen = false;
    private static UsbSerialPort port;


    public static void connect(Context  ctx) {
        Log.v("Serial","Connect : ");

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager)  ctx.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            Log.v("Serial","available Drivers isEmpty ");

            return;
        }

// Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
       // manager.requestPermission(driver.getDevice(), mPermissionIntent);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }

// Read some data! Most have just one port (port 0).
        port = driver.getPorts().get(0);
        try {
            Log.v("Serial","Devices : " + driver.getPorts().size());
            port.open(connection);
            port.setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            usbOpen = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setColor(ArrayList<Integer> color) {
        byte coucou[] = new byte[(Config.VIRTUAL_DISPLAY_WIDTH * 2 + Config.VIRTUAL_DISPLAY_HEIGHT * 2) * 3];

        for (int i = 0; i < color.size(); i++) {
            coucou[i * 3] = (byte) (Color.red(color.get(i))-128);
            coucou[i * 3 + 1] = (byte) (Color.green(color.get(i))-128);
            coucou[i * 3 + 2] = (byte) (Color.blue(color.get(i))-128);
            if (coucou[i * 3] < 10 && coucou[i * 3 + 1] < 10 && coucou[i * 3 + 2] < 10) {
               // Log.v("network", "error "+ i);

            }

        }
        Log.v("Serial", "sending RED :" + coucou[477] + "GREEN :" + coucou[478] + "BLUE :" + coucou[479]);

        if (usbOpen) {
            try {
                port.write(coucou, Config.FREQUENCE_OF_SCREENSHOTS);
               // port.w

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            port.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        usbOpen = false;
    }


    public int getNbLight() {
        return 12;
    }
}
