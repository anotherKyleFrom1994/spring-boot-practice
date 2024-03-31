package org.anotherkyle.commonlib.log;

import org.apache.logging.log4j.Level;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

public enum LogLevel {
    INFO(Level.INFO),
    DEBUG(Level.DEBUG),
    TRACE(Level.TRACE),
    WARN(Level.WARN),
    ERROR(Level.ERROR),
    CRITICAL(Level.ERROR, new BasicMarkerFactory().getMarker("CRITICAL"));

    private final Level level;
    private Marker marker;

    LogLevel(Level level) {
        this.level = level;
    }

    LogLevel(Level level, Marker marker) {
        this.level = level;
        this.marker = marker;
    }
}
