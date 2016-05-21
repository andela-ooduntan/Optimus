package com.adio.optimus;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class Main_menu extends AppCompatActivity {

    Button network, process, screen;
    Button btnOpt,btnOpt2;
    BluetoothAdapter bluetoothController;
    Context cont;
    Boolean reBlue=false,reWifi = false,reData = false, reScreen= false, reRotation = false, reSyn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        bluetoothController =  BluetoothAdapter.getDefaultAdapter();
        initViews();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer_method();
            }
        }, 0, 1800000);

    }

    private void timer_method(){
        Timer nextTimer=new Timer();
        nextTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ContentResolver.setMasterSyncAutomatically(true);
                try {
                    setMobileDataEnabled(true);
                } catch (Exception e) {
                }
            }
        }, 300000);
        ContentResolver.setMasterSyncAutomatically(false);
        try{
            setMobileDataEnabled(false);
        }catch (Exception e){}
        this.runOnUiThread(Timer_Tick);

    }
    private Runnable Timer_Tick=new Runnable(){
        public void run(){
            Toast.makeText(getApplicationContext(), "Background Sync and 3G was Turned on for 5 min after 30 min", Toast.LENGTH_LONG).show();
        }
    };

    public void setMobileDataEnabled(boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class<?> conmanClass = Class.forName(conman.getClass().getName());
        final java.lang.reflect.Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class<?> connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }


    public void initViews(){
        network =(Button) findViewById(R.id.network_btn);
        process =(Button) findViewById(R.id.process_btn);
        screen =(Button) findViewById(R.id.display_btn);
        btnOpt = (Button) findViewById(R.id.optimize);
        btnOpt2 = (Button) findViewById(R.id.restore);

        network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NetworkScreen.class));
            }
        });

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Process_activty.class));
            }
        });

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), screen_activity.class));
            }
        });





        btnOpt.setOnClickListener(new View.OnClickListener() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

            @Override
            public void onClick(View v) {
                int counter = 0;
                //Toast.makeText(Home.this, counter+" items were Optimized", Toast.LENGTH_LONG).show();
                // TODO Auto-generated method stub
                if (bluetoothController.isEnabled()) {
                    counter++;
                    bluetoothController.disable();
                    reBlue = true;
                }
                if (WifiChecker()) {
                    counter++;
                    wifiManager.setWifiEnabled(false);
                    reWifi =true;

                }

                if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                    counter++;
                    reRotation = true;
                }


                if (checkMobileDataStatus() == true) {
                    //Boolean name=turnData(false);
                    try {
                        setMobileDataEnabled(false);
                        reData = true;
                        counter++;
                    } catch (InvocationTargetException es) {
                        Toast.makeText(getApplicationContext(), es.getCause().toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }


                int brightnessmode = 1;

                try {
                    brightnessmode = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (Exception e) {
                    Log.d("tag", e.toString());
                }
                if (brightnessmode == 0) {
                    counter++;
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                    reScreen = true;
                }

                if (ContentResolver.getMasterSyncAutomatically()) {
                    ContentResolver.setMasterSyncAutomatically(false);
                    reSyn = true;
                    counter++;
                }
                String result = "";

                if (reWifi == true) {
                    result += " WIFI ";
                }
                if (reData == true) {
                    result += " 3G ";
                }
                if (reBlue == true) {
                    result += " BLUETOOTH ";
                }
                if (reRotation == true) {
                    result += " ROTATION ";
                }
                if (reScreen) {
                    result += " BRIGHTNESS ";
                }
                if (reSyn == true) {
                    result += " SYNCHRONIZATION ";
                }


                Toast.makeText(getApplicationContext(), counter + " items were Optimized: " + result, Toast.LENGTH_LONG).show();
            }
        });



        btnOpt2.setOnClickListener(new View.OnClickListener() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

            @Override
            public void onClick(View v) {
                int itemsNumber = 0;
                String itemsRestored = "";
                if (reWifi) {
                    wifiManager.setWifiEnabled(true);
                    itemsNumber++;
                    reWifi = false;
                    itemsRestored += " WIFI ";
                }
                if (reBlue == true) {
                    bluetoothController.enable();
                    itemsNumber++;
                    itemsRestored += " BLUETOOTH ";
                    reBlue = false;
                }
                if (reRotation == true) {
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                    itemsNumber++;
                    itemsRestored += " ROTATION ";
                    reRotation = false;
                }
                if (reData == true) {
                    try {
                        setMobileDataEnabled(true);
                        itemsNumber++;
                        itemsRestored += " 3G ";
                        reData = false;
                    } catch (Exception e) {
                    }
                }
                if (reScreen == true) {
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    itemsNumber++;
                    itemsRestored += " BRIGHTNESS ";
                    reScreen = false;
                }
                if (reSyn == true) {
                    ContentResolver.setMasterSyncAutomatically(true);
                    itemsNumber++;
                    reSyn = false;
                    itemsRestored += " SYNCHRONIZATION ";
                }
                Toast.makeText(getApplicationContext(), itemsNumber + " Items Restored: " + itemsRestored, Toast.LENGTH_LONG).show();
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


    public boolean WifiChecker(){
        WifiManager  wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getConnectionInfo().getNetworkId() == -1){
            return false;
        }else{
            return true;
        }
    }
}
