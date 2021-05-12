package com.wackycodes.sleepapneatest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 *   Written by Shailendra Lodhi ( Wackycodes Design and development )
 *   Visit : http://linktr.ee/wackycodes
 *
 */

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_RECORD = 101;
    public static final int REQUEST_CODE_RESULTS = 100;

    /* components */
    private Button btnRecord;
    private Button btnResults;


    private Button btnTestMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_main);

        //get widgets
        btnRecord = findViewById(R.id.button_record);
        btnResults = findViewById(R.id.button_result);
        btnTestMode = findViewById(R.id.button_test_mode);

        //setup widget behavior
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecord();
            }
        });

        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResults();
            }
        });

        btnTestMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( ActivityMain.this, MainActivity.class ));
            }
        });
    }

    //own methods down here
    private void openRecord(){
        Intent intentRecord = new Intent(ActivityMain.this, RecordActivity.class);
        //intentRecord.putExtra("message", msg);
        //startActivity(intentActivity2);
        startActivityForResult(intentRecord, REQUEST_CODE_RECORD);
    }

    private void openResults(){
        Intent intentResults = new Intent(ActivityMain.this, ResultsActivity.class);
        //intentResults.putExtra("message", msg);
        //startActivity(intentActivity2);
        startActivityForResult(intentResults, REQUEST_CODE_RESULTS);
    }


}