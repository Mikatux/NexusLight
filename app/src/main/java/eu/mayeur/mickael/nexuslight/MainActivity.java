/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package eu.mayeur.mickael.nexuslight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import eu.powet.android.serialUSB.ISerial;
import eu.powet.android.serialUSB.SerialError;
import eu.powet.android.serialUSB.SerialEvent;
import eu.powet.android.serialUSB.SerialListener;
import eu.powet.android.serialUSB.UsbDeviceID;
import eu.powet.android.serialUSB.UsbSerial;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ISerial usb_serial = null;


        usb_serial = new UsbSerial(UsbDeviceID.FT232RL, 19200, this);
        usb_serial.open();


        usb_serial.addEventListener(new SerialListener() {
            @Override
            public void incomingDataEvent(final SerialEvent evt) {
                {
                    Log.v("Main", "Event from Usb Serial" + new String(evt.read()));

                }
            }
        });

        try {
            usb_serial.write("Hello World");
        } catch (SerialError e) {
            Log.v("Main", e.toString());
        }
        usb_serial.close();
    }
}
