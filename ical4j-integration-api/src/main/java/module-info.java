module ical4j.integration.api {
    requires java.base;
    requires transitive ical4j.core;
    requires transitive ical4j.vcard;

    requires org.slf4j;

    exports org.ical4j.integration;
    exports org.ical4j.integration.event;
    exports org.ical4j.integration.local;
}