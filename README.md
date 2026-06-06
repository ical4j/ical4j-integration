[Enterprise Integration Patterns]: https://www.enterpriseintegrationpatterns.com/

# iCal4j Integration

A Java API for iCalendar transport protocol integrations.

> Releases are published to [Maven Central](https://central.sonatype.com/) (group `org.ical4j`)
> automatically when an `ical4j-integration-*` tag is pushed. See [RELEASING.md](RELEASING.md).

## Overview

Where the main iCal4j library provides support for data interoperability via the iCalendar specification,
this library focuses on transport protocols and patterns to exchange data between applications. There are many
proven [Enterprise Integration Patterns] in use today, and this project provides support for some of these common
integration approaches.

### Message Producer

A common use case for iCal4j is to send calendar invites via email to attendees. The `ChannelAdapter` interface
defines a common contract for sending data via email, or other implementations of underlying transport protocols.


### Message Polling

The most common method of exchanging iCalendar data is email, which is a kind of
asynchronous message queue. Messages are retrieved via polling the "queue" (i.e. Inbox)
and iCalendar data is extracted from attachments.

Implementations of the `ChannelConsumer` interface provide support for message polling, whilst abstracting the
complexities of the underlying transports.


### Reactive Streams

Java Reactive Streams provides support for the Publisher/Subscriber pattern, which is another asynchronous
messaging pattern for sharing data with multiple consumers.

Implementors of the `ChannelPublisher` interface provide support for subscribing multiple consumers to a data
stream.


## Examples

### Webhooks

Publish calendar object to webhook target:

    Calendar calendar = ...
    ChannelAdapter<Calendar> producer = new ApacheHttpClientAdapter(null, "POST", "http://ical.example.com");
    producer.publish(() -> calendar);

Consume calendar object from HTTP endpoint:

    Calendar calendar = null;
    ChannelConsumer<Calendar> consumer = new ApacheHttpClientAdapter("http://tzurl.org/zoneinfo/Australia/Melbourne");
    Calendar calendar = consumer.accept(c -> calendar = c, 30);


### Email

> **Note:** As of this release the mail channels require an explicit Jakarta Mail `Session`
> (constructors no longer fall back to `Session.getDefaultInstance(null)`), and outgoing messages
> derive their `From`/`To`/`Cc` from the calendar's `ORGANIZER`/`ATTENDEE` properties following the
> iTIP (RFC 5546) `METHOD` matrix. Any addressing you set explicitly on the message overrides the
> derived values. This is a **breaking** change to the channel/builder constructor signatures.

Publish a calendar object via email (recipients derived from the calendar):

    Calendar calendar = ...   // ORGANIZER + ATTENDEEs + METHOD:REQUEST
    Session session = ...     // configured with mail.smtp.host / mail.smtp.port
    EgressChannel<Calendar> producer = new JakartaMailSMTPChannel(session);
    producer.send(() -> calendar);

Consume a calendar object delivered to an email address (polling the INBOX):

    Session session = ...     // configured with an IMAP store
    IngressChannel<Calendar> consumer = new JakartaMailPollingChannel(session,
            new CalendarAttachmentProcessor());
    consumer.poll(calendar -> { /* handle */ }, 30);

Subscribe to calendar objects delivered to an email address (reactive, IMAP IDLE):

    Session session = ...
    JakartaMailPublisher publisher = new JakartaMailPublisher(session,
            new CalendarAttachmentProcessor(), "INBOX");
    publisher.subscribe(subscriber);

### Apache Camel

Added the ical4j-integration dependency to your project and configure your routes:

`from('ical:http://tzurl.org/zoneinfo/Australia/Melbourne').to("mock:result")`
