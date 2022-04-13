package org.ical4j.integration.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the metadata and templated structure for sending a calendar.
 */
public class MessageTemplate {

    private String fromAddress;

    private List<String> toAddresses = new ArrayList<>();

    private String subject;

    private String textBody;

    public MessageTemplate withFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    public MessageTemplate withToAddress(String toAddress) {
        this.toAddresses.add(toAddress);
        return this;
    }

    public MessageTemplate withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MessageTemplate withTextBody(String textBody) {
        this.textBody = textBody;
        return this;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public String getSubject() {
        return subject;
    }

    public String getTextBody() {
        return textBody;
    }
}
