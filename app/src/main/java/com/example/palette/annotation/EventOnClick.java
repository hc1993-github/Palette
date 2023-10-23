package com.example.palette.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventType(methodName = "setOnClickListener",className = View.OnClickListener.class,hasReturn = false)
public @interface EventOnClick {
    int[] value() default -1;
}
