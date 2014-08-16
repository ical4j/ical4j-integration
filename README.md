[![Build Status](https://drone.io/github.com/ical4j/ical4j-integration/status.png)](https://drone.io/github.com/ical4j/ical4j-integration/latest)

Enterprise Integration
======================

iCal4j Integration provides iCalendar support for Enterprise Integration Pattern frameworks such as Apache Camel.

## Usage

Added the ical4j-integration dependency to your project and configure your routes:

`from('ical:http://tzurl.org/zoneinfo/Australia/Melbourne').to("mock:result")`
