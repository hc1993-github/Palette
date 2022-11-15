package com.example.palette.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.ProgressAdapter;
import com.example.palette.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

import static android.util.Base64.NO_WRAP;

public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        String[] keyPair = SecurityUtil.generateRSAKeyPair();
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<100;i++){
            builder.append("WORLD");
        }
        String encryptString = SecurityUtil.encryptStringRSAPublic(keyPair[0], builder.toString(),NO_WRAP);
        Log.d("ThirdActivity", "encrypt "+encryptString);
        String decryptString = SecurityUtil.decryptStringRSAPrivate(keyPair[1], encryptString,NO_WRAP);
        Log.d("ThirdActivity", "decrypt "+decryptString);
    }
}