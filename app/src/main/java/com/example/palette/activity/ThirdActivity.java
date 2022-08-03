package com.example.palette.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.ProgressAdapter;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {
    List<String> data;
    ProgressAdapter adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Log.d("huachen", "onCreate: ");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        data = new ArrayList<>();
        adapter1 = new ProgressAdapter(this,data);
        recyclerView.setAdapter(adapter1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        data.clear();
        data.add("http://guolin.tech/book.png");
        data.add("http://guolin.tech/test.gif");
        adapter1.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("huachen", "onPause: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("huachen", "onSaveInstanceState:");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("huachen", "onDestroy: ");
    }
}