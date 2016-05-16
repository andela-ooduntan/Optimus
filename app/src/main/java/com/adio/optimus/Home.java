package com.adio.optimus;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {
	Toolbar tbar;
    ViewPager mPager;
    SlidingTabLayout tabs;
    boolean reWifi=false,reBlue=false,reScreen=false,reData=false,reSyn=false,reRotation=false;
    BluetoothAdapter bluetoothController; //= BluetoothAdapter.getDefaultAdapter();
    WifiManager wifiManager;// = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    Button btnOpt,btnOpt2;
    Switch wifi,bluetooth,threeGSwitch,rotation,Brightness,sync;
    Context cont;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        bluetoothController =  BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		initViews();
		//setSupportActionBar(tbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        //tabs.setCustomTabView(R.layout.customtablayout, R.id.tabtext);
        //tabs.setBackgroundColor(getResources().getColor(R.color.tabcolor));
        //tabs.setDistributeEvenly(true);
		//tabs.setViewPager(mPager);
		optimize();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer_method();
            }
        },0,1800000);
	}

    public void initViews(){
        btnOpt = (Button) findViewById(R.id.btnOpt);
        btnOpt2 = (Button) findViewById(R.id.btnOpt2);
        wifi=(Switch) findViewById(R.id.wifi);
        threeGSwitch=(Switch) findViewById(R.id.threeGSwitch);
        bluetooth=(Switch) findViewById(R.id.bluetoothSwitch);
        Brightness=(Switch) findViewById(R.id.screen);
        rotation=(Switch) findViewById(R.id.rotationSwitch);
        sync=(Switch) findViewById(R.id.sync);
		//tbar = (Toolbar) findViewById(R.id.toolbar);
		//tabs = (SlidingTabLayout) findViewById(R.id.tabstrip);
		//mPager = (ViewPager) findViewById(R.id.pager);
		btnOpt = (Button) findViewById(R.id.btnOpt);
        btnOpt2 = (Button) findViewById(R.id.btnOpt2);
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
        if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            rotation.setChecked(true);
        }else{
            rotation.setChecked(false);
        }
        int brightnessmode = 1;

        try {
            brightnessmode = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            Log.d("tag", e.toString());
        }
        if (brightnessmode == 0) {
            Brightness.setChecked(false);
        }else{
            Brightness.setChecked(true);
        }
        if (ContentResolver.getMasterSyncAutomatically()){
           sync.setChecked(true);
        }else{
            sync.setChecked(false);
        }
	}

    private void timer_method(){
        Timer nextTimer=new Timer();
        nextTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ContentResolver.setMasterSyncAutomatically(true);
                try{
                    setMobileDataEnabled(true);
                }catch (Exception e){}
            }
        },300000);
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



    public boolean WifiChecker(){
         WifiManager  wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getConnectionInfo().getNetworkId() == -1){
            return false;
        }else{
            return true;
        }
    }


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




    public void optimize(){

        bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bluetoothController.enable();
                    reBlue=false;
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
                    reWifi=false;
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
                        setMobileDataEnabled(true);
                        reData=false;
                    }catch (Exception e){Toast.makeText(getApplicationContext(), " items were Optmized", Toast.LENGTH_LONG).show();}
                } else {
                    try {
                        setMobileDataEnabled(false);
                    }catch (Exception e){Toast.makeText(getApplicationContext(), " items were ", Toast.LENGTH_LONG).show();}
                }
            }
        });


        rotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                    reRotation=false;
                }else{
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                }
            }
        });

        Brightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                    reScreen=false;
                }else{
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                }
            }
        });


        sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ContentResolver.getMasterSyncAutomatically()){
                    ContentResolver.setMasterSyncAutomatically(false);
                    reSyn=false;
                }else {
                    ContentResolver.setMasterSyncAutomatically(true);
                }
            }
        });











		btnOpt.setOnClickListener(new View.OnClickListener() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			@Override
			public void onClick(View v) {
                int counter=0;
                //Toast.makeText(Home.this, counter+" items were Optimized", Toast.LENGTH_LONG).show();
				// TODO Auto-generated method stub
                if(bluetoothController.isEnabled()){
                    counter++;
                    bluetoothController.disable();
                    bluetooth.setChecked(false);
                    reBlue=true;
                }
                if(WifiChecker()==true){
                    counter++;
                    wifiManager.setWifiEnabled(false);
                    wifi.setChecked(false);
                    reWifi=true;
                }

                if(android.provider.Settings.System.getInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
                {
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                    counter++;
                    reRotation=true;
                    rotation.setChecked(false);
                }


                if (checkMobileDataStatus()==true) {
                    //Boolean name=turnData(false);
                    try {
                        setMobileDataEnabled( false);
                        reData=true;
                        threeGSwitch.setChecked(false);
                        counter++;
                    }catch (InvocationTargetException es){ Toast.makeText(getApplicationContext(), es.getCause().toString(), Toast.LENGTH_LONG).show();}
                    catch (Exception e){ Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}
                }


                int brightnessmode=1;

                try {
                    brightnessmode = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (Exception e) {
                    Log.d("tag", e.toString());
                }
                if(brightnessmode==0){
                    counter++;
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                    Brightness.setChecked(true);
                    reScreen=true;
                }

                if (ContentResolver.getMasterSyncAutomatically()){
                    ContentResolver.setMasterSyncAutomatically(false);
                    reSyn=true;
                    sync.setChecked(false);
                    counter++;
                }
                String result="";

                if (reWifi==true){
                    result+=" WIFI ";
                }
                if (reData==true){
                    result+=" 3G ";
                }
                if (reBlue==true){
                    result+=" BLUETOOTH ";
                }
                if (reRotation==true){
                    result+=" ROTATION ";
                }
                if (reScreen){
                    result+=" BRIGHTNESS ";
                }
                if (reSyn==true){
                    result+=" SYNCHRONIZATION ";
                }





                Toast.makeText(getApplicationContext(), counter+" items were Optimized: "+result, Toast.LENGTH_LONG).show();
			}
		});



        btnOpt2.setOnClickListener(new View.OnClickListener() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            @Override
            public void onClick(View v) {
                int itemsNumber=0;
                String itemsRestored="";
                if(reWifi==true){
                    wifiManager.setWifiEnabled(true);
                    wifi.setChecked(true);
                    itemsNumber++;
                    itemsRestored+=" WIFI ";
                    reWifi=false;
                }
                if(reBlue==true){
                    bluetoothController.enable();
                    bluetooth.setChecked(true);
                    itemsNumber++;
                    itemsRestored+=" BLUETOOTH ";
                    reBlue=false;
                }
                if(reRotation==true){
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                    itemsNumber++;
                    itemsRestored+=" ROTATION ";
                    rotation.setChecked(true);
                    reRotation=false;
                }
                if(reData==true){
                    try {
                        setMobileDataEnabled(true);
                        itemsNumber++;
                        itemsRestored+=" 3G ";
                        reData=false;
                        threeGSwitch.setChecked(true);
                    }catch (Exception e){}
                }
                if (reScreen==true){
                    android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    itemsNumber++;
                    itemsRestored+=" BRIGHTNESS ";
                    reScreen=false;
                    Brightness.setChecked(false);
                }
                if (reSyn==true){
                    ContentResolver.setMasterSyncAutomatically(true);
                    itemsNumber++;
                    reSyn=false;
                    sync.setChecked(true);
                    itemsRestored+=" SYNCHRONIZATION ";
                }
                Toast.makeText(getApplicationContext(), itemsNumber+" Items Restored: "+itemsRestored, Toast.LENGTH_LONG).show();
            }
        });
	}

    public Boolean turnData(boolean ON){
        try{
        final ConnectivityManager conman = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);

        }catch (InvocationTargetException es){ Toast.makeText(getApplicationContext(), es.getCause().toString(), Toast.LENGTH_LONG).show();}
        catch (Exception e){ Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();}
        return true;
    }





	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
			case R.id.search_action:{
				Toast.makeText(getApplicationContext(), "Search action clicked", Toast.LENGTH_LONG).show();
				break;
			}case R.id.settings:{
				Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}






//    @Override
//    public void onActionModeStarted (Bundle savedInstanceState) {
//        super.onActionModeStarted(savedInstanceState);
//
//        TextView tv = (TextView) getView().findViewById(R.id.text);
//        tv.setText(getActivity.getSomeText());
//    }
}


