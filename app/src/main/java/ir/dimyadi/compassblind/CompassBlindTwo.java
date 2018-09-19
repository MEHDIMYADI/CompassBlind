package ir.dimyadi.compassblind;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author MEHDI DIMYADI
 * MEHDIMYADI
 */

public class CompassBlindTwo extends AppCompatActivity implements SensorEventListener {
    Button mButtonOne, mButtonTwo;
    TextView mCompass;
    float mCalibrate, mCompassValue;
    int mCount, mMaxVolume;
    boolean mToggleVibrate, mToggleSound;
    String mStart;
    String mPause;
    String mHeadPhones;
    SensorManager mSensorManager;
    MediaPlayer mSound;
    Vibrator mVibrator;
    WakeLock mWakeLock;

    @SuppressLint("WrongConstant")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_blind_two);
        mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "CompassBlindTwo");
        //mWakeLock.acquire();
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
        mSound = MediaPlayer.create(this, R.raw.sound);
        mSound.setLooping(true);
        mButtonOne = findViewById(R.id.mButtonVibrate);
        mButtonTwo = findViewById(R.id.mButtonSound);
        mCompass = findViewById(R.id.mTextCompass);
        mVibrator = (Vibrator) getSystemService("vibrator");
        mSensorManager = (SensorManager) getSystemService("sensor");
        mMaxVolume = 181;
        mCount = 0;
        mCalibrate = 0.0f;
        mStart = getResources().getString(R.string.start);
        mPause = getResources().getString(R.string.pause);
        mHeadPhones = getResources().getString(R.string.headphones);
    }

    @SuppressLint("DefaultLocale")
    public void onSensorChanged(SensorEvent event) {
        float vib;
        mCompassValue = (float) Math.round(event.values[0]);
        float degree = ((((float) Math.round(event.values[0])) - mCalibrate) + 360.0f) % 360.0f;
        ((TextView) findViewById(R.id.mTextCompass)).setText(String.format("%.1f", degree));
        if (degree < 0.5f) {
            ((TextView) findViewById(R.id.mTextCompass)).setText(R.string.N);
        }
        if (degree > 180.0f) {
            vib = 360.0f - degree;
        } else {
            vib = degree;
        }
        float volume = (float) (1.0d - (Math.log((double) (((float) mMaxVolume) - vib)) / Math.log((double) mMaxVolume)));
        mSound.setVolume(volume, volume);
        if (mCount == 1 && mToggleVibrate) {
            autoVibrate((int) vib, 500);
        }
        mCount = (mCount + 1) % 2;
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(3), 3);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void autoVibrate(int vib, int pause) {
        mVibrator.vibrate(new long[]{0, (long) vib, (long) pause}, -1);
    }

    public void mToggleVibrate(View view) {
        mToggleVibrate = !mToggleVibrate;
        ((Button) findViewById(R.id.mButtonVibrate)).setText(mToggleVibrate ? mPause : mStart);
    }

    public void mToggleSound(View view) {
        mToggleSound = !mToggleSound;
        ((Button) findViewById(R.id.mButtonSound)).setText(mToggleSound ? mPause : mStart);
        if (mToggleSound) {
            mSound.start();
            Toast.makeText(getApplicationContext(), mHeadPhones, Toast.LENGTH_SHORT).show();
            return;
        }
        mSound.pause();
    }

    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
        mWakeLock.release();
    }

    @Override
    public void onBackPressed() {
        finish();
        mSound.pause();
        mToggleSound = false;
        mToggleVibrate = false;
    }
}
