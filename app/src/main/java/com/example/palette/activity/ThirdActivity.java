package com.example.palette.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palette.R;
import com.example.palette.adapter.ProgressAdapter;
import com.example.palette.util.SecurityUtil;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import static android.util.Base64.NO_WRAP;

/**
 * soapService调用样例
 */
public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<100;i++){
            builder.append("WORLD");
        }
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        Log.d("ThirdActivity", "number "+getNumberInfo("13812815447"));
                    }
                }.start();
            }
        });
    }

    private String getNumberInfo(String phoneNumber){
        String url = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";
        String namespace = "http://WebXml.com.cn/";
        String methodName = "getMobileCodeInfo";
        String action = "http://WebXml.com.cn/getMobileCodeInfo";
        SoapObject soapObject = new SoapObject(namespace,methodName);
        soapObject.addProperty("mobileCode",phoneNumber);
        soapObject.addProperty("userID","");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE httpTransportSE = new HttpTransportSE(url);
        try {
            httpTransportSE.call(action,envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SoapObject result = (SoapObject) envelope.bodyIn;
        return result.getProperty(0).toString();
    }
}