package com.aliouswang.practice.olympic.bean;

import com.aliouswang.practice.olympic.util.L;

public class TestClassLoader {

    public static int staticValue = 100;

    static {
        L.d("TestClassLoader class init!");
    }

}
