package com.powerful.activity.myapplication;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.powerful.activity.service.BeyondUpnpService;
import com.powerful.activity.service.SystemService;
import com.powerful.activity.upnp.SystemManager;

public class MainActivity extends AppCompatActivity {
    public static final String DIALOG_FRAGMENT_TAG = "dialog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Bind UPnP service
        Intent upnpServiceIntent = new Intent(MainActivity.this, BeyondUpnpService.class);
        bindService(upnpServiceIntent, mUpnpServiceConnection, Context.BIND_AUTO_CREATE);
        // Bind System service
        Intent systemServiceIntent = new Intent(MainActivity.this, SystemService.class);
        bindService(systemServiceIntent, mSystemServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //从FragmentManager中获取到FragmentTransaction的实例
            // （作用是为了对Fragment进行添加（add），移除(remove)，替换(replace)以及执行其他动作）
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            //DIALOG_FRAGMENT_TAG通过查找 得到该Fragment,第一次还没添加的时候得到的Fragment为null;
            Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
            if(prev != null){
                ft.remove(prev);
            }

            //将Fragment加入到回退栈。取决于你是否要在回退的时候显示上一个Fragment
            ft.addToBackStack(null);

            DialogFragment newFragment  = DeviceListDialogFragment.newInstance();
            newFragment.show(ft,DIALOG_FRAGMENT_TAG);
        }

        return super.onOptionsItemSelected(item);
    }


    private ServiceConnection mUpnpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BeyondUpnpService.LocalBinder binder = (BeyondUpnpService.LocalBinder) service;
            BeyondUpnpService beyondUpnpService = binder.getService();

            SystemManager systemManager = SystemManager.getInstance();
            systemManager.setUpnpService(beyondUpnpService);
            //Search on service created.
            systemManager.searchAllDevices();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            SystemManager.getInstance().setUpnpService(null);
        }
    };

    private ServiceConnection mSystemServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SystemService.LocalBinder systemServiceBinder = (SystemService.LocalBinder) service;
            //Set binder to SystemManager
            SystemManager systemManager = SystemManager.getInstance();
            systemManager.setSystemService(systemServiceBinder.getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            SystemManager.getInstance().setSystemService(null);
        }
    };
}
