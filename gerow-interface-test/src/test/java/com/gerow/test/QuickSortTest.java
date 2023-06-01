package com.gerow.test;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Random;

public class QuickSortTest {

    @Test
    public void testQuickSort() {
        Random random = new Random();
        int[] arr = new int[100];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(1000);
        }
        quickSort(arr, 0, arr.length-1);
        System.out.println(Arrays.toString(arr));
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high); // 获取枢轴元素的索引
            quickSort(arr, low, pivotIndex - 1); // 对枢轴左侧的子数组进行快速排序
            quickSort(arr, pivotIndex + 1, high); // 对枢轴右侧的子数组进行快速排序
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // 以最后一个元素为枢轴元素
        int i = low - 1; // i指向小于枢轴元素的最近位置
        for (int j = low; j <= high - 1; j++) {
            if (arr[j] < pivot) { // 如果当前元素小于枢轴元素，则交换i和j位置上的元素
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high); // 将枢轴元素移到正确的位置上
        return i + 1; // 返回枢轴元素的索引
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }


    public static void halfInsertionSort(int[] arr, int low, int high) {
        if (low < high) {
            int mid = low + (high - low) / 2; // 获取中间元素的索引
            halfInsertionSort(arr, low, mid); // 对左侧子数组进行排序
            halfInsertionSort(arr, mid + 1, high); // 对右侧子数组进行排序
            insertionSort(arr, low, mid, high); // 合并两个已排序的子数组
        }
    }

    private static void insertionSort(int[] arr, int low, int mid, int high) {
        for (int i = low + 1; i <= high; i++) {
            int j = i;
            int temp = arr[i];
            while (j > mid && arr[j - 1] > temp) {
                arr[j] = arr[j - 1];
                j--;
            }
            arr[j] = temp;
        }
    }


}
