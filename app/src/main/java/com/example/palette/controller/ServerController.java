package com.example.palette.controller;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import com.example.palette.util.FileUtil;
import com.example.palette.util.SPUtil;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

@RestController
public class ServerController {
    @GetMapping("/")
    public String ping() {
        return "SERVER OK";
    }

    @PostMapping("/user/login")
    public JSONObject login(@RequestBody String str) throws Exception {
        JSONObject jsonObject = new JSONObject(str);
        return jsonObject;
    }

    @GetMapping("/user/item")
    public JSONObject requestItem(@RequestParam(value = "name",required = false) String name,
                                  @RequestParam(value = "id",required = false) String id) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", SPUtil.getString("name",""));
        jsonObject.put("id", SPUtil.getString("id",""));
        return jsonObject;
    }

    @GetMapping("/user/{userId}")
    public JSONObject getUser(@PathVariable("userId") String userId,
                              @QueryParam("key") String key) throws Exception{
        JSONObject user = new JSONObject();
        user.put("id", userId);
        user.put("key", key);
        user.put("year", 2022);

        return user;
    }

//    @PostMapping("/test1")
//    public String test1(@RequestBody String string){
//        Bitmap bitmap = stringToBitmap(string);
//        String result = "success";
//        return result;
//    }

    @PostMapping("/upload")
    public String test2(@RequestParam(name = "filename") MultipartFile filename) {
        try {
            filename.transferTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+filename.getFilename()));
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
    }

    @GetMapping("/download")
    public ResponseBody test3(HttpResponse response, @RequestParam(name = "filename") String filename) {
        try {
            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+filename);
            FileBody body=new FileBody(file);
            response.setHeader("Content-Disposition", "attachment;filename="+filename);
            return body;
        }catch (Exception e){
            e.printStackTrace();
            return new StringBody("文件不存在");
        }
    }

    private Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String file2String(File file, String encoding) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            //将输入流写入输出流
            char[] buffer = new char[2048];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        //返回转换结果
        if (writer != null)
            return writer.toString();
        else return null;
    }
}
