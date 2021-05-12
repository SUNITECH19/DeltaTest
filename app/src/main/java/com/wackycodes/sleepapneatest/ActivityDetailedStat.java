package com.wackycodes.sleepapneatest;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.wackycodes.sleepapneatest.helper.ApneaDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ActivityDetailedStat extends AppCompatActivity {
    String name;
    String path;
    TextView nameview;
    TextView pathview;
    TextView apneaview;

    // TEST
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_stat);
        Intent receivedFromList = getIntent();
        name = receivedFromList.getStringExtra("recordName");
        path = receivedFromList.getStringExtra("recordPath");
        nameview = findViewById(R.id.NAME);
        pathview = findViewById(R.id.PATH);
        apneaview = findViewById(R.id.APNEA);


        nameview.setText("Record date: " + name);
        pathview.setText("Record path: " + path);
        apneaview.setText("INITIALISING DATA");

//        if (path != null)
//            new MyAsyncTask( path ).execute(); // Use Another thread to Smooth running ...

        if (path != null)
            apneaview.setText(new ApneaDetector().getStatus( path ));

    }


    // public class MyAsyncTask : Run in different Thread
    public class MyAsyncTask extends AsyncTask<Void, Void, String> {

        private String filePath;

        public MyAsyncTask(String path) {
            this.filePath = path;
        }

        @Override
        protected String doInBackground(Void... voids) {
//            return proccessSignal ( getPath( new File(filePath) ) );
            return new ApneaDetector().getStatus( filePath );
//            return proccessSignal ( getMedia( filePath ) );
        }


        @Override
        protected void onPostExecute(String doubles) {
            // Set Text on TextView : Whatever You got response...
            apneaview.setText( doubles );
        }

        // Get Array[] from File...
        private double[] getPath(File audiofile ){
//            JarFileRead k;

            List<Double> doubleList = new ArrayList<>();
            try {

                FileReader input2 = new FileReader( filePath );
                BufferedReader bufferedReader = new BufferedReader( input2 );

//                bufferedReader.readLine().

                FileInputStream fileInputStream = new FileInputStream(audiofile);
                BufferedInputStream buf  = new BufferedInputStream( fileInputStream );

                while (buf.read() > -1 ){
                    double d = buf.read();
                    doubleList.add( d );
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERR", e.getMessage());
            }
            finally {

                double[] arrayDouble = new double[doubleList.size()];
                for (int i = 0; i < doubleList.size(); i++ ){
                    arrayDouble[i] = doubleList.get(i);
                }
//            Log.e("DOUBLE", "Size: " + arrayDouble.length + " " + doubleList.size() );
                return arrayDouble;
            }
        }

        private double[] getMedia(String filePath){

            List<Double> doubleList = new ArrayList<>();

            String[] projection = new String[] {
//                    media-database-columns-to-retrieve
                    filePath
            };
//            String selection = sql-where-clause-with-placeholder-variables;
            String selection = MediaStore.Audio.Media.EXTRA_MAX_BYTES;
            String[] selectionArgs = new String[] {
                    filePath
            };
            String sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

            Cursor cursor = getApplicationContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );

            while (cursor.moveToNext()) {
                // Use an ID column from the projection to get
                // a URI representing the media item itself.
//                double d = cursor.getDouble( cursor.getColumnIndex( MediaStore.Audio.Media.DATA ));
                doubleList.add( cursor.getDouble( cursor.getColumnIndex( MediaStore.Audio.Media.DATA )));
            }

            double[] arrayDouble = new double[doubleList.size()];
            for (int i = 0; i < doubleList.size(); i++ ){
                arrayDouble[i] = doubleList.get(i);
            }
            Log.e("SIZE", "SIE : "+ arrayDouble.length );

            return arrayDouble;
        }


    }


}


/*   % Importing audio
[y,Fs] = audioread('Andrea_normal_1.m4a');

% Audio normalization
ampMax = 0.05;
y = audioNormalization_YW(y,ampMax);

% Changing sampling frequency to seconds
q=length(y)/Fs;
min=floor(q/60);
sek=floor(mod(q,60));
t=linspace(0,q,length(y));

% Lowpass filter
sig = lowpass(y,150,Fs);

% Bandpass filter
signal = bandpass(sig,[200 500],Fs);

% Amplified signal
x = signal*1000;

% Square wave to get only the low frequencies
x_2 = x.^2;

%Envelope
x_3 = movmean(x_2,Fs/2);


% Segmentation
frame_duration = 1;
overlap_duration = 0.25;
frame_len = frame_duration*Fs;
overlap_len = overlap_duration*Fs;
buffered_signal = buffer(x_3, frame_len, overlap_len);

si = size(buffered_signal);

% define zeros matrix with same size as buffered_signal
classify_signal = zeros(si(2),1);

% set threshold which classifies the segment into breathing or not
% breathing. Data saved into a new matrix with 1 for breathing and 0 for
% non breathing
for j = 1:si(2)
    for k = 1:si(1)
        %
        if buffered_signal(k,j) > 0.0052
            classify_signal(j,:) = 1;
            k = 44100;
        end
    end
end

% Find apnea event by finding where we have XX non breathing (0) in
% row. apnea gives the number of apnea events detected from each signal.
count = 0;
apnea = 0;
for j = 1:si(2)
    if classify_signal(j,1) == 0
        count = count + 1;
        if count == 11 %12 %14
            apnea = apnea + 1;
            count = 0;
        end
    else
        count = 0;
    end
end*/