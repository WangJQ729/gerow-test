package com.gerow.test;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Random;

public class RandomDoubleColorBallTest {
    @Test(invocationCount = 100)
    public void test() {
        // 生成红球号码
        int[] redBalls = generateRedBalls();
        System.out.print("红球号码为：" + Arrays.toString(redBalls) + "   ");

        // 生成蓝球号码
        int blueBall = generateBlueBall();
        System.out.println("蓝球号码为：" + blueBall);
    }

    /**
     * 生成指定数量的红球号码，每个号码在1-33之间随机选择。
     *
     * @return 红球号码数组
     */
    private static int[] generateRedBalls() {
        int[] redBalls = new int[6];
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int result = random.nextInt(33) + 1; // 在1-33之间随机选择红球号码
            while (ArrayUtils.contains(redBalls, result)) {
                result = random.nextInt(33) + 1;
            }
            redBalls[i] = result;
        }
        Arrays.sort(redBalls);
        return redBalls;
    }

    /**
     * 生成一个1-16之间的随机整数作为蓝球号码。
     *
     * @return 蓝球号码
     */
    private static int generateBlueBall() {
        Random random = new Random();
        return random.nextInt(16) + 1; // 在1-16之间随机选择蓝球号码
    }
}
