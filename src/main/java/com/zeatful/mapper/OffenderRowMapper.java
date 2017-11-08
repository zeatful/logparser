package com.zeatful.mapper;

import com.zeatful.domain.Offender;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allows the results of the query for offenders to be mapped to a domain object
 */
public class OffenderRowMapper implements RowMapper<Offender> {
    private static final String COUNT = "count";
    private static final String IP_ADDRESS = "ip_address";

    private String duration;
    private String threshold;

    public OffenderRowMapper(String duration, String threshold) {
        this.duration = duration;
        this.threshold = threshold;
    }

    @Override
    public Offender mapRow(ResultSet rs, int rowNum) throws SQLException {
        Offender offender = new Offender();
        offender.setCount(rs.getInt(COUNT));
        offender.setIpAddress(rs.getString(IP_ADDRESS));
        offender.setDuration(this.duration);
        offender.setThreshold(this.threshold);
        return offender;
    }
}
