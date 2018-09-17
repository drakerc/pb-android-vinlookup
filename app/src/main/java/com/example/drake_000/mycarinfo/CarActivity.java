package com.example.drake_000.mycarinfo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import static android.app.PendingIntent.getActivity;
import android.os.Handler;
import com.example.drake_000.mycarinfo.fragment_carInfo;

public class CarActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_car);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new fragment_carInfoPrompt())
                    .commit();
        }

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                Fragment fragmentA = getSupportFragmentManager().findFragmentByTag("carInfoTag");
                if (fragmentA != null) {
                    onBackPressed();
                    Toast.makeText(context,
                            getResources().getString(R.string.goingBack),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
