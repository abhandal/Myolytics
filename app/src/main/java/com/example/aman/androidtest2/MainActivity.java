package com.example.aman.androidtest2;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

import java.util.List;

import eu.darken.myolib.MyoCmds;
import eu.darken.myolib.MyoConnector;
import eu.darken.myolib.msgs.MyoMsg;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private Toast mToast;

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            showToast("connected");
        }
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            showToast("disconnected");
        }
        // onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Show the name of the pose in a toast.
            showToast(pose.toString());
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Hub hub = Hub.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hub.init(this)) {
            showToast("Couldn't initialize Hub");
            finish();
            return;
        }
        // Disable standard Myo locking policy. All poses will be delivered.
        hub.setLockingPolicy(Hub.LockingPolicy.STANDARD);
        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        // Finally, scan for Myo devices and connect to the first one found that is very near.
        hub.attachToAdjacentMyo();


        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);

        Button button= (Button) findViewById(R.id.myoConnect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMyo(v);
            }
        });

    }

    public void connectMyo(View v) {
        Intent intent = new Intent(this, ScanActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        Hub.getInstance().shutdown();
    }
    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }



}
