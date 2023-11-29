package net.deechael.dodo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
    private static Logger logger = LoggerFactory.getLogger("DoDoSDK");

    public static Logger getLogger(String name){
        return logger;
    }
    public static Logger getLogger(Class<?> name){
        return logger;
    }

    public static void setLogger(Logger logger) {
        LoggerUtils.logger = logger;
    }
}
