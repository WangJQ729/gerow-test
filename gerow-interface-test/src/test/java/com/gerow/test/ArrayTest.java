package com.gerow.test;

import org.testng.annotations.Test;

import java.util.Arrays;

public class ArrayTest {
    @Test
    public void test() {
        int[] nums1 = {3, 3, 4, 4, 5, 6, 0, 0, 0};
        int m = 6;
        int[] nums2 = {2, 4, 6};
        int n = 3;
        merge(nums1, m, nums2, n);
        System.out.println(Arrays.toString(nums1));

        nums1 = new int[]{0, 0, 0};
        m = 0;
        nums2 = new int[]{1, 2, 3};
        merge(nums1, m, nums2, n);
        System.out.println(Arrays.toString(nums1));

        nums1 = new int[]{};
        nums2 = new int[]{};
        n = 0;
        merge(nums1, m, nums2, n);
        System.out.println(Arrays.toString(nums1));

        nums1 = new int[]{1, 2, 3};
        m = 3;
        nums2 = new int[]{};
        merge(nums1, m, nums2, n);
        System.out.println(Arrays.toString(nums1));
    }


    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1;
        int j = n - 1;
        for (int a = m + n - 1; a >= 0; a--) {
            if (j < 0 || (i >= 0 && nums1[i] > nums2[j])) {
                nums1[a] = nums1[i];
                i--;
            } else {
                nums1[a] = nums2[j];
                j--;
            }
        }
    }
}
