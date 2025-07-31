package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TrafficRecord {
    private final LocalDateTime timestamp;
    private final int count;

    public TrafficRecord(LocalDateTime timestamp, int count) {
        this.timestamp = timestamp;
        this.count = count;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getCount() {
        return count;
    }

    public String getDate() {
        return timestamp.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public String toString() {
        return timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " " + count;
    }
}
