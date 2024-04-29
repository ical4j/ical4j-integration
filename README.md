[Enterprise Integration Patterns]: https://www.enterpriseintegrationpatterns.com/

# iCal4j Integration

A Java API for iCalendar transport protocol integrations.

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

Publish calendar object via email:

    Calendar calendar = ...
    Session session = ...
    ChannelAdapter<Calendar> producer = new JakartaMailAdapter(session);
    producer.send(calendar);

Consume calendar object delivered to an email address:

    Calendar calendar = null;
    Session session = ...
    ChannelConsumer<Calendar> consumer = new JakartaMailAdapter(session);
    Calendar calendar = consumer.consume(c -> calendar = c, 30);

Subscribe to calendar objects delivered to an email address:

TBD.

### Apache Camel

Added the ical4j-integration dependency to your project and configure your routes:

`from('ical:http://tzurl.org/zoneinfo/Australia/Melbourne').to("mock:result")`
