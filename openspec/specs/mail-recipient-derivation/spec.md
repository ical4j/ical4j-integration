# mail-recipient-derivation Specification

## Purpose

Derive email envelope addressing (`From`, `To`, `Cc`) for iTIP messages from an iCalendar object's scheduling properties, routing per the RFC 5546 method matrix.

## Requirements

### Requirement: Derive envelope addressing from calendar properties

The system SHALL derive email envelope addressing (`From`, `To`, `Cc`) from an iCalendar object's `ORGANIZER` and `ATTENDEE` properties. A `CAL-ADDRESS` value SHALL be mapped to an email address by stripping the `mailto:` URI scheme, and the `CN` parameter SHALL be used as the address personal name. Calendar user addresses that do not use the `mailto:` scheme SHALL be skipped.

#### Scenario: Organizer mapped to From
- **WHEN** a calendar has `ORGANIZER:mailto:jane@example.com` with `CN=Jane Doe`
- **THEN** the derived `From` is `Jane Doe <jane@example.com>`

#### Scenario: Non-mailto address skipped
- **WHEN** an `ATTENDEE` has a `CAL-ADDRESS` of `urn:uuid:1234`
- **THEN** that attendee is not included in any derived recipient list

### Requirement: Route messages per the iTIP METHOD matrix

The system SHALL choose the direction of addressing based on the calendar `METHOD` property per RFC 5546. For organizer-originated methods (`REQUEST`, `CANCEL`, `ADD`) the `From` SHALL be the `ORGANIZER` and the `To` SHALL be all `ATTENDEE`s. For attendee-originated methods (`REPLY`, `COUNTER`, `REFRESH`) the `From` SHALL be the relevant attendee and the `To` SHALL be the `ORGANIZER`. For `DECLINECOUNTER` the `From` SHALL be the `ORGANIZER` and the `To` SHALL be the relevant attendee.

#### Scenario: REQUEST goes to attendees
- **WHEN** a calendar has `METHOD:REQUEST`, an organizer, and two attendees
- **THEN** the derived `From` is the organizer and the derived `To` contains both attendees

#### Scenario: REPLY goes to the organizer
- **WHEN** a calendar has `METHOD:REPLY`, an organizer, and a single attendee
- **THEN** the derived `To` is the organizer and the derived `From` is the attendee

#### Scenario: DECLINECOUNTER goes to the attendee
- **WHEN** a calendar has `METHOD:DECLINECOUNTER`, an organizer, and a single attendee
- **THEN** the derived `From` is the organizer and the derived `To` is the attendee

### Requirement: Honour SENT-BY and resource filtering

The system SHALL use the `ORGANIZER` `SENT-BY` parameter, when present, as the `From` address (sending on behalf of the organizer). The system SHALL exclude attendees whose `CUTYPE` parameter is `RESOURCE` or `ROOM` from derived recipient lists.

#### Scenario: SENT-BY overrides organizer From
- **WHEN** an `ORGANIZER:mailto:boss@example.com` has `SENT-BY="mailto:assistant@example.com"` and the method is `REQUEST`
- **THEN** the derived `From` is `assistant@example.com`

#### Scenario: Room resource excluded from recipients
- **WHEN** a `REQUEST` calendar has a human attendee and an attendee with `CUTYPE=ROOM`
- **THEN** the derived `To` contains only the human attendee

### Requirement: Fall through when addressing cannot be derived

The system SHALL return no derived addressing when the required properties for the method are absent (e.g. `PUBLISH` with no explicit recipients, or an attendee-originated method without an identifiable attendee), leaving the caller responsible for addressing.

#### Scenario: PUBLISH has no derivable recipients
- **WHEN** a calendar has `METHOD:PUBLISH` and an organizer but no attendees
- **THEN** no `To` recipients are derived
