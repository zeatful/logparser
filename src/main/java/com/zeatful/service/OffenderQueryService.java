package com.zeatful.service;

import com.zeatful.domain.Offender;
import com.zeatful.mapper.OffenderRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates and executes the query for finding offending ip addresses
 */
@Service
public class OffenderQueryService {
    private DataSource dataSource;

    @Value("${startDate}")
    private String startDateString;

    @Value("${threshold}")
    private String threshold;

    @Value("${duration}")
    private String duration;

    private static final String DAILY = "daily";

    private DateTimeFormatter commandFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
    private DateTimeFormatter mysqlFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Logger logger = LoggerFactory.getLogger(OffenderQueryService.class);

    @Inject
    public OffenderQueryService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // execute query and process found offenders
    public JdbcCursorItemReader<Offender> offenderItemReader() {
        // Setup item reader
        JdbcCursorItemReader<Offender> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql(generateQueryString());
        itemReader.setRowMapper(new OffenderRowMapper(this.duration, this.threshold));
        return itemReader;
    }

    private String generateQueryString() {
        LocalDateTime start = LocalDateTime.parse(startDateString, commandFormat);
        String query = "SELECT ip_address, COUNT(*) as count FROM (SELECT ip_address FROM server_logs WHERE entry_time BETWEEN '" +
                start.format(mysqlFormat) +
                "' and '" +
                generateEndDuration(start) + "')result GROUP BY ip_address HAVING count > " + threshold + " ORDER BY count DESC";
        logger.info(query);
        return query;
    }

    // Generate end duration as a string based on the durations
    private String generateEndDuration(LocalDateTime start) {
        LocalDateTime end = (duration.equals(DAILY) ? start.plusDays(1) : start.plusHours(1));
        return end.format(mysqlFormat);
    }
}