package com.intbird.soft.socketiolib;

import com.intbird.soft.socketiolib.components.SocketIOMessageBody;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * aspect your own method,such as Logger, LifeCycle...
 * configs in app 'build.gradle'
 */
@Aspect
public class SocketIOAspect {

    @After("execution(* android.app.Activity.on**(..))")
    public void onSocketIOAspectLogger(JoinPoint joinPoint) {
        Object[] objects = joinPoint.getArgs();
        StringBuilder result = new StringBuilder();
        if (null != objects) {
            for (Object object : objects) {
                if (null != object) {
                    result.append(object.toString()).append("\n");
                }
            }
        }
        SocketIOSender.sendMessage(new SocketIOMessageBody("aspect", result.toString()));
    }
}