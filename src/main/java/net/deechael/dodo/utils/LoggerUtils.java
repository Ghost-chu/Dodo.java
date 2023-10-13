package net.deechael.dodo.utils;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerUtils {

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz, Level.INFO);
    }

    public static Logger getLogger(Class<?> clazz, Level level) {
        return LoggerFactory.getLogger("dodo.java:"+clazz.getSimpleName());
    }

    private LoggerUtils() {
    }

}
