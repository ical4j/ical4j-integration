package org.ical4j.integration.mail;

import org.ical4j.integration.command.SendCalendarCommand;
import org.ical4j.integration.mail.command.JettyRunCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "email", description = "iCal4j Mail",
        subcommands = {SendCalendarCommand.class, JettyRunCommand.class})
public class ICalendarMailMain implements Runnable {

    @Override
    public void run() {
        System.out.println("iCal4j Mail. Usage: email <subcommand> [options]");
    }

    public static void main(String[] args) throws Exception {
        new CommandLine(new ICalendarMailMain()).execute(args);
    }
}
