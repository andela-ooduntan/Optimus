package com.adio.optimus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Context context;
    WifiManager wifiManager;
    Boolean reBlue=false,reWifi = false,reData = false, reScreen= false, reRotation = false, reSyn = false;
    boolean busyBluetooth = false, activate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        context = this;
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        bluetoothController =  BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        initViews();
    }

    private void timer_method(){
        ContentResolver.setMasterSyncAutomatically(false);
        try{
            setMobileDataEnabled(false);
        }catch (Exception e){}
        this.runOnUiThread(Timer_Tick);
//        if (!activate) activateResources();

    }
    private  void activateResources() {
        Timer nextTimer=new Timer();
        nextTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("The second coming", "kjkjkjkkjgftfytfyy");

                ContentResolver.setMasterSyncAutomatically(false);
                try {

//                    Log.d("TAG","Health: " + bluetoothController.getProfileConnectionState(3));
//                    Log.d("TAG","HEADSET: " + bluetoothController.getProfileConnectionState(1));
//                    Log.d("TAG","A2dp: " + bluetoothController.getProfileConnectionState(2));
                    setMobileDataEnabled(false);
                } catch (Exception e) {
                }
            }
        }, 180000);
        activate = true;
        this.runOnUiThread(Timer_Tick2);
        timer_method();

    }

    private Runnable Timer_Tick2 = new Runnable(){
        public void run(){
            Toast.makeText(getApplicationContext(), "3G turned on for 3min", Toast.LENGTH_LONG).show();
        }
    };

    private Runnable Timer_Tick = new Runnable(){
        public void run(){
            Toast.makeText(getApplicationContext(), "3G turned off", Toast.LENGTH_LONG).show();
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
        network = (Button) findViewById(R.id.network_btn);
        process = (Button) findViewById(R.id.process_btn);
        screen = (Button) findViewById(R.id.display_btn);
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
            @Override
            public void onClick(View v) {

                int counter = 0;
                //Toast.makeText(Home.this, counter+" items were Optimized", Toast.LENGTH_LONG).show();
                // TODO Auto-generated method stub
                if (bluetoothController.isEnabled() && !busyBluetooth) {
                    counter++;
                    bluetoothController.disable();
                    reBlue = true;
                }
                if (!WifiChecker()) {
                    counter++;
                    wifiManager.setWifiEnabled(false);
                    reWifi = true;

                }

                if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                    counter++;
                    reRotation = true;
                }


                if (checkMobileDataStatus() == true) {

                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    View prompt = layoutInflater.inflate(R.layout.delay_dialog, null);
                    AlertDialog.Builder alertBulder = new AlertDialog.Builder(context);
                    alertBulder.setView(prompt);
                    final EditText input = (EditText) prompt.findViewById(R.id.userInput);

                    alertBulder
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Do the timer here
                                    turnOffData(input.getText().toString());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertBulder.create();
                    alertBulder.show();
//
//


//                    try {
//                        setMobileDataEnabled(false);
//                    } catch (ClassNotFoundException e) {
//                        Log.d("jnjnjnj", "error 1 ");
//                    } catch (NoSuchFieldException e) {
//                        Log.d("jnjnjnj", "error 2");
//
//                    } catch (IllegalAccessException e) {
//                        Log.d("jnjnjnj", "error 3");
//
//                    } catch (NoSuchMethodException e) {
//                        Log.d("jnjnjnj", "error 4" + e);
//
//                    } catch (InvocationTargetException e) {
//                        Log.d("jnjnjnj", "error 5");
//
//                    }
//                    reData=true;

//                    Boolean name=turnData(false);
//                    Timer myTimer = new Timer();
//                    myTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            timer_method();
//                            reData = true;
//                        }
//                    }, 0);
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

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                     //Device found
                    busyBluetooth = false;
                }
                else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                     //Device is now connected
                    busyBluetooth = true;
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                     //Done searching
                    busyBluetooth = false;
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                     //Device is about to disconnect
                    busyBluetooth = false;
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                     //Device has disconnected
                    busyBluetooth = false;
                }
            }
        };

        btnOpt2.setOnClickListener(new View.OnClickListener() {

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




    public void turnOffData(String DelayTime) {
        int delayTime = 0;

        try {
            delayTime = Integer.parseInt(DelayTime);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid time", Toast.LENGTH_LONG).show();

        }
//        int realTimer = DelayTime || 0;
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer_method();
                reData = true;
//                Toast.makeText(Home.this, +" items were Optimized", Toast.LENGTH_LONG).show();
            }
        }, delayTime);
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

    public boolean checkIfWifiIsBusy(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }


    public boolean WifiChecker(){

        if (wifiManager.getConnectionInfo().getNetworkId() == -1){
            return false;
        }else{
            return true;
        }
    }
}
