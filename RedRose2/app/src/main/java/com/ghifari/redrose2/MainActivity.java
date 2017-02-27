package com.ghifari.redrose2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SectionsPagerAdapter msectionsPagerAdapter;

    private static int notifCount;
    /**
     * The ViewPager that will host the section contents.
     */
    private ViewPager mViewPager;

    SensorManager mSensorManager;
    Sensor mLuminousity;

    TextView t_luminousity;
    TextView t_temperature;
    TextView t_humidity;

    int l;
    int t;
    int h;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView textview = (TextView) findViewById(R.id.textNotification);
            String textTemp = (String) textview.getText();
            Bundle bundle = intent.getBundleExtra("msg");
            if (notifCount < 3) {
                textview.setText((notifCount+1) + ". " +
                                textTemp + "\n" +bundle.getString("msgBody"));
                notifCount++;
            } else {
                textview.setText((notifCount+1) + ". " + bundle.getString("msgBody"));
                notifCount = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pagerview);
        mViewPager.setAdapter(msectionsPagerAdapter);

        notifCount = 0;

        IntentFilter intentFilter = new IntentFilter("ACTION_STRING_ACTIVITY");
            registerReceiver(activityReceiver, intentFilter);


        Button b_signin = (Button) findViewById(R.id.button_main_signin);
        b_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Goal = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent_Goal);
                //startActivity(intent_Goal);
            }
        });

        Button b_capture = (Button) findViewById(R.id.button_main_capture);
        b_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScreenShot();
            }
        });

        Button b_pohon = (Button) findViewById(R.id.b_main_pohon);
        b_pohon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_Goal = new Intent(MainActivity.this,PohonStatus.class);
                MainActivity.this.startActivity(intent_Goal);
                //startActivity(intent_Goal);
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLuminousity = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager.registerListener(this, mLuminousity, SensorManager.SENSOR_DELAY_NORMAL);


        t_luminousity = (TextView) findViewById(R.id.luminousity);
        t_temperature = (TextView) findViewById(R.id.temperature);
        t_humidity = (TextView) findViewById(R.id.humidity);

        l = 0;
        t = 0;

        t_humidity.setText(h + "%");
        t_temperature.setText(t + " C");
    }

    public void Generatenew_Pohon()
    {

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.v("sensor", "changed");
        l = (int)sensorEvent.values[0];
        t_luminousity.setText(l + " cd");
        Log.v("sval", String.valueOf(sensorEvent.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void ScreenShot()
    {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Log.v("send intent", "should work");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, getScreenShot(rootView));
        sendIntent.setType("image/bitmap");
        MainActivity.this.startActivity(Intent.createChooser(sendIntent, "dang son"));
    }

    public Uri getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        Context context = MainActivity.this;
        return getImageUri(context, scaleDownBitmap(bitmap, 100, context));
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}