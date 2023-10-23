package com.example.palette.annotation;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class EventInjectUtil {
    private static final String TAG = "EventInjectUtil";

    public static void activityInjectEvent(Activity activity) {
        if (activity != null) {
            realInject(activity, null, null, 1);
        }
    }

    public static void dialogInjectEvent(Dialog dialog) {
        if (dialog != null) {
            realInject(dialog, null, null, 1);
        }
    }

    public static void fragmentInjectEvent(Fragment fragment, View fragmentLayoutView) {
        if (fragment != null && fragmentLayoutView != null) {
            realInject(null, fragment, fragmentLayoutView, 2);
        }
    }

    private static void realInject(Object object, Fragment fragment, View fragmentView, int type) {
        Method[] declaredMethods = null;
        if (type == 1) {
            declaredMethods = object.getClass().getDeclaredMethods();
        } else if (type == 2) {
            declaredMethods = fragment.getClass().getDeclaredMethods();
        }
        for (Method method : declaredMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> annotationType = annotation.annotationType();
                EventType eventType = annotationType.getAnnotation(EventType.class);
                if (eventType == null) {
                    continue;
                }
                String methodName = eventType.methodName();
                Class<?> className = eventType.className();
                boolean hasReturn = eventType.hasReturn();
                Class<?> returnType = method.getReturnType();
                if (hasReturn) {
                    if (returnType != boolean.class) {
                        throw new NullPointerException(TAG + " realInject Error: you have not set returnType for the " + method.getName());
                    }
                } else {
                    if (returnType != void.class) {
                        throw new NullPointerException(TAG + " realInject Error: you can not set returnType for the " + method.getName());
                    }
                }
                try {
                    Method value = annotationType.getDeclaredMethod("value");
                    int[] ids = (int[]) value.invoke(annotation);
                    if (type == 1) {
                        for (int id : ids) {
                            Method findViewById = object.getClass().getMethod("findViewById", int.class);
                            View view = (View) findViewById.invoke(object, id);
                            if (view == null || view instanceof RecyclerView) {
                                Log.e(TAG, "realInject Error: view may be is null or RecyclerView, that cause " + methodName + " not effective");
                                continue;
                            }
                            Object onClickListener = Proxy.newProxyInstance(object.getClass().getClassLoader(), new Class[]{className}, new EventHandler(object, method));
                            Method setOnClickListener = view.getClass().getMethod(methodName, className);
                            setOnClickListener.invoke(view, onClickListener);
                        }
                    } else if (type == 2) {
                        for (int id : ids) {
                            View view = fragmentView.findViewById(id);
                            if (view == null || view instanceof RecyclerView) {
                                Log.e(TAG, "realInject Error: view may be is null or RecyclerView, that cause " + methodName + " not effective");
                                continue;
                            }
                            Object onClickListener = Proxy.newProxyInstance(fragment.getClass().getClassLoader(), new Class[]{className}, new EventHandler(fragment, method));
                            Method setOnClickListener = view.getClass().getMethod(methodName, className);
                            setOnClickListener.invoke(view, onClickListener);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
