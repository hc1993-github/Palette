package com.example.palette.async;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AppStartTaskUtil {

    /**
     * 是否在主进程
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context){
        return context.getPackageName().equals(getProcessName(context));
    }

    /**
     * 获取当前线程名
     * @param context
     * @return
     */
    private static String getProcessName(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        int myPid = Process.myPid();
        if(runningAppProcesses==null || runningAppProcesses.isEmpty()){
            return null;
        }
        for(ActivityManager.RunningAppProcessInfo info:runningAppProcesses){
            if(info.processName.equals(context.getPackageName())){
                if(info.pid == myPid){
                    return info.processName;
                }
            }
        }
        return null;
    }

    /**
     * 任务排序
     * @param taskList
     * @param taskMap
     * @param taskChildMap
     * @return
     */
    public static ArrayList<AppStartTask> sortAppStartTask(ArrayList<AppStartTask> taskList, HashMap<Class<? extends AppStartTask>,AppStartTask> taskMap,HashMap<Class<? extends AppStartTask>,HashSet<Class<? extends AppStartTask>>> taskChildMap){
        ArrayList<AppStartTask> sortTaskList = new ArrayList<>();
        HashMap<Class<? extends AppStartTask>,Integer> taskIntMap = new HashMap<>();
        Deque<Class<? extends AppStartTask>> deque = new ArrayDeque<>();
        for(AppStartTask task:taskList){
            if(!taskIntMap.containsKey(task.getClass())){
                taskMap.put(task.getClass(),task);
                taskIntMap.put(task.getClass(),task.getParentTask()==null?0:task.getParentTask().size());
                if(taskIntMap.get(task.getClass())==0){
                    deque.offer(task.getClass());
                }
            }else {
                throw new RuntimeException("the task "+task.getClass().getSimpleName()+" is already exist");
            }
            List<Class<? extends AppStartTask>> list = task.getParentTask();
            if(list!=null){
                for(Class<? extends AppStartTask> clz:list){
                    HashSet<Class<? extends AppStartTask>> orDefault = taskChildMap.getOrDefault(clz, new HashSet<>());
                    orDefault.add(task.getClass());
                    taskChildMap.put(clz,orDefault);
                }
            }
        }
        while (!deque.isEmpty()) {
            Class<? extends AppStartTask> clz = deque.poll();
            AppStartTask task = taskMap.get(clz);
            sortTaskList.add(task);
            HashSet<Class<? extends AppStartTask>> set = taskChildMap.get(clz);
            if(set!=null){
                for(Class<? extends AppStartTask> cls:set){
                    taskIntMap.put(cls,taskIntMap.getOrDefault(cls,0)-1);
                    if(taskIntMap.get(cls)==0){
                        deque.offer(cls);
                    }
                }
            }
        }
        if(sortTaskList.size()!=taskList.size()){
            throw new RuntimeException("something error happened in sort");
        }
        return sortTaskList;
    }
}
