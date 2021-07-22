package com.YTrollman.CableTiers.util;

public class MathUtil {

    public static int ceilDiv(int a, int b) {
        return a % b == 0 ? a / b : a / b + 1;
    }
}
