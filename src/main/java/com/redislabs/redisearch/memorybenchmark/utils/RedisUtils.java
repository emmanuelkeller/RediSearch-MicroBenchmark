package com.redislabs.redisearch.memorybenchmark.utils;

import java.util.HashMap;
import java.util.Map;

public class RedisUtils {

    public static Map<String, String> parseInfo(String info) {
        final Map<String, String> map = new HashMap<>();
        for (String line : info.split("\\r?\\n")) {
            final int sep = line.indexOf(':');
            if (sep < 0) continue;
            map.put(line.substring(0, sep), line.substring(sep + 1));
        }
        return map;
    }
}
