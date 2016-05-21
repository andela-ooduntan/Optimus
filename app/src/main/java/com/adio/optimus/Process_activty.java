package com.adio.optimus;

import android.content.ContentResolver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Process_activty extends AppCompatActivity {
    Switch sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activty);

        initViews();
        //super.onBackPressed();
    }

    public void initViews () {
        sync=(Switch) findViewById(R.id.sync_switch);
        if (ContentResolver.getMasterSyncAutomatically()){
            sync.setChecked(true);
        }else{
            sync.setChecked(false);
        }

        sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ContentResolver.getMasterSyncAutomatically()){
                    ContentResolver.setMasterSyncAutomatically(false);
                }else {
                    ContentResolver.setMasterSyncAutomatically(true);
                }
            }
        });
    }
}
