package eu.mayeur.mickael.nexuslight.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import eu.mayeur.mickael.nexuslight.R;
import eu.mayeur.mickael.nexuslight.core.Config;

public class SettingActivity extends Activity {
    EditText ledWidth;
    EditText ledHeight;
    EditText ledIp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ledWidth = (EditText) findViewById(R.id.etNumberLedWidth);
        ledHeight = (EditText) findViewById(R.id.etNumberLedHeight);
        ledIp = (EditText) findViewById(R.id.etIp);
        Button ok = (Button) findViewById(R.id.bt_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveConfig();
                finish();
            }
        });
    }

    public void saveConfig() {
        SharedPreferences prefs = this.getSharedPreferences(
                "eu.mayeur.mickael.nexuslight", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.ledWidthKey), Integer.parseInt(ledWidth.getText().toString()));
        editor.putInt(getString(R.string.ledHeightKey), Integer.parseInt(ledHeight.getText().toString()));
        editor.putString(getString(R.string.ledIpKey), ledIp.getText().toString());
        editor.commit();


    }
}
