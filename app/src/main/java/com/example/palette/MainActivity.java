package com.example.palette;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        constraintLayout = findViewById(R.id.rootlayout);
        constraintLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        imageView = findViewById(R.id.img);
        button = findViewById(R.id.btn);
        ViewCompat.setOnApplyWindowInsetsListener(button, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                layoutParams.topMargin = insets.getSystemWindowInsetTop();
                return insets;
            }
        });
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        imageView.setImageBitmap(bitmap);
        Palette.from(bitmap).maximumColorCount(5).setRegion(0,0,getScreenWidth(),getStatusBarHeight()).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                List<Palette.Swatch> swatches = palette.getSwatches();
                Palette.Swatch mostSwatch = null;
                for(Palette.Swatch swatch:swatches){
                    if(mostSwatch==null || swatch.getPopulation()>mostSwatch.getPopulation()){
                        mostSwatch = swatch;
                    }
                }
                double luminance = ColorUtils.calculateLuminance(mostSwatch.getRgb());
                if(luminance<0.5){
                    setDark();
                }else {
                    setLight();
                }
            }
        });
    }
    private void setLight(){
        View decorView = getWindow().getDecorView();
        int uiVisibility = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(uiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private void setDark(){
        View decorView = getWindow().getDecorView();
        int i = decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(i^View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private int getScreenWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    private int getStatusBarHeight(){
        int result = 0;
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(identifier>0){
            result = getResources().getDimensionPixelSize(identifier);
        }
        return result;
    }
}