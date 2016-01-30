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

import eu.mayeur.mickael.nexuslight.light.LightSerial;
import eu.mayeur.mickael.nexuslight.light.MirroringHelper;
import eu.mayeur.mickael.nexuslight.service.LightsService;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    MirroringHelper mMirroring;

    /**
     * Called when the activity is first created.
     */
    private ScreenRecorder mRecorder;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

            }
        });

       // mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != MirroringHelper.PERMISSION_CODE) {
            return;
        }
        if (resultCode != RESULT_OK) {
            Log.e("error", "no permission");
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }
}
