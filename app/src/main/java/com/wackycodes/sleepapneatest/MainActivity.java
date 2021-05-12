package com.wackycodes.sleepapneatest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

import android.media.MediaRecorder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.wackycodes.sleepapneatest.helper.ApneaDetector;

/**
 *   Written by Shailendra Lodhi ( Wackycodes Design and development )
 *   Visit : http://linktr.ee/wackycodes
 *
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private  TextView xText, yText, zText;
    private  Sensor accelerometer;
    private SensorManager sensorManager;

    private Button terminator;
    private int numMovements = 0;

    private float threshold = 0;

    private float deltaX = 0, deltaY = 0, deltaZ = 0;

    private String initEntry = "xValue,yValue,zValue\n";

    private float lastX = 0, lastY = 0, lastZ = 0;

    TextView sleepMovement;

    final int REQUEST_CODE_WRITE = 123;
    final int REQUEST_CODE_RECORD = 1000;

    private final String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    Button btnRecord, btnStopRecord;
    //Button btnPlay, btnStop;

    public static String pathSave = "";
    public static String pathRaw = "";

    MediaRecorder mediaRecorder;
    //MediaPlayer mediaPlayer;

    ApneaDetector apneaDetector = new ApneaDetector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkWritePermissions()) requestStoragePermissions();

        getSupportActionBar().setTitle("Goodnight");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_bar);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            threshold = accelerometer.getMaximumRange() / 2;
        }
        else {
        Toast.makeText(getBaseContext(), "Can't Find Accelerometer", Toast.LENGTH_LONG).show();
        }

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        sleepMovement = (TextView)findViewById(R.id.sleepMovement);

        //btnPlay = (Button)findViewById(R.id.btnPlay);
        btnRecord = (Button)findViewById(R.id.btnStartRecord);
        //btnStop = (Button)findViewById(R.id.btnStop);
        btnStopRecord = (Button)findViewById(R.id.btnStopRecord);
        terminator = (Button)findViewById(R.id.terminateButton);


        /* ---AUDIO RECORDING LOGIC--- */

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });

        //Request runtime position
        if (!checkPermissionFromDevice())
            requestPermission();

        //Init view
        //btnPlay = (Button)findViewById(R.id.btnPlay);
        btnRecord = (Button)findViewById(R.id.btnStartRecord);
        //btnStop = (Button)findViewById(R.id.btnStop);
        btnStopRecord = (Button)findViewById(R.id.btnStopRecord);

        // from Android M, request runtime permission
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onResume();
                if (checkPermissionFromDevice()) {
                    setUpMediaRecorder();
                } else {
                    requestPermission();
                }
            }
        });

        terminator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                onPause();
                if (mediaRecorder != null ){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                }
                btnRecord.setEnabled(true);
                btnStopRecord.setEnabled(false);
                convertFile();

                Log.e("PATHTHISONE", pathRaw);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        if (deltaX + deltaY + deltaZ > 10) {
            numMovements ++;
        }

        if (numMovements < 10){
            sleepMovement.setText("Sleep Movement: Low");
        }
        else if (numMovements >= 10 && numMovements < 20){
            sleepMovement.setText("Sleep Movement: Medium");
        }
        else if (numMovements >= 20){
            sleepMovement.setText("Sleep Movement: High");
        }
        System.out.println(deltaX);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];
    }

    private void convertFile() {
        Log.e("MEDIAFILE BEF",  pathSave);
        File aacFile = new File(pathSave);
        Log.e("MEDIAFILE CONV", "CONVERTING");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Log.e("MEDIAFILE SUC", convertedFile.toString());

                TextView status = (TextView) findViewById(R.id.status);
                status.setText(apneaDetector.getStatus(pathSave));
                //Toast.makeText(MainActivity.this, "STATUS: " + apneaDetector.getStatus(pathRaw), Toast.LENGTH_LONG).show();
                // So fast? Love it!
            }
            @Override
            public void onFailure(Exception error) {
                Log.e("MEDIAFILE ERR", error.toString());
                // Oops! Something went wrong
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(aacFile)

                // Your desired audio format
                .setFormat(AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }

    public void displayCleanValues() {
        xText.setText("0.0");
        yText.setText("0.0");
        zText.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        xText.setText(Float.toString(deltaX));
        yText.setText(Float.toString(deltaY));
        zText.setText(Float.toString(deltaZ));
    }

    public void requestStoragePermissions() {
        ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                REQUEST_CODE_WRITE);
    }

    private void setUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

//        pathRaw = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                "/" + UUID.randomUUID().toString();

        pathRaw = getExternalFilesDir("/").getAbsolutePath();
        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        pathSave = pathRaw + "/Recording_" + formatter.format(now) + ".3aac";

        mediaRecorder.setOutputFile(pathSave);

        try {
            mediaRecorder.prepare();
            Log.e("AudioRecordTest", "preparing! ");
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AudioRecordTest", "preparing Failed - " + e.getMessage());
        }
        Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
    }

    private boolean checkWritePermissions() {
        for (String permission : requiredPermissions) {
            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)) {

                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return (write_external_storage_result == PackageManager.PERMISSION_GRANTED) && (record_audio_result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CODE_RECORD);
    }

}
