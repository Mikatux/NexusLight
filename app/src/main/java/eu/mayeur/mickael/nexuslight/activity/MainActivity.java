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

package eu.mayeur.mickael.nexuslight.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import eu.mayeur.mickael.nexuslight.R;
import eu.mayeur.mickael.nexuslight.ScreenRecorder;
import eu.mayeur.mickael.nexuslight.core.Config;
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
        if (!readConfig()) {
            Intent settingI = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(settingI);
        }
        ImageView img = (ImageView) findViewById(R.id.iv_background);
        img.setImageBitmap(createBitmap());
        // getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), createBitmap()));
        mMirroring = MirroringHelper.get();


        Button connect = (Button) findViewById(R.id.bt_connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LightSerial.connect(getApplicationContext());
                if (mMirroring.isRunning()) {
                    stop();
                } else {
                    //showProgress(R.string.connecting);
                    mMirroring.askForPermission(MainActivity.this);
                   // moveTaskToBack(true);
                }

            }
        });

        Button setting = (Button) findViewById(R.id.bt_setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LightSerial.connect(getApplicationContext());
                if (mMirroring.isRunning()) {
                    stop();
                }

                Intent settingI = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(settingI);


            }
        });

        // mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);


    }

    public Bitmap createBitmap() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(Config.VIRTUAL_DISPLAY_WIDTH, Config.VIRTUAL_DISPLAY_HEIGHT, conf);

        for (int i = 0; i < Config.VIRTUAL_DISPLAY_WIDTH; i++) {
            bitmap.setPixel(i, 0, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, 1, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, 2, Color.rgb(255, 0, 0));
        }

        for (int i = 0; i < Config.VIRTUAL_DISPLAY_HEIGHT; i++) {
            bitmap.setPixel(Config.VIRTUAL_DISPLAY_WIDTH - 1, i, Color.rgb(0, 255, 0));
            bitmap.setPixel(Config.VIRTUAL_DISPLAY_WIDTH - 2, i, Color.rgb(0, 255, 0));
            bitmap.setPixel(Config.VIRTUAL_DISPLAY_WIDTH - 3, i, Color.rgb(0, 255, 0));


        }
        for (int i = Config.VIRTUAL_DISPLAY_WIDTH - 1; i >= 1; i--) {
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 1, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 2, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 3, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 4, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 5, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 6, Color.rgb(255, 0, 0));
            bitmap.setPixel(i, Config.VIRTUAL_DISPLAY_HEIGHT - 7, Color.rgb(255, 0, 0));

        }
        for (int i = Config.VIRTUAL_DISPLAY_HEIGHT - 1; i >= 1; i--) {
            bitmap.setPixel(0, i, Color.rgb(0, 0, 255));
            bitmap.setPixel(1, i, Color.rgb(0, 0, 255));
            bitmap.setPixel(2, i, Color.rgb(0, 0, 255));

        }

        return bitmap;
    }

    public boolean readConfig() {

        SharedPreferences prefs = this.getSharedPreferences(
                "eu.mayeur.mickael.nexuslight", Context.MODE_PRIVATE);

        int ledWidth = prefs.getInt(getString(R.string.ledWidthKey), 0);
        int ledHeight = prefs.getInt(getString(R.string.ledHeightKey), 0);
        String ledIp = prefs.getString(getString(R.string.ledIpKey), "0");
        if (ledHeight == 0 || ledWidth == 0 || ledIp == "0") {
            return false;
        }
        Config.IP = ledIp;
        Config.LED_DISPLAY_WIDTH = ledWidth;
        Config.LED_DISPLAY_HEIGHT = ledHeight;
        Config.VIRTUAL_DISPLAY_WIDTH = ledWidth*2;
        Config.VIRTUAL_DISPLAY_HEIGHT= ledHeight*2;
        return true;

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
