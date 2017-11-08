package com.zeatful;

import com.zeatful.domain.Offender;
import com.zeatful.domain.ServerEntry;
import com.zeatful.mapper.ServerEntryFieldSetMapper;
import com.zeatful.process.OffenderProcessor;
import com.zeatful.service.OffenderQueryService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Value("${accesslog}")
    private String accessLogLocation;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final OffenderQueryService offenderQueryService;
    private final DataSource dataSource;

    @Inject
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, OffenderQueryService offenderQueryService, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.offenderQueryService = offenderQueryService;
        this.dataSource = dataSource;
    }

    // load and read the access log file
    private FlatFileItemReader<ServerEntry> reader() {
        FlatFileItemReader<ServerEntry> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(accessLogLocation));
        //reader.setResource(new ClassPathResource("access.log"));
        reader.setLineMapper(new DefaultLineMapper<ServerEntry>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                // override the default delimiter since this is not a CSV
                setDelimiter("|");
                setNames(new String[]{"time", "ipAddress", "request", "requestStatus", "userAgent"});
            }});
            // provide a custom mapper to handle dates
            setFieldSetMapper(new ServerEntryFieldSetMapper());
        }});
        return reader;
    }

    // Writer to load access log file entries into server_logs table
    @Bean
    public JdbcBatchItemWriter<ServerEntry> serverEntryWriter() {
        JdbcBatchItemWriter<ServerEntry> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO server_logs (entry_time, ip_address, request, request_status, " + "user_agent) VALUES (:time, :ipAddress, :request, :requestStatus, :userAgent)");
        writer.setDataSource(dataSource);
        return writer;
    }

    // Writer to load query results into offenders table
    @Bean
    public JdbcBatchItemWriter<Offender> offenderWriter() {
        JdbcBatchItemWriter<Offender> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO offenders (ip_address, request_count, reason) VALUES (:ipAddress, :count, :reason)");
        writer.setDataSource(dataSource);
        return writer;
    }

    // job to orchestrate reading log file, loading data into database, query for offenders, then load them into a new table
    @Bean
    public Job importServerEntries() {
        return jobBuilderFactory.get("importServerEntries")
                .incrementer(new RunIdIncrementer())
                .start(processServerEntries())
                .next(processOffenders())
                .build();
    }

    // step to parse log file and write results to the database
    @Bean
    public Step processServerEntries() {
        return stepBuilderFactory.get("processServerEntries")
                .<ServerEntry, ServerEntry>chunk(10)
                .reader(reader())
                .writer(serverEntryWriter())
                .build();
    }

    // step to query server entries for offenders, write ip address of results to commandline, then write them to the offenders table
    @Bean
    public Step processOffenders() {
        return stepBuilderFactory.get("readerOffenders")
                // NOTE: this step is not ideal for spring batch, so arbitrarily setting a chunk size so all read results are available
                .<Offender, Offender>chunk(10000)
                .reader(offenderQueryService.offenderItemReader())
                .processor(new OffenderProcessor())
                .writer(offenderWriter())
                .build();
    }
}