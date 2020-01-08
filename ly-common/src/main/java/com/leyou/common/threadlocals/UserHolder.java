package com.leyou.common.threadlocals;

/**
 * 线程安全的userId
 * @author changkunhui
 * @date 2020/1/8 16:10
 */
public class UserHolder {

    private static final ThreadLocal<String> TL = new ThreadLocal<>();

    public static String getUserId() {
        return TL.get();
    }

    public static void setUserId(String userId) {
        TL.set(userId);
    }

    public static void removeUserId() {
        TL.remove();
    }

}
