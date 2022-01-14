# iCal4j Integration

A collection of transport protocol integrations for iCalendar.

## Overview

Whilst the iCalendar specification supports data interoperability we also need shared
transport protocols to exchange these common data formats. This project implements
support for sharing iCalendar data via the most common transports.

## Usage

Added the ical4j-integration dependency to your project and configure your routes:

`from('ical:http://tzurl.org/zoneinfo/Australia/Melbourne').to("mock:result")`
