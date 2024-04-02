package com.gerow.test;

import org.testng.annotations.Test;

import java.util.Arrays;

public class MyTest {

    @Test
    public void test() {
        int[] a = {49, 38, 65, 97, 76, 13, 27};
        int temp;
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
        for (int j : a) {
            System.out.println(j);
        }
    }
}
