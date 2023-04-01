package org.ical4j.integration;

import org.ical4j.integration.command.*;
import picocli.CommandLine;

@CommandLine.Command(name = "integration", description = "iCal4j Integration",
        subcommands = {ReceiveCalendarCommand.class, ReceiveVCardCommand.class, SendCalendarCommand.class,
                SendVCardCommand.class},
        mixinStandardHelpOptions = true, versionProvider = VersionProvider.class)
public class IntegrationMain {

    public static void main(String[] args) {
        new CommandLine(new IntegrationMain()).execute(args);
    }
}
