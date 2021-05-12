package com.wackycodes.sleepapneatest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class RecordActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final int REQUEST_CODE_RESULTS = 100;

    String recordFile = null;
    MediaRecorder recorder = new MediaRecorder();
    private ToggleButton recordToggle;
    private TextView textviewRecord;
    private TextView textviewNotRecord;
    private Button btnResults;
    private static String fileName = null;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};


    public static String pathSave = "";
    public static String pathRaw = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        recordToggle = findViewById(R.id.toggleButton1);
        textviewRecord = findViewById(R.id.recordingText);
        textviewNotRecord = findViewById(R.id.not_recordingText);
        btnResults = findViewById(R.id.button_result);


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


        recordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                Log.d("Toggle", "onCheckedChanged: "+isChecked);
                if (isChecked){
                    textviewNotRecord.setVisibility(View.INVISIBLE);
                    textviewRecord.setVisibility(View.VISIBLE);
                    recorder = new MediaRecorder();
                    startRecording();
//                    setUpMediaRecorder();
                }else{
                    textviewNotRecord.setVisibility(View.VISIBLE);
                    textviewRecord.setVisibility(View.INVISIBLE);
                    stopRecording();
                }
            }

        });

        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordToggle.setChecked( false );
                stopRecording();
                openResults();
            }
        });

    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
//            convertFile();
        }
    }

    private void startRecording() {
        //Get app external directory path
        String recordPath = getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".wav";
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);

        //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        recorder.setOutputFile(recordPath + "/" + recordFile);

        recorder.setAudioChannels(1);

        try {
            recorder.prepare();
            Log.e("AudioRecordTest", "preparing....");
        } catch (IOException e) {
            Log.e("AudioRecordTest", "prepare failed " + e.getMessage());
        }

        recorder.start();
    }

    private void setUpMediaRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

//        pathRaw = Environment.getExternalStorageDirectory().getAbsolutePath() ;
        pathRaw = getExternalFilesDir("/").getAbsolutePath();
//                +"/" + UUID.randomUUID().toString();
//        pathSave = pathRaw + "_audio_record.3aac";


        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        pathSave = pathRaw +  "/Recording_" + formatter.format(now) + ".mav";

        recorder.setOutputFile(pathSave);
        recorder.setAudioChannels(1);

        try {
            recorder.prepare();
            Log.e("AudioRecordTest", "preparing....");
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AudioRecordTest", "prepare() failed");
        }
        Toast.makeText(RecordActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
    }

    private void convertFile() {
        Log.e("MEDIAFILE BEF",  pathSave);
        File aacFile = new File(pathSave);
        Log.e("MEDIAFILE CONV", "CONVERTING");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                //Toast.makeText(MainActivity.this, "STATUS: " + apneaDetector.getStatus(pathRaw), Toast.LENGTH_LONG).show();
                // So fast? Love it!
                Log.e("CONV", "PATH : " + convertedFile.getAbsolutePath());
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
                .setFormat(cafe.adriel.androidaudioconverter.model.AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

    }

    private void openResults(){
        Intent intentResults = new Intent(RecordActivity.this, ResultsActivity.class);
        //intentResults.putExtra("message", msg);
        //startActivity(intentActivity2);
        startActivityForResult(intentResults, REQUEST_CODE_RESULTS);
    }



}

