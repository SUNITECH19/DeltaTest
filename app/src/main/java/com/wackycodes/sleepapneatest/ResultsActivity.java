package com.wackycodes.sleepapneatest;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wackycodes.sleepapneatest.adaptor.ResultListAdaptor;
import com.wackycodes.sleepapneatest.model.ResultItem;

import java.io.File;
import java.util.ArrayList;

import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;

public class ResultsActivity extends AppCompatActivity {
    private static ResultListAdaptor Adapter;
    private RecyclerView recyclerView;
    public ArrayList<ResultItem> items = new ArrayList<>();
    //    public AudioManager audioManager;
    public MediaMetadataRetriever metadataRetriever;
    private File[] allFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        metadataRetriever = new MediaMetadataRetriever();
        Populatelist();
        setupRecyclerview();
        Adapter.setOnItemClickListener(new ResultListAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO : UPDATE
                Intent intent = new Intent(ResultsActivity.this, ActivityDetailedStat.class);
                intent.putExtra("recordName",allFiles[position].getName());
                intent.putExtra("recordPath",allFiles[position].getAbsolutePath());
                startActivity(intent);
            }
        });
    }

    private void Populatelist() {
        String path = getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();

        for (File file : allFiles) {
            metadataRetriever.setDataSource(file.getAbsolutePath());
            String duration = metadataRetriever.extractMetadata(METADATA_KEY_DURATION);
            if(duration != null){
                ResultItem item2 = new ResultItem(Integer.parseInt(duration)/1000,file.getName());
                items.add(item2);
            }
        }

    }

    private void setupRecyclerview() {
        //Setup recyclerview and attach layout and adapter + EXIT button
        recyclerView = findViewById(R.id.RecyclerResultView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerListActivity = new LinearLayoutManager(this);
        Adapter = new ResultListAdaptor(this,items);
        recyclerView.setLayoutManager(layoutManagerListActivity);
        recyclerView.setAdapter(Adapter);
        Adapter.notifyDataSetChanged();
    }



}
