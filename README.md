[Enterprise Integration Patterns]: https://www.enterpriseintegrationpatterns.com/

# iCal4j Integration

A Java API for iCalendar transport protocol integrations.

## Overview

Whilst iCal4j provides support for data interoperability via the iCalendar specifation,
we also need shared transport protocols to exchange data between applications. There are many
proven [Enterprise Integration Patterns] in use today, and this project provides
support for some of the more common integration approaches.

### Messaging Polling Model

The most common means of exchanging iCalendar data is via Email, which is a form of
asynchronous message queue. Messages are retrieved via polling the "queue" (i.e. Inbox)
and iCalendar data is extracted from attachments, etc.

### Publish/Subscribe Model

Another common pattern for exchanging data asynchronously is the Publish/Subscribe model, whereby
data is published to a shared channel and subscribers listen on that channel. A message
Topic is a common implementation of this, and is supported by many queue implementations.

### Webhooks

Webhooks are a special type of Publish/Subscribe model, whereby the publishing target is
configured per subscriber. As such it is usually implemented as a single-subscriber
pubsub model.

## Usage

The iCal4j Integration API provides two primary interfaces that support the implementation
of the different integration models described above. These interfaces are implemented by
most of the concrete integrations and aim to simplify the integration with different
transport mechanisms.

### Channel Adapter

The Channel Adapter interface supports sending or receiving data. It
defines methods used to publish and consume messages via the integration point.

### Channel Subscriber

The Channel Subscriber interface provides support for subscribers via the Publish/Subscribe model.


## Examples

### HTTP

Publish calendar object to HTTP target:

    Calendar calendar = ...
    ChannelAdapter<Calendar> producer = new ApacheHttpClientAdapter(null, "POST", "http://ical.example.com");
    producer.send(calendar);

Consume calendar object from HTTP endpoint:

    ChannelAdapter<Calendar> consumer = new ApacheHttpClientAdapter("http://tzurl.org/zoneinfo/Australia/Melbourne");
    Calendar calendar = consumer.poll(30);


### Email

Publish calendar object via email:

    Calendar calendar = ...
    Session session = ...
    ChannelAdapter<Calendar> producer = new JakartaMailAdapter(session);
    producer.send(calendar);

Consume calendar object delivered to an email address:

    Session session = ...
    ChannelAdapter<Calendar> consumer = new JakartaMailAdapter(session);
    Calendar calendar = consumer.poll(30);

Listen for calendar objects delivered to an email address:

TBD.

### Apache Camel

Added the ical4j-integration dependency to your project and configure your routes:

`from('ical:http://tzurl.org/zoneinfo/Australia/Melbourne').to("mock:result")`
