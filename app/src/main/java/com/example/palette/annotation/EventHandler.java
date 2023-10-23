package com.example.palette.annotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EventHandler implements InvocationHandler {
    private Object object;
    private Method method;

    public EventHandler(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        return method.invoke(object);
    }
}
