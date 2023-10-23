package com.example.palette.annotation;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventType(methodName = "setOnLongClickListener",className = View.OnLongClickListener.class,hasReturn = true)
public @interface EventOnLongClick {
    int[] value() default -1;
}
