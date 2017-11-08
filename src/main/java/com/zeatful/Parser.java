package com.zeatful;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Parser {

    // should stand up the spring batch application and load in data
    public static void main(String[] args) throws Exception {
        // Set default timezone to UTC to avoid parsing issues with dates
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication app = new SpringApplication(Parser.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}