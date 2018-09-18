package ir.dimyadi.compassblind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;

/**
 * @author MEHDI DIMYADI
 * MEHDIMYADI
 */

public class MainActivity extends Activity implements SensorEventListener, OnClickListener, OnInitListener {
    float[] FormFitFunctionAndInterface = new float[16];
    float[] mAttitude = new float[3];
    private Button mDirectionButton;
    private String mCompass = "";
    float[] mGeomagnetic = new float[3];
    float[] mGravity = new float[3];
    float[] mInR = new float[16];
    private SensorManager mSensorManager;
    float[] mOrientation = new float[3];
    float[] mOutR = new float[16];
    private TextToSpeech mTTS;

    @SuppressLint("WrongConstant")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mDirectionButton = findViewById(R.id.direction_button);
        mSensorManager = (SensorManager) getSystemService("sensor");
        mTTS = new TextToSpeech(getApplicationContext(), this);
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(1), 2);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(2), 2);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(3), 2);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case 1:
                mGravity = event.values.clone();
                break;
            case 2:
                mGeomagnetic = event.values.clone();
                break;
            case 3:
                mOrientation = event.values.clone();
                break;
        }
        if (mGravity != null && mGeomagnetic != null && mOrientation != null) {
            SensorManager.getRotationMatrix(mInR, FormFitFunctionAndInterface, mGravity, mGeomagnetic);
            SensorManager.remapCoordinateSystem(mInR, 1, 2, mOutR);
            SensorManager.getOrientation(mOutR, mAttitude);
            float dNum = (float) Math.toDegrees((double) mAttitude[0]);
            mCompass = null;
            if (-22.5d > ((double) dNum) || ((double) dNum) >= 22.5d) {
                mDirectionButton.setTextColor(-1);
                mDirectionButton.setBackgroundColor(Color.BLACK);
                vibratorStop();
            } else {
                mCompass = getString(R.string.N);
                mDirectionButton.setTextColor(InputDeviceCompat.SOURCE_ANY);
                mDirectionButton.setBackgroundColor(SupportMenu.CATEGORY_MASK);
                Vibrator();
            }
            if (22.5d <= ((double) dNum) && ((double) dNum) < 67.5d) {
                mCompass = getString(R.string.NE);
            }
            if (67.5d <= ((double) dNum) && ((double) dNum) < 112.5d) {
                mCompass = getString(R.string.E);
            }
            if (112.5d <= ((double) dNum) && ((double) dNum) < 157.5d) {
                mCompass = getString(R.string.SE);
            }
            if (157.5d <= ((double) dNum) || ((double) dNum) < -157.5d) {
                mCompass = getString(R.string.S);
            }
            if (((double) dNum) >= -157.5d && ((double) dNum) < -112.5d) {
                mCompass = getString(R.string.SW);
            }
            if (((double) dNum) >= -112.5d && ((double) dNum) < -67.5d) {
                mCompass = getString(R.string.W);
            }
            if (((double) dNum) >= -67.5d && ((double) dNum) < -22.5d) {
                mCompass = getString(R.string.NW);
            }
            mDirectionButton.setText(mCompass);
        }
    }

    public void onClick(View v) {
        SpeechDirection(mCompass);
    }

    public void onInit(int status) {
        if (status == 0) {
            Locale locale = Locale.getDefault();
            if (mTTS.isLanguageAvailable(locale) >= 0) {
                mTTS.setLanguage(locale);
            } else {
                ToastText(getString(R.string.un_support_lang));
            }
        } else if (status == -1) {
            ToastText(getString(R.string.error_speech));
        }
        SpeechDirection(mCompass);
    }
    
    private void SpeechDirection(String str) {
        if (mTTS.isSpeaking()) {
            mTTS.stop();
        }
        mTTS.setSpeechRate(1.0f);
        mTTS.speak(str, 0, null);
    }

    private void ToastText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("WrongConstant")
    public void Vibrator() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert v != null;
            v.vibrate(VibrationEffect.createOneShot(60000, VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            assert v != null;
            v.vibrate(60000);
        }
    }

    @SuppressLint("WrongConstant")
    public void vibratorStop() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        v.cancel();
    }
}
