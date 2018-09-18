package ir.dimyadi.compassblind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author MEHDI DIMYADI
 * MEHDIMYADI
 */

public class MainActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Button mActivityOne = findViewById(R.id.activity_one);
        mActivityOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CompassBlindOne.class));
            }
        });

        Button mActivityTwo = findViewById(R.id.activity_two);
        mActivityTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CompassBlindTwo.class));
            }
        });

    }
}
