package com.wonbin.autoclick;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by wilburnLee on 2019/4/22.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mStart;
    private EditText mInterval;
    private RadioGroup mCheckMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStart = findViewById(R.id.start);
        mInterval = findViewById(R.id.interval);
        mCheckMode = findViewById(R.id.check_mode);
        mStart.setOnClickListener(this);

        // 获取无障碍权限弹窗 - 每次安装都会弹一次
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AutoService.class);
        int idV = v.getId();
        if (idV == R.id.start) {
            intent.putExtra(AutoService.ACTION, AutoService.SHOW);
            intent.putExtra("interval", Integer.valueOf(mInterval.getText().toString()));
            int id = mCheckMode.getCheckedRadioButtonId();
            intent.putExtra(AutoService.MODE, id == R.id.swipe ? AutoService.SWIPE : AutoService.TAP);
        }
        startService(intent);
        finish();
    }
}
