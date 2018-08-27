package com.aliouswang.practice.olympic;

import com.aliouswang.practice.olympic.bean.Apple;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) {
	// write your code here

        Class clazz = Apple.class;
        Class<?>[] interfaces = clazz.getDeclaredClasses();
        if (interfaces != null) {

        }

        clazz.getConstructors();

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors != null) {

        }

        Method[] methods = clazz.getDeclaredMethods();
//        clazz.getMethod()
        if (methods != null) {

        }

        try {
            Method sealMethod = clazz.getDeclaredMethod("seal", float.class);
            if (sealMethod != null) {

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        clazz.getFields();
        clazz.getDeclaredFields();
    }
}
