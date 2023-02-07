package com.example.palette.controller;


import com.example.palette.util.SPUtil;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;

import org.json.JSONObject;

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
}
