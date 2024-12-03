package com.akichou.cache.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class LockManagerUtil {

    private static final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>() ;

    public static Object getLock(String targetId) {

        return lockMap.computeIfAbsent(targetId, k -> new WeakReference<>(new Object())) ;
    }
}