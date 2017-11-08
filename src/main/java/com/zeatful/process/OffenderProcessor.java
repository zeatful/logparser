package com.zeatful.process;

import com.zeatful.domain.Offender;
import org.springframework.batch.item.ItemProcessor;

public class OffenderProcessor implements ItemProcessor<Offender, Offender> {
    // Used to write each offender IP address to commandline
    @Override
    public Offender process(Offender offender) throws Exception {
        System.out.println(offender.getIpAddress());
        return offender;
    }
}
