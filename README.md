# iCal4j Integration

A Java API for iCalendar transport protocol integrations.

## Overview

Whilst iCal4j provides support for data interoperability via the iCalendar specifation,
we also need shared transport protocols to exchange data between sytems. This project
implements support for transporting iCalendar data via some of the more common integration
transports.

### Messaging Polling Model

The most common means of exchanging iCalendar data is via Email, which is a form of
asynchronous message queue. Messages are retrieved via polling the "queue" (i.e. Inbox)
and iCalendar data is extracted from attachments, etc.

### Publish/Subscribe Model

Another common pattern for exchanging data asynchronously is the Publish/Subscribe model, whereby
data is published to a shared transport and subscribers listen on that transport. A message
Topic is a common implementation of this, and is supported by many queue implementations.

### Webhooks

Webhooks are a special type of Publish/Subscribe model, whereby the publishing target is
configured per subscriber. As such it is usually implemented as a single-subscriber
pubsub model.

## Usage

The iCal4j Integration API provides three primary interfaces that support the implementation
of the different integration models described above. These interfaces are implemented by
most of the concrete integrations and aim to simplify the integration with different
transports.

### Calendar Producer

The Calendar Producer interface is concerned with sending or publishing iCalendar data. It
defines a single `send()` method used to deliver messages via the integration point.

### Calendar Consumer

A Calendar Consumer implements support for polling the underlying transport to retrieve
iCalendar data. It defines a single `poll()` method used to fetch any data available from
the integration point.

### Calendar Listener

A collection of listener interfaces corresponding to the iCalendar iTIP Method definitions
provide support for subscribers via the Publish/Subscribe model. Typically these listeners
are implemented according to specific domain requirements, whereas the concrete integration
points implement the Calendar Listener Support that provides the pubsub notification
mechanism.

## Examples

Added the ical4j-integration dependency to your project and configure your routes:

`from('ical:http://tzurl.org/zoneinfo/Australia/Melbourne').to("mock:result")`
