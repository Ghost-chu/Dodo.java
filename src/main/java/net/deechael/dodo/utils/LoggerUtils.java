package net.deechael.dodo.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
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
