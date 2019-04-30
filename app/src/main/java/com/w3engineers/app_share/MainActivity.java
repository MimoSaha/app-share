package com.w3engineers.app_share;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.w3engineers.appshare.application.ui.InAppShareControl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.open);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InAppShareControl.getInstance().startInAppShareProcess(MainActivity.this, null);
            }
        });
    }
}
