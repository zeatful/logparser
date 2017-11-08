package com.zeatful.process;

import com.zeatful.domain.Offender;
import org.springframework.batch.item.ItemProcessor;

/**
 * Custom processor to write the offending IP address to the commandline
 */
public class OffenderProcessor implements ItemProcessor<Offender, Offender> {
    // Used to write each offender IP address to commandline
    @Override
    public Offender process(Offender offender) throws Exception {
        System.out.println(offender.getIpAddress());
        return offender;
    }
}
