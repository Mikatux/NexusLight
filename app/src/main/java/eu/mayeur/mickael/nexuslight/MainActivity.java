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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import eu.mayeur.mickael.nexuslight.light.MirroringHelper;
import eu.mayeur.mickael.nexuslight.service.LightsService;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 1;
    private boolean wsOpen = false;
    MirroringHelper mMirroring;

    /**
     * Called when the activity is first created.
     */
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    WebSocketClient mWebSocketClient;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        mMirroring = MirroringHelper.get();

        Button connect = (Button) findViewById(R.id.bt_connect);
        EditText ipView = (EditText) findViewById(R.id.et_ip);
        final String ip = ipView.getText().toString();
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMirroring.isRunning()) {
                    stop();
                } else {
                    //showProgress(R.string.connecting);
                    mMirroring.askForPermission(MainActivity.this);
                }
                /*
                if (!wsOpen){
                connectWebSocket(ip);
                }
                else{
                    mWebSocketClient.close();
                }

                if (mRecorder != null) {
                    mRecorder.quit();
                    mRecorder = null;
                } else {
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CODE);
                }
                */
            }
        });

        Button send = (Button) findViewById(R.id.bt_send);
        EditText messageView = (EditText) findViewById(R.id.et_message);
        final String message = messageView.getText().toString();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebSocketClient.send(message);

            }
        });

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != MirroringHelper.PERMISSION_CODE) {
            return;
        }
        if (resultCode != RESULT_OK) {
            Log.e("error","no permission");
            //showError(R.string.give_permission);
            return;
        }
        mMirroring.permissionGranted(resultCode, data);
        Intent intent = new Intent(this, LightsService.class);
        intent.setAction("START");
        startService(intent);
    }
    private void stop() {
        //stop light

        Intent intent = new Intent(this, LightsService.class);
        intent.setAction("STOP");
        startService(intent);

    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }
        // video size
        EditText heightview = (EditText) findViewById(R.id.et_height);
        final int height = Integer.parseInt(heightview.getText().toString());

        EditText widthview = (EditText) findViewById(R.id.et_width);
        final int width = Integer.parseInt(widthview.getText().toString());

        File file = new File(Environment.getExternalStorageDirectory(),
                "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
        final int bitrate = 6000000;
        mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
        mRecorder.start();

        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
        moveTaskToBack(true);
    }
*/
    private void connectWebSocket(String ip) {
        URI uri;
        try {
            uri = new URI("ws://" + ip);
            Log.i("Websocket", "Connection to " + ip);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }
}
