package com.zeatful.domain;

import lombok.Data;
import lombok.ToString;

/**
 * Represents an offending ip address which exceeds the daily or hourly threshold
 */
@Data
@ToString
public class Offender {
    // count of requests that exceeded a threshold for a given duration
    private int count;

    // ip address of the offender
    private String ipAddress;

    // duration that limit was exceeded
    private String duration;

    // threshold of requests that was exceeded
    private String threshold;

    // string representing why an ip has been blocked
    private String reason;

    public String getReason() {
        return "Blocked for exceeding " + duration + " limit of " + threshold + " requests";
    }

    public void setReason() {}
}
