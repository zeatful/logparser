package com.zeatful.domain;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Represents a line from the parsed log file that will be added to the ServerEntry table
 */
@Data // auto generate getter/setter methods
@ToString // auto generate a toString method
public class ServerEntry {

    // time the request was received by the server
    private LocalDateTime time;

    // ip address where the request originated
    private String ipAddress;

    // actual request made
    private String request;

    // the resulting request status (200, 201, 404, 500, etc...)
    private int requestStatus;

    // the user agent from which the request originated
    private String userAgent;
}
