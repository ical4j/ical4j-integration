## ADDED Requirements

### Requirement: Send a calendar over SMTP

The system SHALL send an iCalendar object as an email via SMTP, building a MIME message that includes the calendar as a `text/calendar` part and reporting whether the send succeeded.

#### Scenario: Calendar is delivered
- **WHEN** a calendar is submitted to the SMTP channel and the SMTP server accepts it
- **THEN** `send` returns `true` and the server receives a message with a `text/calendar` part

#### Scenario: Send failure reported
- **WHEN** the SMTP transport raises a messaging error
- **THEN** `send` returns `false` and the error is logged

### Requirement: Derive recipients by default with caller override

The system SHALL derive `From`/`To`/`Cc` for the outgoing message from the calendar (per the recipient-derivation capability) by default. The system SHALL NOT overwrite any addressing field that the caller has already set on the message; explicitly supplied addressing always takes precedence.

#### Scenario: Recipients derived automatically
- **WHEN** a `REQUEST` calendar with an organizer and attendees is sent and the caller sets no addressing
- **THEN** the message `From` is the organizer and the `To` contains the attendees

#### Scenario: Caller addressing preserved
- **WHEN** the caller sets the message `To` explicitly before sending
- **THEN** the derived recipients do not replace the caller-supplied `To`

### Requirement: Set the iTIP method on the calendar content type

The system SHALL set the `method` parameter of the calendar body part's content type to the calendar's `METHOD` value (e.g. `text/calendar; method=REQUEST; charset=UTF-8`).

#### Scenario: Method parameter present
- **WHEN** a calendar with `METHOD:REQUEST` is sent
- **THEN** the calendar body part content type includes `method=REQUEST`

### Requirement: Use an explicitly provided mail Session

The system SHALL use a mail `Session` supplied by the caller for message construction and transport rather than a global default instance.

#### Scenario: Configured session used
- **WHEN** the channel is constructed with a session configured for a specific SMTP host and port
- **THEN** messages are transported using that host and port
