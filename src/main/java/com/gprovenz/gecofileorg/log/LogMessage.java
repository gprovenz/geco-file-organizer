package com.gprovenz.gecofileorg.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LogMessage {
    private static final Logger logger = LoggerFactory.getLogger(LogMessage.class);
    public static final String FROM_TO = "'{}' -> '{}'";

    public static void logMoved(File source, File dest) {
        logger.info("[MOVED] " + FROM_TO, source.getAbsolutePath(), dest.getAbsolutePath());
    }

    public static void logCopied(File source, File dest) {
        logger.info("[COPIED] " + FROM_TO, source.getAbsolutePath(), dest.getAbsolutePath());
    }

    public static void logIgnoredDuplicate(File source, File dest) {
        logger.info("[IGNORED DUPLICATE] '{}' (already existing file: '{}')", source.getAbsolutePath(), dest.getAbsolutePath());
    }

    public static void logRemovedDuplicate(File source, File dest) {
        logger.info("[REMOVED DUPLICATE] '{}' (already existing file: '{}')", source.getAbsolutePath(), dest.getAbsolutePath());
    }

    public static void logDifferentContent(File source, File dest) {
        logger.warn("[DIFFERENCES FOUND] " + FROM_TO, source.getAbsolutePath(), dest.getAbsolutePath());
    }

}
