package com.example.palette.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palette.R;

/**
 * js互调样例
 */
public class FiveActivity extends AppCompatActivity {
    int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);
        WebView webView = findViewById(R.id.wv);
        TextView textView = findViewById(R.id.tv);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidForWebInterface(this),"Android");
        webView.loadUrl("file:///android_asset/webView.html");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    progress = 100;
                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progress==100){
                    webView.loadUrl("javascript:test('"+123456+"')");
                }else {
                    return;
                }
            }
        });
    }
    private class AndroidForWebInterface{
        Context context;

        public AndroidForWebInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void showToast(String str){
            Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showTest(String str){
            Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        }
    }
}