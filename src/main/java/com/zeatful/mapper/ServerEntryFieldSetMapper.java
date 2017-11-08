package com.zeatful.mapper;

import com.zeatful.domain.ServerEntry;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.sql.Timestamp;

/**
 * Custom FieldSet Mapper allows parsing ServerEntry time as as TimeStamp and RequestStatus as an Int
 */
public class ServerEntryFieldSetMapper implements FieldSetMapper<ServerEntry> {

    @Override
    public ServerEntry mapFieldSet(FieldSet fieldSet) throws BindException {
        /* Example server access log entry
            2017-01-01 00:00:23.003|192.168.169.194|"GET / HTTP/1.1"|200
            |"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393"
         */
        ServerEntry serverEntry = new ServerEntry();
        serverEntry.setTime(Timestamp.valueOf(fieldSet.readRawString(0)).toLocalDateTime());
        serverEntry.setIpAddress(fieldSet.readRawString(1));
        serverEntry.setRequest(fieldSet.readRawString(2));
        serverEntry.setRequestStatus(fieldSet.readInt(3));
        serverEntry.setUserAgent(fieldSet.readRawString(4));
        return serverEntry;
    }
}
