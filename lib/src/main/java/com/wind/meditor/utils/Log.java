package com.wind.meditor.utils;

/**
 * @author Windysha
 */
public class Log {

    private static final boolean DEBUG = false;

    public static void i(String msg) {
        System.out.println(msg);
    }

    public static void e(String msg) {
        System.err.println(msg);
    }

    public static void d(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
}
