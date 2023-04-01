package org.ical4j.integration.command;

import net.fortuna.ical4j.model.Calendar;
import picocli.CommandLine;

public class VersionProvider implements CommandLine.IVersionProvider {

    private String integrationVersion;

    private String ical4jVersion;

    private String javaVersion;

    public VersionProvider() {
        integrationVersion = getClass().getPackage().getImplementationVersion();
        ical4jVersion = Calendar.class.getPackage().getImplementationVersion();
        javaVersion = System.getProperty("java.version");
    }

    @Override
    public String[] getVersion() throws Exception {
        return new String[] {"iCal4j Connector " + integrationVersion,
                "\niCal4j: " + ical4jVersion, "\nJVM: " + javaVersion
        };
    }
}
