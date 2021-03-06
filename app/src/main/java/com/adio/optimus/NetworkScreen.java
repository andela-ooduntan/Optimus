package com.adio.optimus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkScreen extends AppCompatActivity {

    BluetoothAdapter bluetoothController; //= BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;// = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    Switch wifi,bluetooth,threeGSwitch;
    boolean busyBluetooth = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_screen);
        bluetoothController =  BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        initViews();

        //super.onBackPressed();
    }

    public void initViews() {
        wifi=(Switch) findViewById(R.id.wifi_switch);
        threeGSwitch=(Switch) findViewById(R.id.threeG_switch);
        bluetooth=(Switch) findViewById(R.id.bluetooth_switch);

        if (bluetoothController.isEnabled()){
            bluetooth.setChecked(true);
        }else{
            bluetooth.setChecked(false);
        }
        if (wifiManager.isWifiEnabled()){
            wifi.setChecked(true);
        }else{
            wifi.setChecked(false);
        }
        if (checkMobileDataStatus()){
            threeGSwitch.setChecked(true);
        }else{
            threeGSwitch.setChecked(false);
        }

        bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bluetoothController.enable();
                }else {
                    bluetoothController.disable();
                }
            }
        });
        wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    wifiManager.setWifiEnabled(true);
                }else{
                    //isla.WifiTurnOn();
                    wifiManager.setWifiEnabled(false);
                }
            }
        });
        threeGSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (threeGSwitch.isChecked() == true) {
                    try {
                        setMobileDataEnabled(getBaseContext(), true);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        setMobileDataEnabled(getApplicationContext(), false);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }


    private Boolean  checkMobileDataStatus(){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return  mobileDataEnabled;
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {
        try {

            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
