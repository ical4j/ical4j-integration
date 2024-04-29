module ical4j.integration.mail {
    requires java.base;
    requires ical4j.integration.api;

    requires jakarta.mail;
    requires org.slf4j;

    exports org.ical4j.integration.mail;
}