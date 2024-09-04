package com.zyp.proxy.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static Logger logger = LoggerFactory.getLogger(Log.class);
    public static void main(String[] args) {
        logger.trace("trace message");
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
    }

    public static void trace(String msg) {
        logger.trace(msg);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }
}
